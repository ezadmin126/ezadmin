package top.ezadmin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import top.ezadmin.blog.constants.Nologin;
import top.ezadmin.common.utils.StringUtils;
import top.ezadmin.web.EzResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Handler;

@Controller
@RequestMapping("/doc")
@Nologin
public class DocController {
    @RequestMapping("/{template}.html")
    @Nologin
    public String welcome(@PathVariable("template") String template)  {
        return"doc/"+template;
    }


    @RequestMapping("/testKV")
    @ResponseBody
    public EzResult testKV(){
        List<Map<String,String>> list=new ArrayList<>();
       Map<String,String> map=new HashMap<>();
       map.put("K","ID");
       map.put("V","1");
        list.add(map);

        Map<String,String> map2=new HashMap<>();
        map2.put("K","NAME");
        map2.put("V","NAME");
        list.add(map2);
        return EzResult.instance().data(list);
    }

    @RequestMapping("/testlevel")
    @ResponseBody
    public EzResult testlevel(){
        List<Map<String,String>> list=new ArrayList<>();
        Map<String,String> map=new HashMap<>();
        map.put("ID","1");
        map.put("PARENT_ID","0");
        map.put("NAME","id1");
        list.add(map);

        Map<String,String> map2=new HashMap<>();
        map2.put("ID","2");
        map2.put("PARENT_ID","1");
        map2.put("NAME","id2");
        list.add(map2);

        Map<String,String> map3=new HashMap<>();
        map3.put("ID","3");
        map3.put("PARENT_ID","1");
        map3.put("NAME","id3");
        list.add(map3);
        return EzResult.instance().data(list);
    }
}
