package top.ezadmin.camunda.constants;

public enum CheckStatus {
    //0 待提交审核 1待审核 2审核不通过 3审核通过
    PREPARE(0), WAIT(1), REJECT(2), PASS(3);
    private int  i;
    CheckStatus(int i) {
        this.i=i;
    }
    public int getValue(){
        return i;
    }

}
