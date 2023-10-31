package com.ezadmin;

import com.ezadmin.common.utils.StringUtils;
import com.ezadmin.web.Config;
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
    static Logger logger= LoggerFactory.getLogger(ConfigUtils.class);
    public static  List<Config> loadFiles(String config) throws IOException {
        List<Config> listConfig=new ArrayList();
        logger.info(" ezadmin 开始加载文件{} ",config);
        String paths[]= StringUtils.split(config,";");
        for(int i=0;i<paths.length;i++){
            try {
                if (paths[i].startsWith("classpath")) {
                    ResourcePatternResolver re = new PathMatchingResourcePatternResolver();
                    Resource[] rs = re.getResources(paths[i]);
                    for (int j = 0; j < rs.length; j++) {

                       logger.debug(" ezadmin 开始加载文件{} ", rs[j].getURL());

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
                        logger.debug(" ezadmin 结束加载文件{} protocol: {} size:{}", rs[j].getURL(),protocol,listConfig.size());

                        listConfig.add(c);
                    }
                } else {
                    Path path = Paths.get(paths[i]);
                    final List<Path> files = new ArrayList<Path>();
                    Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
                        @Override
                        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                            if (file.toString().endsWith("html")) {
                                files.add(file);
                            }
                            return FileVisitResult.CONTINUE;
                        }
                    });
                    for (int i1 = 0; i1 < files.size(); i1++) {
                        Config c = new Config();
                        logger.debug(" ezadmin 开始加载文件{} ", files.get(i1).toUri().toURL());

                        c.setFile(files.get(i1).toFile());
                        c.setUrl(files.get(i1).toUri().toURL());
                        c.setPath(files.get(i1).toString());
                        c.setProtocol("file");
                        if (logger.isDebugEnabled()) {
                            logger.debug(" ezadmin edit 开始加载文件{} ", files.get(i1).toUri().toURL());
                        }
                        listConfig.add(c);
                        logger.debug(" ezadmin 结束加载文件{} ", files.get(i1).toUri().toURL());

                    }
                }
            }catch(Exception e){
                logger.error("ezadmin paths 加载异常"+paths,e);
            }
        }
        logger.info(" ezadmin 结束加载文件{} {}",config,listConfig.size());
        return listConfig;
    }
}
