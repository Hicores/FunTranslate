package fun.project.translate.main;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import fun.project.translate.R;
import fun.project.translate.simpleCache.HashCache;
import fun.project.translate.utils.FileSelect;
import fun.project.translate.utils.FunConf;
import fun.xloader.anno.AutoRun;
import fun.xloader.api.XUtils.XInject;

public class MainSetRoot extends Activity {


    CheckBox main_switch;
    TextView tv_show_name;
    CheckBox apiLimit;
    EditText ed_api_limit;
    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            XInject.injectContext(this);
            setTheme(androidx.appcompat.R.style.Theme_AppCompat_DayNight);
        } catch (Exception ignored) {  }
        setContentView(R.layout.main_root);
        Button openSourceAddr = findViewById(R.id.main_open_link);
        openSourceAddr.setOnClickListener(v -> {
            Uri u = Uri.parse("https://github.com/FunProjectX/FunTranslate");
            Intent in = new Intent(Intent.ACTION_VIEW, u);
            startActivity(in);
        });

        Button openJoinGroup = findViewById(R.id.main_open_join_group);
        openJoinGroup.setOnClickListener(v->{
            Uri u = Uri.parse("https://t.me/QToolC");
            Intent in = new Intent(Intent.ACTION_VIEW, u);
            startActivity(in);
        });

        ////////////////////////////////////////////////////////////

        main_switch = findViewById(R.id.main_all_switch);
        main_switch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (buttonView.isPressed()){
                GlobalConfig.global_switch = buttonView.isChecked();
                FunConf.setBoolean("FunTranslate","switch",buttonView.isChecked());
            }
        });

        //////////////////////////////////////////

        Button btn_input = findViewById(R.id.main_open_item_input);
        Button btn_output = findViewById(R.id.main_item_output);
        Button btn_set_name = findViewById(R.id.btn_set_name);


        btn_input.setOnClickListener(v -> FileSelect.requestOpenFile(v.getContext(), content -> {
            try {
                PluginInfo newInfo = new PluginInfo();
                newInfo.fromJson(content);
                PluginInfo.currentPluginInfo = newInfo;
                FunConf.setBoolean("FunTranslate","api_limit_open",PluginInfo.currentPluginInfo.isLimit);
                FunConf.setInt("FunTranslate","api_limit",PluginInfo.currentPluginInfo.limit);
                FunConf.setString("FunTranslate","pluginContent",PluginInfo.currentPluginInfo.pluginContent);
                FunConf.setString("FunTranslate","name",PluginInfo.currentPluginInfo.name);

                updateUI();
            }catch (Exception e){
                Toast.makeText(this, "解析文件失败:\n" + e, Toast.LENGTH_SHORT).show();
            }
        }));

        btn_output.setOnClickListener(v->{
            FileSelect.requestSaveFile(v.getContext(), PluginInfo.currentPluginInfo.toJsonText());
        });

        btn_set_name.setOnClickListener(v->{
            EditText editText = new EditText(v.getContext());
            editText.setText(PluginInfo.currentPluginInfo.name);
            new AlertDialog.Builder(v.getContext())
                    .setTitle("输入名字")
                    .setView(editText)
                    .setNegativeButton("保存", (dialog, which) -> {
                        PluginInfo.currentPluginInfo.name = editText.getText().toString();
                        FunConf.setString("FunTranslate","name",PluginInfo.currentPluginInfo.name);
                    }).show();

        });

        /////////////////////////////////////////////

        tv_show_name = findViewById(R.id.tv_show_current_item);

        apiLimit = findViewById(R.id.change_api_limit);

        ed_api_limit = findViewById(R.id.edit_api_limit);
        apiLimit.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (buttonView.isPressed()){
                FunConf.setBoolean("FunTranslate","api_limit_open",apiLimit.isChecked());
                PluginInfo.currentPluginInfo.isLimit = apiLimit.isChecked();
            }
        });
        ed_api_limit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() < 10){
                    FunConf.setInt("FunTranslate","api_limit",Integer.parseInt(ed_api_limit.getText().toString()));
                    PluginInfo.currentPluginInfo.limit = Integer.parseInt(ed_api_limit.getText().toString());
                }


            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        Button btn_edit_plugin = findViewById(R.id.btn_edit_plugin);
        btn_edit_plugin.setOnClickListener(v -> {
            EditTextActivity.requestEdit(v.getContext(), PluginInfo.currentPluginInfo.pluginContent, saveContent -> {
                PluginInfo.currentPluginInfo.pluginContent = saveContent;
                FunConf.setString("FunTranslate","pluginContent",PluginInfo.currentPluginInfo.pluginContent);
            });
        });
        Button btn_reload_plugin = findViewById(R.id.btn_reload_plugin);
        btn_reload_plugin.setOnClickListener(v -> PluginManager.stop());



        ////////////////////////////////
        Button btn_change_format = findViewById(R.id.btn_edit_format);
        btn_change_format.setOnClickListener(v->{
            EditTextActivity.requestEdit(v.getContext(), GlobalConfig.show_format, saveContent -> {
                GlobalConfig.show_format = saveContent;
                FunConf.setString("FunTranslate","show_format",GlobalConfig.show_format);
            });
        });
        Button btn_clean_cache = findViewById(R.id.btn_clean_cache);
        btn_clean_cache.setOnClickListener(v-> HashCache.cleanCache());

        updateUI();
    }

    public void updateUI(){
        main_switch.setChecked(GlobalConfig.global_switch);
        tv_show_name.setText("当前启用的项目:" + PluginInfo.currentPluginInfo.name);
        apiLimit.setChecked(PluginInfo.currentPluginInfo.isLimit);
        ed_api_limit.setText("" + PluginInfo.currentPluginInfo.limit);
    }

}
