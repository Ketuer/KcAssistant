package crack.cduestc.jw.net.entity.request;

import crack.cduestc.jw.net.anno.RequestParam;

public class SelectClassRequest extends Request{
    @RequestParam("optype")
    private boolean optype;
    @RequestParam("operator0")
    private String operator0;
    @RequestParam("lesson0")
    private String lesson0;

    public SelectClassRequest(boolean optype, String lesson0) {
        this.optype = optype;
        this.operator0 = lesson0+":true:0";
        this.lesson0 = lesson0;
    }
}
