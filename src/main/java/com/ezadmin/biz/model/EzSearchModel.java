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

public class EzSearchModel  {



    public static String sql(Map<String,Object> search,Map<String,Object> request) {
        StringBuilder result=new StringBuilder();
        String plugin=Utils.getStringByObject(search,JsoupUtil.PLUGIN);
        String jdbctype=Utils.getStringByObject(search,JsoupUtil.JDBCTYPE);
        String oper=Utils.getStringByObject(search,JsoupUtil.OPER);
        String name=Utils.getStringByObject(search,JsoupUtil.ITEM_NAME);
        String alias=Utils.getStringByObject(search,JsoupUtil.ALIAS);

        //普通字段
        String value=Utils.getStringByObject(request,name);
        String valueS=Utils.getStringByObject(request,name+"_START");
        String valueE=Utils.getStringByObject(request,name+"_END");

        // 多字段搜索
        String itemSearchKey=Utils.getStringByObject(request,ParamNameEnum.itemSearchKey.getName() );
        String itemSearchValue=Utils.getStringByObject(request,ParamNameEnum.itemSearchValue.getName());

        //多字段合并后搜索
        String itemSearchConcatValue=Utils.getStringByObject(request,ParamNameEnum.itemSearchConcatValue.getName());

        //多字段时间搜索
        String itemSearchDateKey=Utils.getStringByObject(request,ParamNameEnum.itemSearchDateKey.getName());
        String itemSearchDateValueStart=Utils.getStringByObject(request,ParamNameEnum.itemSearchDateValueStart.getName());
        String itemSearchDateValueEnd=Utils.getStringByObject(request,ParamNameEnum.itemSearchDateValueEnd.getName());


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
                for (int i = 0; i < ((List<Map<String,Object>>)search.get("children")).size(); i++) {
                    Map<String,Object> c=((List<Map<String,Object>>)search.get("children")).get(i);
                    String cname=Utils.getStringByObject(c,"item_name") ;
                    String calias=Utils.getStringByObject(c,"alias") ;
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
                for (int i = 0; i < ((List<Map<String,Object>>)search.get("children")).size(); i++) {
                    Map<String,Object> c=((List<Map<String,Object>>)search.get("children")).get(i);
                    String cname=Utils.getStringByObject(c,"item_name") ;
                    String calias=Utils.getStringByObject(c,"alias") ;
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
        if(plugin.equalsIgnoreCase("uniondate") ){
            //没有搜索值
            if(StringUtils.isBlank(itemSearchDateValueStart)&&StringUtils.isBlank(itemSearchDateValueEnd)){
                return "";
            }
            if(search.get("children")==null){
                return "";
            }
            //时间区间 确保选择了一项
                //找到对应key的search，然后拼接SQL
            for (int i = 0; i < ((List<Map<String,Object>>)search.get("children")).size(); i++) {
                    Map<String,Object> c=((List<Map<String,Object>>)search.get("children")).get(i);
                    String cname=Utils.getStringByObject(c,"item_name") ;
                    String calias=Utils.getStringByObject(c,"alias") ;
                    if(StringUtils.equalsIgnoreCase(cname,itemSearchDateKey)){
                        result.append(   SqlUtils.transToWhere(OperatorEnum.BETWEEN.name(),
                                cname,calias, JdbcTypeEnum.DATETIME.getName(),//默认日期时间
                                "",itemSearchDateValueStart,itemSearchDateValueEnd
                        ));
                        break;
                    }
                }

        }

        //
        //多字段搜索
        if(plugin.equalsIgnoreCase("union")&&StringUtils.isNotBlank(itemSearchConcatValue)){
            //选择了type
            if(StringUtils.isNotBlank(itemSearchKey)){
                //找到对应key的search，然后拼接SQL
                for (int i = 0; i < ((List<Map<String,Object>>)search.get("children")).size(); i++) {
                    Map<String,Object> c=((List<Map<String,Object>>)search.get("children")).get(i);
                    String cname=Utils.getStringByObject(c,"item_name") ;
                    String calias=Utils.getStringByObject(c,"alias") ;
                    if(StringUtils.equalsIgnoreCase(cname,itemSearchKey)){
                        result.append( SqlUtils.like2(" and ",calias,cname,
                                OperatorEnum.LIKE , itemSearchConcatValue ) );
                        break;
                    }
                }
            }
            else{ //没选择type
                result.append(" and ( 1=1 ");
                Set<String> nameSet=new HashSet<>();
                String[] ar=name.toUpperCase().split(",");
                Collections.addAll(Arrays.asList(ar));
                String concat=concat_ws(((List<Map<String,Object>>)search.get("children")));
                String[] values=StringUtils.split(StringUtils.replaceToSearch(itemSearchConcatValue),"%");
                for (int i = 0; i < values.length; i++) {
                    result.append(" and "+ concat+" like concat('%','"+values[i]+"','%')");
                }
                result.append(" ) ");
            }

        }
        return result.toString();
    }

    private static String concat_ws(List<Map<String,Object>> list){
        StringBuilder sb=new StringBuilder();
        sb.append(" concat_ws(' '");
        for (int i = 0; i < list.size(); i++) {
            Map<String,Object> c=list.get(i);
            String cname=Utils.getStringByObject(c,"item_name") ;
            String calias=Utils.getStringByObject(c,"alias") ;

            sb.append(","+SqlUtils.alias(calias,cname));
        } ;
        sb.append(")");
        return sb.toString();
    }




}
