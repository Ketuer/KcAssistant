package crack.cduestc.jw.score;

import java.util.*;
import java.util.function.BiConsumer;

public interface ScoreList {

    /**
     * 获取某一个学期的成绩
     * @param term 某个学期的成绩
     * @return 及格成绩
     */
    List<Score> getScore(String term);

    /**
     * 获取所有的学期
     * @return 学期列表
     */
    Set<String> getTerms();

    /**
     * 遍历并操作
     * @param consumer 操作接口
     */
    void forEach(BiConsumer<String, List<Score>> consumer);
}
