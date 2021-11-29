package crack.cduestc.jw.auth;

import com.alibaba.fastjson.JSONObject;
import crack.cduestc.jw.auth.func.AuthFunction;
import crack.cduestc.jw.auth.func.ClazzFunction;
import crack.cduestc.jw.auth.func.ScoreFunction;
import crack.cduestc.jw.clazz.ClassTable;
import crack.cduestc.jw.clazz.SelectableClass;
import crack.cduestc.jw.eval.Evaluation;
import crack.cduestc.jw.eval.EvaluationTable;
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
import sun.nio.ch.Net;

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
    /* 学籍信息 */
    private UserInfoResponse info;
    /* ProfileId */
    private String profileId;

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

    /**
     * 退出登陆此账号
     */
    @Override
    public void logout() {
        if(NetManager.logout(this.cookie).getCode() != 200){
            throw new NetworkException("网络错误，请检查网络！");
        }
        info = null;
    }

    /**
     * 重置密码
     * @param newPassword 新密码
     */
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
     * 更新个人信息，重新拉取个人信息数据
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

    /**
     * 获取成绩信息
     * @return 成绩信息
     */
    @Override
    public ScoreList getScore() {
        if(info == null) throw new AuthorizationException("账户未登录！");
        Integer grade = (Integer) info.get("年级");
        Response response = NetManager.score(cookie);
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

    /**
     * 获取课程表
     * @param term 学期（本科为 1-8，专科为 1-6）
     * @return 课程表
     */
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

    /**
     * 获取选课列表
     * @return 选课列表
     */
    public List<SelectableClass> getSelectClassList(){
        profileId = null;
        if(info == null) throw new AuthorizationException("账户未登录！");
        Response response = NetManager.selectClassList(cookie);
        if(response.getCode() == 200){
            SelectClassResponse classResponse = (SelectClassResponse) response;
            this.profileId = classResponse.getProfileId();
            List<SelectableClass> list = new ArrayList<>();
            classResponse.forEach(resp -> list.add(new SelectableClass(resp)));
            return list;
        }else {
            throw new IllegalStateException(((ErrorResponse)response).getReason());
        }
    }

    /**
     * 执行选课操作，选课失败会抛出异常
     * @param classId 课程id
     */
    public void doSelectClass(String classId){
        if(profileId == null) throw new IllegalStateException("需要先获取选修课程列表用于后台生成！");
        if(info == null) throw new AuthorizationException("账户未登录！");
        Response response = NetManager.doSelectClass(cookie, classId, profileId);
        if(response.getCode() == 200) return;
        throw new IllegalStateException(((ErrorResponse)response).getReason());
    }

    /**
     * 执行退课操作
     * @param classId 课程id
     */
    public void undoSelectClass(String classId){
        if(profileId == null) throw new IllegalStateException("需要先获取选修课程列表用于后台生成！");
        if(info == null) throw new AuthorizationException("账户未登录！");
        Response response = NetManager.undoSelectClass(cookie, classId, profileId);
        if(response.getCode() == 200) return;
        throw new IllegalStateException(((ErrorResponse)response).getReason());
    }

    @Override
    public List<Evaluation> getEvalList() {
        if(info == null) throw new AuthorizationException("账户未登录！");
        Response response = NetManager.getEvaluationList(cookie);
        if(response.getCode() != 200)
            throw new IllegalStateException(((ErrorResponse)response).getReason());
        EvaluationResponse evaluationResponse = (EvaluationResponse) response;
        List<Evaluation> list = new ArrayList<>();
        evaluationResponse.forEach(eval -> list.add(new Evaluation(eval)));
        return list;
    }

    @Override
    public EvaluationTable getEvalTable(Evaluation e) {
        if(e.isStatus()) throw new IllegalStateException("已经评教完成，无法再次获取评估表单！");
        if(info == null) throw new AuthorizationException("账户未登录！");
        Response response = NetManager.getEvalTable(cookie, e);
        if(response.getCode() != 200)
            throw new IllegalStateException(((ErrorResponse)response).getReason());
        return ((EvaluationTableResponse) response).getTable();
    }

    public void finishEval(EvaluationTable table){
        if(info == null) throw new AuthorizationException("账户未登录！");
        Response response = NetManager.finishEval(cookie, table);
        if(response.getCode() != 200)
            throw new IllegalStateException(((ErrorResponse)response).getReason());
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

    /**
     * 获取用户信息
     * @return 用户信息
     */
    public UserInfoResponse getInfo() {
        return info;
    }
}
