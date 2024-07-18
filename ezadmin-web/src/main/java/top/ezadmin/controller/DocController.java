package top.ezadmin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import top.ezadmin.blog.constants.Nologin;
import top.ezadmin.common.utils.StringUtils;

@Controller
@RequestMapping("/doc")
@Nologin
public class DocController {
    @RequestMapping("/{template}.html")
    @Nologin
    public String welcome(@PathVariable("template") String template)  {
        return"doc/"+template;
    }
}
