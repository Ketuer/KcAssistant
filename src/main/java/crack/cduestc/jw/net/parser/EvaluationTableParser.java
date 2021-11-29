package crack.cduestc.jw.net.parser;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import crack.cduestc.jw.eval.EvaluationTable;
import crack.cduestc.jw.net.entity.response.EvaluationResponse;
import crack.cduestc.jw.net.entity.response.EvaluationTableResponse;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class EvaluationTableParser implements Parser<EvaluationTableResponse>{
    @Override
    public EvaluationTableResponse parse(Document document) {
        String html = document.html();
        String rawJsonData = html.substring(html.indexOf("new Questions(eval('") + 20, html.indexOf("}]'));") + 2);
        rawJsonData = rawJsonData.replace("\\\"", "\"");
        JSONArray array = JSONObject.parseArray(rawJsonData);
        Elements elements = document.getElementsByTag("input");
        EvaluationTable table = new EvaluationTable(
                Integer.parseInt(elements.get(1).attr("value")),
                Integer.parseInt(elements.get(2).attr("value")),
                Integer.parseInt(elements.get(3).attr("value")));
        for (Object o : array) {
            JSONObject object = JSONObject.parseObject(o.toString());
            if(!object.containsKey("remark")) continue;
            if(object.getBoolean("objective")){
                EvaluationTable.OptionLine line = new EvaluationTable.OptionLine(
                        object.getString("name"), 1,
                        object.getInteger("remark"),
                        object.getDouble("proportion"));
                object.getJSONArray("options").forEach(option -> {
                    JSONObject opObj = JSONObject.parseObject(option.toString());
                    line.addOption(new EvaluationTable.OptionLine.Option(opObj.getString("name"),
                            opObj.getDouble("proportion")));
                });
                table.addLine(line);
            }else {
                table.addLine(new EvaluationTable.TextLine(
                        object.getString("name"), 1,
                        object.getInteger("remark")));
            }
        }
        return new EvaluationTableResponse(table);
    }
}
