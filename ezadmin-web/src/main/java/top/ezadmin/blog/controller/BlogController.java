package top.ezadmin.blog.controller;


import com.github.pagehelper.PageInfo;
import top.ezadmin.blog.constants.Nologin;
import top.ezadmin.blog.model.PageVo;
import top.ezadmin.common.constants.SessionConstants;
import top.ezadmin.common.utils.EzPage;
import top.ezadmin.common.utils.IpUtils;
import top.ezadmin.common.utils.NumberUtils;
import top.ezadmin.domain.model.BlogMessage;
import top.ezadmin.web.EzResult;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import top.ezadmin.blog.service.BlogService;
import top.ezadmin.blog.utils.UrlTool;
import top.ezadmin.blog.vo.BlogConfigurations;
import top.ezadmin.blog.vo.BlogMessageVO;
import top.ezadmin.blog.vo.BlogVO;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;

;

@Controller
@RequestMapping("/blog")
@Nologin
public class BlogController {
    Logger logger = LoggerFactory.getLogger(BlogController.class);

    @Resource
    BlogService blogService;


    @RequestMapping(value = {"/index.html", "/search.html"}, method = RequestMethod.GET)
    public String index(Model model,
                        @RequestParam(value = "currentPage", required = false, defaultValue = "1") Integer currentPage,
                        Integer categoryId, String keyword, HttpServletRequest request, HttpServletResponse response) {

        Page page = PageHelper.startPage(currentPage, 10);
        EzPage pp = new EzPage();
        pp.setCurrentPage(currentPage);
        pp.setPerPageInt(10);
        List<BlogVO> list=blogService.list(keyword, categoryId);
        try {
            //所有blog
            model.addAttribute("list", list);
            pp.setTotalRecord(page.getTotal());
            //热门blog
            model.addAttribute("hotBlogs",blogService.hottest());
            //最新blog
            model.addAttribute("newBlogs",blogService.newtest());
            //分类
            model.addAttribute("categoryList",blogService.categorys());

        } catch (Exception e) {
            //ignor
            e.printStackTrace();
        }
        if(currentPage<page.getTotal()){
            model.addAttribute("nextPageUrl", UrlTool.blogSearch(categoryId,currentPage+1));
        }
        PageInfo p=new PageInfo(list);
        PageVo pageVo=PageVo.create(UrlTool.blogSearchPage(categoryId),currentPage,p);


        model.addAttribute("page", pp);
        model.addAttribute("pageVo", pageVo);
        model.addAttribute("categoryId", categoryId);
        model.addAttribute("keyword", keyword);
        model.addAttribute("current", currentPage);
        model.addAttribute("pageName", "首页");
        model.addAttribute("configurations", BlogConfigurations.config());
        return "blog/index";
    }

    @RequestMapping(value = {"detail.html"}, method = RequestMethod.GET)
    public String detail(Model model, Integer blogId,@RequestParam(value = "currentPage", required = false, defaultValue = "1") Integer currentPage,
                         HttpServletRequest request, HttpServletResponse response) throws Exception {
        BlogVO blogVO = blogService.detail(blogId);
        if (blogVO != null) {
            blogService.addView(blogId, blogVO.getViewTimes());
        }else{
            return "";
        }
        Page page = PageHelper.startPage(currentPage, 10);

        List<BlogMessageVO> messageList= blogService.listMessage(blogId);
        EzPage  pp = new EzPage();
        pp.setCurrentPage(currentPage);
        pp.setPerPageInt(10);
        pp.setTotalRecord(page.getTotal());
        try {
            blogVO.setCommentCount(NumberUtils.toInt(page.getTotal() + ""));
        }catch (Exception e){}
        model.addAttribute("page", pp);
        if(currentPage<page.getTotal()){
            model.addAttribute("nextPageUrl", UrlTool.blogDetail(blogId,currentPage+1));
        }
        model.addAttribute("current", 1);
        model.addAttribute("blogDetailVO", blogVO);
        model.addAttribute("messageList", messageList);
        model.addAttribute("configurations", BlogConfigurations.config());
        model.addAttribute("pageName", blogVO.getBlogTitle());
        Long code=System.currentTimeMillis();
        model.addAttribute("valicode", code+"");
        request.getSession().setAttribute(SessionConstants.SESSION_CAPTCHA_KEY,code);
        return "blog/detail";
    }

    @RequestMapping(value = {"addComments.html"})
    @ResponseBody
    public EzResult addComments(Model model, String valicode, BlogMessage blog, HttpServletRequest request, HttpServletResponse response) throws Exception {
        boolean v=request.getSession().getAttribute(SessionConstants.SESSION_CAPTCHA_KEY)==null||!StringUtils.equalsIgnoreCase(valicode,
                request.getSession().getAttribute(SessionConstants.SESSION_CAPTCHA_KEY) + "");

        if (v) {
            logger.error("验证码错误"+ IpUtils.getRealIp(request));
           return EzResult.instance().fail().msg("403","验证码错误");
        }
        try {
            blog.setAddTime(new Date());
            blog.setAddIp(IpUtils.getRealIp(request));
            blogService.addMessage(blog);
            return EzResult.instance().msg("200", "成功");
        }catch(Exception e){
            logger.error("",e);
            return EzResult.instance().msg("500", "失败，请联系管理员ezadmin@126.com");
        }
    }



    @RequestMapping(value = {"about.html"})
    public String about(Model model, HttpServletRequest request, HttpServletResponse response) {
        return "forward:/blog/detail-1-1.html";
    }

    @RequestMapping(value = {"contact.html"})
    public String contact(Model model, HttpServletRequest request, HttpServletResponse response) {
        model.addAttribute("current", 3);
        model.addAttribute("serverType", request.getServerName().contains("ttqia") ? "1" : 0);

        return "blog/contact";
    }
}
