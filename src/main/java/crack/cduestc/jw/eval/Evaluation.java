package crack.cduestc.jw.eval;

import crack.cduestc.jw.net.entity.response.EvaluationResponse;

import java.lang.reflect.Field;

public class Evaluation {
    /* 任务号 */
    String id;
    /* 课程名称 */
    String name;
    /* 课程类别 */
    String type;
    /* 教师名称 */
    String teacher;
    /* 问卷名称 */
    String tableName;
    /* 填写地址 */
    String url;
    /* 评教完成 */
    boolean status;

    public Evaluation(EvaluationResponse.Eval response){
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

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getTeacher() {
        return teacher;
    }

    public String getTableName() {
        return tableName;
    }

    public String getUrl() {
        return url;
    }

    /**
     * 是否评教完成
     * @return 完成状态
     */
    public boolean isStatus() {
        return status;
    }

    @Override
    public String toString() {
        return "Evaluation{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", teacher='" + teacher + '\'' +
                ", tableName='" + tableName + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
