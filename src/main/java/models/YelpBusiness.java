package models;

import com.google.common.base.Objects;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class YelpBusiness {
    private String id;
    private List<String> photoUrls;
    private double latitude;
    private double longitude;
    private String name;
    private String address;
    private String url;
    private String price;
    private double rating;
    private int numberOfClicks;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<String> getPhotoUrls() {
        return photoUrls;
    }

    public void setPhotoUrls(List<String> photoUrls) {
        this.photoUrls = photoUrls;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public int getNumberOfClicks() {
        return numberOfClicks;
    }

    public void setNumberOfClicks(int numberOfClicks) {
        this.numberOfClicks = numberOfClicks;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        YelpBusiness that = (YelpBusiness) o;
        return Double.compare(that.latitude, latitude) == 0 &&
                Double.compare(that.longitude, longitude) == 0 &&
                Double.compare(that.rating, rating) == 0 &&
                Objects.equal(id, that.id) &&
                Objects.equal(photoUrls, that.photoUrls) &&
                Objects.equal(name, that.name) &&
                Objects.equal(address, that.address) &&
                Objects.equal(url, that.url) &&
                Objects.equal(price, that.price);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id, photoUrls, latitude, longitude, name, address, url, price, rating);
    }

    @Override
    public String toString() {
        return "YelpBusiness{" +
                "id='" + id + '\'' +
                ", photoUrls=" + photoUrls +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", url='" + url + '\'' +
                ", price='" + price + '\'' +
                ", rating=" + rating +
                '}';
    }

    public static YelpBusiness fromApiString(String responseString) {
        JSONObject json = new JSONObject(responseString);
        return fromJsonObject(json);
    }

    public static YelpBusiness fromJsonObject(JSONObject json) {
        YelpBusiness business = new YelpBusiness();
        business.setId(json.getString("id"));
        business.setName(json.getString("name"));
        business.setUrl(json.getString("url"));
        business.setRating(json.optDouble("rating"));
        business.setPrice(json.optString("price"));

        JSONObject coordinates = json.has("coordinates") ?
                json.getJSONObject("coordinates") : null;
        if (coordinates != null &&
                !coordinates.isNull("latitude") &&
                !coordinates.isNull("longitude"))
        {
            business.setLatitude(coordinates.getDouble("latitude"));
            business.setLongitude(coordinates.getDouble("longitude"));
        }

        JSONArray photos = json.has("photos") ?
                json.getJSONArray("photos") : null;
        if (photos != null && photos.length() > 0) {
            List<String> photoUrls = new ArrayList<>();
            for (int i = 0; i < photos.length() && i < 3; i++) {
                photoUrls.add(photos.getString(i));
            }
            business.setPhotoUrls(photoUrls);
        }

        JSONObject location = json.has("location") ?
                json.getJSONObject("location") : null;
        if (location != null && !location.isNull("display_address")) {
            JSONArray displayAddress = location.getJSONArray("display_address");
            String address = "";
            for (int i = 0; i < displayAddress.length(); i++) {
                address += displayAddress.getString(i) + "\n";
            }
            business.setAddress(address);
        }

        return business;
    }
}
