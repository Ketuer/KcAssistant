package crack.cduestc.jw.net.entity.response;

import crack.cduestc.jw.net.anno.Info;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class ClassesResponse extends Response{

    private final List<Clazz> clazzList = new ArrayList<>();

    public ClassesResponse(List<Clazz> clazzList){
        this.clazzList.addAll(clazzList);
    }

    public void forEach(Consumer<Clazz> consumer){
        clazzList.forEach(consumer);
    }

    public static class Clazz extends JSONResponse{
        @Info("名称")
        public final String name;
        @Info("课程号")
        public final String id;
        @Info("任课教师")
        public final String teacher;
        @Info("上课地点")
        public final String local;
        @Info("星期数")
        public final int day;
        @Info("上课周数")
        public final Set<Integer> weekSet;
        @Info("上课时间")
        public final Set<Integer> indexSet;

        public Clazz(String name, String id, String teacher, String local, int day, Set<Integer> weekSet, Set<Integer> indexSet) {
            this.name = name;
            this.id = id;
            this.teacher = teacher;
            this.local = local;
            this.day = day;
            this.weekSet = weekSet;
            this.indexSet = indexSet;
        }
    }
}
