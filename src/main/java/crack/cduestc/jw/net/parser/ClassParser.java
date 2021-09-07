package crack.cduestc.jw.net.parser;

import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import crack.cduestc.jw.net.entity.response.ClassesResponse;
import crack.cduestc.jw.net.entity.response.ErrorResponse;
import crack.cduestc.jw.net.entity.response.Response;

import java.util.*;

public class ClassParser implements Parser<Response> {
    @Override
    public Response parse(HtmlPage page) {
        if(page.getWebResponse().getContentAsString().contains("账号密码登录")) {
            return new ErrorResponse("账户未登录！", 401);
        }
        Map<Integer, Set<ClassesResponse.Clazz>> clazzMap = new HashMap<>();
        for (int i = 0; i < 7; i++) {
            scanClazzAndAdd(i, 0, 4, page, clazzMap);
            scanClazzAndAdd(i, 6, 13, page, clazzMap);
        }
        List<ClassesResponse.Clazz> clazzList = new ArrayList<>();
        clazzMap.forEach((integer, clazz) -> clazzList.addAll(clazz));
        return new ClassesResponse(clazzList);
    }

    protected int offset(){
        return 2;
    }

    protected void scanClazzAndAdd(int i, int start, int end, HtmlPage page, Map<Integer, Set<ClassesResponse.Clazz>> clazzMap){
        for (int j = start; j < end; j++) {
            int index = i * 13 + j;
            DomElement e = page.getElementById("TD"+index+"_0");
            if(e == null) {
                Set<ClassesResponse.Clazz> clazz = null;
                int x = 1;
                while (index - x >= 0 && (clazz = clazzMap.get(index - x)) == null) x++;
                if(clazz != null) {
                    int finalJ = j + 1;
                    clazz.forEach(c -> c.indexSet.add(finalJ > 3 ? finalJ - offset() : finalJ));
                }
                continue;
            }
            String content = e.getTextContent();
            if(content.isEmpty()) continue;
            clazzMap.put(index, asClazzSet(content, i, j + 1));
        }
    }

    protected Set<ClassesResponse.Clazz> asClazzSet(String content, int day, int index){
        Set<ClassesResponse.Clazz> clazzSet = new HashSet<>();
        char[] chars = content.toCharArray();
        int i = 0;
        StringBuilder builder;
        System.out.println(content);
        while (i < chars.length - 1){
            builder = new StringBuilder();
            while(chars[i++] != '(' || !isWord(chars, i)) builder.append(chars[i - 1]);
            String name = builder.toString();
            builder.setLength(0);
            while(chars[i++] != ')') builder.append(chars[i - 1]);
            String id = builder.toString();
            i+=2;
            builder.setLength(0);
            while (chars[i++] != ')') builder.append(chars[i - 1]);
            String teacher = builder.toString();
            i++;
            builder.setLength(0);
            while (chars[i++] != ',') builder.append(chars[i - 1]);
            String week = builder.toString();
            builder.setLength(0);
            while (chars[i++] != ')') builder.append(chars[i - 1]);
            String local = builder.toString();

            clazzSet.add(new ClassesResponse.Clazz(name, id, teacher, local, day+1,
                    toWeeks(week), new HashSet<Integer>(){{this.add(index > 3 ? index - offset() : index);}}));

            System.out.println(name);
        }
        return clazzSet;
    }

    protected boolean isWord(char[] arr, int i){
        boolean is = true;
        while (arr[i] != ')'){
            if((arr[i] < 'a' || arr[i] > 'z') && (arr[i] < 'A' || arr[i] > 'Z') &&
                    (arr[i] < '0' || arr[i] > '9') && arr[i] != '.'){
                is = false;
                break;
            }
            i++;
        }
        return is && new String(arr).contains(".");
    }

    protected Set<Integer> toWeeks(String weekString){
        int i = 0;
        switch (weekString.charAt(0)){
            case '单':
                i = 1;
                break;
            case '双':
                i = 2;
                break;
        }
        if(i > 0) weekString = weekString.substring(1);
        String[] str = weekString.split("-");
        int start = Integer.parseInt(str[0]), end = Integer.parseInt(str[1]);
        Set<Integer> set = new TreeSet<>();
        for (int j = start; j <= end; j++) {
            if(i == 1 && j % 2 == 0) continue;
            if(i == 2 && j % 2 == 1) continue;
            set.add(j);
        }
        return set;
    }
}
