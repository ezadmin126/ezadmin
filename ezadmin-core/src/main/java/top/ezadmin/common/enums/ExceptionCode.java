package top.ezadmin.common.enums;

public enum ExceptionCode {

        QLBIZ("业务代码错误"),QLERROR("表达式错误"),
        DATASOURCE_IS_NULL("数据源为空");
        private String ms;
        private ExceptionCode(String m){
            ms=m;
        }

        public String message() {
                return ms;
        }
}