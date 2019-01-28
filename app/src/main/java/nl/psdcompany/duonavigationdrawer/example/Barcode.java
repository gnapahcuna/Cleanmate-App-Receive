package nl.psdcompany.duonavigationdrawer.example;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;

import com.google.zxing.Result;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class Barcode extends AppCompatActivity implements ZXingScannerView.ResultHandler{
    private ZXingScannerView mScannerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode);

        mScannerView = findViewById(R.id.zxscan);
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();

        ImageView back = findViewById(R.id.btn_flash);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(Barcode.this, MainActivity.class);
                in.putExtra("IsCheckerView",""+1);
                startActivity(in);
                finish();

            }
        });
    }
    @Override
    public void handleResult(Result rawResult) {
        //mPlayer.start();
        if(this.getIntent().getExtras() != null && this.getIntent().getExtras().containsKey("Type")) {
            if (Integer.parseInt(getIntent().getExtras().getString("Type")) == 1) {
                if (rawResult.getText().length() != 9) {
                    new MyToast(Barcode.this, "รูปแบบบาร์โค้ดไม่ถูกต้อง", 0);
                    mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
                    mScannerView.startCamera();
                } else {
                    mScannerView.stopCamera();
                    Intent itent = new Intent();
                    itent.putExtra("barcode", rawResult.getText());
                    setResult(RESULT_OK, itent);
                    finish();
                }
            } else if (Integer.parseInt(getIntent().getExtras().getString("Type")) == 2) {
                if (rawResult.getText().length() != 11) {
                    new MyToast(Barcode.this, "รูปแบบบาร์โค้ดไม่ถูกต้อง", 0);
                    mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
                    mScannerView.startCamera();
                } else {
                    mScannerView.stopCamera();
                    Intent itent = new Intent();
                    itent.putExtra("barcode", rawResult.getText());
                    setResult(RESULT_OK, itent);
                    finish();
                }
            }
        }else if(this.getIntent().getExtras() != null && this.getIntent().getExtras().containsKey("IsScan")) {

            mScannerView.stopCamera();
            Intent itent = new Intent();
            itent.putExtra("barcode", rawResult.getText());
            setResult(RESULT_OK, itent);
            finish();

        }

    }
    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();          // Start camera on resume
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();           // Stop camera on pause
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //preventing default implementation previous to android.os.Build.VERSION_CODES.ECLAIR
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}

