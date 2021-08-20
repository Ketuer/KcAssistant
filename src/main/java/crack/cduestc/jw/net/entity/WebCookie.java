package crack.cduestc.jw.net.entity;

import java.util.Map;
import java.util.function.BiConsumer;

public class WebCookie {
    private final Map<String, String> cookies;

    public WebCookie(Map<String, String> cookies) {
        this.cookies = cookies;
    }

    public void forEachAddCookie(BiConsumer<String, String> consumer){
        cookies.forEach(consumer);
    }

    @Override
    public String toString() {
        return cookies.toString();
    }
}
