package crack.cduestc.jw.clazz;

import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

public interface ClassTable {
    /**
     * 获取某一天的课程表，存储形式为 第i节 - 课程
     * @param day 1-7（周一 ~ 周日）
     * @return 课程表
     */
    Map<Integer, List<Clazz>> getClassInOneDay(int day);

    /**
     * 遍历操作
     * @param consumer 消费者
     */
    void forEach(BiConsumer<Integer, Map<Integer, List<Clazz>>> consumer);
}
