package com.ti.tidhdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;

import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private final Executor mExecutor = Executors.newSingleThreadExecutor();

    private TextView mLabelClientKey;
    private TextView mLabelServerKey;
    private TextView mLabelMatch;

    private final TiDHDemo mDHTest = new TiDHDemo();

    public void onProcessDH(View view) {
        processDH();
    }

    private void processDH() {
        mExecutor.execute(() -> {
            try {
                mDHTest.processPublicKey();
                freshUI();
            } catch (IOException e) {
                e.printStackTrace();
                toastInner(e.getMessage());
            } catch (JSONException e) {
                e.printStackTrace();
                toastInner(e.getMessage());
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLabelClientKey = findViewById(R.id.label_client_key);
        mLabelServerKey = findViewById(R.id.label_server_key);
        mLabelMatch = findViewById(R.id.label_match);
    }

    private void freshUI() {
        runOnUiThread(()->{
            mLabelClientKey.setText(mDHTest.getClientKey());
            mLabelServerKey.setText(mDHTest.getServerKey());
            mLabelMatch.setText(mDHTest.isKeyMath() ? "Yes!!" : "NO!!");
        });
    }
    private void toastInner(String msg) {
        runOnUiThread(()-> {
            Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
        });
    }
}
