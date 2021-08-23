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
     */
    void logout();

    /**
     * 重置密码
     * @param newPassword 新密码
     */
    void resetPassword(String newPassword);
}
