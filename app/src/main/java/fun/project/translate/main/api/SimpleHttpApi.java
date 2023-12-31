package fun.project.translate.main.api;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

public class SimpleHttpApi {
    public static SimpleHttpApi newRequest(){
        return new SimpleHttpApi();
    }
    private SimpleHttpApi(){

    }
    private HashMap<String,String> headers = new HashMap<>();
    private String url;
    private boolean isJsonBody;
    private HashMap<String,String> IBody = new HashMap<>();
    private String binaryText;
    public void header(String name,String value){
        headers.put(name,value);
    }
    public void url(String url){
        this.url = url;
    }
    public void jsonBody(String key, String value){
        IBody.put(key, value);
        isJsonBody = true;
    }
    public void kvBody(String key,String value){
        IBody.put(key,value);
        isJsonBody = false;
    }
    public void binaryText(String text){
        this.binaryText = text;
    }
    private byte[] generateBody(){
        try {
            if (isJsonBody){
                JSONObject body = new JSONObject();
                for (String key : IBody.keySet()){
                    body.put(key,IBody.get(key));
                }
                return body.toString().getBytes(StandardCharsets.UTF_8);
            }else {
                StringBuilder bodyText = new StringBuilder();
                for (String key : IBody.keySet()){
                    bodyText.append(key).append("=").append(URLEncoder.encode(IBody.get(key),"UTF-8")).append("&");
                }
                return bodyText.toString().getBytes(StandardCharsets.UTF_8);
            }
        }catch (Exception e){
            return new byte[0];
        }

    }
    public String post(){
        try {
            URL u = new URL(this.url);
            HttpURLConnection connection = (HttpURLConnection) u.openConnection();
            connection.setDoOutput(true);
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            for (String key : headers.keySet()){
                connection.addRequestProperty(key,headers.get(key));
            }

            OutputStream out = connection.getOutputStream();
            out.write(generateBody());
            InputStream ins = connection.getInputStream();
            byte[] bytes = readAllBytes(ins);
            return new String(bytes, StandardCharsets.UTF_8);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public String get(){
        try {
            URL u = new URL(this.url);
            HttpURLConnection connection = (HttpURLConnection) u.openConnection();
            connection.setDoOutput(true);
            connection.setConnectTimeout(4000);
            connection.setReadTimeout(4000);
            for (String key : headers.keySet()){
                connection.addRequestProperty(key,headers.get(key));
            }
            InputStream ins = connection.getInputStream();
            byte[] bytes = readAllBytes(ins);
            return new String(bytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static byte[] readAllBytes(InputStream inp) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int read;
        while ((read = inp.read(buffer)) != -1) out.write(buffer, 0, read);
        return out.toByteArray();
    }
}
