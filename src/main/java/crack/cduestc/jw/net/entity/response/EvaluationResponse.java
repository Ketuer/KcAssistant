package crack.cduestc.jw.net.entity.response;

import crack.cduestc.jw.net.anno.Info;

import java.util.List;
import java.util.function.Consumer;

public class EvaluationResponse extends Response{

    private final List<Eval> list;

    public EvaluationResponse(List<Eval> list) {
        this.list = list;
    }

    public void forEach(Consumer<Eval> consumer){
        list.forEach(consumer);
    }

    public static class Eval extends JSONResponse{
        @Info("任务号")
        String id;
        @Info("课程名称")
        String name;
        @Info("类型")
        String type;
        @Info("任课教师")
        String teacher;
        @Info("问卷名称")
        String tableName;
        @Info("填写地址")
        String url;
        @Info("评教完成")
        boolean status;
    }
}
