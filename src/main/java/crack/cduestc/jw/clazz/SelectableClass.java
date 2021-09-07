package crack.cduestc.jw.clazz;

import com.alibaba.fastjson.JSONArray;
import crack.cduestc.jw.net.entity.response.ScoreResponse;
import crack.cduestc.jw.net.entity.response.SelectClassResponse;

import java.lang.reflect.Field;

public class SelectableClass {
    /* 课程id */
    String id;
    /* 课程编号 */
    String no;
    /* 课程名称 */
    String name;
    /* 学分 */
    String credits;
    /* 开始周 */
    int startWeek;
    /* 结束周 */
    int endWeek;
    /* 周课时 */
    int weekHour;
    /* 任课教师 */
    String teachers;
    /* 校区 */
    String campusName;
    /* 教学班级 */
    String teachClassName;
    /* 上课信息 */
    JSONArray arrangeInfo;

    public SelectableClass(SelectClassResponse.SingleSelectClass response){
        for (Field field : response.getClass().getDeclaredFields()) {
            if(!field.isAccessible()) field.setAccessible(true);
            try {
                Field thisField = this.getClass().getDeclaredField(field.getName());
                if(!thisField.isAccessible()) thisField.setAccessible(true);
                thisField.set(this, field.get(response));
            } catch (NoSuchFieldException | IllegalAccessException ignored) {}
        }
    }

    public String getId() {
        return id;
    }

    public String getNo() {
        return no;
    }

    public String getName() {
        return name;
    }

    public String getCredits() {
        return credits;
    }

    public int getStartWeek() {
        return startWeek;
    }

    public int getEndWeek() {
        return endWeek;
    }

    public int getWeekHour() {
        return weekHour;
    }

    public String getTeachers() {
        return teachers;
    }

    public String getCampusName() {
        return campusName;
    }

    public String getTeachClassName() {
        return teachClassName;
    }

    public JSONArray getArrangeInfo() {
        return arrangeInfo;
    }

    @Override
    public String toString() {
        return "SelectableClass{" +
                "id='" + id + '\'' +
                ", no='" + no + '\'' +
                ", name='" + name + '\'' +
                ", credits='" + credits + '\'' +
                ", startWeek=" + startWeek +
                ", endWeek=" + endWeek +
                ", weekHour=" + weekHour +
                ", teachers='" + teachers + '\'' +
                ", campusName='" + campusName + '\'' +
                ", teachClassName='" + teachClassName + '\'' +
                ", arrangeInfo=" + arrangeInfo +
                '}';
    }
}
