package crack.cduestc.jw.auth.func;

/**
 * 账户操作功能
 *
 * @author Ketuer
 * @since 1.0
 */
public interface AuthFunction {
    /**
     * 登陆
     */
    void login();

    /**
     * 登出
     * @return 是否登出成功
     */
    void logout();

    /**
     * 重置密码
     * @return 是否重置成功
     */
    void resetPassword(String newPassword);
}
