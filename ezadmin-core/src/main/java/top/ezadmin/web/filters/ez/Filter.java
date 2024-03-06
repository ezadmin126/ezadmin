package top.ezadmin.web.filters.ez;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract  class Filter {
    Logger logger= LoggerFactory.getLogger(Filter.class);
   public abstract void doFilter(HttpServletRequest request , HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException;
    private List<Pattern> includePattern = new ArrayList<Pattern>(1);
    private List<Pattern> excludePattern = new ArrayList<Pattern>(1);

    public String getInclude() {
        return include;
    }

    public void setInclude(String include) {
        this.include = include;
    }

    public String getExclude() {
        return exclude;
    }

    public void setExclude(String exclude) {
        this.exclude = exclude;
    }

    String include;
    String exclude;
    Filter next;
    public Filter getNext() {
        return next;
    }
    public Filter next(Filter next) {
        this.next = next;
        return next;
    }
    public void initFilterBean()   {
        excludePattern.addAll( loadPattern(exclude).keySet());
        includePattern.addAll( loadPattern(include).keySet());
    }
    public    boolean include( String s) {
        return match(includePattern,s);
    }
    public    boolean exclude( String s) {
            return match(excludePattern,s);
    }
    public    boolean match(List<Pattern> list, String s) {
        if (s == null || list == null || list.size() == 0) {
            return false;
        } else {
            for (int i = 0; i < list.size(); i++) {
                Pattern pInclude = list.get(i);
                Matcher matcher = pInclude.matcher(s);
                if (matcher.matches()) {
                    return true;
                }
            }
            return false;
        }
    }

    public   Map<Pattern, String> loadPattern(String conf) {
        if (conf == null || "".equals(conf)) {
            return Collections.emptyMap();
        }
        Map<Pattern, String> map = new HashMap<Pattern, String>();
        String[] includes = conf.split(";");
        for (int i = 0; i < includes.length; i++) {
            if (conf == null || "".equals(conf)) {
                continue;
            }
            String[] include = includes[i].split(":");
            Pattern pInclude = Pattern.compile(include[0]);
            if (include.length == 2) {
                map.put(pInclude, include[1]);
            } else {
                map.put(pInclude, "1");
            }
        }
        return map;
    }
}
