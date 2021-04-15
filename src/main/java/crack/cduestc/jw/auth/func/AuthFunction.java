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
     * @return 是否登陆成功
     */
    boolean login();

    /**
     * 登出
     * @return 是否登出成功
     */
    boolean logout();
}
