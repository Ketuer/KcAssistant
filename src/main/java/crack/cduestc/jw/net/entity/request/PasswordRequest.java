package crack.cduestc.jw.net.entity.request;

import crack.cduestc.jw.net.anno.Param;

public class PasswordRequest extends Request{
    @Param("oldPassword")
    private final String oldPassword;

    @Param("password")
    private final String password;

    @Param("repeatedPassword")
    private final String repeatedPassword;

    @Param("mail")
    private final String mail;

    @Param("user.id")
    private final String userId;

    public PasswordRequest(String oldPassword, String password, String repeatedPassword, String mail, String userId) {
        this.oldPassword = oldPassword;
        this.password = password;
        this.repeatedPassword = repeatedPassword;
        this.mail = mail;
        this.userId = userId;
    }
}
