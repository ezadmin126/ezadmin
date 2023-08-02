package com.ezadmin.biz.model;

import com.ezadmin.biz.emmber.list.SearchVO;
import com.ezadmin.common.enums.JdbcTypeEnum;
import com.ezadmin.common.enums.OperatorEnum;
import com.ezadmin.common.enums.ParamNameEnum;
import com.ezadmin.common.utils.JsoupUtil;
import com.ezadmin.common.utils.SqlUtils;
import com.ezadmin.common.utils.StringUtils;
import com.ezadmin.common.utils.Utils;

import java.util.*;

public class EzSearchModel extends  EzModel{

   private String sql;
   private String html;
   private String tableHtml;

    public String sql() {
        StringBuilder result=new StringBuilder();
        String plugin=getConfig(JsoupUtil.PLUGIN);
        String jdbctype=getConfig(JsoupUtil.JDBCTYPE);
        String oper=getConfig(JsoupUtil.OPER);
        String name=getConfig(JsoupUtil.ITEM_NAME);
        String alias=getConfig(JsoupUtil.ALIAS);

        //普通字段
        String value=getParam(name);
        String valueS=getParam(name+"_START");
        String valueE=getParam(name+"_END");

        // 多字段搜索
        String itemSearchKey=getParam(ParamNameEnum.itemSearchKey.getName() );
        String itemSearchValue=getParam(ParamNameEnum.itemSearchValue.getName());

        //多字段合并后搜索
        String itemSearchConcatValue=getParam(ParamNameEnum.itemSearchConcatValue.getName());

        //多字段时间搜索
        String itemSearchDateValueStart=getParam(ParamNameEnum.itemSearchDateValueStart.getName());
        String itemSearchDateValueEnd=getParam(ParamNameEnum.itemSearchDateValueEnd.getName());


        //常规
        if(!plugin.equalsIgnoreCase("hidden-nowhere")&&
                !jdbctype.equalsIgnoreCase("BODY")){
            //不拼接where
            result.append( SqlUtils.transToWhere(oper,name,alias,jdbctype,value,valueS,valueE));
        }
        //多字段搜索
        if(plugin.equalsIgnoreCase("unionor")&&StringUtils.isNotBlank(itemSearchValue)){
                //选择了type
            if(StringUtils.isNotBlank(itemSearchKey)){
                //找到对应key的search，然后拼接SQL
                for (int i = 0; i < getChildren().size(); i++) {
                    EzModel c=getChildren().get(i);
                    String cname=c.getConfig("item_name");
                    String calias=c.getConfig("alias");
                    if(StringUtils.equalsIgnoreCase(cname,itemSearchKey)){
                        result.append( SqlUtils.like2(" and ",calias,cname,
                                OperatorEnum.LIKE , itemSearchValue ) );
                        break;
                    }
                }
            }
            else{ //没选择type
                result.append(" and ( 1=2 ");
                Set<String> nameSet=new HashSet<>();
                String[] ar=name.toUpperCase().split(",");
                Collections.addAll(Arrays.asList(ar));
                for (int i = 0; i < getChildren().size(); i++) {
                    EzModel c=getChildren().get(i);
                    String cname=c.getConfig("item_name").toUpperCase();
                    String calias=c.getConfig("alias");
                    if(nameSet.contains(cname)){
                        result.append( SqlUtils.like2(" or ",calias,cname,
                                OperatorEnum.LIKE , itemSearchValue ) );
                        break;
                    }
                }
                result.append(" ) ");

            }

        }
        //多字段时间搜索
        if(plugin.equalsIgnoreCase("uniondate")&&StringUtils.isNotBlank(itemSearchValue)){
                //选择了type
            if(StringUtils.isNotBlank(itemSearchKey)){
                //找到对应key的search，然后拼接SQL
                for (int i = 0; i < getChildren().size(); i++) {
                    EzModel c=getChildren().get(i);
                    String cname=c.getConfig("item_name");
                    String calias=c.getConfig("alias");
                    if(StringUtils.equalsIgnoreCase(cname,itemSearchKey)){
                        result.append(   SqlUtils.transToWhere(OperatorEnum.BETWEEN.name(),
                                cname,calias, JdbcTypeEnum.DATETIME.getName(),//默认日期时间
                                "",itemSearchDateValueStart,itemSearchDateValueEnd
                        ));
                        break;
                    }
                }
            }
        }

        //
        //多字段搜索
        if(plugin.equalsIgnoreCase("union")&&StringUtils.isNotBlank(itemSearchConcatValue)){
            //选择了type
            if(StringUtils.isNotBlank(itemSearchKey)){
                //找到对应key的search，然后拼接SQL
                for (int i = 0; i < getChildren().size(); i++) {
                    EzModel c=getChildren().get(i);
                    String cname=c.getConfig("item_name");
                    String calias=c.getConfig("alias");
                    if(StringUtils.equalsIgnoreCase(cname,itemSearchKey)){
                        result.append( SqlUtils.like2(" and ",calias,cname,
                                OperatorEnum.LIKE , itemSearchValue ) );
                        break;
                    }
                }
            }
            else{ //没选择type
                result.append(" and ( 1=1 ");
                Set<String> nameSet=new HashSet<>();
                String[] ar=name.toUpperCase().split(",");
                Collections.addAll(Arrays.asList(ar));
                String concat=concat_ws(getChildren());
                String[] values=StringUtils.split(StringUtils.replaceToSearch(itemSearchConcatValue),"%");
                for (int i = 0; i < values.length; i++) {
                    result.append(" and "+ concat+" like concat('%','"+values[i]+"','%')");
                }
                result.append(" ) ");
            }

        }
        return result.toString();
    }

    private static String concat_ws(List<EzModel> list){
        StringBuilder sb=new StringBuilder();
        sb.append(" concat_ws(' '");
        for (int i = 0; i < list.size(); i++) {
            EzModel c=list.get(i);
            String cname=c.getConfig("item_name");
            String calias=c.getConfig("alias");

            sb.append(","+SqlUtils.alias(calias,cname));
        } ;
        sb.append(")");
        return sb.toString();
    }



    public String html() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
    }

    public String getTableHtml() {
        return tableHtml;
    }

    public void setTableHtml(String tableHtml) {
        this.tableHtml = tableHtml;
    }
}
