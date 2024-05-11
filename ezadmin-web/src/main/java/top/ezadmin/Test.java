package top.ezadmin;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Test {

    public static void main(String[] args) throws IOException {
        String x="D:\\ws\\github\\ezadmin\\ezadmin-web\\src\\main\\resources\\1.txt";
        Path p= Paths.get(x);
        for (String readAllLine : Files.readAllLines(p)) {
            try{
                URL a=new URL("https://"+readAllLine+"/actuator");
                HttpURLConnection connection = (HttpURLConnection) a.openConnection();
                System.out.println(readAllLine+"\t"+connection.getResponseCode());
            }catch (Exception e){
                System.out.println(readAllLine+"\t无法联机");
            }
        }
    }
}
