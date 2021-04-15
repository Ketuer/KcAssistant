package crack.cduestc.jw.net;

import com.alibaba.fastjson.JSONObject;
import crack.cduestc.jw.net.resp.ResponseParser;
import crack.cduestc.jw.net.resp.WebResponse;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.BufferedInputStream;
import java.io.IOException;

/**
 * 基于Jsoup实现的网络接口管理类，包含全部网络接口操作
 *
 * @author Ketuer
 * @since 1.0
 */
public class NetManager {

    private static String ip = "http://newjw.cduestc.cn:1234";

    /**
     * 是否切换为内网访问
     * @param toggle 是否切换
     */
    public static void switchInnerIp(boolean toggle){
        ip = toggle ? "http://newjw.cduestc.cn" : "http://newjw.cduestc.cn:1234";
    }

    public static <T> T login(JSONObject data, ResponseParser<T> parser){
        return parser.parse(createPost("/loginAction.do", data));
    }

    public static <T> T logout(String session, JSONObject data, ResponseParser<T> parser){
        return parser.parse(createPostWithSession(session, "/logout.do", data));
    }

    public static <T> T userDetail(String session, ResponseParser<T> parser){
        return parser.parse(createGetWithSession(session, "/xjInfoAction.do?oper=xjxx"));
    }

    public static BufferedInputStream userDetailHeadImg(String session){
        try {
            Connection con = Jsoup.connect("/xjInfoAction.do?oper=img");
            con.header("Cookie", "JSESSIONID="+session);
            con.ignoreContentType(true);
            Connection.Response response = con.execute();
            return response.bodyStream();
        }catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }

    public static <T> T scorePass(String session, ResponseParser<T> parser){
        return parser.parse(createGetWithSession(session, "/gradeLnAllAction.do?type=ln&oper=qbinfo"));
    }

    public static <T> T scoreFailed(String session, ResponseParser<T> parser){
        return parser.parse(createGetWithSession(session, "/gradeLnAllAction.do?type=ln&oper=bjg"));
    }

    public static <T> T scoreProgram(String session, ResponseParser<T> parser){
        WebResponse response = createGetWithSession(session, "/gradeLnAllAction.do?type=ln&oper=fa");
        if(response.getStatusCode() != 200) return null;
        String url = response.getDocument().getElementsByAttributeValue("name", "lnfaIfra").first().attr("src");
        return parser.parse(createGetWithSession(session, ip+"/"+url));
    }

    private static WebResponse createGetWithSession(String session, String url){
        Connection con = Jsoup.connect(ip+url);
        con.header("Cookie", "JSESSIONID="+session);
        return connect(con, Connection.Method.GET);
    }

    private static WebResponse createPostWithSession(String session, String url, JSONObject data){
        Connection con = Jsoup.connect(ip+url);
        con.header("Cookie", "JSESSIONID="+session);
        data.forEach((k, v) -> con.data(k, v.toString()));
        return connect(con, Connection.Method.POST);
    }

    private static WebResponse createGet(String url){
        Connection con = Jsoup.connect(ip+url);
        return connect(con, Connection.Method.GET);
    }

    private static WebResponse createPost(String url, JSONObject data){
        Connection con = Jsoup.connect(ip+url);
        data.forEach((k, v) -> con.data(k, v.toString()));
        return connect(con, Connection.Method.POST);
    }

    private static WebResponse connect(Connection con, Connection.Method method){
        try {
            con.timeout(3000);
            Connection.Response resp=con.method(method).execute();
            return new WebResponse(resp.statusCode(), resp.parse(), resp.cookies());
        } catch (IOException e) {
            e.printStackTrace();
            return WebResponse.ERR_RESPONSE;
        }
    }
}
