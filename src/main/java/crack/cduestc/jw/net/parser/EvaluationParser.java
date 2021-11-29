package crack.cduestc.jw.net.parser;

import com.gargoylesoftware.htmlunit.html.HtmlPage;
import crack.cduestc.jw.eval.Evaluation;
import crack.cduestc.jw.net.entity.response.EvaluationResponse;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.Objects;
import java.util.stream.Collectors;

public class EvaluationParser implements Parser<EvaluationResponse>{

    @Override
    public EvaluationResponse parse(HtmlPage page) {
        System.out.println(page.getWebResponse().getContentAsString());

        return null;
    }

    @Override
    public EvaluationResponse parse(Document document) {
        Elements elements = document.getElementsByTag("table")
                .get(2)
                .getElementsByTag("tbody")
                .first()
                .getElementsByTag("tr");

        return new EvaluationResponse(elements
                .stream()
                .map(element -> {
                    Elements tds = element.getElementsByTag("td");
                    EvaluationResponse.Eval eval = new EvaluationResponse.Eval();
                    eval.setField("任务号", tds.get(0).text());
                    eval.setField("课程名称", tds.get(1).text());
                    eval.setField("类型", tds.get(2).text());
                    eval.setField("任课教师", tds.get(3).text());
                    eval.setField("问卷名称", tds.get(4).text());
                    if(!element.text().contains("评教完成")){
                        eval.setField("填写地址", tds.get(5).getElementsByTag("a").first().attr("href"));
                        eval.setField("评教完成", "false");
                    }else {
                        eval.setField("评教完成", "true");
                    }

                    return eval;
                })
                .collect(Collectors.toList()));
    }
}
