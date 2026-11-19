package top.ezadmin.scaffold;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import top.ezadmin.dao.model.Info;
import top.ezadmin.dao.model.InitVO;
import top.ezadmin.web.EzResult;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@RestController
class NavigationController {

    private final InitVO navigation;

    NavigationController(ObjectMapper json, @Value("classpath:laynavs.json") Resource resource) throws IOException {
        try (InputStream config = resource.getInputStream()) {
            navigation = json.readValue(config, InitVO.class);
        }
    }

    @GetMapping(path = "${topezadmin.nav-url:/laynavs.html}", produces = MediaType.APPLICATION_JSON_VALUE)
    EzResult navigation(@RequestParam(name = "noroot", defaultValue = "") String noRoot) {
        return EzResult.instance().msg("0", "ok").data(noRoot.isBlank() ? withSystemRoot() : navigation);
    }

    private InitVO withSystemRoot() {
        Info system = new Info();
        system.setTitle("系统");
        system.setChild(navigation.getMenuInfo());

        InitVO result = InitVO.create();
        result.setHomeInfo(navigation.getHomeInfo());
        result.setLogoInfo(navigation.getLogoInfo());
        result.setMenuInfo(List.of(system));
        return result;
    }
}
