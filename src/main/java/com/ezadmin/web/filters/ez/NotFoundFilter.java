package com.ezadmin.web.filters.ez;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class NotFoundFilter extends Filter {
    @Override
    public void doFilter(HttpServletRequest request , HttpServletResponse response) throws IOException {
         response.sendRedirect("/404");
    }
}
