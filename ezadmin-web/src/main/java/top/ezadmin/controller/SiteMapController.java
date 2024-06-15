package top.ezadmin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import top.ezadmin.EzClientBootstrap;
import top.ezadmin.blog.service.BlogService;
import top.ezadmin.domain.model.Blog;
import top.ezadmin.plugins.cache.Callback;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/")
public class SiteMapController {
	@Resource
	BlogService blogService;


	@RequestMapping(value = "/sitemap-1.xml", produces = "text/xml;charset=UTF-8")
	public void sitemap(HttpServletRequest request, HttpServletResponse response) throws Exception {
		StringBuilder sb=new StringBuilder();

		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String dateString = formatter.format(new Date());
		List<Blog> list=(List<Blog>) EzClientBootstrap.instance().getCache().get60("allblog", "allblog",
				new Callback() {
					@Override
					public Object call(String key) {
						return blogService.all();
					}
				}
		);
		sb.append("<urlset>");
		sb.append(System.lineSeparator());
		list.forEach(item->{
			sb.append("<url>");
			sb.append(System.lineSeparator());

				sb.append("<loc>");
				sb.append("/blog/detail-"+item.getBlogId()+"-1.html");
				sb.append("</loc>");

				sb.append("<lastmod>");
				sb.append(dateString);
				sb.append("</lastmod>");

				sb.append("<changefreq>");
				sb.append("weekly");
				sb.append("</changefreq>");

			sb.append(System.lineSeparator());
			sb.append("</url>");
		});
		sb.append(System.lineSeparator());
		sb.append("</urlset>");
		response.setContentType("text/xml;charset=UTF-8");
		response.getWriter().print(sb.toString());
	}


	@RequestMapping(value = "/robots.txt", produces = "text/txt;charset=UTF-8")
	public void robots(HttpServletRequest request, HttpServletResponse response) throws Exception {
		StringBuilder sb=new StringBuilder();
		sb.append("User-agent: *");
		sb.append(System.lineSeparator());
		sb.append("Disallow: /system");
		sb.append(System.lineSeparator());
		sb.append("Disallow: /topezadmin");
		sb.append(System.lineSeparator());
		sb.append(System.lineSeparator());
		sb.append("Sitemap: https://www.ezadmin.top/sitemap-1.xml");
		  response.setContentType("text/txt;charset=UTF-8");
		  response.getWriter().print(sb.toString());
	}

}
