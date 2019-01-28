package nl.psdcompany.duonavigationdrawer.example;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bugfender.sdk.Bugfender;
import com.github.rubensousa.raiflatbutton.RaiflatImageButton;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.williamww.silkysignature.views.SignaturePad;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class FragmentSignature extends Fragment {

    private View myView;
    private LayoutInflater mLayoutInflater;
    private ViewGroup mContainer;

    private SignaturePad mSignaturePad;
    private Button mClearButton;
    private Button mSaveButton;
    private TextView mName;

    private SQLiteHelper mSQLite;
    private SQLiteDatabase mDb;
    private ProgressDialog dialog;
    //private Button mCompressButton;
    int ii;

    private String type,ID,branchID,firstname,latname,branchGroup,sTitle,title,branchName;
    private GetIPAPI getIPAPI;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mLayoutInflater = inflater;
        mContainer = container;
        myView = mLayoutInflater.inflate(R.layout.fragment_signature, mContainer, false);

        getIPAPI=new GetIPAPI();
        //Log
        /*Bugfender.init(getActivity(), "RlG2SafK3kOHo2XvAfqwEZMMOnLl0yGB", BuildConfig.DEBUG);
        Bugfender.enableLogcatLogging();
        Bugfender.enableUIEventLogging(getActivity().getApplication());*/

        mSQLite=SQLiteHelper.getInstance(getActivity());
        mDb = mSQLite.getReadableDatabase();


        mSignaturePad = (SignaturePad) myView.findViewById(R.id.signature_pad);
        mSignaturePad.setOnSignedListener(new SignaturePad.OnSignedListener() {
            @Override
            public void onStartSigning() {
            }

            @Override
            public void onSigned() {
                mSaveButton.setEnabled(true);
                mClearButton.setEnabled(true);
                //mCompressButton.setEnabled(true);
            }

            @Override
            public void onClear() {
                mSaveButton.setEnabled(false);
                mClearButton.setEnabled(false);

                String sqlDeleteProduct = "Delete FROM tb_barcode";
                mDb.execSQL(sqlDeleteProduct);
                //mCompressButton.setEnabled(false);
            }
        });
        SharedPreferences sharedPreferences2 = getActivity().getSharedPreferences("ID", Activity.MODE_PRIVATE);
        Map<String, ?> entries2 = sharedPreferences2.getAll();
        Set<String> keys2 = entries2.keySet();
        String[] getData2;
        List<String> list2 = new ArrayList<String>(keys2);
        for (String temp : list2) {
            //System.out.println(temp+" = "+sharedPreferences.getStringSet(temp,null));
            for (int i = 0; i < sharedPreferences2.getStringSet(temp, null).size(); i++) {
                getData2 = sharedPreferences2.getStringSet(temp, null).toArray(new String[sharedPreferences2.getStringSet(temp, null).size()]);
                //System.out.println(temp + " : " + getData2[i]);
                char chk = getData2[i].charAt(1);
                if (chk == 'a') {
                    ID = getData2[i].substring(3);
                } else if (chk == 'b') {
                    branchID = getData2[i].substring(3);
                } else if (chk == 'c') {
                    firstname = getData2[i].substring(3);
                } else if (chk == 'd') {
                    latname = getData2[i].substring(3);
                } else if (chk == 'e') {
                    branchGroup = getData2[i].substring(3);
                } else if (chk == 'f') {
                    title = getData2[i].substring(3);
                } else if (chk == 'g') {
                    branchName = getData2[i].substring(3);
                }
            }
        }
        mName=myView.findViewById(R.id.signature_pad_description);
        mName.setText("( "+firstname+" "+latname+" )");

        mClearButton = (Button) myView.findViewById(R.id.clear_button);
        mSaveButton = (Button) myView.findViewById(R.id.save_button);

        mClearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSignaturePad.clear();
            }
        });


        /*mCompressButton = (Button) myView.findViewById(R.id.compress_button);
        mCompressButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap signatureBitmap = mSignaturePad.getCompressedSignatureBitmap(50);
                *//*System.out.println(signatureBitmap);
                final Dialog dialog = new Dialog(getActivity());
                dialog.setContentView(R.layout.custon_alert_dialog);
                dialog.show();
                Window window = dialog.getWindow();
                window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                ImageView imageView = dialog.findViewById(R.id.img);
                imageView.setImageBitmap(signatureBitmap);
                TextView title = dialog.findViewById(R.id.tv_quit_learning);
                title.setText("ลายเซ็น");

                Button declineButton = dialog.findViewById(R.id.btn_cancel);
                declineButton.setVisibility(View.GONE);
                Button okButton = dialog.findViewById(R.id.btn_ok);*//*

                if (addJpgSignatureToGallery(signatureBitmap)) {
                    Toast.makeText(getActivity(), "50% compressed signature saved into the Gallery", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "Unable to store the signature", Toast.LENGTH_SHORT).show();
                }
            }
        });*/
        myView.setFocusableInTouchMode(true);
        myView.requestFocus();
        myView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        Intent in =new Intent(getActivity(),MainActivity.class);
                        getActivity().startActivity(in);

                        return true;
                    }
                }
                return false;
            }
        });

        return myView;
    }
    public File getAlbumStorageDir(String albumName) {
        // Get the directory for the user's public pictures directory.
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), albumName);
        if (!file.mkdirs()) {
            Log.e("SignaturePad", "Directory not created");
        }
        return file;
    }

    public void saveBitmapToJPG(Bitmap bitmap, File photo) throws IOException {
        Bitmap newBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(newBitmap);
        canvas.drawColor(Color.WHITE);
        canvas.drawBitmap(bitmap, 0, 0, null);
        OutputStream stream = new FileOutputStream(photo);
        newBitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);
        stream.close();
    }

    public boolean addJpgSignatureToGallery(Bitmap signature) {
        boolean result = false;
        try {
            File photo = new File(getAlbumStorageDir("SignaturePad"), String.format("Signature_%d.jpg", System.currentTimeMillis()));
            saveBitmapToJPG(signature, photo);
            scanMediaFile(photo);
            result = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    private void scanMediaFile(File photo) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri contentUri = Uri.fromFile(photo);
        mediaScanIntent.setData(contentUri);
        getActivity().sendBroadcast(mediaScanIntent);
    }

    public boolean addSvgSignatureToGallery(String signatureSvg) {
        boolean result = false;
        try {
            File svgFile = new File(getAlbumStorageDir("SignaturePad"), String.format("Signature_%d.svg", System.currentTimeMillis()));
            OutputStream stream = new FileOutputStream(svgFile);
            OutputStreamWriter writer = new OutputStreamWriter(stream);
            writer.write(signatureSvg);
            writer.close();
            stream.flush();
            stream.close();
            scanMediaFile(svgFile);
            result = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
    @Override
    public void onStart() {
        super.onStart();

        mDb = mSQLite.getReadableDatabase();

        final ArrayList<String>arrBarcode=new ArrayList<>();

        String sql = "SELECT * FROM tb_barcode";
        Cursor cursor = mDb.rawQuery(sql, null);
        while (cursor.moveToNext()) {
            System.out.println(cursor.getString(1));
            arrBarcode.add(cursor.getString(1));
        }

        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(arrBarcode.size()==0){
                    new MyToast(getActivity(),"ระบบได้บันทึกลายเซ็นแล้วเรียบร้อย",2);
                }else {
                    if (Integer.parseInt(branchID) == 1) {
                        Bitmap signatureBitmap = mSignaturePad.getSignatureBitmap();
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        signatureBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                        byte[] byteArray = byteArrayOutputStream.toByteArray();
                        String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);

                        System.out.println(arrBarcode.size());
                        android.text.format.DateFormat df = new android.text.format.DateFormat();
                        String dates = ID.trim() + df.format("yyyyMMddHHmmss", new java.util.Date());
                        String datesCreate = "" + df.format("yyyy-MM-dd HH:mm:ss", new java.util.Date());

                        final ProgressDialog dialog = new ProgressDialog(getActivity());
                        dialog.setIcon(R.mipmap.loading);
                        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        dialog.setMessage("กรุณารอสักครู่....");
                        dialog.setIndeterminate(true);
                        dialog.show();
                        for (ii = 0; ii < arrBarcode.size(); ii++) {
                            Ion.with(getActivity())
                                    .load(getIPAPI.IPAddress+"/CleanmateCheckerIN/ImageLicene1.php")
                                    .setBodyParameter("Data1", encoded)
                                    .setBodyParameter("Data2", dates)
                                    .setBodyParameter("Data3", datesCreate)
                                    .setBodyParameter("barcode", "" + arrBarcode.get(ii).trim())
                                    .asString()
                                    .setCallback(new FutureCallback<String>() {

                                        @Override
                                        public void onCompleted(Exception e, String result) {
                                            System.out.println(ii);
                                            if (ii == arrBarcode.size()) {
                                                dialog.dismiss();
                                                new MyToast(getActivity(), result, 2);
                                                Intent intent = new Intent(getActivity(), MainActivity.class);
                                                startActivity(intent);
                                                getActivity().finish();
                                            }

                                        }
                                    });

                        }

                        mSignaturePad.clear();


                    }else{
                        Bitmap signatureBitmap = mSignaturePad.getSignatureBitmap();
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        signatureBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                        byte[] byteArray = byteArrayOutputStream.toByteArray();
                        String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);

                        System.out.println(encoded + " : " + arrBarcode);
                        android.text.format.DateFormat df = new android.text.format.DateFormat();
                        String dates = ID.trim() + df.format("yyyyMMddHHmmss", new java.util.Date());
                        String datesCreate = "" + df.format("yyyy-MM-dd HH:mm:ss", new java.util.Date());

                        final ProgressDialog dialog = new ProgressDialog(getActivity());
                        dialog.setIcon(R.mipmap.loading);
                        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        dialog.setMessage("กรุณารอสักครู่....");
                        dialog.setIndeterminate(true);
                        dialog.show();
                        for (ii = 0; ii < arrBarcode.size(); ii++) {
                            Ion.with(getActivity())
                                    .load(getIPAPI.IPAddress+"/CleanmateCheckerIN/ImageLiceneEmp1.php")
                                    .setBodyParameter("Data1", encoded)
                                    .setBodyParameter("Data2", dates)
                                    .setBodyParameter("Data3", datesCreate)
                                    .setBodyParameter("barcode", "" + arrBarcode.get(ii).trim())
                                    .asString()
                                    .setCallback(new FutureCallback<String>() {

                                        @Override
                                        public void onCompleted(Exception e, String result) {

                                            System.out.println(ii);
                                            if (ii == arrBarcode.size()) {
                                                dialog.dismiss();
                                                new MyToast(getActivity(), result, 2);
                                                Intent intent = new Intent(getActivity(), MainActivity.class);
                                                startActivity(intent);
                                                getActivity().finish();
                                            }

                                        }
                                    });

                        }

                        mSignaturePad.clear();

                    }
                }

            }
        });
    }

    class MyAsyncTask extends AsyncTask<String,Void,String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(getActivity());
            dialog.setIcon(R.mipmap.ic_launcher);
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setMessage("กำลังตรวจสอบข้อมูล");
            dialog.setIndeterminate(true);
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            String response="";
            try {
                URL url=new URL(strings[0]);
                HttpURLConnection httpURLConnection=(HttpURLConnection)url.openConnection();
                //httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);
                httpURLConnection.connect();

                InputStream inputStream = httpURLConnection.getInputStream();
                Scanner scanner=new Scanner(inputStream,"UTF-8");
                response=scanner.useDelimiter("\\A").next();

            }catch (Exception ex){
                System.out.println("Error1");
            }

            String output="";
            try {
                System.out.println(response);
                //JSONObject jsonObject=new JSONObject(response);

            }catch (Exception ex){
                System.out.println("Error2");
            }
            return output;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            dialog.dismiss();
        }
    }

}



