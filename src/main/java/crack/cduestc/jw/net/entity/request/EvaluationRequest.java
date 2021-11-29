package crack.cduestc.jw.net.entity.request;

import crack.cduestc.jw.eval.EvaluationTable;
import crack.cduestc.jw.net.anno.RequestParam;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class EvaluationRequest extends Request{

    @RequestParam("teacher.id")
    private int teacher_id;
    @RequestParam("semester.id")
    private int semester_id;
    @RequestParam("evaluationLesson.id")
    private int evaluationLesson_id;
    @RequestParam("result1Num")
    private int result1Num = 0;
    @RequestParam("result2Num")
    private int result2Num = 0;

    List<EvaluationTable.Line> lines = new ArrayList<>();

    public EvaluationRequest(EvaluationTable table){
        this.teacher_id = table.getTeacherId();
        this.semester_id = table.getSemesterId();
        this.evaluationLesson_id = table.getClassId();
        lines.addAll(table.getLines());
        for (EvaluationTable.Line line : lines) {
            if(line instanceof EvaluationTable.OptionLine) result1Num++;
            else result2Num++;
        }
    }

    @Override
    public void forEachAddData(Consumer<String[]> consumer) {
        super.forEachAddData(consumer);
        lines.forEach(line -> {
            if(line instanceof EvaluationTable.OptionLine){
                EvaluationTable.OptionLine optionLine = (EvaluationTable.OptionLine) line;
                String prefix = "result1_"+(optionLine.getIndex() - 1);
                consumer.accept(new String[]{prefix+".questionName", optionLine.getQuestionName()});
                consumer.accept(new String[]{prefix+".content", optionLine.getCurrentOption().getName()});
                consumer.accept(new String[]{prefix+".option", optionLine.getCurrentOption().getProportion()+""});
                consumer.accept(new String[]{prefix+".score", optionLine.getScore()+""});
            }else {
                EvaluationTable.TextLine textLine = (EvaluationTable.TextLine) line;
                String prefix = "result2_"+(textLine.getIndex() - result1Num);
                consumer.accept(new String[]{prefix+".questionName", textLine.getQuestionName()});
                consumer.accept(new String[]{prefix+".content", textLine.getContent()});
            }
        });
    }
}
