package com.example.inventorymanagement;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import com.google.zxing.Result;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class ScannerActivity extends Activity implements ZXingScannerView.ResultHandler
{
    private ZXingScannerView mScannerView;

    @Override
    public void onCreate(Bundle state)
    {
        super.onCreate(state);
        mScannerView = new ZXingScannerView(this);
        setContentView(mScannerView);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
    }

    public void onBackPressed()
    {
        super.onBackPressed();
    }

    @Override
    public void onPause()
    {
        super.onPause();
        mScannerView.stopCamera();
    }

    @Override
    public void handleResult(Result rawResult)
    {
        Intent data = new Intent();
        data.setData(Uri.parse(rawResult.getText()));
        setResult(RESULT_OK, data);
        this.finish();
    }
}