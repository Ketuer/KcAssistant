package crack.cduestc.jw.net.enums;

import org.jsoup.Connection;

public enum Method {
    GET(Connection.Method.GET), POST(Connection.Method.POST);

    private final Connection.Method method;

    Method(Connection.Method method){
        this.method = method;
    }

    public Connection.Method getMethod() {
        return method;
    }
}
