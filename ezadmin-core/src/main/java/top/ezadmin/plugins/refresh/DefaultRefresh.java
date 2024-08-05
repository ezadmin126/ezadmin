package top.ezadmin.plugins.refresh;

import top.ezadmin.EzClientBootstrap;
import top.ezadmin.common.utils.JsoupUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

public class DefaultRefresh extends EzRefresh {
    public static final Logger log = LoggerFactory.getLogger(DefaultRefresh.class);

    @Override
    public  void refreshAll() throws IOException {
        if(!EzClientBootstrap.instance().isSqlCache()){
            WatchService watcher = FileSystems.getDefault().newWatchService();
            new Thread(){
                public void run(){
                    try {
                        File listfold = new File(JsoupUtil.editPath("topezadmin"+File.separator+"config"+File.separator+EzClientBootstrap.instance().getAdminStyle())
                                );

                        Files.walkFileTree(listfold.toPath(), new SimpleFileVisitor<Path>() {
                            @Override
                            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                              // 处理文件
                              //  file.register(watcher, ENTRY_CREATE, ENTRY_MODIFY);
                              return FileVisitResult.CONTINUE;
                            }
                            @Override
                            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                              // 处理文件夹
                             //    log.info("监听文件夹 " +dir);
                                 dir.register(watcher,  ENTRY_MODIFY);
                              return FileVisitResult.CONTINUE;
                            }
                        });
                    }catch (Exception e){
                        log.warn("",e);
                    }

                    while (true) {
                        try {
                            WatchKey key = watcher.take();
                            String currentDir = key.watchable().toString();
                            for (WatchEvent<?> event : key.pollEvents()) {
                                String fileName= event.context().toString();
                                if(!fileName.endsWith(".html")){
                                     log.debug("文件 " + currentDir + File.separator  +event.context().toString()+ " 被"+event.kind()+"了,文件名不是html结尾不处理。");
                                    continue;
                                }
                                File currentFile=new File(currentDir + File.separator +fileName);
                                WatchEvent.Kind<?> kind = event.kind();
                                 if(currentDir.contains("plugins")&&fileName.contains(".html")){
                                    refreshPlugins(currentFile);
                                }else if(currentDir.contains("list")&&fileName.contains(".html")){
                                    refreshList(currentFile);
                                }else if(currentDir.contains("form")&&fileName.contains(".html")){
                                    refreshForm(currentFile);
                                }
                                // 处理不同种类的事件
                                if (kind == ENTRY_CREATE) {
                                    // 处理文件创建事件
                                  //  log.info("文件 " + currentDir + File.separator + fileName +event.context().toString() + " 被"+event.kind()+"了。");
                                } else if (kind == ENTRY_MODIFY) {
                                    // 处理文件修改事件
                                   // log.info("文件 " + currentDir + File.separator + fileName+ " 被"+event.kind()+"了。");
                                }
                            }
                            // 重置 WatchKey
                            boolean valid = key.reset();
                            if (!valid) {
                                log.warn("valid not" );
                                break;
                            }
                        } catch (Exception e) {
                            log.error("", e);
                        }
                    }
                }
            }.start();
        }
    }
}
