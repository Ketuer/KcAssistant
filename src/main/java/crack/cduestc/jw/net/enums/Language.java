package crack.cduestc.jw.net.enums;

/**
 * 语言类型
 */
public enum Language {
    CN("zh_CN"), US("en_US");

    private final String name;

    Language(String name){
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
