package top.ezadmin.plugins.express;


 import top.ezadmin.common.utils.Page;

 import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OperatorParam {
     DataSource ds;
   private Map<String, Object> listDto;

//    public EzListDTO getListDTO() {
//        return listDTO;
//    }
//
//    public void setListDTO(EzListDTO listDTO) {
//        this.listDTO = listDTO;
//    }
//
//    private EzListDTO listDTO;
    /**
     * 用于配置的搜索参数
     */
    Map<String,Object> params=new HashMap<>();
    /**
     * 用于session参数
     */
    private Map<String,Object> sessionParams=new HashMap<>();
    /**
     * 用于URL后面加特殊后缀,主要用 $,isNotBlank
     */
    private Map<String,Object> requestParams=new HashMap<>();

    private Map<String,Object> envParams=new HashMap<>();



    public Map<String, Object> getSessionParams() {
        return sessionParams;
    }

    public void setSessionParams(Map<String, Object> sessionParams) {
        this.sessionParams = sessionParams;
    }

    public Map<String, Object> getRequestParams() {
        return requestParams;
    }

    public void setRequestParams(Map<String, Object> requestParams) {
        this.requestParams = requestParams;
    }



    private List<Map<String, String>> items=new ArrayList<>();
    private Page page=new Page();

    public Map<String, Object> getParams() {
        return params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }


    public List<Map<String, String>> getItems() {
        return items;
    }

    public void setItems(List<Map<String, String>> items) {
        this.items = items;
    }


    public Page getPage() {
        return page;
    }

    public void setPage(Page page) {
        this.page = page;
    }



    public boolean isCount() {
        return count;
    }

    public void setCount(boolean count) {
        this.count = count;
    }

    private boolean count=false;



    public DataSource getDs() {
        return ds;
    }

    public void setDs(DataSource ds) {
        this.ds = ds;
    }


    public Map<String, Object> getListDto() {
        return listDto;
    }

    public void setListDto(Map<String, Object> listDto) {
        this.listDto = listDto;
    }


    public void setEnv(Map<String, Object> e) {
        envParams=e;
    }
    public Object getEnv(String k){
        return envParams.get(k);
    }
}
