package top.ezadmin.controller;


import com.aliyun.oss.OSS;
import com.aliyun.oss.model.OSSObject;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import top.ezadmin.EzClientProperties;
import top.ezadmin.blog.constants.Nologin;
import top.ezadmin.blog.constants.SuffixType;
import top.ezadmin.common.utils.JSONUtils;
import top.ezadmin.common.utils.Resources;
import top.ezadmin.common.utils.StringUtils;
import top.ezadmin.common.utils.Utils;
import top.ezadmin.dao.Dao;
import top.ezadmin.domain.mapper.CoreFileMapper;
import top.ezadmin.domain.model.CoreFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/core")
public class DownloadController {
    Logger logger = LoggerFactory.getLogger(DownloadController.class);

    @Value("${system.ossBucketName:}")
    private String ossBucketName;
    @Autowired
    EzClientProperties ezAdminProperties;

    private String uploadType="OSS";


    @Autowired
    @Qualifier("dataSource")
    DataSource dataSource;


    @Autowired
    OSS ossClient;
    @RequestMapping(value = "/downloadDesc-{fileId}.jpg")
    @Nologin
    public void downloadStatic(Model model, String fileCode, @PathVariable("fileId") Long fileId, HttpServletRequest request, HttpServletResponse response) throws Exception {

        downloadDesc(model,"",fileId,request,response);
    }
    //
    @RequestMapping(value = "/downloadDesc.html")
    @Nologin
    public void downloadDesc(Model model, String fileCode, Long fileId, HttpServletRequest request, HttpServletResponse response) throws Exception {
         try {
             response.setHeader("Access-Control-Allow-Origin","*");
             response.setHeader("Cache-Controll","max-age="+6*30*24*60*60);

             Calendar c=Calendar.getInstance();
             c.add(Calendar.MONTH,6);
             response.setDateHeader("Expires",c.getTime().getTime());
             if(fileId==null){
                 OutputStream outputStream = response.getOutputStream();
                 response.setHeader("Access-Control-Allow-Origin","*");
                 response.setContentType(SuffixType.getContentType(Utils.trimNull("jpeg")).getMime());
                 IOUtils.copy(Resources.getResourceAsStream("static/images/error.jpeg"), outputStream);
                 return  ;

             }
            if(StringUtils.equals(uploadType,"OSS")&&fileId>0){
                 downloadOSS(fileCode, fileId, response );
            }else{
//                if(fileId.contains(",")){
//                    fileId=fileId.split(",")[0];
//                }
                downloadLocal(fileCode, fileId, response );
            }
            return;
        } catch (Exception e) {
            logger.error(""+fileCode+fileId );
             OutputStream outputStream = response.getOutputStream();
             response.setHeader("Access-Control-Allow-Origin","*");
             response.setContentType(SuffixType.getContentType(Utils.trimNull("jpeg")).getMime());
             IOUtils.copy(Resources.getResourceAsStream("static/images/error.svg"), outputStream);
        } finally {
            response.getOutputStream().flush();
            response.getOutputStream().close();
        }
    }
    @Autowired
    CoreFileMapper coreFileMapper;
    private boolean downloadOSS(String fileCode, Long fileId, HttpServletResponse response ) throws Exception {

        CoreFile coreFile=coreFileMapper.selectByPrimaryKey(fileId);
        if (coreFile==null) {
            OutputStream outputStream = response.getOutputStream();
            response.setHeader("Access-Control-Allow-Origin","*");
            response.setContentType(SuffixType.getContentType(Utils.trimNull("jpeg")).getMime());
            IOUtils.copy(Resources.getResourceAsStream("static/images/error.svg"), outputStream);
            return true;
        }
//        String uploadFold = DateFormatUtils.format(new Date(), "yyyyMMddHH");
//
//        String headStr = "attachment; filename=\"" + new String((uploadFold + Utils.trimNull(existFile.get("FILE_NAME"))).getBytes(), "iso8859-1") + "\"";

        response.setContentType(SuffixType.getContentType(coreFile.getFileSuffix())
                .getMime());
        response.setHeader("Access-Control-Allow-Origin","*");
        //response.setContentType("application/x-download");
        // response.setHeader("Content-Disposition", headStr);

        OSSObject ossObject = null;
        InputStream inputStream=null;
        try {
            if(coreFile==null||StringUtils.isBlank(coreFile.getOssName())){
                return false;
            }
            ossObject=ossClient.getObject(ossBucketName, coreFile.getOssName());
            inputStream = ossObject.getObjectContent();
            IOUtils.copy(inputStream, response.getOutputStream());
        }catch (Exception e){
              logger.error("",e);
        }finally {
            ossObject.close();
            inputStream.close();
        }
        return false;
    }
    private boolean downloadLocal(String fileCode, Long fileId, HttpServletResponse response ) throws Exception {
        OutputStream outputStream = response.getOutputStream();
        Map<String, Object> existFile = Dao.getInstance().executeQueryOne(dataSource, "select FILE_SUFFIX,file_domain,file_id,file_name from T_CORE_FILE" +
                " where file_id=?   ", new Object[]{fileId});
        if (!Utils.isNotEmpty(existFile)) {
            response.setCharacterEncoding("UTF-8");
            response.setContentType("text/html;charset=UTF-8");
            Map<String, String> map = new HashMap<>();
            map.put("code", "404");
            map.put("message", "下载异常，" + fileId + fileCode);
            outputStream.write(JSONUtils.toJSONString(map).getBytes());
            return true;
        }
        String uploadFold = DateFormatUtils.format(new Date(), "yyyyMMddHH");
        response.setHeader("Access-Control-Allow-Origin","*");
        String headStr = "attachment; filename=\"" + new String((uploadFold + Utils.trimEmptyDefault(existFile.get("FILE_NAME"),System.currentTimeMillis()+"")).getBytes(), "iso8859-1") + "\"";
        response.setContentType(SuffixType.getContentType(Utils.trimNull(existFile.get("FILE_SUFFIX"))).getMime());
       // response.setHeader("Content-Disposition", headStr);
        Files.copy(Paths.get(ezAdminProperties.getUploadPath() + File.separator + Utils.trimNull(existFile.get("FILE_DOMAIN")) + File.separator + fileId), outputStream);
        return false;
    }
}
