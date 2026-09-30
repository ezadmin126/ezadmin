package top.ezadmin.service;

import top.ezadmin.web.RequestContext;
import java.util.Map;

public interface ListService {
    void initComponentData(RequestContext context, Map<String, Object> component);
}
