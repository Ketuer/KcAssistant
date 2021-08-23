package crack.cduestc.jw.net.entity.request;

import com.alibaba.fastjson.JSONObject;
import crack.cduestc.jw.net.anno.RequestParam;

import java.lang.reflect.Field;
import java.util.function.Consumer;

public abstract class Request {
    public final void forEachAddData(Consumer<String[]> consumer){
        try {
            for (Field field : this.getClass().getDeclaredFields()) {
                if(!field.isAccessible()) field.setAccessible(true);
                RequestParam param = field.getAnnotation(RequestParam.class);
                if(param == null) continue;
                if(param.value() == null || param.value().isEmpty()) continue;
                consumer.accept(new String[]{param.value(), field.get(this).toString()});
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public final JSONObject asJSON(){
        JSONObject object = new JSONObject();
        try {
            for (Field field : this.getClass().getDeclaredFields()) {
                if(!field.isAccessible()) field.setAccessible(true);
                RequestParam param = field.getAnnotation(RequestParam.class);
                if(param == null) continue;
                if(param.value() == null || param.value().isEmpty()) continue;
                object.put(param.value(), field.get(this).toString());
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return object;
    }

    public final String asParam(){
        StringBuilder builder = new StringBuilder();
        try {
            for (Field field : this.getClass().getDeclaredFields()) {
                if(!field.isAccessible()) field.setAccessible(true);
                RequestParam param = field.getAnnotation(RequestParam.class);
                if(param == null) continue;
                if(param.value() == null || param.value().isEmpty()) continue;
                builder.append(param.value()).append("=").append(field.get(this).toString()).append("&");
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return builder.substring(0, builder.length() - 1);
    }
}
