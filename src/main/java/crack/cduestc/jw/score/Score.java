package crack.cduestc.jw.score;

import crack.cduestc.jw.net.entity.response.ScoreResponse;

import java.lang.reflect.Field;

/**
 * 成绩信息实体类
 *
 * @author Ketuer
 * @since 1.0
 */
public class Score {

    public Score(ScoreResponse.SingleScoreResponse response){
        for (Field field : response.getClass().getDeclaredFields()) {
            if(!field.isAccessible()) field.setAccessible(true);
            try {
                Field thisField = this.getClass().getDeclaredField(field.getName());
                if(!thisField.isAccessible()) thisField.setAccessible(true);
                thisField.set(this, field.get(response));
            } catch (NoSuchFieldException | IllegalAccessException ignored) {}
        }
    }

    /* 学年 */
    String year;
    /* 学期 */
    int term;
    /* 课程代码 */
    String code;
    /* 课程序号 */
    String index;
    /* 课程名称 */
    String name;
    /* 课程类别 */
    String type;
    /* 学分 */
    double credits;
    /* 总评成绩 */
    double score_all;
    /* 课堂平时成绩 */
    double score_normal;
    /* 课堂期末成绩 */
    double score_terminal;
    /* 课堂期中成绩 */
    double score_middle;
    /* 实践平时成绩 */
    double score_task_normal;
    /* 实践期末成绩 */
    double score_task_terminal;
    /* 实验平时成绩 */
    double score_exp_normal;
    /* 实验期末成绩 */
    double score_exp_terminal;
    /* 最终成绩 */
    double score_final;
    /* 绩点 */
    double points;

    @Override
    public String toString() {
        return "Score{" +
                "year='" + year + '\'' +
                ", term=" + term +
                ", code='" + code + '\'' +
                ", index='" + index + '\'' +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", credits=" + credits +
                ", score_all=" + score_all +
                ", score_normal=" + score_normal +
                ", score_terminal=" + score_terminal +
                ", score_middle=" + score_middle +
                ", score_task_normal=" + score_task_normal +
                ", score_task_terminal=" + score_task_terminal +
                ", score_exp_normal=" + score_exp_normal +
                ", score_exp_terminal=" + score_exp_terminal +
                ", score_final=" + score_final +
                ", points=" + points +
                '}';
    }
}
