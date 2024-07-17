package top.ezadmin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import top.ezadmin.blog.constants.Nologin;
import top.ezadmin.common.utils.StringUtils;

@Controller
@RequestMapping("/doc")
@Nologin
public class DocController {
    @RequestMapping("/index.html")
    @Nologin
    public String welcome(String p)  {
        if(StringUtils.isBlank(p)){
            p="index";
        }
        return"doc/"+p;
    }
}
