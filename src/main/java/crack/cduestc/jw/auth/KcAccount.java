package crack.cduestc.jw.auth;

import crack.cduestc.jw.auth.func.AuthFunction;
import crack.cduestc.jw.auth.func.ClazzFunction;
import crack.cduestc.jw.auth.func.ScoreFunction;
import crack.cduestc.jw.clazz.ClassTable;
import crack.cduestc.jw.exception.AuthorizationException;
import crack.cduestc.jw.exception.NetworkException;
import crack.cduestc.jw.exception.PasswordRegexException;
import crack.cduestc.jw.net.NetManager;
import crack.cduestc.jw.net.entity.WebCookie;
import crack.cduestc.jw.net.entity.request.LoginRequest;
import crack.cduestc.jw.net.entity.response.*;
import crack.cduestc.jw.net.enums.Language;
import crack.cduestc.jw.score.Score;
import crack.cduestc.jw.score.ScoreList;

import java.util.*;

/**
 * 教务系统账号实体类
 *
 * @author Ketuer
 * @since 1.0
 */
public class KcAccount implements AuthFunction, ScoreFunction, ClazzFunction {
    /* 账号 */
    private final String id;
    /* 密码 */
    private final String password;
    /* 语言 */
    private final Language language;
    /* 登陆成功后，会保存本次会话的JSESSIONID */
    private WebCookie cookie;
    /* 用户角色 */
    private String role;
    /* 用户名称 */
    private String name;
    /* 个人信息 */
    private UserInfoResponse info;


    private KcAccount(String id, String password, Language language){
        this.id = id;
        this.password = password;
        this.language = language;
    }

    /**
     * 创建一个新的账户实体
     * @param id 账号
     * @param password 密码
     * @return 账户实体
     */
    public static KcAccount create(String id, String password){
        return create(id, password, Language.CN);
    }

    /**
     * 创建一个新的账户实体
     * @param id 账号
     * @param password 密码
     * @param language 语言
     * @return 账户实体
     */
    public static KcAccount create(String id, String password, Language language){
        return new KcAccount(id, password, language);
    }

    /**
     * 登陆此账户
     */
    @Override
    public void login() {
        Response response = NetManager.login(new LoginRequest(this.id, this.password, language));
        if(response.getCode() == 200){
            LoginResponse loginResponse = (LoginResponse) response;
            this.cookie = loginResponse.getCookie();
            this.name = loginResponse.getUserName();
            this.role = loginResponse.getRole();
            this.refreshInfo();
        }else {
            ErrorResponse errorResponse = (ErrorResponse) response;
            throw new AuthorizationException(errorResponse.getReason());
        }
    }

    @Override
    public void logout() {
        if(NetManager.logout(this.cookie).getCode() != 200){
            throw new NetworkException("网络错误，请检查网络！");
        }
        info = null;
    }

    @Override
    public void resetPassword(String newPassword) {
        Response response = NetManager.resetPassword(cookie, newPassword, password);
        if(response.getCode() != 200){
            ErrorResponse error = (ErrorResponse) response;
            if(response.getCode() == 401){
                throw new AuthorizationException(error.getReason());
            }else {
                throw new PasswordRegexException(error.getReason());
            }
        }
    }

    /**
     * 更新个人信息
     */
    public void refreshInfo(){
        Response response = NetManager.info(cookie);
        if(response.getCode() == 401){
            ErrorResponse err = (ErrorResponse) response;
            throw new AuthorizationException(err.getReason());
        }else if(response.getCode() == 200){
            this.info = (UserInfoResponse) response;
        }else {
            ErrorResponse err = (ErrorResponse) response;
            throw new NetworkException(err.getReason());
        }
    }

    @Override
    public ScoreList getScore() {
        if(info == null) throw new AuthorizationException("账户未登录！");
        Integer grade = (Integer) info.get("年级");
        Response response = NetManager.score(cookie, 1, grade);
        if(response.getCode() == 401){
            ErrorResponse err = (ErrorResponse) response;
            throw new AuthorizationException(err.getReason());
        }else if(response.getCode() == 200){
            ScoreResponse scoreResponse = (ScoreResponse) response;
            Map<String, List<Score>> scoreMap = new HashMap<>();
            Set<String> terms = new HashSet<>();
            scoreResponse.forEach(score -> {
                terms.add(score.get("学年").toString());
                String key = score.get("学年").toString()+score.get("学期");
                if(!scoreMap.containsKey(key)) scoreMap.put(key, new ArrayList<>());
                scoreMap.get(key).add(new Score(score));
            });
            return new ScoreList(scoreMap, terms);
        }else {
            ErrorResponse err = (ErrorResponse) response;
            throw new NetworkException(err.getReason());
        }
    }

    @Override
    public ClassTable getClassTable(int term) {
        if(info == null) throw new AuthorizationException("账户未登录！");
        Response response = NetManager.classes(cookie, term, (Integer) info.get("年级"));
        if(response.getCode() == 401){
            ErrorResponse err = (ErrorResponse) response;
            throw new AuthorizationException(err.getReason());
        }else if(response.getCode() == 200){
            return ClassTable.convertToTable((ClassesResponse) response);
        }else {
            ErrorResponse err = (ErrorResponse) response;
            throw new NetworkException(err.getReason());
        }
    }

    public String getId() {
        return id;
    }

    public String getPassword() {
        return password;
    }

    public Language getLanguage() {
        return language;
    }

    public String getRole() {
        return role;
    }

    public String getName() {
        return name;
    }

    public UserInfoResponse getInfo() {
        return info;
    }
}
