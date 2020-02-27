package api;

import com.google.common.base.Strings;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import models.YelpBusiness;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * API class to fetch data from Yelp.
 */
public class YelpApi {
    private static final String API_TOKEN = "Bearer <api token>";
    private static final String URL_PREFIX = "https://api.yelp.com/v3/businesses/";
    private static final String BASE_SEARCH = URL_PREFIX + "search?limit=10&open_now=true";
    private final OkHttpClient client;

    public YelpApi() {
        client = new OkHttpClient();
    }

    private Request.Builder addAuthorization(Request.Builder builder) {
        return builder.addHeader("Authorization", API_TOKEN);
    }

    public List<YelpBusiness> searchBusinesses(String term, double latitude, double longitude) {
        String url = BASE_SEARCH +
                "&latitude=" + latitude +
                "&longitude=" + longitude;
        if (!Strings.isNullOrEmpty(term)) {
            url += "&term=" + term;
        } else {
            url += "&term=restaurants";
        }
        Request.Builder builder = addAuthorization(new Request.Builder().url(url));
        try {
            Response response = client.newCall(builder.build()).execute();
            JSONObject body = new JSONObject(response.body().string());
            JSONArray businesses = body.getJSONArray("businesses");
            List<YelpBusiness> yelpBusinesses = new ArrayList<>();
            for (int i = 0; i < businesses.length(); i++) {
                yelpBusinesses.add(YelpBusiness.fromJsonObject(businesses.getJSONObject(i)));
            }

            return yelpBusinesses;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public YelpBusiness getBusinessDetails(String businessId) {
        String url = URL_PREFIX + businessId;
        Request.Builder builder = addAuthorization(new Request.Builder().url(url));
        try {
            // The Yelp api has a queries per second limit.
            Thread.sleep(200L);

            Response response = client.newCall(builder.build()).execute();
            return YelpBusiness.fromApiString(response.body().string());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
