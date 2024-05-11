package top.ezadmin.controller;


import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.util.MapUtils;
import top.ezadmin.blog.constants.Constants;
import top.ezadmin.blog.constants.Nologin;
import top.ezadmin.blog.model.User;
import top.ezadmin.blog.utils.CaptchaUtil;
import top.ezadmin.blog.utils.DoCookie;
import top.ezadmin.blog.vo.BlogConfigurations;
import top.ezadmin.common.constants.SessionConstants;
import top.ezadmin.common.utils.DESUtils;
import top.ezadmin.common.utils.IpUtils;
import top.ezadmin.common.utils.StringUtils;
import top.ezadmin.dao.Dao;
import top.ezadmin.domain.mapper.SysUserMapper;
import top.ezadmin.domain.mapper.ext.SysUserExtMapper;
import top.ezadmin.domain.model.SysUser;
import top.ezadmin.domain.model.SysUserExample;
import top.ezadmin.web.EzResult;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * : LoginController
 * @author Hank.he
 * @since: 2022/2/12 20:10
 */
@Controller
@RequestMapping("/login/")
public class LoginControllerCustom extends CustomBaseController {
    Logger logger = LoggerFactory.getLogger(LoginControllerCustom.class);

    @Autowired
    @Qualifier("dataSource")
    DataSource dataSource;

    @Autowired
    SysUserMapper userMapper;
    @Resource
    SysUserExtMapper sysUserExtMapper;


    @RequestMapping(value = "login.html",method = RequestMethod.GET)
    @Nologin
    public String login(Model model, String error, HttpServletRequest request, HttpServletResponse response) throws Exception {
        if(StringUtils.isNotBlank(request.getParameter("welcome"))){
            return "welcome2";
        }
        model.addAttribute("error", error);
        model.addAttribute("num", ThreadLocalRandom.current().nextInt(16) + 1);
        return "login";
    }
    @RequestMapping(value = "regist.html",method = RequestMethod.GET)
    @Nologin
    public String register(Model model, String error, HttpServletRequest request, HttpServletResponse response) throws Exception {
        model.addAttribute("pageName", "首页");
        model.addAttribute("configurations", BlogConfigurations.config());
        model.addAttribute("error", error);
        model.addAttribute("num", ThreadLocalRandom.current().nextInt(16) + 1);
        return "blog/regist";
    }



    @RequestMapping(value = "signout.html",method = RequestMethod.GET)
    @Nologin
    public String logout(Model model, String error, HttpServletRequest request, HttpServletResponse response) {


        DoCookie cookie=new DoCookie();
        cookie.deleteCookie(Constants.EZ_SID);
        request.getSession().setAttribute(SessionConstants.EZ_SESSION_USER_KEY,null);
        return "redirect:/login/login.html";
    }

    @RequestMapping(value = "resetPassword.html",method = RequestMethod.GET)
    public String resetPassWord(Model model, String error, HttpServletRequest request, HttpServletResponse response) {
        model.addAttribute("error", error);
        model.addAttribute("num", ThreadLocalRandom.current().nextInt(16) + 1);
        return "resetpassword";
    }
    @RequestMapping(value = "userInfo.html" )
    @ResponseBody
    public EzResult userInfo(Model model, String error, HttpServletRequest request, HttpServletResponse response) {
        model.addAttribute("error", error);
        return EzResult.instance().data("user",
                JSON.toJSONString(getSessionUser().getRoleNames()));
    }
    @RequestMapping(value = "doLogin.html",method = RequestMethod.POST)
    @Nologin
    public String doLogin(Model model,String companyId,String userName,String password,  HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (!CaptchaUtil.isSafe(IpUtils.getRealIp(request)) || StringUtils.isBlank(password)) {
            return "login";
        }
//        String userSql=" select USER_ID,USER_NAME,PASSWORD,  A.COMPANY_ID  \n" +
//                "                 from T_SYS_USER A  \n" +
//                "                 WHERE USER_NAME=?  limit 1 ";
//        Map<String, Object>  userMap = Dao.getInstance().executeQueryOne(dataSource, userSql,
//                new Object[]{userName});
        SysUserExample exampleU=new SysUserExample();
        exampleU.createCriteria().andUserNameEqualTo(userName).andStatusEqualTo(1).andDeleteFlagEqualTo(0);
        List<SysUser> userList=userMapper.selectByExample(exampleU);
        try {
            if (CollectionUtils.isEmpty(userList) || !
                    StringUtils.equals(DESUtils.decryptDES(userList.get(0).getPassword()), password)) {
                return login(model, "用户名或密码错误,请直连数据库查看密码", request, response);
            }
        }catch (Exception e){
            return login(model, "用户名或密码错误,请直连数据库查看密码", request, response);
        }
        SysUser sysUser=userList.get(0);
        User user=new User()    ;
        user.setUserId(sysUser.getUserId());
        user.setUserName(sysUser.getUserName());
        user.setCompanyName(sysUser.getCompanyId()+"");
        user.setCompanyId( sysUser.getCompanyId());
        user.setParentId(sysUser.getParentId());
        user.setRoleNames(sysUserExtMapper.selectUserRoles(sysUser.getUserId()));
        Enumeration<String> e= request.getSession().getAttributeNames();
        while(e.hasMoreElements()){
            request.getSession().removeAttribute(e.nextElement());
        }
        request.getSession().setAttribute(SessionConstants.EZ_SESSION_USER_KEY, user);
        DoCookie cookie = new DoCookie();
        String sid=cookie.addEncodeCookie(Constants.EZ_SID, user.getUserId() + "@@" +  UUID.randomUUID(), 3600*24*30);
        SysUser sidUser=new SysUser();
        sidUser.setUserId(user.getUserId());
        sidUser.setSid(sid);
        sidUser.setUpdateTime(new Date());
        userMapper.updateByPrimaryKeySelective(sidUser);
        return "redirect:/topezadmin/index.html";
    }


    @RequestMapping(value = "doResetPassword.html",method = RequestMethod.POST)
    public String resetPassword2(Model model, String password1
            ,String password2,String password3,HttpServletRequest request, HttpServletResponse response) throws Exception {


        if(StringUtils.isBlank(password2 )||StringUtils.isBlank(password3)){
            return resetPassWord(model,"新密码不能为空",request,response);
        }
        if(!StringUtils.equals(password2,password3)){
            return resetPassWord(model,"两次新密码不一致",request,response);
        }
        String userSql="select user_id,user_name,password from T_SYS_USER WHERE user_id=?";
        Map<String, Object>  userMap = Dao.getInstance().executeQueryOne(dataSource, userSql,
                new Object[]{getSessionUser().getUserId()});
        if(MapUtils.isEmpty(userMap)||!
                StringUtils.equals(DESUtils.decryptDES(userMap.get("PASSWORD")+""),password1)){
            return resetPassWord(model,"当前旧密码错误",request,response);
        }

        Dao.getInstance().executeUpdate(dataSource,"" +
                "update T_SYS_USER SET PASSWORD=? WHERE user_id=? ",new Object[]{
                DESUtils.encryptDES(password2),getSessionUser().getUserId()
        });

        DoCookie cookie=new DoCookie();
        cookie.deleteCookie(Constants.EZ_SID);
        request.getSession().setAttribute(SessionConstants.EZ_SESSION_USER_KEY,null);

        return "redirect:/ezadmin/index.html";
    }

}
