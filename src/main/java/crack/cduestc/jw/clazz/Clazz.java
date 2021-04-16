package crack.cduestc.jw.clazz;

import java.util.Arrays;

public class Clazz {
    /* 上课周 */
    private final boolean[] week;
    /* 地点 */
    private final String place;
    /* 详细地点 */
    private final String subPlace;
    /* 任课教师 */
    private final String teacher;
    /* 课程名称 */
    private final String name;

    public Clazz(String place, String subPlace, String teacher, String name, String week) {
        this.week = rangeToArray(week);
        this.place = place;
        this.subPlace = subPlace;
        this.teacher = teacher;
        this.name = name;
    }

    public Clazz(String place, String subPlace, String teacher, String name, String[] week) {
        this.week = pointsToArray(week);
        this.place = place;
        this.subPlace = subPlace;
        this.teacher = teacher;
        this.name = name;
    }

    private boolean[] pointsToArray(String[] points){
        boolean[] arr = new boolean[25];
        for (String point : points){
            arr[Integer.parseInt(point.replace("周上", ""))] = true;
        }
        return arr;
    }

    private boolean[] rangeToArray(String range){
        String[] dates = range.split("-");
        int a = Integer.parseInt(dates[0]), b = Integer.parseInt(dates[1]);
        boolean[] arr = new boolean[25];
        for (int i = a; i < b; i++) {
            arr[i] = true;
        }
        return arr;
    }

    public boolean[] getWeek() {
        return week;
    }

    /**
     * 将周信息转换为字符串
     * @return 信息
     */
    public String getWeekString(){
        int first = -1, end = -1;
        boolean x = false, y = false, mush = false, leak = false;
        for (int i = 0; i < week.length; i++) {
            if(week[i]) {
                if(leak) mush = false;
                if(first == -1){
                    first = i;
                    mush = true;
                }
                end = i;
                if(i % 2 == 0) {
                    y = true;
                }else{
                    x = true;
                }
            }else {
                if(first != -1) leak = true;
            }
        }

        if(x && !y) return first+"-"+end+"单周上";
        if(!x && y) return first+"-"+end+"双周上";
        if(x && mush) return first+"-"+end+"周上";

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < week.length; i++) {
            if(week[i]) builder.append(i).append(",");
        }
        builder.deleteCharAt(builder.length() - 1);
        builder.append("周上");
        return builder.toString();
    }

    public String getPlace() {
        return place;
    }

    public String getSubPlace() {
        return subPlace;
    }

    public String getTeacher() {
        return teacher;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Clazz{" +
                " week='" + this.getWeekString() + '\'' +
                ", place='" + place + '\'' +
                ", subPlace='" + subPlace + '\'' +
                ", teacher='" + teacher + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
