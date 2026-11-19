package top.ezadmin.scaffold;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import top.ezadmin.annotations.EnableEzadmin;

@EnableEzadmin
@SpringBootApplication
public class ScaffoldApplication {

    public static void main(String[] args) {
        SpringApplication.run(ScaffoldApplication.class, args);
    }
}

@Controller
class HomeController {

    @GetMapping("/")
    String index() {
        return "redirect:/p/list/page-demo/asset";
    }
}
