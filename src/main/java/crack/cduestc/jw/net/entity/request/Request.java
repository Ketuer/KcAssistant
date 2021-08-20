package crack.cduestc.jw.net.entity.request;

import crack.cduestc.jw.net.anno.Param;

import java.lang.reflect.Field;
import java.util.function.Consumer;

public abstract class Request {
    public final void forEachAddData(Consumer<String[]> consumer){
        try {
            for (Field field : this.getClass().getDeclaredFields()) {
                if(!field.isAccessible()) field.setAccessible(true);
                Param param = field.getAnnotation(Param.class);
                if(param == null) continue;
                if(param.value() == null || param.value().isEmpty()) continue;
                consumer.accept(new String[]{param.value(), field.get(this).toString()});
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
