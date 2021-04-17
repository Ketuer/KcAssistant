package crack.cduestc.jw.net;

import com.alibaba.fastjson.JSONObject;
import crack.cduestc.jw.net.resp.ResponseParser;
import crack.cduestc.jw.net.resp.WebResponse;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

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

    public static <T> T scorePlan(String session, ResponseParser<T> parser){
        return parser.parse(createGetWithSession(session, "/gradeLnAllAction.do?type=ln&oper=lnFajhKcCjInfo"));
    }

    public static <T> T scoreProgram(String session, ResponseParser<T> parser){
        WebResponse response = createGetWithSession(session, "/gradeLnAllAction.do?type=ln&oper=fa");
        if(response.getStatusCode() != 200) return null;
        String url = response.getDocument().getElementsByAttributeValue("name", "lnfaIfra").first().attr("src");
        return parser.parse(createGetWithSession(session, "/"+url));
    }

    public static <T> T changePassword(String session, JSONObject data, ResponseParser<T> parser){
        return parser.parse(createPostWithSession(session,  "/modifyPassWordAction.do", data));
    }

    public static <T> T classes(String session, String clazz, String term, ResponseParser<T> parser){
        try {
            clazz = URLEncoder.encode(clazz, "gbk");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return parser.parse(createGetWithSession(session, "/bjKbInfoAction.do?oper=bjkb_xx&xzxjxjhh="+term+"&xbjh="+clazz+"&xbm="+clazz));
    }

    public static <T> T classesElective(String session, ResponseParser<T> parser){
        return parser.parse(createGetWithSession(session, "/xkAction.do?actionType=6"));
    }

    public static List<String> classesElectiveList(String session, ResponseParser<List<String>> parser){
        return parser.parse(createGetWithSession(session, "/gradeLnAllAction.do?type=ln&oper=lnfaqk&flag=zx"));
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
            con.timeout(30000);
            Connection.Response resp=con.method(method).execute();
            return new WebResponse(resp.statusCode(), resp.parse(), resp.cookies());
        } catch (IOException e) {
            e.printStackTrace();
            return WebResponse.ERR_RESPONSE;
        }
    }
}
