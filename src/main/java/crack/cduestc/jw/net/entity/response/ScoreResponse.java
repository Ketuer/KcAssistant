package crack.cduestc.jw.net.entity.response;

import com.alibaba.fastjson.JSONArray;
import crack.cduestc.jw.net.anno.Info;

import java.util.List;
import java.util.function.Consumer;

public class ScoreResponse extends Response{

    private final List<SingleScoreResponse> scores;

    public ScoreResponse(List<SingleScoreResponse> scores){
        this.scores = scores;
    }

    public JSONArray asJSONArray(){
        JSONArray array = new JSONArray();
        scores.forEach(s -> array.add(s.asJSON()));
        return array;
    }

    public void forEach(Consumer<SingleScoreResponse> consumer){
        scores.forEach(consumer);
    }

    public static class SingleScoreResponse extends JSONResponse{
        @Info("学年")
        String year;
        @Info("学期")
        int term;
        @Info("课程代码")
        String code;
        @Info("课程序号")
        String index;
        @Info("课程名称")
        String name;
        @Info("课程类别")
        String type;
        @Info("学分")
        double credits;
        @Info("补考成绩")
        double redo_score_all;
        @Info("总评成绩")
        double score_all;
        @Info("课堂平时成绩")
        double score_normal;
        @Info("课堂期末成绩")
        double score_terminal;
        @Info("课堂期中成绩")
        double score_middle;
        @Info("实践平时成绩")
        double score_task_normal;
        @Info("实践期末成绩")
        double score_task_terminal;
        @Info("实验平时成绩")
        double score_exp_normal;
        @Info("实验期末成绩")
        double score_exp_terminal;
        @Info("最终成绩")
        double score_final;
        @Info("绩点")
        double points;
    }
}
