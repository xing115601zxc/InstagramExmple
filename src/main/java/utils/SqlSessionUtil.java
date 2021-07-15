package utils;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

public class SqlSessionUtil {

    /**
     * 取得SQL連線 SqlSession
     *
     * ip: 172.18.40.18
     * schema: train1
     */
    public static SqlSession getSqlSession() {
        try {
//            連結sql
            InputStream is = Resources.getResourceAsStream("Mybatis-config.xml");
            SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(is);
            SqlSession sqlSession = sqlSessionFactory.openSession(true);
            return sqlSession;
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Fail");
        return null;
    }

    public static void main(String[] args) {
        long l=1626155176;
        Date date=new Date();
        date.setTime(l*1000l);
        System.out.println(date);
//        System.out.println(date.getTime());
    }
}
