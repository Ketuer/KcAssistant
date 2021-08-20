package crack.cduestc.jw.net.entity.response;

public abstract class Response {
    private final int code;

    protected Response(int code) {
        this.code = code;
    }

    protected Response() {
        this.code = 200;
    }

    public int getCode() {
        return code;
    }
}
