package crack.cduestc.jw.net.entity.response;

import crack.cduestc.jw.net.entity.WebCookie;

public class LoginResponse extends Response{

    private final String userName;

    private final String role;

    private final WebCookie cookie;

    public LoginResponse(String userName, String role, WebCookie cookie) {
        this.userName = userName;
        this.role = role;
        this.cookie = cookie;
    }

    public String getUserName() {
        return userName;
    }

    public String getRole() {
        return role;
    }

    public WebCookie getCookie() {
        return cookie;
    }
}
