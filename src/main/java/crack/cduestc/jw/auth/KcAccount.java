package crack.cduestc.jw.auth;

import com.alibaba.fastjson.JSONObject;
import crack.cduestc.jw.auth.func.AuthFunction;
import crack.cduestc.jw.auth.func.ClazzFunction;
import crack.cduestc.jw.auth.func.ScoreFunction;
import crack.cduestc.jw.clazz.ClassTable;
import crack.cduestc.jw.clazz.Clazz;
import crack.cduestc.jw.net.NetManager;
import crack.cduestc.jw.score.Score;
import crack.cduestc.jw.score.ScoreList;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedInputStream;
import java.util.*;
import java.util.function.BiConsumer;

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
    /* 登陆成功后，会保存本次会话的JSESSIONID */
    private String session;
    /* 学籍信息 */
    private JSONObject userDetail;
    /* 班级 */
    private String clazz;
    /* 分流方向 */
    private String level;
    /* 年级 */
    private int grade;

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
                    if(key.contains("分流方向")) level = value;
                    if(key.contains("年级")) grade = Integer.parseInt(value.replace("级", ""));
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
        if(session == null) return false;
        JSONObject object = new JSONObject();
        object.put("loginType", "platformLogin");
        return NetManager.logout(session, object, response -> response.getStatusCode() == 200);
    }

    /**
     * 重置账户密码
     * @return 是否重置成功
     */
    @Override
    public boolean resetPassword(String newPassword) {
        if(session == null) return false;
        JSONObject object = new JSONObject();
        object.put("yhlbdm", "01");
        object.put("zjh", id);
        object.put("oldPass", password);
        object.put("newPass1", newPassword);
        object.put("newPass2", newPassword);
        return NetManager.changePassword(session, object, response -> response.getStatusCode() == 200);
    }

    /**
     * 获取成绩（包含及格和不及格成绩）
     * @return 成绩
     */
    @Override
    public ScoreList getScore() {
        if(session == null) return null;
        return new ScoreList() {
            private final Map<String, List<Score>> listMap = NetManager.scoreProgram(session, response -> {
                Map<String, Score> scoreList = new HashMap<>();
                Document document = response.getDocument();
                Elements elements = document.getElementById("user").getElementsByTag("tr");
                for (int i = 1; i < elements.size(); i++) {
                    Elements tds = elements.get(i).getElementsByTag("td");
                    scoreList.put(tds.get(0).text(), new Score(tds.get(0).text(), tds.get(2).text(), tds.get(4).text(),
                            tds.get(6).text(),tds.get(5).text(), tds.get(7).text()));
                }
                return NetManager.scorePlan(session, responsePlan -> {
                    Map<String, List<Score>> map = new LinkedHashMap<>();
                    Elements titles = responsePlan.getDocument().getElementsByClass("title");
                    Elements tables = responsePlan.getDocument().getElementsByClass("displayTag");
                    for (int i = 3; i < titles.size(); i+=3) {
                        map.put(titles.get(i).getElementsByTag("b").first().text(), new ArrayList<>());
                    }
                    int i = 0;
                    for (String term : map.keySet()){
                        Elements scoreIn = tables.get(i*3+2).getElementsByTag("tr");
                        for (int j = 1; j < scoreIn.size() - 1; j++) {
                            Elements tds = scoreIn.get(j).getElementsByTag("td");
                            String id = tds.get(0).text();
                            map.get(term).add(scoreList.get(id));
                        }
                        Elements scoreOut = tables.get(i*3+3).getElementsByTag("tr");
                        for (int j = 1; j < scoreOut.size(); j++) {
                            Elements tds = scoreOut.get(j).getElementsByTag("td");
                            String id = tds.get(0).text();
                            map.get(term).add(scoreList.get(id));
                        }
                        i++;
                    }
                    return map;
                });
            });

            public List<Score> getScore(String term){
                return listMap.getOrDefault(term, new ArrayList<>());
            }

            public Set<String> getTerms(){
                return listMap.keySet();
            }

            public void forEach(BiConsumer<String, List<Score>> consumer){
                listMap.forEach(consumer);
            }
        };
    }

    private Map<Integer, Map<Integer, List<Clazz>>> getTableMap(int term){
        int add = (term-1)/2;
        String termStr = (grade+add)+"-"+(grade+add+1)+"-"+(term%2 == 1 ? "1":"2")+"-1";
        return NetManager.classes(session, clazz, termStr, responseMain -> {
            List<String> elective = NetManager.classesElectiveList(session, response -> {
                List<String> list = new ArrayList<>();
                String element = response.getDocument().getElementsByTag("script").html();
                int start;
                if (element.contains("专业选修类")) {
                    start = element.indexOf("专业选修类");
                } else {
                    start = element.indexOf("开放选修类");
                }
                element = element.substring(start);
                int end = element.indexOf("最低修读学分", 20);
                if (end != -1) element = element.substring(0, end);
                int pos = 0;
                do {
                    int head = element.indexOf("'[", pos);
                    int tail = element.indexOf("'", head + 2);
                    if (head == -1 || tail == -1) break;
                    String content = element.substring(head + 1, tail);
                    int left = content.indexOf("]"), right = content.indexOf("[", left);
                    if (right == -1) right = content.indexOf("(");
                    if (right == -1) right = content.length();
                    list.add(content.substring(left + 1, right));
                    pos = tail;
                } while (true);
                return list;
            });
            Elements table = responseMain.getDocument().getElementById("user").getElementsByTag("tr");
            Map<Integer, Map<Integer, List<Clazz>>> map = new HashMap<>();
            int line = 1;
            for (int i = 2; i < table.size(); i++) {
                if (i == 6 || i == 11) continue;
                Elements e = table.get(i).getElementsByAttributeValue("valign", "top");
                for (int j = 0; j < e.size(); j++) {
                    if (!map.containsKey(j + 1)) map.put(j + 1, new HashMap<>());
                    String[] classes = e.get(j).text().split("周上\\) ");
                    if (!map.get(j + 1).containsKey(line)) map.get(j + 1).put(line, new ArrayList<>());
                    for (String info : classes) {
                        if (info.isEmpty()) continue;
                        if (!info.contains("周上)")) info += "周上)";
                        int a = info.indexOf('_') + 3, b = info.length() - 1;
                        String name = info.substring(0, a);
                        if (elective != null) {
                            boolean flag = false;
                            for (String match : elective) {
                                if (name.contains(match)) {
                                    flag = true;
                                    break;
                                }
                            }
                            if (flag) continue;
                        }

                        String[] data = info.substring(a + 1, b).split(",");
                        if (data[3].contains("-")) {
                            String week = data[3].replace("周上", "");
                            map.get(j + 1).get(line).add(new Clazz(data[0], data[1], data[2], name, week));
                        } else {
                            String[] week = Arrays.copyOfRange(data, 3, data.length);
                            map.get(j + 1).get(line).add(new Clazz(data[0], data[1], data[2], name, week));
                        }
                    }
                }
                line++;
            }
            return map;
        });
    }

    /**
     * 仅获取必修的课程表
     * @param term  学期（本科为 1-8，专科为 1-6）
     * @return 课程表
     */
    @Override
    public ClassTable getMainClassTable(int term) {
        if(session == null) return null;
        if(term < 1 || term > 8) return null;
        return new ClassTable() {
            final Map<Integer, Map<Integer, List<Clazz>>> mapTable = getTableMap(term);

            @Override
            public Map<Integer, List<Clazz>> getClassInOneDay(int day) {
                if(day < 1 || day > 7) return null;
                return mapTable.get(day);
            }

            @Override
            public void forEach(BiConsumer<Integer, Map<Integer, List<Clazz>>> consumer) {
                mapTable.forEach(consumer);
            }
        };
    }

    /**
     * 获取课程表，包括必修课和选修课（选修课只有已选的才会显示）
     * @param term 学期（本科为 1-8，专科为 1-6）
     * @return 课程表
     */
    @Override
    public ClassTable getClassTable(int term) {
        if(session == null) return null;
        if(term < 1 || term > 8) return null;
        Map<Integer, Map<Integer, List<Clazz>>> map = getTableMap(term);
        NetManager.classesElective(session, response -> {
            Elements main = response.getDocument().getElementsByAttributeValue("id", "user");
            Elements clazzDetail = main.get(1).getElementsByTag("tr");
            Clazz last = null;
            for (int i = 1; i < clazzDetail.size(); i++) {
                Elements tds = clazzDetail.get(i).getElementsByTag("td");
                String name = tds.get(2).text()+"_"+tds.get(3).text();
                int day, l, count;
                Clazz clazz;
                if(tds.size() < 8){
                    day = Integer.parseInt(tds.get(1).text());
                    l = (convert(tds.get(2).text()) - 1) * 2;
                    count = Integer.parseInt(tds.get(3).text());
                    clazz = new Clazz(tds.get(4).text(), tds.get(6).text(), last.getTeacher(),
                            last.getName(), tds.get(0).text().replace("周上", ""));
                }else {
                    day = Integer.parseInt(tds.get(12).text());
                    l = (convert(tds.get(13).text()) - 1) * 2;
                    count = Integer.parseInt(tds.get(14).text());
                    clazz = new Clazz(tds.get(15).text(), tds.get(16).text(),
                            tds.get(7).text().replace("*", ""), name,
                            tds.get(11).text().replace("周上", ""));
                }
                for (int j = 1; j <= count; j++) {
                    List<Clazz> list = map.get(day).get(l+j);
                    if(!list.contains(clazz)) list.add(clazz);
                }
                last = clazz;
            }
            return null;
        });
        return new ClassTable() {
            final Map<Integer, Map<Integer, List<Clazz>>> mapTable = map;

            @Override
            public Map<Integer, List<Clazz>> getClassInOneDay(int day) {
                if(day < 1 || day > 7) return null;
                return mapTable.get(day);
            }

            @Override
            public void forEach(BiConsumer<Integer, Map<Integer, List<Clazz>>> consumer) {
                mapTable.forEach(consumer);
            }
        };
    }

    private int convert(String index){
        switch (index){
            default:
            case "一": return 1;
            case "二": return 2;
            case "三": return 3;
            case "四": return 4;
            case "五": return 5;
        }
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
     * 获取本次登陆后的会话session
     * @return session
     */
    public String getSession() {
        return session;
    }

    /**
     * 获取用的详细信息(学籍信息)头像照片
     * @return 图片输入流
     */
    public BufferedInputStream getUserDetailHeadImg(){
        return NetManager.userDetailHeadImg(session);
    }

    /**
     * 获取分流方向（本科、专科）
     * @return 分流方向
     */
    public String getLevel() {
        return level;
    }

    /**
     * 入学年级
     * @return 年级
     */
    public int getGrade() {
        return grade;
    }

    /**
     * 获取班级
     * @return 班级
     */
    public String getClazz() {
        return clazz;
    }
}
