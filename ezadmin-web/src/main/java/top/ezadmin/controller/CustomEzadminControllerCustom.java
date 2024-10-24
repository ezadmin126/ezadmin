package top.ezadmin.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import top.ezadmin.EzClientBootstrap;
import top.ezadmin.blog.constants.Nologin;
import top.ezadmin.blog.model.User;
import top.ezadmin.common.utils.NumberUtils;
import top.ezadmin.common.utils.Utils;
import top.ezadmin.dao.Dao;
import top.ezadmin.dao.model.Info;
import top.ezadmin.dao.model.InitVO;
import top.ezadmin.dao.model.SysNavVO;
import top.ezadmin.web.EzResult;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.util.*;

@Controller
@RequestMapping("/")
public class CustomEzadminControllerCustom extends CustomBaseController {
    @Value("${topezadmin.logoUrl:}")
    String logo;
    @Value("${topezadmin.navUrl:}")
    String navUrl;
    @Value("${topezadmin.systemName:}")
    String systemName;
    @Value("${topezadmin.index:}")
    String serverIndex;
    @Autowired
    @Qualifier("dataSource")
    DataSource dataSource;

    /**
     * 通知
     *
     * @param model
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "message.html", method = RequestMethod.GET)
    @ResponseBody
    @Nologin
    public void message(Model model, HttpServletRequest request, HttpServletResponse response) throws IOException {
        EzResult.instance().code("200").count(103).data(null).printJSONUtils(response);
    }
    @RequestMapping(value = "error.html", method = RequestMethod.GET)
    @Nologin
    public String error(Model model, HttpServletRequest request, HttpServletResponse response) throws IOException {
        model.addAttribute("message",request.getParameter("message"));
        return "error/success";
    }
    //转到所有消息列表
    @RequestMapping(value = "messageAll.html", method = RequestMethod.GET)
    @Nologin

    public String messageAll(Model model, HttpServletRequest request, HttpServletResponse response) {
        return "redirect:/";
    }

    //转到所有消息列表
    @RequestMapping(value = "noticeAll.html", method = RequestMethod.GET)
    public String noticeAll(Model model, HttpServletRequest request, HttpServletResponse response) {
        return "redirect:/";
    }
    @RequestMapping(value = {"/error/404.html","/error/404"}, method = RequestMethod.GET)
    @Nologin
    public String noticeAll1(Model model, HttpServletRequest request, HttpServletResponse response) {
        return "error/4xx";
    }
    @RequestMapping(value = {"/error/500.html","error"}, method = RequestMethod.GET)
    @Nologin
    public String noticeAll2(Model model, HttpServletRequest request, HttpServletResponse response) {
        return "error/5xx";
    }
    @RequestMapping(value = "/error/403.html", method = RequestMethod.GET)
    @Nologin
    public String noticeAll3(Model model, HttpServletRequest request, HttpServletResponse response) {
        return "error/403";
    }

    @RequestMapping(value = "laynavs.html", method = RequestMethod.GET)
    @Nologin
    @ResponseBody
    public void laynavs(Model model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        User user=getSessionUser();
        List<Map<String,Object>> navList=Dao.getInstance().executeQuery(dataSource,
                 MININAV ,new Object[]{user.getUserId(),0});
        List<Info> root=list(navList,user);
        fillRootLayNav(root, user );
        InitVO vo=new InitVO()  ;

        List<Info> toproot=new ArrayList<>();
        Info TOP=new Info( );
        TOP.setTitle("系统");
        TOP.setChild(root);
        toproot.add(TOP);
        String logo=EzClientBootstrap.instance().getConfig().get("logo")+"";
        vo.homeInfo(EzClientBootstrap.instance().getSystemName(),"").
                logoInfo("","/ezadmin/index.html","/static/logo.jpg"  )
                .setMenuInfo(toproot);
        EzResult.instance().msg("0","ok")
                .data(vo).printJSONUtils(response);
    }

    private List<Info> list(List<Map<String,Object>> navList,User user){

        if(Utils.isEmpty(navList)){
            return Collections.emptyList()  ;
        }
        List<Info> list=new ArrayList<>();
        navList.forEach(item->{

             Info in=new Info(
                     Utils.trimNull(item.get("TITLE")),
                     Utils.trimNull(item.get("HREF")),
                     Utils.trimNull(item.get("IMAGE")),
                     Utils.trimEmptyDefault(item.get("TARGET"),"_self"),
                     Utils.trimEmptyDefault(item.get("ICON"),"  layui-icon")
             );

             if(user.hasRole("管理员")){
                 in.setTitle(in.getTitle() +item.get("RESOURCE_DESC"));
             }


             in.setId(Utils.trimNull(item.get("ID")));
             list.add(in);
        });
        return list;
    }

    private  void fillRootLayNav(List<Info> root,  User user) {
        if(CollectionUtils.isEmpty(root)){
            return;
        }
        for (int i = 0; i < root.size(); i++) {
            Info map= root.get(i);
            try {
                List<Map<String, Object>> childList = Dao.getInstance().executeQuery(dataSource,
                         MININAV, new Object[]{user.getUserId(), map.getId()});
                List<Info> clist=list(childList,user);
               map.setChild(clist);
                if(!CollectionUtils.isEmpty(childList)){
                    fillRootLayNav(clist, user );
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
    public static final String MININAV="SELECT\n" +
            "\tD.RESOURCE_ID id ,D.RESOURCE_URL href,D.RESOURCE_NAME title , D.RESOURCE_DESC  ,RESOURCE_ICON icon" +
            ",'' target\n" +
            "FROM\n" +
            "\tT_SYS_USER A\n" +
            "\tLEFT JOIN T_SYS_USER_ROLE B ON A.USER_ID = B.USER_ID \n" +
            "\tAND B.DELETE_FLAG = 0 \n" +
            "\tLEFT JOIN T_SYS_ROLE_RESOURCE C ON B.ROLE_ID = C.ROLE_ID \n" +
            "\tAND C.DELETE_FLAG = 0   \n" +
            "\tLEFT JOIN T_SYS_RESOURCE D ON   C.RESOURCE_ID = D.RESOURCE_ID AND D.RESOURCE_TYPE=1 \n" +
            "WHERE\n" +
            "\tA.USER_ID = ? \n" +
            "\tAND D.DELETE_FLAG = 0 \n" +
            "\tAND D.PARENT_RESOURCE_ID =? order by DISPLAY_ORDER DESC";
}
