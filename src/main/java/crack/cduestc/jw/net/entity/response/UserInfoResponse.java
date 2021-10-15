package crack.cduestc.jw.net.entity.response;

import crack.cduestc.jw.net.anno.Info;

import java.util.Date;

public class UserInfoResponse extends JSONResponse{
    @Info("姓名")
    private String name;
    @Info("英文名")
    private String englishName;
    @Info("学号")
    private String id;
    @Info("性别")
    private String sex;
    @Info("学制")
    private int time;
    @Info("项目")
    private String project;
    @Info("学历层次")
    private String level;
    @Info("年级")
    private int grade;
    @Info("所属校区")
    private String local;
    @Info("学籍状态")
    private String status;
    @Info("所属班级")
    private String clazz;
    @Info("学习形式")
    private String study;
    @Info("院系")
    private String department;
    @Info("学生类别")
    private String type;
    @Info("专业")
    private String subject;
    @Info("方向")
    private String direct;
    @Info("行政管理院系")
    private String management;
    @Info("政治面貌")
    private String political;
    @Info("民族")
    private String nationality;
    @Info("出生年月")
    private String birth;
    @Info("证件类型")
    private String certificateType;
    @Info("证件号码")
    private String certificateId;
    @Info("入校时间")
    private Date startDate;
    @Info("毕业时间")
    private Date endDate;
    @Info("学籍生效日期")
    private Date useDate;
}
