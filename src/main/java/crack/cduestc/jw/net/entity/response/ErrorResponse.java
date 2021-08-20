package crack.cduestc.jw.net.entity.response;

public class ErrorResponse extends Response {
    private final String reason;

    public ErrorResponse(String reason, int code) {
        super(code);
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }
}
