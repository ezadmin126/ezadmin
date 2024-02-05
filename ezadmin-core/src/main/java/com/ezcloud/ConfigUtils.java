package com.ezcloud;

import com.ezcloud.common.utils.StringUtils;
import com.ezcloud.web.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

public class ConfigUtils {

    public static void main(String[] args) throws IOException {
        ResourcePatternResolver re = new PathMatchingResourcePatternResolver();
        Resource[] rs = re.getResources("file:/data/logs/ez/**");
        System.out.println(rs);
    }
    static Logger logger= LoggerFactory.getLogger(ConfigUtils.class);
    public static  List<Config> loadFiles(String config) throws IOException {

        List<Config> listConfig=new ArrayList();
        logger.info(" ezcloud 开始加载文件{} ",config);
        String paths[]= StringUtils.split(config,";");
        for(int i=0;i<paths.length;i++){
            try {
                if(StringUtils.isBlank(paths[i])){
                    continue;
                }
                ResourcePatternResolver re = new PathMatchingResourcePatternResolver();
                Resource[] rs = re.getResources(paths[i]);
                for (int j = 0; j < rs.length; j++) {

                   // logger.info(" ezcloud 开始加载文件{} ", rs[j].getURL());

                    Config c = new Config();
                    String protocol = rs[j].getURL().getProtocol();
                    if ("jar".equals(protocol)) {
                        c.setUrl(rs[j].getURL());
                        c.setPath(rs[j].getURL().getPath());
                        c.setIn(rs[j].getInputStream());
                        c.setProtocol("jar");
                    } else {
                        c.setFile(rs[j].getFile());
                        c.setUrl(rs[j].getURL());
                        c.setPath(rs[j].getURL().getPath());
                        c.setProtocol("file");
                    }
                    //    logger.info(" ezcloud 结束加载文件{} protocol: {} size:{}", rs[j].getURL(),protocol,listConfig.size());

                    listConfig.add(c);
                }
            }catch(Exception e){
                logger.error("ezcloud paths 加载异常"+paths[i],e);
            }
        }
        logger.info(" ezcloud 结束加载文件{} {}",config,listConfig.size());
        return listConfig;
    }
}
