package crack.cduestc.jw.net.entity.request;

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
}
