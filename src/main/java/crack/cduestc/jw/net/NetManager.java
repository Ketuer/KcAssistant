package crack.cduestc.jw.net;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.util.Cookie;
import crack.cduestc.jw.net.captcha.ICaptcha;
import crack.cduestc.jw.net.captcha.WindowInputCaptcha;
import crack.cduestc.jw.net.entity.WebCookie;
import crack.cduestc.jw.net.entity.WebResponse;
import crack.cduestc.jw.net.entity.request.*;
import crack.cduestc.jw.net.entity.response.ErrorResponse;
import crack.cduestc.jw.net.entity.response.Response;
import crack.cduestc.jw.net.entity.response.SelectClassResponse;
import crack.cduestc.jw.net.entity.response.SuccessResponse;
import crack.cduestc.jw.net.enums.Method;
import crack.cduestc.jw.net.parser.*;
import org.apache.commons.codec.digest.DigestUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.security.GeneralSecurityException;
import java.util.Base64;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 基于Jsoup实现的网络接口管理类，包含全部网络接口操作
 *
 * @author Ketuer
 * @since 1.0
 */
public class NetManager {

    private static ICaptcha captcha = new WindowInputCaptcha();
    private static final LoginParser loginParser = new LoginParser();
    private static final InfoParser infoParser = new InfoParser();
    private static final ScoreParser scoreParser = new ScoreParser();
    private static final ClassParser classParser = new ClassParser();
    private static final OldClassParser oldClassParser = new OldClassParser();

    public static void setCaptcha(ICaptcha captcha) {
        NetManager.captcha = captcha;
    }

    /* 资源消耗巨大，某些需要进行动态js渲染的页面才会用到 */
    private static final WebClient client = new WebClient(BrowserVersion.CHROME);
    static {
        client.getOptions().setTimeout(10000);
        Logger.getLogger("com.gargoylesoftware").setLevel(Level.OFF);
    }

    private static final String ip = "http://www.cduestc.cn/eams";

    public static Response login(LoginRequest loginData){

        WebResponse main = request("/loginExt.action", Method.GET, null, null);
        if(main.getStatusCode() != 200){
            return new ErrorResponse(main.getReason(), main.getStatusCode());
        }
        BufferedImage image = downImage("/captcha/image.action", main.getCookies());
        String verifyCode = captcha.ocr(image);
        loginData.captcha(verifyCode);
        String str = main.getDocument().toString();
        String salt = str.substring(str.indexOf("CryptoJS.SHA1('") + 15, str.indexOf("' + form['password']"));  //解析动态盐
        loginData.password(DigestUtils.sha1Hex(salt+loginData.getPassword()));  //密码加盐计算SHA1处理
        WebResponse response = request("/loginExt.action", Method.POST, main.getCookies(), loginData);
        if(response.getStatusCode() != 200){
            return new ErrorResponse(response.getReason(), response.getStatusCode());
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

    public static Response info(WebCookie cookie){
        WebResponse response = request("/stdInfoApply!stdInfoCheck.action", Method.GET, cookie, null);
        if(response.getStatusCode() != 200){
            return new ErrorResponse(response.getReason(), response.getStatusCode());
        }
        return infoParser.parse(response.getDocument());
    }

    public static Response score(WebCookie cookie){
        WebResponse response = request("/teach/grade/course/person!historyCourseGrade.action?projectType=MAJOR",
                Method.GET, cookie, null);
        if(response.getStatusCode() != 200){
            return new ErrorResponse(response.getReason(), response.getStatusCode());
        }
        return scoreParser.parse(response.getDocument());
    }

    public static Response classes(WebCookie cookie, int term, int grade){
        WebResponse response = request("/courseTableForStd.action", Method.GET, cookie, null);
        String ids = response.getDocument().toString();
        int index = ids.lastIndexOf("bg.form.addInput(form,\"ids\",\"") + 29, i = 1;
        char[] chars = ids.substring(index, index + 20).toCharArray();
        while (chars[i] != '\"') i++;
        ids = new String(chars).substring(0, i);
        int semester = (grade - 2003) * 2 + term;
        ClassesRequest request = new ClassesRequest(1, "class", 1, semester, ids, "");
        HtmlPage page = requestAsClient("/courseTableForStd!courseTable.action", Method.POST, cookie, request);
        if(page == null) return new ErrorResponse("网络错误！", 404);
        return semester > 36 ? classParser.parse(page) : oldClassParser.parse(page);
    }

    public static Response selectClassList(WebCookie cookie){
        WebResponse response = request("/stdElectCourse.action", Method.GET, cookie, null);
        String start = "<a href=\"/eams/stdElectCourse!defaultPage.action?electionProfile.id=";
        String resp = response.getDocument().toString();
        if(resp.contains("现在未到选课时间")) return new ErrorResponse(response.getDocument().text(), 401);
        int left = resp.indexOf(start) + start.length(), right = resp.indexOf("\" style=\"margin:auto;font-size:16px;\" target=\"elect_page\">进入选课&gt;&gt;&gt;&gt;</a>");
        String profileId = resp.substring(left, right);
        request("/stdElectCourse!defaultPage.action?electionProfile.id="+profileId, Method.GET, cookie, null);
        WebResponse list = request("/stdElectCourse!data.action?profileId="+profileId, Method.GET, cookie, null);
        String data = list.getDocument().text();
        JSONArray classArray = JSON.parseArray(data.substring(18, data.length() - 1));
        return new SelectClassResponse(classArray, profileId);
    }

    public static Response doSelectClass(WebCookie cookie, String classId, String profileId){
        SelectClassRequest request = new SelectClassRequest(true, classId);
        WebResponse response = request("/stdElectCourse!batchOperator.action?profileId="+profileId, Method.POST, cookie, request);
        String select = response.getDocument().text();
        if(select.contains("成功") || select.contains("uccess")) {
            return new SuccessResponse();
        }
        return new ErrorResponse(select, 401);
    }

    private synchronized static HtmlPage requestAsClient(String api, Method method, WebCookie cookie, Request data){
        try {
            client.getCookieManager().clearCookies();
            cookie.forEachAddCookie((k, v) -> client.getCookieManager().addCookie(new Cookie("www.cduestc.cn", k, v)));
            WebRequest request = new WebRequest(new URL(ip+api), method.getHttpMethod());
            if(method != Method.GET) request.setRequestBody(data.asParam());
            return client.getPage(request);
        } catch (IOException e) {
            return null;
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
            if(resp.contains("验证码不正确")){
                return WebResponse.AUTH_FAILURE_CODE;
            }else {
                return WebResponse.AUTH_FAILURE_INFO;
            }
        }
        return response;
    }

    private static WebResponse connect(Connection con, Connection.Method method){
        try {
            con.timeout(30000);
            Connection.Response resp=con.method(method).execute();
            TimeUnit.MILLISECONDS.sleep(500);  //有过快访问限流机制，需要缓冲时间，最低0.5秒！
            return new WebResponse(resp.statusCode(), resp.parse(), new WebCookie(resp.cookies()));
        } catch (IOException | InterruptedException e) {
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

    private static BufferedImage downImage(String imgUrl, WebCookie webCookie) {
        try {
            URL url = new URL(ip+imgUrl);
            URLConnection connection = url.openConnection();
            connection.setConnectTimeout(10 * 1000);
            connection.setRequestProperty("Cookie", webCookie.toCookieString());
            InputStream in = connection.getInputStream();
            BufferedImage image = ImageIO.read(in);
            in.close();
            return image;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
