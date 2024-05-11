package top.ezadmin.common;

import top.ezadmin.common.enums.ExceptionCode;

public class EzAdminRuntimeException extends RuntimeException  {
    private String code;
    private String message;
    private EzAdminRuntimeException(){
        super();
    }
    public EzAdminRuntimeException(String code,String message,  Throwable cause){
        super(message,cause);
        this.code=code;
    }
    public EzAdminRuntimeException(ExceptionCode c, Throwable cause){
        super(c.message(),cause);
        this.code=c.name();
    }
    public String code(){
        return code;
    }
    @Override
    public String toString() {
        return code+super.toString();
    }
}
