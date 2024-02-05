package top.ezadmin.controller;

import com.ezcloud.EzClientBootstrap;
import com.ezcloud.dao.model.Info;
import com.ezcloud.dao.model.InitVO;
import com.ezcloud.web.EzResult;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/")
public class IndexController {
	@RequestMapping("/checkpreload.html")
	@ResponseBody
	public String checkpreload()  {
		return"OK";
	}
	@RequestMapping("/welcome.html")
	public String welcome()  {
		return"welcome";
	}


	@RequestMapping("/")
	public String index()  {
		return"redirect:/ezcloud/index.html";
	}
	@RequestMapping("eznavs.html")
	public void navs(HttpServletRequest request, HttpServletResponse response) throws Exception {
		InitVO vo = new InitVO();
		List<Info> toproot = new ArrayList<>();
		List<Info> root = new ArrayList<>();
		Info listW = new Info();
		listW.setTitle("接入指南");
		listW.setId("1");
		listW.setPid("0");
		listW.setHref("/welcome.html");
		root.add(listW);

		Info listN = new Info();
		listN.setTitle("列表管理");
		listN.setId("1");
		listN.setPid("0");
		listN.setHref("/ezcloud/list/list-listmanage?perPageInt=20");
		root.add(listN);

		Info listF = new Info();
		listF.setTitle("表单管理");
		listF.setId("2");
		listF.setPid("0");
		listF.setHref("/ezcloud/list/list-formmanage?perPageInt=20");
		root.add(listF);

		Info sql = new Info();
		sql.setTitle("慢SQL统计");
		sql.setId("3");
		sql.setPid("0");
		sql.setHref("/sql/slow");
		root.add(sql);
////
		Info testsq = new Info();
		testsq.setTitle("SQL统计");
		testsq.setId("4");
		testsq.setPid("0");
		testsq.setHref("/ezadmin/list/list-sqlog?perPageInt=20");
		root.add(testsq);

		Info TOP = new Info();
		TOP.setTitle("系统管理");
		TOP.setChild(root);
		toproot.add(TOP);
		vo.homeInfo(EzClientBootstrap.instance().getSystemName(), "").
				logoInfo("", "/ezcloud/index.html", EzClientBootstrap.instance().getConfig().get("logo") + "")
				.setMenuInfo(toproot);
		EzResult.instance().msg("0", "ok")
				.data(vo).printJSONUtils(response);
	}
}
