package utils;

import org.jsoup.Jsoup;

import java.io.IOException;
import java.net.Proxy;
import java.net.Socket;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UsefulProxyUtil {

     public static ArrayList<String> getProxyList() {
         String list = null;
         try {
             list = Jsoup.connect("https://www.us-proxy.org/").ignoreContentType(true)
                     .get().html();
         } catch (IOException e) {
             e.printStackTrace();
         }
         Matcher matcher= Pattern.compile("<textarea class=[\\S\\s]+UTC\\.([\\S\\s]+)<\\/textarea>").matcher(list);
        if(matcher.find()) list=matcher.group(1).trim();
        String[] array=list.split("\\n");
        ArrayList<String> useful=new ArrayList<>();
        System.out.println(useful.size());
        for (String s:array) {
            String ip,port;
            String[] ipPort=s.split(":");
            try {
                System.getProperties().setProperty("proxySet","true");
                System.getProperties().setProperty("http.proxyHost",ipPort[0]);
                System.getProperties().setProperty("http.proxyPort",ipPort[1]);
                String a =  Jsoup.connect("https://www.google.com/").ignoreContentType(true)
                        .timeout(1000).validateTLSCertificates(false).get().html();
                useful.add(s);
            }catch (Exception e){
                System.out.println("失敗"+ipPort[0]+":"+ipPort[1]);
            }
        }
         System.out.println(useful.size());
        return useful;
    }

    public static void main(String[] args) throws IOException {


    }
}
