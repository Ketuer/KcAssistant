package crack.cduestc.jw.net.parser;

import crack.cduestc.jw.net.entity.response.Response;
import org.jsoup.nodes.Document;

public interface Parser<T extends Response> {
    T parse(Document document);
}
