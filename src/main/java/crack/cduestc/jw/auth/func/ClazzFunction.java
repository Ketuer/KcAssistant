package crack.cduestc.jw.auth.func;

import crack.cduestc.jw.clazz.ClassTable;

public interface ClazzFunction {
    /**
     * 获取课程信息
     * @param term 学期（本科为 1-8，专科为 1-6）
     * @return 课程表
     */
    ClassTable getClassTable(int term);

    /**
     * 仅获取必修课程信息
     * @param term  学期（本科为 1-8，专科为 1-6）
     * @return 课程表
     */
    ClassTable getMainClassTable(int term);
}
