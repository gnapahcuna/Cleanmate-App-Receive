package nl.psdcompany.duonavigationdrawer.example;

import android.app.Activity;
import android.app.Application;
import android.app.Dialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bugfender.sdk.Bugfender;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by anucha on 2/28/2018.
 */

public class MyAdapterProduct extends ArrayAdapter {

    Fragment fragment;
    private ProgressDialog dialog;
    private Context mContext;
    private ArrayList<MyItemProduct> mArrayList;
    private int mLayout;
    LayoutInflater inflater;
    View rowView, v;
    private String barcode,branch,IsChecker;
    private String branchID,ID;
    private ListView listview;
    private int mPosition=0;

    private SQLiteHelper mSQLite;
    private SQLiteDatabase mDb;

    private GetIPAPI getIPAPI;

    public MyAdapterProduct(Context context, int layout, ArrayList<MyItemProduct> arrayList,ListView list) {
        super(context, layout, arrayList);
        mContext = context;
        mLayout = layout;
        mArrayList = arrayList;
        listview=list;

    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        rowView = convertView;
        v = convertView;
        if (rowView == null) {
            inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(mLayout, parent, false);

        }

       /* //Log
        Bugfender.init(mContext, "RlG2SafK3kOHo2XvAfqwEZMMOnLl0yGB", BuildConfig.DEBUG);
        Bugfender.enableLogcatLogging();
        Bugfender.enableUIEventLogging((Application) mContext);*/
       getIPAPI=new GetIPAPI();

        mSQLite = SQLiteHelper.getInstance(mContext);
        mDb = mSQLite.getReadableDatabase();

        final LinearLayout linearLayout=rowView.findViewById(R.id.list_select);
        TextView textBarcode = rowView.findViewById(R.id.barcode);
        TextView textBranch = rowView.findViewById(R.id.branch);
        ImageView image = rowView.findViewById(R.id.imageView);

        if(position%2==0){
            linearLayout.setBackgroundColor(Color.parseColor("#b4cde4"));
        }else{
            linearLayout.setBackgroundColor(Color.parseColor("#8fb4d6"));
        }

        mPosition=position;

        MyFont myFont = new MyFont(mContext);
        textBarcode.setTypeface(myFont.setFont());
        textBranch.setTypeface(myFont.setFont());

        barcode= mArrayList.get(position).getBarcode();
        branch = mArrayList.get(position).getBranch();
        IsChecker = mArrayList.get(position).getSelected();

        textBarcode.setText(barcode);
        textBranch.setText(branch);

        image.setBackgroundResource(R.drawable.ic_indeterminate_check_box_black_24dp);
        /*if(Integer.parseInt(IsChecker)==1){
            image.setBackgroundResource(R.drawable.ic_check_circle_black_24dp);
        }else{
            image.setBackgroundResource(R.drawable.ic_indeterminate_check_box_black_24dp);
        }*/
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //if(Integer.parseInt(IsChecker)==0) {

                final Dialog dialog = new Dialog(mContext);
                dialog.setContentView(R.layout.custon_alert_dialog);
                dialog.setCancelable(false);
                dialog.show();
                Window window = dialog.getWindow();
                window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                TextView title = dialog.findViewById(R.id.tv_quit_learning);
                TextView des = dialog.findViewById(R.id.tv_description);
                title.setText("แจ้งเตือน");
                des.setText("ต้องการรับผ้าหมายเลข : " + mArrayList.get(position).getBarcode());


                SharedPreferences sharedPreferences2 = mContext.getSharedPreferences("ID", Activity.MODE_PRIVATE);
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
                        }
                    }
                }

                Button okButton = dialog.findViewById(R.id.btn_ok);
                okButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (Integer.parseInt(branchID) == 1) {
                            String url = getIPAPI.IPAddress+"/CleanmateCheckerIN/IsChecker.php?ID=" + ID.trim() + "&barcode=" + mArrayList.get(position).getBarcode();
                            new MyAsyncTask().execute(url);
                            dialog.dismiss();

                            Toast.makeText(mContext, "รับถุง " + mArrayList.get(position).getBarcode() + " แล้ว", Toast.LENGTH_SHORT).show();
                            insert_barcode(mArrayList.get(position).getBarcode());

                            mArrayList.remove(mArrayList.get(position));
                            MyAdapterProduct.this.notifyDataSetChanged();
                        }else{
                            String url = getIPAPI.IPAddress+"/CleanmateCheckerIN/IsCheckerEmp.php?ID=" + ID.trim() + "&barcode=" + mArrayList.get(position).getBarcode();
                            new MyAsyncTask().execute(url);
                            dialog.dismiss();

                            Toast.makeText(mContext, "รับถุง " + mArrayList.get(position).getBarcode() + " แล้ว", Toast.LENGTH_SHORT).show();
                            insert_barcode(mArrayList.get(position).getBarcode());

                            mArrayList.remove(mArrayList.get(position));
                            MyAdapterProduct.this.notifyDataSetChanged();
                        }

                    }
                });
                Button declineButton = dialog.findViewById(R.id.btn_cancel);
                declineButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                MyFont myFont = new MyFont(mContext);
                okButton.setTypeface(myFont.setFont());
                declineButton.setTypeface(myFont.setFont());
                title.setTypeface(myFont.setFont());
                des.setTypeface(myFont.setFont());
               /* }else{
                    Toast.makeText(mContext,"รับผ้าแล้ว",Toast.LENGTH_SHORT).show();
                }*/
            }
        });

        return rowView;
    }
    private void insert_barcode(String barcode) {
        ContentValues cv=new ContentValues();
        cv.put("Bracode",barcode);
        mDb.insert("tb_barcode",null,cv);

        //showMessage("บันทึกข้อมูลแล้ว");
    }

    class MyAsyncTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(mContext);
            dialog.setIcon(R.mipmap.loading);
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setMessage("กำลังตรวจสอบข้อมูล");
            dialog.setIndeterminate(true);
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            String response = "";
            String output = "";
            try {
                URL url = new URL(strings[0]);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                //httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);
                httpURLConnection.connect();

                InputStream inputStream = httpURLConnection.getInputStream();
                Scanner scanner = new Scanner(inputStream, "UTF-8");
                response = scanner.useDelimiter("\\A").next();

            } catch (Exception ex) {
                System.out.println("Error1");
            }

            try {
                output="1";
                System.out.println(response);
            } catch (Exception ex) {
                output="2";
                System.out.println("Error2");
            }
            return output;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            dialog.dismiss();

            if(Integer.parseInt(s)==1) {
                //new MyToast(mContext,"บันทึกรายการเรียบร้อยแล้ว",2);
                /*Intent in = new Intent(mContext, MainActivity.class);
                mContext.startActivity(in);*/
            }else{
                new MyToast(mContext,"เกิดข้อผิดพลาด",2);
            }
        }
    }
}


