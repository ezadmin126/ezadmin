package top.ezadmin.controller;

import top.ezadmin.EzClientBootstrap;
import top.ezadmin.common.utils.StringUtils;
import top.ezadmin.dao.model.Info;
import top.ezadmin.dao.model.InitVO;
import top.ezadmin.web.EzResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/")
public class EzIndexController {
	@RequestMapping("/health.html")
	@ResponseBody
	public String checkpreload()  {
		return"OK";
	}
	@RequestMapping("/welcome.html")
	public String welcome()  {
		return"welcome";
	}

	@Value("${topezadmin.indexUrl:}")
	private String indexUrl;


	@RequestMapping("/")
	public String index()  {
		if(StringUtils.isBlank(indexUrl)){
			return"redirect:/topezadmin/index.html";
		}
		return"forward:"+indexUrl;
	}
}
