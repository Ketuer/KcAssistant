package crack.cduestc.jw.score;

/**
 * 成绩信息实体类
 *
 * @author Ketuer
 * @since 1.0
 */
public class Score {
    String id;
    /* 名称 */
    String name;
    /* 学分 */
    double value;
    /* 成绩 */
    String score;
    /* 属性 */
    String type;
    /* 原因 */
    String reason;

    public Score(String id, String name, String value, String score, String type, String reason) {
        this.id = id;
        this.name = name;
        this.value = Double.parseDouble(value);
        this.score = score;
        this.type = type;
        this.reason = reason;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getValue() {
        return value;
    }

    public String getScore() {
        return score;
    }

    public String getType() {
        return type;
    }


    @Override
    public String toString() {
        return "Score{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", value=" + value +
                ", score=" + score +
                ", type='" + type + '\'' +
                ", reason='" + reason + '\'' +
                '}';
    }
}
