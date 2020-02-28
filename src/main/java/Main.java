import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static spark.Spark.*;

import api.YelpApi;
import models.YelpBusiness;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.json.JSONObject;
import soy.ClosureTemplateEngine;
import spark.ModelAndView;
import spark.Request;

/**
 * Main application class that handles the requests, renders templates, and returns business information.
 */
public class Main {
    private static final Map<String, YelpBusiness> businessDetailsMap = new HashMap<>();

    public static void main(String[] args) {
        try {
            port(getHerokuAssignedPort());
            staticFiles.location("/public");

            get("/", (req, res) -> renderHomeScreen());
            get("/index", (req, res) -> renderHomeScreen());
            get("/search", (req, res) -> renderHomeScreen());
            post("/search", (req, res) -> renderSearch(req));
            get("/search/:id", (req, res) -> renderSearchDetails(req));
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private static int getHerokuAssignedPort() {
        ProcessBuilder processBuilder = new ProcessBuilder();
        if (processBuilder.environment().get("PORT") != null) {
            return Integer.parseInt(processBuilder.environment().get("PORT"));
        }
        return 4567; //return default port if heroku-port isn't set (i.e. on localhost)
    }

    private static String renderHomeScreen() {
        return renderTemplate(new HashMap<>(), "yelp.home.main", "base.soy", "home.soy");
    }

    private static String renderSearch(Request request) {
        List<NameValuePair> nameValuePairs = URLEncodedUtils.parse(request.body(), Charset.defaultCharset());
        Map<String, String> requestValues = toMap(nameValuePairs);

        double latitude, longitude;
        String term;
        try {
            latitude = Double.parseDouble(requestValues.get("latitude"));
            longitude = Double.parseDouble(requestValues.get("longitude"));
            term = requestValues.getOrDefault("term", "");
        } catch (NumberFormatException e) {
            System.out.println("Error reading location information");
            return renderHomeScreen();
        }

        YelpApi api = new YelpApi();

        List<YelpBusiness> generalBusinesses = api.searchBusinesses(term, latitude, longitude);
        Map<String, String> photoToId = new HashMap<>();

        for (YelpBusiness business : generalBusinesses) {
            YelpBusiness businessDetails;
            if (!businessDetailsMap.containsKey(business.getId())) {
                businessDetails = api.getBusinessDetails(business.getId());
                businessDetailsMap.put(businessDetails.getId(), businessDetails);
            } else {
                businessDetails = businessDetailsMap.get(business.getId());
            }

            if (businessDetails.getPhotoUrls() != null && !businessDetails.getPhotoUrls().isEmpty()) {
                businessDetails.getPhotoUrls().forEach(url -> photoToId.put(url, businessDetails.getId()));
            }
        }

        Map<String, Object> model = new HashMap<>();
        model.put("photos", photoToId);

        return renderTemplate(model, "yelp.home.search", "base.soy", "home.soy");
    }

    private static String renderSearchDetails(Request request) {
        String id = request.params(":id");
        YelpBusiness businessDetails = businessDetailsMap.get(id);
        if (businessDetails == null) {
            notFound("<html><body><h1>Business id not found</h1></body></html>");
            return null;
        }

        // Increase click count
        businessDetails.setNumberOfClicks(businessDetails.getNumberOfClicks() + 1);

        JSONObject details = new JSONObject();
        details.put("name", businessDetails.getName());
        details.put("price", businessDetails.getPrice());
        details.put("address", businessDetails.getAddress());
        details.put("rating", businessDetails.getRating());
        details.put("url", businessDetails.getUrl());
        details.put("latitude", businessDetails.getLatitude());
        details.put("longitude", businessDetails.getLongitude());
        details.put("numClicks", businessDetails.getNumberOfClicks());
        return details.toString();
    }

    private static Map<String, String> toMap(List<NameValuePair> pairs){
        Map<String, String> map = new HashMap<>();
        for(int i = 0; i < pairs.size(); i++){
            NameValuePair pair = pairs.get(i);
            map.put(pair.getName(), pair.getValue());
        }
        return map;
    }

    private static String renderTemplate(Map<String, Object> model, String soyTemplate, String... fileNames) {
        return new ClosureTemplateEngine(fileNames).render(new ModelAndView(model, soyTemplate));
    }
}