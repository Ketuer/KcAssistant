package crack.cduestc.jw.net.enums;

import com.gargoylesoftware.htmlunit.HttpMethod;
import org.apache.http.HttpRequest;
import org.jsoup.Connection;

public enum Method {
    GET(Connection.Method.GET, HttpMethod.GET), POST(Connection.Method.POST, HttpMethod.POST);

    private final Connection.Method method;
    private final HttpMethod httpMethod;

    Method(Connection.Method method, HttpMethod httpMethod){
        this.method = method;
        this.httpMethod = httpMethod;
    }

    public Connection.Method getMethod() {
        return method;
    }

    public HttpMethod getHttpMethod(){
        return httpMethod;
    }
}
