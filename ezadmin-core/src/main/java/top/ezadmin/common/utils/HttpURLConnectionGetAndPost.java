package top.ezadmin.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpURLConnectionGetAndPost {
   static Logger logger= LoggerFactory.getLogger(HttpURLConnectionGetAndPost.class);

    public static String doGet(String getUrl) throws IOException {
        InputStream in =null;
        BufferedReader bufferReader=null;
        HttpURLConnection httpURLConnection=null;
        try {
            URL url = new URL(getUrl);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.connect();
            httpURLConnection.setReadTimeout(10000);

                StringBuffer sb = new StringBuffer();
                in = httpURLConnection.getInputStream();
                bufferReader = new BufferedReader(new InputStreamReader(in));
                String readLine = "";

                while ((readLine = bufferReader.readLine()) != null) {
                    sb.append(readLine);
                }
                in.close();
                bufferReader.close();
                httpURLConnection.disconnect();

                return sb.toString();
        }catch (Exception e){
            logger.error("",e);
        }finally {
            try {
                in.close();
            }catch (Exception e){}try {
            bufferReader.close();
            }catch (Exception e){}try {
            httpURLConnection.disconnect();
            }catch (Exception e){}
        }
        return "error";
    }


    public static String doPost(String posturl, String s,int timeout) throws IOException {
        InputStream in =null;
        BufferedReader bufferReader=null;
        DataOutputStream dataOutputStream=null;
        HttpURLConnection httpURLConnection=null;
        try{
            URL url = new URL(posturl  );
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setDoInput(true);
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setReadTimeout(timeout);
            httpURLConnection.setConnectTimeout(timeout);
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setUseCaches(false);
            httpURLConnection.setRequestProperty("content-type", "");
             httpURLConnection.connect();
            dataOutputStream = new DataOutputStream(httpURLConnection.getOutputStream());

            String content =  s;

            dataOutputStream.writeBytes(content);
            dataOutputStream.flush();


            if (httpURLConnection.getResponseCode() == 200) {
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String readLine = "";
                StringBuffer sb = new StringBuffer();
                while ((readLine = bufferedReader.readLine()) != null) {
                    sb.append(readLine);
                }
                return sb.toString();
            }
        }catch (Exception e){
            logger.error("",e);
        }finally {
            try {
                in.close();
            }catch (Exception e){}try {
                bufferReader.close();
            }catch (Exception e){}try {
                httpURLConnection.disconnect();
            }catch (Exception e){}
            try {
                dataOutputStream.close();
            }catch (Exception e){}
        }
        return "error";
    }
}