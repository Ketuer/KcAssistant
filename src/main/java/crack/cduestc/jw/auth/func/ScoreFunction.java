package crack.cduestc.jw.auth.func;

import crack.cduestc.jw.eval.Evaluation;
import crack.cduestc.jw.eval.EvaluationTable;
import crack.cduestc.jw.score.Score;
import crack.cduestc.jw.score.ScoreList;

import java.util.List;

public interface ScoreFunction {
    ScoreList getScore();
    List<Evaluation> getEvalList();
    EvaluationTable getEvalTable(Evaluation e);
}
