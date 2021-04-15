package crack.cduestc.jw.auth;

import com.alibaba.fastjson.JSONObject;
import crack.cduestc.jw.auth.func.AuthFunction;
import crack.cduestc.jw.auth.func.ScoreFunction;
import crack.cduestc.jw.net.NetManager;
import crack.cduestc.jw.net.resp.WebResponse;
import crack.cduestc.jw.score.Score;
import crack.cduestc.jw.score.ScoreList;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.*;
import java.util.function.BiConsumer;

/**
 * 教务系统账号实体类
 *
 * @author Ketuer
 * @since 1.0
 */
public class KcAccount implements AuthFunction, ScoreFunction {
    /* 账号 */
    private final String id;
    /* 密码 */
    private final String password;
    /* 登陆成功后，会保存本次会话的JSESSIONID */
    private String session;
    /* 学籍信息 */
    private JSONObject userDetail;
    /* 班级 */
    private String clazz;

    private KcAccount(String id, String password){
        this.id = id;
        this.password = password;
    }

    /**
     * 创建一个新的账户实体
     * @param id 账号
     * @param password 密码
     * @return 账户实体
     */
    public static KcAccount create(String id, String password){
        return new KcAccount(id, password);
    }

    /**
     * 登陆此账户
     * @return 是否登陆成功
     */
    @Override
    public boolean login() {
        JSONObject object = new JSONObject();
        object.put("zjh", id);
        object.put("mm", password);
        boolean login = NetManager.login(object, response -> {
            if(response.getStatusCode() != 200) return false;
            Document document = response.getDocument();
            boolean success = document.getElementsByClass("errorTop").isEmpty();
            if(success) session = response.getCookies().get("JSESSIONID");
            return success;
        });
        if(login){
            this.userDetail = NetManager.userDetail(session, response -> {
                if(response.getStatusCode() != 200) return null;
                JSONObject detail = new JSONObject();
                Document document = response.getDocument();
                Element table = document.getElementById("tblView");
                Elements data = table.getElementsByTag("td");
                int i = 0;
                while (i < data.size() - 1){
                    if(i == 4) {
                        i += 2;
                        continue;
                    }
                    String key = data.get(i++).text().replace(":", "");
                    String value = data.get(i++).text();
                    if(key.contains("班级")) clazz = value;
                    detail.put(key, value);
                }
                return detail;
            });
            return true;
        }else {
            return false;
        }
    }

    /**
     * 登出此账户
     * @return 登出结果
     */
    @Override
    public boolean logout() {
        JSONObject object = new JSONObject();
        object.put("loginType", "platformLogin");
        return NetManager.logout(session, object, response -> response.getStatusCode() == 200);
    }

    /**
     * 获取成绩（包含及格和不及格成绩）
     * @return 成绩
     */
    @Override
    public ScoreList getScore() {
        return new ScoreList() {
            private final Map<String, List<Score>> listMap = NetManager.scorePass(session, response -> {
                Map<String, List<Score>> listMap = new HashMap<>();
                Elements titles = response.getDocument().getElementsByAttributeValue("id", "tblHead");
                Elements scores = response.getDocument().getElementsByClass("titleTop2");
                for (int i = 0; i < scores.size(); i++) {
                    String term = titles.get(i).getElementsByTag("b").first().text();
                    Elements trs = scores.get(i).getElementsByClass("displayTag").first().getElementsByTag("tr");
                    List<Score> scoreList = new ArrayList<>();
                    for (int j = 1; j < trs.size(); j++) {
                        Elements tds = trs.get(j).getElementsByTag("td");
                        scoreList.add(new Score(tds.get(0).text(), tds.get(2).text(), tds.get(4).text(), tds.get(6).text(),tds.get(5).text(), ""));
                    }
                    listMap.put(term, scoreList);
                }
                return listMap;
            });
            private final List<Score> listFailed = NetManager.scoreFailed(session, response -> {
                List<Score> scoreList = new ArrayList<>();
                Elements trs = response.getDocument().getElementById("user").getElementsByTag("tr");
                for (int i = 1; i < trs.size(); i++) {
                    Elements tds = trs.get(i).getElementsByTag("td");
                    scoreList.add(new Score(tds.get(0).text(), tds.get(2).text(), tds.get(4).text(), tds.get(6).text(),tds.get(5).text(), tds.get(8).text()));
                }
                return scoreList;
            });

            public List<Score> getScore(String term){
                return listMap.getOrDefault(term, new ArrayList<>());
            }

            public List<Score> getFailedScore(){
                return listFailed;
            }

            public Set<String> getTerms(){
                return listMap.keySet();
            }

            public void forEach(BiConsumer<String, List<Score>> consumer){
                listMap.forEach(consumer);
            }
        };
    }

    /**
     * 获取方案成绩（不会进行学期分类）
     * @return 方案成绩
     */
    @Override
    public List<Score> getScoreProgram() {
        return NetManager.scoreProgram(session, response -> {
            List<Score> scoreList = new ArrayList<>();
            Document document = response.getDocument();
            Elements elements = document.getElementById("user").getElementsByTag("tr");
            for (int i = 1; i < elements.size(); i++) {
                Elements tds = elements.get(i).getElementsByTag("td");
                scoreList.add(new Score(tds.get(0).text(), tds.get(2).text(), tds.get(4).text(), tds.get(6).text(),tds.get(5).text(), tds.get(7).text()));
            }
            return scoreList;
        });
    }

    /**
     * 获取学号
     * @return 学号
     */
    public String getId() {
        return id;
    }

    /**
     * 获取密码
     * @return 密码
     */
    public String getPassword() {
        return password;
    }

    /**
     * 获取用的详细信息(学籍信息)，所有的信息都装在一个JSON中
     * @return 用户信息
     */
    public JSONObject getUserDetail() {
        return userDetail;
    }

    /**
     * 获取用的详细信息(学籍信息)头像照片
     * @return 图片输入流
     */
    public BufferedInputStream getUserDetailHeadImg(){
        return NetManager.userDetailHeadImg(session);
    }
}
