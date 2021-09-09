package crack.cduestc.jw.net.entity;

import org.jsoup.nodes.Document;

import java.util.Map;

public class WebResponse {
    private final int statusCode;
    private final Document document;
    private final WebCookie cookies;
    private final String reason;

    public WebResponse(int statusCode, Document document, WebCookie cookies) {
        this(statusCode, document, cookies, "");
    }

    public WebResponse(int statusCode, Document document, WebCookie cookies, String reason) {
        this.statusCode = statusCode;
        this.document = document;
        this.cookies = cookies;
        this.reason = reason;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public Document getDocument() {
        return document;
    }

    public WebCookie getCookies() {
        return cookies;
    }

    public String getReason() {
        return reason;
    }

    public final static WebResponse AUTH_FAILURE_INFO= new WebResponse(402, null, null, "用户名或密码错误！");
    public final static WebResponse AUTH_FAILURE_CODE = new WebResponse(403, null, null, "验证码错误！");
    public final static WebResponse AUTH_RESPONSE = new WebResponse(401, null, null, "账户未登陆验证！");
    public final static WebResponse ERR_RESPONSE = new WebResponse(404, null, null, "网络错误！");
    public final static WebResponse LIMIT_RESPONSE = new WebResponse(405, null, null, "请求频率过快！");
}
