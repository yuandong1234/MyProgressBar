package com.yuong.progressbar;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private MyProgressBar progressBar;
    private Button btn;
    private EditText et;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();

    }

    private void initView() {
        progressBar = findViewById(R.id.progressbar);
        btn = findViewById(R.id.btn_confirm);
        et = findViewById(R.id.et_progress);

         progressBar.setProgress(0);
        //progressBar.setProgressWithAnim(30);
        btn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_confirm:
                String progress = et.getText().toString().trim();
                if (!TextUtils.isEmpty(progress)) {
                    progressBar.setProgressWithAnim(Float.parseFloat(progress));
                }
                break;
        }
    }

}
