package crack.cduestc.jw.net.parser;

import crack.cduestc.jw.net.entity.response.UserInfoResponse;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class InfoParser implements Parser<UserInfoResponse> {
    @Override
    public UserInfoResponse parse(Document document) {
        UserInfoResponse response = new UserInfoResponse();
        Elements elements = document.getElementById("studentInfoTb").getElementsByTag("td");
        for (int i = 1; i < 48; i+=2) {
            if(i == 5) i++;
            String name = elements.get(i).text().replace("：", "");
            String value = elements.get(i+1).text();
            response.setField(name, value);
        }
        Elements elements1 = document.getElementsByTag("table").get(1).getElementsByTag("td");
        for (int i = 1; i < 12; i+=2) {
            String name = elements1.get(i).text().replace("：", "");
            String value = elements1.get(i+1).text();
            response.setField(name, value);
        }
        return response;
    }
}
