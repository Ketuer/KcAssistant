package crack.cduestc.jw.net.entity.request;

import crack.cduestc.jw.net.anno.RequestParam;
import crack.cduestc.jw.net.enums.Language;

public class LoginRequest extends Request{
    @RequestParam("username")
    private String name;
    @RequestParam("password")
    private String password;
    @RequestParam("session_locale")
    private Language lang;
    @RequestParam("captcha_response")
    private String captcha;

    public LoginRequest(String name, String password, Language lang) {
        this.name = name;
        this.password = password;
        this.lang = lang;
    }

    public LoginRequest name(String name){
        this.name = name;
        return this;
    }

    public LoginRequest password(String password){
        this.password = password;
        return this;
    }

    public LoginRequest lang(Language lang){
        this.lang = lang;
        return this;
    }

    public LoginRequest captcha(String captcha) {
        this.captcha = captcha;
        return this;
    }

    public String getPassword() {
        return password;
    }
}
