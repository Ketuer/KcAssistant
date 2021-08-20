package crack.cduestc.jw.net;

import com.alibaba.fastjson.JSONObject;
import crack.cduestc.jw.net.entity.WebCookie;
import crack.cduestc.jw.net.entity.request.PasswordRequest;
import crack.cduestc.jw.net.entity.response.SuccessResponse;
import crack.cduestc.jw.net.parser.LoginParser;
import crack.cduestc.jw.net.entity.request.LoginRequest;
import crack.cduestc.jw.net.entity.request.Request;
import crack.cduestc.jw.net.enums.Method;
import crack.cduestc.jw.net.entity.response.ErrorResponse;
import crack.cduestc.jw.net.entity.response.Response;
import crack.cduestc.jw.net.resp.ResponseParser;
import crack.cduestc.jw.net.entity.WebResponse;
import org.apache.commons.codec.digest.DigestUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.GeneralSecurityException;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 基于Jsoup实现的网络接口管理类，包含全部网络接口操作
 *
 * @author Ketuer
 * @since 1.0
 */
public class NetManager {

    private static final LoginParser loginParser = new LoginParser();

    private static final String ip = "http://www.cduestc.cn/eams";

    public static Response login(LoginRequest loginData){
        WebResponse main = request("/loginExt.action", Method.GET, null, null);
        if(main.getStatusCode() != 200){
            return new ErrorResponse(main.getReason(), main.getStatusCode());
        }
        String str = main.getDocument().toString();
        String salt = str.substring(str.indexOf("CryptoJS.SHA1('") + 15, str.indexOf("' + form['password']"));  //解析动态盐
        loginData.password(DigestUtils.sha1Hex(salt+loginData.getPassword()));  //密码加盐计算SHA1处理
        WebResponse response = request("/loginExt.action", Method.POST, main.getCookies(), loginData);
        if(main.getStatusCode() != 200){
            return new ErrorResponse(main.getReason(), main.getStatusCode());
        }
        String resp = response.getDocument().toString();
        if(resp.contains("密码错误") || resp.contains("Error Password")){
            return new ErrorResponse("密码错误，请重新输入！", 401);
        }
        return loginParser.parse(response.getDocument(), main.getCookies());
    }

    public static Response logout(WebCookie cookie){
        WebResponse response = request("/logoutExt.action", Method.GET, cookie, null);
        if(response.getStatusCode() == 200){
            return new SuccessResponse();
        }else {
            return new ErrorResponse(response.getReason(), response.getStatusCode());
        }
    }

    public static Response resetPassword(WebCookie cookie, String newPassword, String password){
        WebResponse response = request("/security/my!edit.action", Method.GET, cookie, null);
        if(response.getStatusCode() != 200){
            return new ErrorResponse(response.getReason(), response.getStatusCode());
        }
        String key = response.getDocument().toString();
        key = key.substring(key.indexOf("var sKey = crypt.enc.Utf8.parse(\"") + 33, key.indexOf("\");\n      var sContent ="));
        String userId = response.getDocument().getElementsByAttributeValue("name", "user.id").val();
        String email = response.getDocument().getElementsByAttributeValue("name", "mail").val();
        String oldPassword = encryptPassword(password, key);
        newPassword = encryptPassword(newPassword, key);

        WebResponse result = request("/security/my!save.action", Method.POST, cookie,
                new PasswordRequest(oldPassword, newPassword, newPassword, email, userId));
        if(result.getStatusCode() != 200){
            return new ErrorResponse(result.getReason(), result.getStatusCode());
        }
        String dom = result.getDocument().toString();
        if(dom.contains("保存成功") || dom.contains("Save Success")){
            return new SuccessResponse();
        } else if(dom.contains("密码的长度不应小于六位")){
            return new ErrorResponse("密码的长度不应小于六位", 404);
        } else if(dom.contains("码至少包含一位大写字母")){
            return new ErrorResponse("新密码至少包含一位大写字母、一位小写字母和一位数字", 404);
        } else {
            return new ErrorResponse("网络错误！", 404);
        }
    }

    private static WebResponse request(String api, Method method, WebCookie cookie, Request data){
        Connection con = Jsoup.connect(ip+api);
        if(cookie != null) cookie.forEachAddCookie(con::cookie);
        if(data != null) data.forEachAddData(con::data);
        WebResponse response = connect(con, method.getMethod());
        String resp = response.getDocument().toString();
        if(resp.contains("请不要过快点击") || resp.contains("Please DO NOT click too quickly.")){
            return WebResponse.LIMIT_RESPONSE;   //你没猜错，有限流机制！
        }
        if(cookie != null && !api.equals("/logoutExt.action") && resp.contains("账号密码登录")){   //是否被重定向到登陆页面，如果是，表示没登陆
            return WebResponse.AUTH_RESPONSE;
        }
        return response;
    }

    private synchronized static WebResponse connect(Connection con, Connection.Method method){
        try {
            con.timeout(30000);
            Connection.Response resp=con.method(method).execute();
            TimeUnit.MILLISECONDS.sleep(500);  //有过快访问限流机制，需要缓冲时间，最低0.5秒！
            return new WebResponse(resp.statusCode(), resp.parse(), new WebCookie(resp.cookies()));
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return WebResponse.ERR_RESPONSE;
        }
    }

    private static String encryptPassword(String src, String key){
        try {
            if (key == null || key.length() != 16) return "";
            byte[] raw = key.getBytes();
            SecretKeySpec keySpec = new SecretKeySpec(raw, "AES");  //远端AES算法进行对称加密
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, keySpec);
            byte[] encrypted = cipher.doFinal(src.getBytes());  //使用密文进行加密
            return Base64.getEncoder().encodeToString(encrypted);   //使用Base64二次加密
        }catch (GeneralSecurityException ignore){}
        return "";
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
}
