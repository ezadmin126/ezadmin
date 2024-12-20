package top.ezadmin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import top.ezadmin.blog.constants.Nologin;
import top.ezadmin.common.constants.SessionConstants;
import top.ezadmin.common.utils.StringUtils;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;

@Controller
@RequestMapping("/login/validate")
@Nologin
public class CaptchaImageCreateController {

	@RequestMapping("/")
	@ResponseBody
	public String check(String valicode, HttpServletRequest request) {
		if (StringUtils.equalsIgnoreCase(valicode,
				request.getSession().getAttribute(SessionConstants.SESSION_CAPTCHA_KEY) + "")) {
			return "true";
		}
		return "false";
	}

	@RequestMapping("/captcha.jpg")
	public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {

		validateCode(request, response);

		return null;
	}

	public void validateCode(HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.setContentType("image/jpeg");
		response.setHeader("Pragma", "No-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 0L);
		int width = 60;
		int height = 20;
		BufferedImage image = new BufferedImage(width, height, 1);
		Graphics g = image.getGraphics();
		Random random = new Random();
		g.setColor(getRandColor(200, 250));
		g.fillRect(0, 0, width, height);
		g.setFont(new Font("Times New Roman", 0, 18));
		g.setColor(getRandColor(160, 200));
		for (int i = 0; i < 100; i++) {
			int x = random.nextInt(width);
			int y = random.nextInt(height);
			int xl = random.nextInt(12);
			int yl = random.nextInt(12);
			g.drawLine(x, y, x + xl, y + yl);
		}

		String sRand = "";
		for (int i = 0; i < 4; i++) {
			String rand = String.valueOf(random.nextInt(10));
			sRand = (new StringBuilder()).append(sRand).append(rand).toString();
			g.setColor(new Color(20 + random.nextInt(110), 20 + random.nextInt(110), 20 + random.nextInt(110)));
			g.drawString(rand, 13 * i + 6, 16);
		}
		request.getSession().setAttribute(SessionConstants.SESSION_CAPTCHA_KEY, sRand);
		g.dispose();
		ServletOutputStream responseOutputStream = response.getOutputStream();
		ImageIO.write(image, "JPEG", responseOutputStream);
		responseOutputStream.flush();
		responseOutputStream.close();
	}

	private Color getRandColor(int fc, int bc) {
		Random random = new Random();
		if (fc > 255)
			fc = 255;
		if (bc > 255)
			bc = 255;
		int r = fc + random.nextInt(bc - fc);
		int g = fc + random.nextInt(bc - fc);
		int b = fc + random.nextInt(bc - fc);
		return new Color(r, g, b);
	}

}
