package top.ezadmin.controller;


import cn.hutool.crypto.digest.DigestUtil;

import com.aliyun.oss.OSS;
import com.aliyun.oss.model.OSSObject;
import com.aliyun.oss.model.PutObjectRequest;

import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.lang.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;
import top.ezadmin.EzClientProperties;
import top.ezadmin.blog.constants.Nologin;
import top.ezadmin.blog.constants.SuffixType;
import top.ezadmin.blog.model.UploadDTO;
import top.ezadmin.common.utils.JSONUtils;
import top.ezadmin.common.utils.StringUtils;
import top.ezadmin.dao.Dao;
import top.ezadmin.domain.mapper.CoreFileMapper;
import top.ezadmin.domain.model.CoreFile;
import top.ezadmin.domain.model.CoreFileExample;
import top.ezadmin.web.EzResult;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * : AdminController
 * @author Hank.he
 * @since: 2022/3/4 21:49
 */
@Controller
@RequestMapping("/system")
public class UploadController {
    Logger logger = LoggerFactory.getLogger(UploadController.class);

    @Autowired
    EzClientProperties ezAdminProperties;

    @Autowired
    @Qualifier("dataSource")
    DataSource dataSource;

    private String uploadType="OSS";

    @Value("${system.ossBucketName:}")
    private String ossBucketName;
    @Value("${system.ossEndpoint:}")
    private String ossEndpoint;
    @Value("${system.imgEndpoint:}")
    private String imgEndpoint;
    @Value("${system.ossAccessKeyId:}")
    private String ossAccessKeyId;
    @Value("${system.ossAccessKeySecret:}")
    private String ossAccessKeySecret;


    @Resource
    OSS ossClient;
    @RequestMapping(value = {"/fileheight.html"})
    @ResponseBody
    @Nologin
    public void size(Long fileId,HttpServletResponse response) throws IOException {

        EzResult.instance().data(coreFileMapper.selectByPrimaryKey(fileId).getFileHeight())
                .printJSONUtils(response);
    }
    @RequestMapping(value = {"/filedelete.html"})
    @ResponseBody
    public void filedelete(Long fileId,HttpServletResponse response) throws IOException {
        CoreFile file=new CoreFile();
        file.setFileId(fileId);
        file.setDeleteFlag(1);
        file.setUpdateTime(new Date());
        coreFileMapper.updateByPrimaryKeySelective(file);
        EzResult.instance().data(0) .printJSONUtils(response);
    }
    @RequestMapping(value = {"/upload.html", "/uploadDesc.html"})
    @ResponseBody
    @Nologin
    public String upload(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> map = new HashMap<>();
        String data=request.getParameter("data");
        response.setContentType("application/json;charset=utf-8");
        MultiValueMap<String, MultipartFile> files = null;
        if(StringUtils.startsWith(data,"data:image")){
            MultipartFile file= new MultipartFile() {
                @Override
                public String getName() {
                    return "file";
                }

                @Override
                public String getOriginalFilename() {
                    return "file.png";
                }

                @Override
                public String getContentType() {
                    return SuffixType.png.getSuffix();
                }

                @Override
                public boolean isEmpty() {
                    return false;
                }

                @Override
                public long getSize() {
                    return 0;
                }

                @Override
                public byte[] getBytes() throws IOException {
                    String data1=data.replace("data:image/png;base64,","");
                    return   Base64Utils.decodeFromString(data1);
                }

                @Override
                public InputStream getInputStream() throws IOException {
                    String data1=data.replace("data:image/png;base64,","");
                    return new ByteArrayInputStream( Base64Utils.decodeFromString(data1));
                }

                @Override
                public void transferTo(File dest) throws IOException, IllegalStateException {
                    FileCopyUtils.copy(this.getInputStream(), Files.newOutputStream(dest.toPath()));
                }
            };
            files=new LinkedMultiValueMap<>();
            files.add("file",file);
        }else {
            files = ((StandardMultipartHttpServletRequest) request).getMultiFileMap();
        }
        if (files == null || files.size() == 0) {
            map.put("errno", 404);
            map.put("message", "文件为空");
            return JSONUtils.toJSONString(map);
        }
        List<UploadDTO> uploads = new ArrayList<>();
        files.forEach((k, v) -> {
            MultipartFile file = v.get(0);
            try {
                UploadDTO vo =null;
                if(StringUtils.equals(uploadType,"OSS")){

                    vo=uploadOSS("",file);

                }else{

                    vo=upload("",file);


                }
                uploads.add(vo);
            } catch (Exception e) {
                logger.error("", e);
            }
        });
        if (request.getRequestURI().startsWith("/system/uploadDesc")&&uploads.size()>0
                ||StringUtils.equals(request.getParameter("descFlag"),"1")
        ) {
            // List<String> urls = uploads.stream().map(UploadVO::getFileUrl).collect(Collectors.toList());
            map.put("errno", 0);
            Map<String,String> dataMap=new HashMap<>();
            dataMap.put("url",uploads.get(0).getFileUrl());
            dataMap.put("src",uploads.get(0).getFileUrl());

            map.put("data", dataMap);
            map.put("location", uploads.get(0).getFileUrl());
            return JSONUtils.toJSONString(map);
        }

        return JSONUtils.toJSONString(EzResult.instance().code("0").data(uploads));
    }
    @Resource
    CoreFileMapper coreFileMapper;

    public UploadDTO uploadOSS(String host, MultipartFile file1) throws Exception {
        CoreFile file= upload(file1.getOriginalFilename(),file1.getBytes(), ORI_FILE);
        UploadDTO uploadDTO=new UploadDTO();
        uploadDTO.setWidth(file.getFileWidth()+"");
        uploadDTO.setHeight(file.getFileHeight()+"");
        uploadDTO.setFileUrl(getUrlById(host, file.getFileId() + "" ));
        uploadDTO.setFileId(file.getFileId() + "");
        uploadDTO.setFileName(file1.getOriginalFilename());
        return uploadDTO;
    }

    public UploadDTO upload(String host, MultipartFile file1) throws Exception {


        String fileName = file1.getOriginalFilename();

        String uploadFold = DateFormatUtils.format(new Date(), "yyyyMMdd");
        Long id = Dao.getInstance().executeUpdate(dataSource, "insert into T_CORE_FILE (FILE_NAME,  " +
                "FILE_DOMAIN,add_time,FILE_SUFFIX)" +
                "VALUES(?,?,?,now(),?) ", new Object[]{file1.getOriginalFilename(), uploadFold,suffix(file1.getOriginalFilename())});
        UploadDTO uploadDTO = new UploadDTO();
        uploadDTO.setFileUrl(getUrlById(host,id + "" ));
        uploadDTO.setFileId(id + "");
        uploadDTO.setFileName(fileName);
        Path uploadFile = Paths.get(ezAdminProperties.getUploadPath() + File.separator + uploadFold + File.separator + id);
        Path pathFold = Paths.get(ezAdminProperties.getUploadPath() + File.separator + uploadFold);
        if (Files.notExists(pathFold)) {
            Files.createDirectories(pathFold);
        }
        if (Files.notExists(uploadFile)) {
            Files.createFile(uploadFile);
        }
        file1.transferTo(uploadFile);
        //不存在
        Dao.getInstance().executeUpdate(dataSource,
                "update T_CORE_FILE set " +
                        " FILE_URL=? " +
                        " WHERE FILE_ID=? ", new Object[]{uploadDTO.getFileUrl(), id});
        return uploadDTO;

    }

    public static String getUrlById(String host,String id ) {
        return host+"/core/downloadDesc.html?fileId=" + id ;
    }


    public CoreFile selectByMd5(String md5){
        CoreFileExample fileExample=new CoreFileExample();
        fileExample.createCriteria().andFileMd5EqualTo(md5).andDeleteFlagEqualTo(0);
        List<CoreFile> coreFiles=coreFileMapper.selectByExample(fileExample);
        if(!CollectionUtils.isEmpty(coreFiles)){
            return coreFiles.get(0);
        }
        return null;
    }
    public CoreFile selectByParent(Long parentId, String fileType){
        CoreFileExample fileExample=new CoreFileExample();
        fileExample.createCriteria().andParentFileIdEqualTo(parentId).andFileTypeEqualTo(fileType)
                .andDeleteFlagEqualTo(0);
        List<CoreFile> coreFiles=coreFileMapper.selectByExample(fileExample);
        if(!CollectionUtils.isEmpty(coreFiles)){
            return coreFiles.get(0);
        }
        return null;
    }
    public static String ORI_FILE="0";
    public static String ORI_TM_FILE="1";
    public static String ORI_YS_TYPE="2";
    public static String ORI_YS_TM_TYPE="3";

    public CoreFile upload(String fileName,byte[] bs,String fileType){
        String md5code= DigestUtil.md5Hex(bs);
        CoreFile coreFile=selectByMd5(md5code);
        if(coreFile!=null){
            return coreFile;
        }else{
            coreFile=new CoreFile();
            coreFile.setAddTime(new Date());
            coreFile.setFileName(fileName);
            coreFile.setFileUrl(imgEndpoint);
            coreFile.setFileType(fileType);
            coreFile.setOssId(md5code);
            coreFile.setDeleteFlag(0);
            coreFile.setFileMd5(md5code);
            coreFile.setOssName(md5code+suffix(fileName));
            coreFile.setFileSuffix(suffix(fileName));
            coreFile.setFileSize(Long.valueOf(bs.length));
            //上传到OSS
            try {
                // 创建PutObjectRequest对象。
                ByteArrayInputStream inputStream1=new ByteArrayInputStream(bs);
                PutObjectRequest putObjectRequest = new PutObjectRequest(ossBucketName, coreFile.getOssName(),inputStream1);
                // 上传字符串。
                ossClient.putObject(putObjectRequest);
                if(coreFile.getFileSuffix().toUpperCase().endsWith("PNG")||coreFile.getFileSuffix().toUpperCase().endsWith("JPG")){
                    ByteArrayInputStream inputStream2=new ByteArrayInputStream(bs);
                    BufferedImage image = ImageIO.read(inputStream2);
                    coreFile.setFileWidth(image.getWidth());
                    coreFile.setFileHeight(image.getHeight());
                }
                coreFileMapper.insert(coreFile);
            } catch (Exception ce) {
                logger.error("",ce);
            }
            return coreFile;
        }
    }
    private static String suffix(String  args) {
        if(StringUtils.isBlank(args)||args.lastIndexOf(".")<0){
            return "";
        }
        return args.substring(args.lastIndexOf("."));
    }

    public CoreFile thumbnailFile(Long fileId,int height) throws  Exception {
        CoreFile thb=selectByParent(fileId, ORI_YS_TYPE);
        if(thb!=null){
            return thb;
        }
        //删除其他尺寸
        CoreFileExample example=new CoreFileExample();
        example.createCriteria().andParentFileIdEqualTo(fileId).andFileTypeEqualTo(ORI_YS_TYPE);
        CoreFile record=new CoreFile();
        record.setDeleteFlag(1);
        coreFileMapper.updateByExampleSelective(record,example);
        //原文件
        CoreFile file = coreFileMapper.selectByPrimaryKey(fileId);

        OSSObject ossObject=ossClient.getObject(ossBucketName, file.getOssName());

        ByteArrayOutputStream out=new ByteArrayOutputStream();
        Thumbnails.of(ossObject.getObjectContent()).height(height).outputQuality(1).toOutputStream(out);

        CoreFile thbfile=  upload(file.getFileName(),out.toByteArray(),ORI_YS_TYPE);
        CoreFile upfile=new CoreFile();
        upfile.setFileId(thbfile.getFileId());
        upfile.setParentFileId(file.getFileId());
        coreFileMapper.updateByPrimaryKeySelective(upfile);
        thbfile.setParentFileId(file.getFileId());
        return thbfile;
    }

}
