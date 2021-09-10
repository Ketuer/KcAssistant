package crack.cduestc.jw.auth.func;

import crack.cduestc.jw.clazz.ClassTable;
import crack.cduestc.jw.clazz.SelectableClass;

import java.util.List;

public interface ClazzFunction {
    /**
     * 获取课程信息
     * @param term 学期（本科为 1-8，专科为 1-6）
     * @return 课程表
     */
    ClassTable getClassTable(int term);

    void doSelectClass(String classId);

    void undoSelectClass(String classId);

    List<SelectableClass> getSelectClassList();
}
