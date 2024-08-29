package top.ezadmin.blog.service;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import top.ezadmin.common.utils.NumberUtils;
import top.ezadmin.common.utils.StringUtils;
import top.ezadmin.domain.mapper.BlogCategoryMapper;
import top.ezadmin.domain.mapper.BlogMapper;
import top.ezadmin.domain.mapper.BlogMessageMapper;
import top.ezadmin.domain.mapper.ext.BlogExtendMapper;
import top.ezadmin.blog.utils.UrlTool;
import top.ezadmin.blog.vo.BlogCategoryVO;
import top.ezadmin.blog.vo.BlogMessageVO;
import top.ezadmin.blog.vo.BlogVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import top.ezadmin.domain.model.*;
import top.ezadmin.web.SpringContextHolder;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BlogService {

    @Resource
    BlogMapper blogMapper;
    @Resource
    BlogCategoryMapper blogCategoryMapper;
    @Resource
    BlogMessageMapper blogMessageMapper;
    @Resource
    BlogExtendMapper blogExtendMapper;


    public List<BlogVO> list( String keyword,Integer categoryId){
        keyword= StringUtils.upperCase(keyword);
        List<BlogVO> list=  blogExtendMapper.search(keyword,categoryId) ;
//        list.forEach(item->{
//            item.setBlogUrl(UrlTool.blogDetail(item.getBlogId()));
//            item.setCategoryUrl(UrlTool.blogSearch(item.getCategoryId(),1));
//        });
        fillBlog(list);
        return list;
    }
    public List<Blog> all(){
        BlogExample example=new BlogExample();
        example.createCriteria().andDeleteFlagEqualTo(new Byte("0")).andStatusEqualTo("1");
        example.setOrderByClause(" BLOG_ID DESC");
        List<Blog> list=  blogMapper.selectByExample(example) ;
        return list;
    }



    public BlogVO detail(Integer blogId) {
        Blog blog= blogMapper.selectByPrimaryKey(blogId);
        BlogVO vo=new BlogVO();
        vo.setBlogUrl(UrlTool.blogDetail(blog.getBlogId()));
        vo.setCategoryName(blogCategoryMapper.selectByPrimaryKey(blog.getCategoryId()).getCategoryName());
        vo.setCategoryUrl(UrlTool.blogSearch(blog.getCategoryId(),1));
        BeanUtils.copyProperties(blog,vo);
        return vo;
    }


    @Transactional
    public void a(){
        SpringContextHolder.getBean(this.getClass()).b();
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void b(){

    }



    @Transactional
    public void addView(Integer blogId,Integer times) {
        Blog blog= new Blog();
        blog.setBlogId(blogId);
        blog.setViewTimes(times==null?1:times+1);
        blogMapper.updateByPrimaryKeySelective(blog);


    }


    public List<BlogMessageVO> listMessage(Integer blogId ) {
        BlogMessageExample example=new BlogMessageExample();
        example.createCriteria().andBlogIdEqualTo(blogId);
        List<BlogMessage> blogMessageVOList= blogMessageMapper.selectByExample(example);
        return transMessage(blogMessageVOList);
    }

    private List<BlogMessageVO> transMessage(List<BlogMessage> blogMessageVOList) {
        List<BlogMessageVO> list=blogMessageVOList.stream()
                .map(item->{
                    BlogMessageVO vo=new BlogMessageVO();
                    BeanUtils.copyProperties(item,vo);
                    return vo;
                })
                .collect(Collectors.toList()) ;
        return list;
    }

    public List<BlogVO> hottest() {

        List<BlogVO> list=  blogExtendMapper.hottest() ;
        fillBlog(list);
        return list;
    }

    public List<BlogVO> newtest() {
        List<BlogVO> list=  blogExtendMapper.newtest() ;
        fillBlog(list);
        return list;
    }

    public List<BlogCategoryVO> categorys() {
        List<BlogCategory> list=blogCategoryMapper.selectByExample(null);
        List<BlogCategoryVO> list2=new ArrayList<>();
        list.forEach(item->{
            BlogCategoryVO vo=new BlogCategoryVO();
            BeanUtils.copyProperties(item,vo);
            vo.setCategoryUrl(UrlTool.blogSearch(item.getCategoryId(),1));
            list2.add(vo);
        });
        return list2;
    }

    private void fillBlog(List<BlogVO> list){
        list.forEach(item->{
            item.setBlogUrl(UrlTool.blogDetail(item.getBlogId()));
            item.setCategoryUrl(UrlTool.blogSearch(item.getCategoryId(),1));
        });
    }

    public void addMessage(BlogMessage blog) {
        blogMessageMapper.insert(blog);
    }
}
