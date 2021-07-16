package utils;

import org.jsoup.Jsoup;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UsefulProxyUtil {

     public void getProxyListFile() {
         String list = null;
         FileWriter writer = null;
         try {
             list = Jsoup.connect("https://www.us-proxy.org/").ignoreContentType(true)
                     .get().html();
             File file = new File("usefulProxy.txt");
             if (file.createNewFile()) {
                 System.out.println("File created: " + file.getName());
                 writer = new FileWriter("usefulProxy.txt");
                 writer.write(new Date().getTime()+"\n");
                 writer.close();
             } else {
                 System.out.println("File already exists.");
                 File usefulProxy = new File("usefulProxy.txt");
                 Scanner reader = new Scanner(usefulProxy);
                 if(reader.hasNextLine()){
                     long usefulProxyDate= reader.nextLong();
//                     判斷是否為12小時內的新資料
                     if(usefulProxyDate>new Date().getTime()-43200000L){
                         System.out.println("usefulProxy已在12小時內有更新紀錄，請問是否繼續 Y/N");
                         Scanner scanner = new Scanner(System.in);
                         if(!scanner.nextLine().equalsIgnoreCase("Y")){
                             System.out.println("結束usefulProxy獲取");
                             System.exit(0);
                         }
                     }
                 }
                 writer = new FileWriter("usefulProxy.txt");
                 writer.write(new Date().getTime()+"\n");
                 writer.close();
             }
         } catch (IOException e) {
             e.printStackTrace();
         }
         Matcher matcher= Pattern.compile("<textarea class=[\\S\\s]+UTC\\.([\\S\\s]+)<\\/textarea>").matcher(list);
        if(matcher.find()) list=matcher.group(1).trim();
        String[] array=list.split("\\n");
        ArrayList<String> useful=new ArrayList<>();

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
                System.out.println("Success connect "+ipPort[0]+":"+ipPort[1]);
                writer = new FileWriter("usefulProxy.txt",true);
                writer.write(ipPort[0]+":"+ipPort[1]+"\n");
                writer.close();
            }catch (Exception e){
                System.out.println("Fail connect "+ipPort[0]+":"+ipPort[1]);
            }

        }
         try {
             writer.close();
         } catch (IOException e) {
             e.printStackTrace();
         }
    }
    public ArrayList<String> getProxyList() {
         ArrayList<String> usefulProxyList=new ArrayList<>();
        File usefulProxy = new File("usefulProxy.txt");
        Scanner reader = null;
        try {
            reader = new Scanner(usefulProxy);
            while (reader.hasNextLine()){
                Matcher matcher=Pattern.compile("[\\d\\.]+:[\\d]+").matcher(reader.nextLine());
                if(matcher.find()){
                    usefulProxyList.add(matcher.group(0));
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

         return usefulProxyList;
    }
    public void setProxy(ArrayList<String> proxyList){
        String proxy = proxyList.get((int) (proxyList.size() * Math.random()));
        String[] ipPort = proxy.split(":");
        System.getProperties().setProperty("proxySet", "true");
        System.getProperties().setProperty("http.proxyHost", ipPort[0]);
        System.getProperties().setProperty("http.proxyPort", ipPort[1]);
    }


        public static void main(String[] args) throws IOException {


    }
}
