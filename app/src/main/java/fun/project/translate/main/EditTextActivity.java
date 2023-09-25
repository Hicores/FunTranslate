package fun.project.translate.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;

import fun.project.translate.R;
import fun.project.translate.utils.ClipboardUtils;
import fun.project.translate.utils.FileSelect;
import fun.xloader.api.XUtils.XInject;

public class EditTextActivity extends Activity {
    private static final HashMap<String,EditCallback> hashMap = new HashMap<>();
    public interface EditCallback {
        void onSave(String saveContent);
    }
    public static void requestEdit(Context context,String content,EditCallback callback){
        String hash = "" + Math.random();
        hashMap.put(hash,callback);


        Intent intent = new Intent();
        intent.setClass(context, EditTextActivity.class);
        intent.putExtra("content",content);
        intent.putExtra("hash",hash);

        context.startActivity(intent);
    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            XInject.injectContext(this);
            setTheme(androidx.appcompat.R.style.Theme_AppCompat_DayNight);
        } catch (Exception ignored) {  }


        String content = getIntent().getStringExtra("content");
        String hash = getIntent().getStringExtra("hash");
        EditCallback callback = hashMap.remove(hash);

        setContentView(R.layout.edit_text);

        Button copy = (Button) findViewById(R.id.edit_copyall);
        Button paste = (Button) findViewById(R.id.edit_pasteAll);
        Button save = (Button) findViewById(R.id.save_all);

        EditText editText = (EditText) findViewById(R.id.inputBox);
        editText.setText(content);

        copy.setOnClickListener(v->{
            ClipboardUtils.copyFileToClipboard(v.getContext(), editText.getText().toString());
        });
        paste.setOnClickListener(v->{
            editText.setText(ClipboardUtils.pasteTextFromClipboard(v.getContext()));
        });
        save.setOnClickListener(v->{
            callback.onSave(editText.getText().toString());
        });

        editText.setSingleLine(false);
    }
}
