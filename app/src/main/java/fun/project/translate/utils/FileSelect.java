package fun.project.translate.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import fun.project.translate.main.GlobalConfig;
import fun.project.translate.main.PluginInfo;
import fun.project.translate.main.PluginManager;
import fun.xloader.api.XUtils.XEnv;

public class FileSelect {
    private static final HashMap<String,OpenCallback> requestHash = new HashMap<>();
    public interface OpenCallback{
        void onOpen(String content);
    }
    public static void requestSaveFile(Context context,String content){
        Intent intent = new Intent();
        intent.setClass(context,FileSelectProxyActivity.class);
        intent.putExtra("content",content);
        intent.putExtra("save",true);

        context.startActivity(intent);
    }
    public static void requestOpenFile(Context context,OpenCallback callback){
        String hash = "" + Math.random();
        requestHash.put(hash,callback);

        Intent intent = new Intent();
        intent.setClass(context,FileSelectProxyActivity.class);
        intent.putExtra("hash",hash);
        intent.putExtra("save",false);

        context.startActivity(intent);
    }


    public static class FileSelectProxyActivity extends Activity {
        OpenCallback helper;
        String content;
        @Override
        protected void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            if (getIntent().getBooleanExtra("save",false)){
                content = getIntent().getStringExtra("content");

                Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("*/*");
                intent.putExtra(Intent.EXTRA_TITLE,"" + PluginInfo.currentPluginInfo.name + ".json");
                startActivityForResult(Intent.createChooser(intent,"保存到文件"),3);

            }else {
                String hash = getIntent().getStringExtra("hash");
                //获取回调
                helper = requestHash.get(hash);
                if (helper == null){
                    finish();
                    return;
                }
                requestHash.remove(hash);
                    //启动文件选择器
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("application/json");
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(Intent.createChooser(intent,"选择要打开的JSON文件"),2);
            }



        }

        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);

            if (resultCode == Activity.RESULT_OK && requestCode == 2){
                Uri u = data.getData();
                try {
                    InputStream ins = getContentResolver().openInputStream(u);
                    String result = new String(DataUtils.readAllBytes(ins));
                    helper.onOpen(result);
                } catch (Exception ignored) { }

            }else if (resultCode == Activity.RESULT_OK && requestCode ==3){
                Uri u = data.getData();
                try (OutputStream out = getContentResolver().openOutputStream(u)){
                    out.write(content.getBytes(StandardCharsets.UTF_8));
                } catch (Exception ignored) { }

            }
            finish();
        }
    }
}
