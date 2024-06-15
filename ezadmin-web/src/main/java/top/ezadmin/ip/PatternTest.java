package top.ezadmin.ip;

import java.util.regex.Pattern;

public class PatternTest {
    public static void main(String[] args) {
        String reg="/(nodejs-vedeng-(pc|m))/(zhaobiao|baike|qixie|details|quicksearch|departments|examinations|tag|doc|data|tender|c-|p/|b-).*";
        Pattern pattern=Pattern.compile(reg);
        System.out.println(pattern.matcher("/nodejs-vedeng-pc/zhaobiao/1.html").matches());
        System.out.println(pattern.matcher("/nodejs-vedeng-pc/zhaobiao/details-1.html").matches());
        System.out.println(pattern.matcher("/nodejs-vedeng-pc/c-1.html").matches());

    }
}
