package crack.cduestc.jw.net.entity.request;

import crack.cduestc.jw.net.anno.RequestParam;

public class ClassesRequest extends Request{
    @RequestParam("ignoreHead")
    private int ignoreHead;
    @RequestParam("setting.kind")
    private String setting_kind;
    @RequestParam("project.id")
    private int project_id;
    @RequestParam("semester.id")
    private int semester_id;
    @RequestParam("ids")
    private String ids;
    @RequestParam("startWeek")
    private String startWeek;

    public ClassesRequest(int ignoreHead, String setting_kind, int project_id, int semester_id, String ids, String startWeek) {
        this.ignoreHead = ignoreHead;
        this.setting_kind = setting_kind;
        this.project_id = project_id;
        this.semester_id = semester_id;
        this.ids = ids;
        this.startWeek = startWeek;
    }
}
