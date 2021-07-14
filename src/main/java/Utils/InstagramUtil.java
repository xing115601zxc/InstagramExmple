package Utils;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.lang3.StringEscapeUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.lang.reflect.Member;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InstagramUtil {
    String html;
    String full_name;
    String query_hash;
    JsonArray edges;

    public String getQuery_hash() {
        return query_hash;
    }

    public void setQuery_hash(String query_hash) {
        this.query_hash = query_hash;
    }

    JsonObject variables=new JsonObject();
    boolean has_next_page;
    void getFirstList(String account){
        StringBuffer insBuffer = new StringBuffer("https://www.instagram.com/");

        try {
            Map<String, String> cookies = new HashMap<>();
            cookies.put("ds_user_id","26112800489");
            cookies.put("sessionid","26112800489%3A05sthAcR0OyGGA%3A23");
            html = Jsoup.connect(insBuffer.insert(insBuffer.length(),account).toString())
                            .cookies(cookies)
                            .header("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                            .get().html();
            JsonObject sharedData;
            Matcher matcher= Pattern.compile("window\\._sharedData = ([\\S\\s]+?);</script>").matcher(html);
            if(matcher.find()){
                html=matcher.group(1);
                sharedData = new JsonParser().parse(html).getAsJsonObject();


                JsonArray ProfilePage=sharedData.getAsJsonObject("entry_data").getAsJsonArray("ProfilePage");
                JsonObject page0 = ProfilePage.get(0).getAsJsonObject();
                JsonObject user=page0.getAsJsonObject("graphql").getAsJsonObject("user");
                JsonObject edge_owner_to_timeline_media = user.getAsJsonObject("edge_owner_to_timeline_media");
                JsonObject page_info = edge_owner_to_timeline_media.getAsJsonObject("page_info");
                edges=edge_owner_to_timeline_media.getAsJsonArray("edges");
                full_name = user.get("full_name").getAsString();
                variables.addProperty("id",user.get("id").getAsString());
                variables.addProperty("first","12");
                variables.addProperty("after",page_info.get("end_cursor").getAsString());
                has_next_page = page_info.get("has_next_page").getAsBoolean();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }



    void getLists(){


    }


    public static void main(String[] args) {
        InstagramUtil util= new InstagramUtil();
        util.getFirstList("lovetaipeizoo");
        util.setQuery_hash("ea4baf885b60cbf664b34ee760397549");

    }
}
