package crack.cduestc.jw.score;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class ScoreList implements Iterable<List<Score>>{

    private final Map<String, List<Score>> scoreMap;
    private final Set<String> terms;

    public ScoreList(Map<String, List<Score>> scoreMap, Set<String> terms) {
        this.scoreMap = scoreMap;
        this.terms = terms;
    }

    /**
     * 获取某一个学年学期的成绩
     * @param year 学年
     * @param term 学期
     * @return 对应学年学期的成绩
     */
    public List<Score> getScore(String year, int term){
        return scoreMap.getOrDefault(year+term, Collections.emptyList());
    }

    /**
     * 获取所有的学年（每个学年两个学期）
     * @return 学年列表
     */
    public Set<String> getTerms(){
        return terms;
    }

    @Override
    public Iterator<List<Score>> iterator() {
        return new ScoreIterator(terms.iterator());
    }

    @Override
    public String toString() {
        return scoreMap.toString();
    }

    class ScoreIterator implements Iterator<List<Score>>{

        Iterator<String> terms;

        ScoreIterator(Iterator<String> terms){
            this.terms = terms;
        }

        @Override
        public boolean hasNext() {
            return terms.hasNext();
        }

        @Override
        public List<Score> next() {
            String term = terms.next();
            List<Score> list = new ArrayList<>(scoreMap.get(term + 1));
            if(scoreMap.containsKey(term+2)) list.addAll(scoreMap.get(term+2));
            return list;
        }
    }
}
