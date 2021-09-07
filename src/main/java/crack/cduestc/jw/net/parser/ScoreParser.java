package crack.cduestc.jw.net.parser;

import crack.cduestc.jw.net.entity.response.ScoreResponse;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class ScoreParser implements Parser<ScoreResponse>{
    @Override
    public ScoreResponse parse(Document document) {
        List<ScoreResponse.SingleScoreResponse> list = new ArrayList<>();
        Elements elements = document.getElementsByTag("tbody").get(1).getElementsByTag("tr");
        Elements attrs = document.getElementsByTag("thead").get(1).getElementsByTag("th");
        List<String> arr = new ArrayList<>();
        attrs.forEach(e -> arr.add(e.text()));
        elements.forEach(e -> {
            ScoreResponse.SingleScoreResponse single = new ScoreResponse.SingleScoreResponse();
            Elements tds = e.getElementsByTag("td");
            single.setField("学年", tds.first().text().split(" ")[0]);
            single.setField("学期", tds.first().text().split(" ")[1]);
            single.setField("课程代码", tds.get(1).text());
            single.setField("课程序号", tds.get(2).text());
            single.setField("课程名称", tds.get(3).text().replaceAll(" \\(.*\\)", ""));
            single.setField("课程类别", tds.get(4).text());
            single.setField("学分", tds.get(5).text());
            for (int i = 6; i < arr.size(); i++) {
                single.setField(arr.get(i),  tds.get(i).text());
            }
            list.add(single);
        });
        return new ScoreResponse(list);
    }
}
