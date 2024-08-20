package top.ezadmin.controller;

import top.ezadmin.EzClientBootstrap;
import top.ezadmin.common.NotExistException;
import top.ezadmin.common.annotation.EzMapping;
import top.ezadmin.common.utils.StringUtils;
import top.ezadmin.common.utils.Utils;
import top.ezadmin.web.EzResult;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@EzMapping("/topezadmin/api/")
public class EzApiController extends BaseController {

    EzClientBootstrap bootstrap= EzClientBootstrap.instance();

    @EzMapping("datagroup.html")
    public void datagroup(HttpServletRequest request, HttpServletResponse response) throws Exception {

        String listUrlCode = Utils.trimNull(request.getAttribute("ENCRYPT_ID"));
        if(StringUtils.isBlank(listUrlCode)){
            throw new NotExistException();
        }
        EzResult.instance().printJSONUtils(response);
    }
}
