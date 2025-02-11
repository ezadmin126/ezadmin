/**
 *    Copyright 2009-2016 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package top.ezadmin.plugins.parser;

import top.ezadmin.plugins.parser.parse.GenericTokenParser;
import top.ezadmin.plugins.parser.parse.Params;
import top.ezadmin.plugins.parser.parse.ResultModel;
import top.ezadmin.plugins.parser.parse.TokenHandler;
import top.ezadmin.common.utils.StringUtils;

import java.util.Map;

/**
 * @author Clinton Begin
 * @author Kazuki Shimizu
 */
public class MapParser {


  private MapParser() {
    // Prevent Instantiation

  }
  private static String prefix="${";
  private static String sufix="}";
  private static String DEFULT_EMPTY_KEY="ResultModel.DEFAULT_EMPTY";



  public static ResultModel parse(String string, Map<String,Object> variables) {
    ResultModel model=new ResultModel();
    if(variables==null|| variables.isEmpty()){
      model.setResult(string);
      return model;
    }

    VariableTokenHandler handler = new VariableTokenHandler(model,variables);
    GenericTokenParser parser = new GenericTokenParser(prefix, sufix, handler);
    String r= parser.parse(string);
    model.setResult(r);
    return model;
  }


  public static ResultModel parseDefaultEmpty(String string, Map<String,Object> variables) {
    ResultModel model2=new ResultModel();
    if(variables==null|| variables.isEmpty()){
      model2.setResult(string);
      return model2;
    }
    if(!StringUtils.contains(string,sufix)){
      model2.setResult(string);
        return model2;
    }

    try {
      variables.put(DEFULT_EMPTY_KEY, true);
    }catch (Exception e){//
      //ignor
        }
    return parse(string,variables);
  }

  private static class VariableTokenHandler implements TokenHandler {
    private final Map<String,Object> variables;
    ResultModel model;

    private VariableTokenHandler(ResultModel model,Map<String,Object> variables) {
      this.variables = variables;
      this.model=model;
    }


    @Override
    public String handleToken(String content) {

      if(StringUtils.isBlank(content)){
        return "";
      }
      String k=content,v="";
      int s=content.indexOf(":");
      if(s>0){
        k=content.substring(0,s);
        v=content.substring(s+1);
      }
      if (variables.containsKey(k)) {
        Params p=new Params();
        p.setParamValue(variables.get(k));
        model.addParam(p);
        return variables.get(k).toString();
      }
      if(StringUtils.isNotBlank(v)){
        Params p=new Params();
        p.setParamValue(v);
        model.addParam(p);
        return v;
      }
      if(variables.containsKey(DEFULT_EMPTY_KEY)||s>0){
        return "";
      }
      return prefix+content+sufix;
    }

  }

}
