package crack.cduestc.jw.clazz;

import com.alibaba.fastjson.JSONObject;
import crack.cduestc.jw.net.entity.response.ClassesResponse;

import java.lang.reflect.Field;
import java.util.Set;

public class Clazz {
    /* 名称 */
    public String name;
    /* 课程号 */
    public String id;
    /* 任课教师 */
    public String teacher;
    /* 上课地点 */
    public String local;
    /* 星期数 */
    public int day;
    /* 上课周数 */
    public Set<Integer> weekSet;
    /* 上课时间 */
    public Set<Integer> indexSet;

    public Clazz(ClassesResponse.Clazz clazz){
        this.name = clazz.name;
        this.id = clazz.id;
        this.teacher = clazz.teacher;
        this.local = clazz.local;
        this.day = clazz.day;
        this.weekSet = clazz.weekSet;
        this.indexSet = clazz.indexSet;
    }

    public JSONObject toJSON(){
        JSONObject object = new JSONObject();
        for (Field fi : this.getClass().getDeclaredFields()) {
            if (!fi.isAccessible()) fi.setAccessible(true);
            try {
                object.put(fi.getName(), fi.get(this));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return object;
    }

    @Override
    public String toString() {
        return "Clazz{" +
                "name='" + name + '\'' +
                ", id='" + id + '\'' +
                ", teacher='" + teacher + '\'' +
                ", local='" + local + '\'' +
                ", day=" + day +
                ", weekSet=" + weekSet +
                ", indexSet=" + indexSet +
                '}';
    }
}
