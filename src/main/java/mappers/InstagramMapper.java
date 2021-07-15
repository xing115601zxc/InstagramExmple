package mappers;

import entity.InstagramPage;

import java.util.ArrayList;


public interface InstagramMapper {
    void addInstagramContent(InstagramPage page);
    void addInstagramList(InstagramPage page);
    ArrayList<InstagramPage> getPagelink(String datetime);
    Boolean existPagelink(String id);
    Boolean existPagecontent(String id);
}
