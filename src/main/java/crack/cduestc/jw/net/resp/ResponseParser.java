package crack.cduestc.jw.net.resp;

/**
 * Html数据解析器，由于服务器返回的是Html数据，
 * 每一个接口都有一个自定义的Html解析器，将数据
 * 解析为需要的类型。
 *
 * @param <T> 解析类型
 * @author Ketuer
 * @since 1.0
 */
@FunctionalInterface
public interface ResponseParser<T> {

    /**
     * 讲返回内容解析为指定类型的数据
     * @param response 返回数据
     * @return 指定类型的数据
     */
    T parse(WebResponse response);
}
