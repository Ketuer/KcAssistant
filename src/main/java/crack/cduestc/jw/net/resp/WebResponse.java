package crack.cduestc.jw.net.resp;

import org.jsoup.nodes.Document;

import java.util.Map;

public class WebResponse {
    private final int statusCode;
    private final Document document;
    private final Map<String, String> cookies;

    public WebResponse(int statusCode, Document document, Map<String, String> cookies) {
        this.statusCode = statusCode;
        this.document = document;
        this.cookies = cookies;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public Document getDocument() {
        return document;
    }

    public Map<String, String> getCookies() {
        return cookies;
    }

    public final static WebResponse ERR_RESPONSE = new WebResponse(404, null, null);
}
