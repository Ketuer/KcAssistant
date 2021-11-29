package crack.cduestc.jw.eval;

import java.util.ArrayList;
import java.util.List;

public class EvaluationTable {

    int teacherId;
    int semesterId;
    int classId;
    List<Line> lines = new ArrayList<>();

    public int getTeacherId() {
        return teacherId;
    }

    public int getSemesterId() {
        return semesterId;
    }

    public int getClassId() {
        return classId;
    }

    public List<Line> getLines() {
        return lines;
    }

    @Override
    public String toString() {
        return "EvaluationTable{" +
                "teacherId=" + teacherId +
                ", semesterId=" + semesterId +
                ", classId=" + classId +
                ", lines=" + lines +
                '}';
    }

    public EvaluationTable(int teacherId, int semesterId, int classId) {
        this.teacherId = teacherId;
        this.semesterId = semesterId;
        this.classId = classId;
    }

    public void addLine(Line line){
        lines.add(line);
    }

    public static class OptionLine extends Line{
        double proportion;
        int optionIndex = 1;
        List<Option> options = new ArrayList<>();

        public OptionLine(String name, int group, int index, double proportion) {
            this.questionName = name;
            this.group = group;
            this.index = index;
            this.proportion = proportion * 5;
        }

        public void addOption(Option option){
            this.options.add(option);
        }

        public void setOption(int option) {
            if(option < 0 || option > 5)
                throw new IllegalArgumentException("选项只能是1-5之间的数字，代表5-1星（反着的，默认五星）");
            this.optionIndex = option;
            this.content = options.get(optionIndex - 1).name;
        }

        public double getScore(){
            return options.get(optionIndex - 1).proportion * proportion;
        }

        public Option getCurrentOption(){
            return options.get(optionIndex - 1);
        }

        public static class Option{
            String name;
            double proportion;

            public Option(String name, double proportion) {
                this.name = name;
                this.proportion = proportion;
            }

            public String getName() {
                return name;
            }

            public double getProportion() {
                return proportion;
            }
        }
    }

    public static class TextLine extends Line{
        public TextLine(String name, int group, int index) {
            this.questionName = name;
            this.group = group;
            this.index = index;
            content = "此表单由KcAssistant自动生成，GitHub地址：https://github.com/Ketuer/KcAssistant";
        }

        public void setContent(String content){
            this.content = content;
        }
    }

    public abstract static class Line {
        int group;
        int index;
        String questionName;
        String content;

        public int getGroup() {
            return group;
        }

        public int getIndex() {
            return index;
        }

        public String getQuestionName() {
            return questionName;
        }

        public String getContent() {
            return content;
        }

        @Override
        public String toString() {
            return "Line{" +
                    "group=" + group +
                    ", index=" + index +
                    ", questionName='" + questionName + '\'' +
                    ", content='" + content + '\'' +
                    '}';
        }
    }
}
