package crack.cduestc.jw.net.parser;

import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import crack.cduestc.jw.net.entity.response.ClassesResponse;
import crack.cduestc.jw.net.entity.response.ErrorResponse;
import crack.cduestc.jw.net.entity.response.Response;

import java.util.*;

public class OldClassParser extends ClassParser{
    @Override
    public Response parse(HtmlPage page) {
        if(page.getWebResponse().getContentAsString().contains("账号密码登录")) {
            return new ErrorResponse("账户未登录！", 401);
        }
        Map<Integer, Set<ClassesResponse.Clazz>> clazzMap = new HashMap<>();
        for (int i = 0; i < 7; i++) {
            scanClazzAndAdd(i, 0, 4, page, clazzMap);
            scanClazzAndAdd(i, 4, 11, page, clazzMap);
        }
        List<ClassesResponse.Clazz> clazzList = new ArrayList<>();
        clazzMap.forEach((integer, clazz) -> clazzList.addAll(clazz));
        return new ClassesResponse(clazzList);
    }

    protected int offset(){
        return 0;
    }
}
