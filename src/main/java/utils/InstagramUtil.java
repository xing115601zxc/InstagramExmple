package utils;


import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import entity.InstagramPage;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.net.Proxy;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InstagramUtil {
    public InstagramPage instagramPage = new InstagramPage();
    public ArrayList<InstagramPage> edgesList = new ArrayList<>();
    public ArrayList<InstagramPage> IGTVedgesList = new ArrayList<>();
    public String html;
    public String full_name;
    public String account;
    public JsonObject user;
    public JsonArray edges;
    public JsonArray IGTVedges;
    public  int count;
    public  int IGTVcount;
    public JsonObject variables=new JsonObject();
    public  boolean has_next_page;
    public  String content;
    public  String query_hash;
    public static ArrayList<String> proxyList = UsefulProxyUtil.getProxyList();

//    {
//        try {
//            content = new String(Files.readAllBytes(Paths.get("C:\\Users\\hsingsu\\Desktop\\UsefulProxyUtil.txt")));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//    String IGTV;
//    {
//        try {
//            IGTV = new String(Files.readAllBytes(Paths.get("C:\\Users\\hsingsu\\Desktop\\IGTV.txt")));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    public void getFirstList(String account){
        this.account =account;
        StringBuffer insBuffer = new StringBuffer("https://www.instagram.com/");

        try {
            Map<String, String> cookies = new HashMap<>();
            cookies.put("ds_user_id","26112800489");
            cookies.put("sessionid","26112800489%3A05sthAcR0OyGGA%3A23");
            html = Jsoup.connect(insBuffer.insert(insBuffer.length(),account).toString())
                            .cookies(cookies)
                            .header("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                            .get().html();

            query_hash =Jsoup.connect("https://www.instagram.com/static/bundles/es6/ConsumerLibCommons.js/497d75d54380.js").ignoreContentType(true).get().text();
            Matcher matcher = Pattern.compile(("queryId:\"([^\"]+?)\"")).matcher(query_hash);
            if(matcher.find()){
                query_hash=matcher.group(1);
            }

            JsonObject sharedData;
            matcher= Pattern.compile("window\\._sharedData = ([\\S\\s]+?);</script>").matcher(html);
            if(matcher.find()){
                html=matcher.group(1);
                sharedData = new JsonParser().parse(html).getAsJsonObject();

                JsonArray ProfilePage=sharedData.getAsJsonObject("entry_data").getAsJsonArray("ProfilePage");
                JsonObject page0 = ProfilePage.get(0).getAsJsonObject();
                user=page0.getAsJsonObject("graphql").getAsJsonObject("user");
                JsonObject edge_owner_to_timeline_media = user.getAsJsonObject("edge_owner_to_timeline_media");
                JsonObject edge_felix_video_timeline=user.getAsJsonObject("edge_felix_video_timeline");
                JsonObject page_info = edge_owner_to_timeline_media.getAsJsonObject("page_info");
                edges=edge_owner_to_timeline_media.getAsJsonArray("edges");
                IGTVedges=edge_felix_video_timeline.getAsJsonArray("edges");
                full_name = user.get("full_name").getAsString();
//                user id
                variables.addProperty("id",user.get("id").getAsString());
                variables.addProperty("first","12");
                variables.addProperty("after",page_info.get("end_cursor").getAsString());
                has_next_page = page_info.get("has_next_page").getAsBoolean();
                count=edge_owner_to_timeline_media.get("count").getAsInt();
                IGTVcount=edge_felix_video_timeline.get("count").getAsInt();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

//https://www.instagram.com/graphql/query/?query_hash=1f950d414a6e11c98c556aa007b3157d
// &
// variables={"shortcode":"CRQJL5CnMqR","child_comment_count":3,"fetch_comment_count":40,"parent_comment_count":24,"has_threaded_comments":true}

//    end_cursor{\"cached_comments_cursor\": \"18155266123088089\", \"bifilter_token\": \"KBkBCgAQABAACAAIAAgAli_f4dr5_0wURAhgAA==\"}
    public ArrayList<InstagramPage> getList(JsonArray edges){
        SimpleDateFormat simpleDateFormat =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        edges.forEach(
                node->{
                    instagramPage=new InstagramPage();
                    JsonObject nodeObject = node.getAsJsonObject().get("node").getAsJsonObject();
                    instagramPage.setAccount(account);
                    instagramPage.setFull_name(full_name);
                    instagramPage.setShortcode(nodeObject.get("shortcode").getAsString());
                    JsonObject edge_media_to_caption=nodeObject.get("edge_media_to_caption").getAsJsonObject();

                    instagramPage.setText(edge_media_to_caption.get("edges").getAsJsonArray().get(0)
                                .getAsJsonObject().getAsJsonObject("node").get("text").getAsString().replaceAll("'","\\\\'"));
                    instagramPage.set__typename(nodeObject.get("__typename").getAsString());
                    instagramPage.setId(nodeObject.get("id").getAsString());
                    try {
                        instagramPage.setProduct_type(nodeObject.get("product_type").getAsString());
                    }catch (Exception e){
                        instagramPage.setProduct_type(instagramPage.get__typename());
                    }
                    instagramPage.setDate(
                            simpleDateFormat.format(nodeObject.get("taken_at_timestamp").getAsInt()*1000L)

                    );

//                    System.out.println(instagramPage.getProduct_type());
//                    System.out.println(instagramPage.getAccount());
//                    System.out.println(instagramPage.getFull_name());
//                    System.out.println(instagramPage.getId());
//                    System.out.println(instagramPage.getShortcode());
//                    System.out.println(instagramPage.getText());
//                    System.out.println(instagramPage.get__typename());
//                    System.out.println(instagramPage.getDatetime());
//                    System.out.println("==============================");
                    edgesList.add(instagramPage);
                }
        );
        return edgesList;
    }
    public ArrayList<InstagramPage> getNextList(){
        try {
            System.out.println("https://www.instagram.com/graphql/query/?query_hash="+query_hash+"&variables="+variables);
//            String json = Jsoup.connect("https://www.instagram.com/graphql/query/?query_hash="+query_hash+"&variables="+variables).ignoreContentType(true)
//                        .proxy("218.32.46.153", 32769).validateTLSCertificates(false).get().text();
            String json = Jsoup.connect("https://www.instagram.com/graphql/query/?query_hash="+query_hash+"&variables="+variables).ignoreContentType(true)
                        .get().text();
//            String json = Jsoup.connect("https://www.instagram.com/graphql/query/?query_hash=ea4baf885b60cbf664b34ee760397549&variables=%7B%22id%22%3A%222929997249%22%2C%22first%22%3A12%2C%22after%22%3A%22QVFDWGtpSk1jZ2dlMnUyOWswdGVUMUZTMVdqWWRZNkZpc2NmTTNhZnZISWVaWHd4OERRUmVFQU05Zzlqb2ZQYVVrYTNaV2JNOWdXWW1XSFRFM1FjaDl5YQ%3D%3D%22%7D").ignoreContentType(true).get().text();
            try {
                String proxy = proxyList.get((int) (proxyList.size() * Math.random()));
                String[] ipPort = proxy.split(":");
                System.getProperties().setProperty("proxySet", "true");
                System.getProperties().setProperty("http.proxyHost", ipPort[0]);
                System.getProperties().setProperty("http.proxyPort", ipPort[1]);
                System.out.println("需要登入");
                JsonObject jsonObject=new JsonParser().parse(json).getAsJsonObject();
            }catch (JsonSyntaxException e){
                Map<String, String> cookies = new HashMap<>();
                cookies.put("ds_user_id","26112800489");
                cookies.put("sessionid","26112800489%3A05sthAcR0OyGGA%3A23");
                String proxy = proxyList.get((int) (proxyList.size() * Math.random()));
                String[] ipPort = proxy.split(":");
                System.getProperties().setProperty("proxySet", "true");
                System.getProperties().setProperty("http.proxyHost", ipPort[0]);
                System.getProperties().setProperty("http.proxyPort", ipPort[1]);
                System.out.println("需要登入");
                json = Jsoup.connect("https://www.instagram.com/graphql/query/?query_hash="+query_hash+"&variables="+variables).ignoreContentType(true)
                        .cookies(cookies)
                        .header("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                        .get().text();
            }

            JsonObject jsonObject=new JsonParser().parse(json).getAsJsonObject();
            JsonObject page_info=jsonObject.getAsJsonObject("data").getAsJsonObject("user").getAsJsonObject("edge_owner_to_timeline_media").getAsJsonObject("page_info");
            has_next_page = page_info.get("has_next_page").getAsBoolean();
            variables.addProperty("after",page_info.get("end_cursor").getAsString());
            edges = jsonObject.getAsJsonObject("data").getAsJsonObject("user").getAsJsonObject("edge_owner_to_timeline_media").getAsJsonArray("edges");
            SimpleDateFormat simpleDateFormat =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            edges.forEach(
                    node->{
                        instagramPage=new InstagramPage();
                        JsonObject nodeObject = node.getAsJsonObject().get("node").getAsJsonObject();
                        instagramPage.setAccount(account);
                        instagramPage.setFull_name(full_name);
                        instagramPage.setShortcode(nodeObject.get("shortcode").getAsString());
                        JsonObject edge_media_to_caption=nodeObject.get("edge_media_to_caption").getAsJsonObject();

                        instagramPage.setText(edge_media_to_caption.get("edges").getAsJsonArray().get(0)
                                .getAsJsonObject().getAsJsonObject("node").get("text").getAsString().replaceAll("'","\\\\'"));
                        instagramPage.set__typename(nodeObject.get("__typename").getAsString());
                        instagramPage.setId(nodeObject.get("id").getAsString());
                        try {
                            instagramPage.setProduct_type(nodeObject.get("product_type").getAsString());
                        }catch (Exception e){
                            instagramPage.setProduct_type(instagramPage.get__typename());
                        }
                        instagramPage.setDate(
                                simpleDateFormat.format(nodeObject.get("taken_at_timestamp").getAsInt()*1000L)

                        );

//                    System.out.println(instagramPage.getProduct_type());
//                    System.out.println(instagramPage.getAccount());
//                    System.out.println(instagramPage.getFull_name());
//                    System.out.println(instagramPage.getId());
//                    System.out.println(instagramPage.getShortcode());
//                    System.out.println(instagramPage.getText());
//                    System.out.println(instagramPage.get__typename());
//                    System.out.println(instagramPage.getDatetime());
//                    System.out.println("==============================");
                        edgesList.add(instagramPage);
                    }
            );
        } catch (IOException e) {
            e.printStackTrace();
        }


        return edgesList;
    }


    public ArrayList<InstagramPage> getIGTVLists(JsonArray IGTVedges){
        edges.forEach(
                node->{

                    JsonObject nodeObject = node.getAsJsonObject().get("node").getAsJsonObject();

                    instagramPage.setShortcode(nodeObject.get("shortcode").getAsString());
                    instagramPage.setTitle(nodeObject.get("title").getAsString());
                    instagramPage.set__typename(nodeObject.get("__typename").getAsString());
                    IGTVedgesList.add(instagramPage);
                }
        );
        return IGTVedgesList;
    }

//    list  query_hash  ea4baf885b60cbf664b34ee760397549
//    page  query_hash  1f950d414a6e11c98c556aa007b3157d
//    單獨page  query_hash  bde1d63e5609fae49137387caae32ada 好像只會抓list
//                         bc3296d1ce80a24b1b6e40b1e72903f5
//    留言                    bc3296d1ce80a24b1b6e40b1e72903f5
    public static void main(String[] args) throws IOException {
        InstagramUtil util= new InstagramUtil();
//
        util.getFirstList("lovetaipeizoo");
//        util.getList(util.edges);
//        if(util.has_next_page){
//            util.getNextList();
//        }

        util.getNextList();
//        util.getList(new JsonParser().parse(util.content).getAsJsonArray() );
    }
}
