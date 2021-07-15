package start;

import entity.InstagramPage;
import mappers.InstagramMapper;
import org.apache.ibatis.session.SqlSession;
import org.apache.log4j.Logger;
import utils.InstagramUtil;
import utils.SqlSessionUtil;
import utils.UsefulProxyUtil;

import java.util.ArrayList;


public class startInstagramLink {
    private static InstagramUtil util = new InstagramUtil();

    private static Logger logger = Logger.getLogger(startInstagramLink.class);
    private static SqlSession sqlSession = SqlSessionUtil.getSqlSession();
    private static InstagramMapper instagramMapper = sqlSession.getMapper(InstagramMapper.class);

    public static void main(String[] args) {
        util.getFirstList("lovetaipeizoo");
        util.getList(util.edges);
        ArrayList<String> proxyList = util.proxyList;
        String proxy = proxyList.get((int) (proxyList.size() * Math.random()));
        String[] ipPort = proxy.split(":");
        System.getProperties().setProperty("proxySet", "true");
        System.getProperties().setProperty("http.proxyHost", ipPort[0]);
        System.getProperties().setProperty("http.proxyPort", ipPort[1]);
        while (util.has_next_page) {
            proxy = proxyList.get((int) (proxyList.size() * Math.random()));
            ipPort = proxy.split(":");
            System.getProperties().setProperty("proxySet", "true");
            System.getProperties().setProperty("http.proxyHost", ipPort[0]);
            System.getProperties().setProperty("http.proxyPort", ipPort[1]);
            util.getNextList();
            try {
                Thread.sleep(800);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        util.edgesList.forEach(page ->
                {
                    try {
                        instagramMapper.addInstagramList(page);
                    } catch (Exception e) {
                        logger.error("資料重複");
                    }
                }
        );


        System.out.println(util.count + ":" + util.edgesList.size());
    }
}
