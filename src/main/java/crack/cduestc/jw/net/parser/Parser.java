package crack.cduestc.jw.net.parser;

import com.gargoylesoftware.htmlunit.html.HtmlPage;
import crack.cduestc.jw.net.entity.response.Response;
import org.jsoup.nodes.Document;

public interface Parser<T extends Response> {
    default T parse(Document document){
        throw new UnsupportedOperationException("No implements!");
    }

    default T parse(HtmlPage page){
        throw new UnsupportedOperationException("No implements!");
    }
}
