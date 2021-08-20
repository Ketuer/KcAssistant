package crack.cduestc.jw.net.parser;

import crack.cduestc.jw.net.entity.WebCookie;
import crack.cduestc.jw.net.entity.response.LoginResponse;
import org.jsoup.nodes.Document;

public class LoginParser implements Parser<LoginResponse> {

    @Override
    public LoginResponse parse(Document document) {
        return null;  //特殊类型，请使用下面的parse
    }

    public LoginResponse parse(Document document, WebCookie session){
        String name = document.getElementsByClass("personal-name").first().text();
        name = name.substring(0, name.indexOf('('));
        String role = document.getElementsByAttributeValue("style", "padding:0 2px;color:#FFF;font-weight:blod;").text();
        return new LoginResponse(name, role, session);
    }
}
