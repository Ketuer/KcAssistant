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
            single.setField("总评成绩", tds.get(6).text());
            single.setField("课堂平时成绩", tds.get(7).text());
            single.setField("课堂期末成绩", tds.get(8).text());
            single.setField("课堂期中成绩", tds.get(9).text());
            single.setField("实践平时成绩", tds.get(10).text());
            single.setField("实践期末成绩", tds.get(11).text());
            single.setField("实验平时成绩", tds.get(12).text());
            single.setField("实验期末成绩", tds.get(13).text());
            single.setField("最终", tds.get(14).text());
            single.setField("绩点", tds.get(15).text());
            list.add(single);
        });
        return new ScoreResponse(list);
    }
}
