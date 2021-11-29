package crack.cduestc.jw.net.entity.response;

import crack.cduestc.jw.eval.EvaluationTable;

public class EvaluationTableResponse extends Response{

    EvaluationTable table;

    public EvaluationTableResponse(EvaluationTable table) {
        this.table = table;
    }

    public EvaluationTable getTable() {
        return table;
    }
}
