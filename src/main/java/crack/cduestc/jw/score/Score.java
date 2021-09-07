package crack.cduestc.jw.score;

import com.alibaba.fastjson.JSONObject;
import crack.cduestc.jw.net.entity.response.ScoreResponse;

import java.lang.reflect.Field;

/**
 * 成绩信息实体类
 *
 * @author Ketuer
 * @since 1.0
 */
public class Score {

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
    /* 补考成绩 */
    double redo_score_all;
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

    public JSONObject toJSON(){
        JSONObject object = new JSONObject();
        for (Field field : this.getClass().getDeclaredFields()) {
            if(!field.isAccessible()) field.setAccessible(true);
            try {
                object.put(field.getName(), field.get(this));
            } catch (IllegalAccessException ignored) {}
        }
        return object;
    }

    public String getYear() {
        return year;
    }

    public int getTerm() {
        return term;
    }

    public String getCode() {
        return code;
    }

    public String getIndex() {
        return index;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public double getCredits() {
        return credits;
    }

    public double getRedo_score_all() {
        return redo_score_all;
    }

    /**
     * 获取最终成绩（补考后的计算）
     * @return 最终成绩
     */
    public double getFinalScore(){
        return Math.max(score_all, redo_score_all);
    }


    public double getScore_all() {
        return score_all;
    }

    public double getScore_normal() {
        return score_normal;
    }

    public double getScore_terminal() {
        return score_terminal;
    }

    public double getScore_middle() {
        return score_middle;
    }

    public double getScore_task_normal() {
        return score_task_normal;
    }

    public double getScore_task_terminal() {
        return score_task_terminal;
    }

    public double getScore_exp_normal() {
        return score_exp_normal;
    }

    public double getScore_exp_terminal() {
        return score_exp_terminal;
    }

    public double getScore_final() {
        return score_final;
    }

    public double getPoints() {
        return points;
    }

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
