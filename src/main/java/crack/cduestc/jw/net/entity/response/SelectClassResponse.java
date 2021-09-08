package crack.cduestc.jw.net.entity.response;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import crack.cduestc.jw.net.anno.Info;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class SelectClassResponse extends Response{

    private final List<SingleSelectClass> selects;
    private final String profileId;

    public SelectClassResponse(JSONArray array, String profileId){
        this.profileId = profileId;
        this.selects = new ArrayList<>();
        array.forEach(raw -> {
            JSONObject object = JSONObject.parseObject(raw.toString());
            SingleSelectClass singleSelectClass = new SingleSelectClass();
            object.forEach((k, v) -> singleSelectClass.setField(k, v.toString()));
            selects.add(singleSelectClass);
        });
    }

    public String getProfileId() {
        return profileId;
    }

    public JSONArray asJSONArray(){
        JSONArray array = new JSONArray();
        selects.forEach(s -> array.add(s.asJSON()));
        return array;
    }

    public void forEach(Consumer<SingleSelectClass> consumer){
        selects.forEach(consumer);
    }

    public static class SingleSelectClass extends JSONResponse{
        @Info("id")
        String id;
        @Info("no")
        String no;
        @Info("name")
        String name;
        @Info("credits")
        String credits;
        @Info("startWeek")
        int startWeek;
        @Info("endWeek")
        int endWeek;
        @Info("weekHour")
        int weekHour;
        @Info("teachers")
        String teachers;
        @Info("campusName")
        String campusName;
        @Info("teachClassName")
        String teachClassName;
        @Info("arrangeInfo")
        JSONArray arrangeInfo;
    }
}
