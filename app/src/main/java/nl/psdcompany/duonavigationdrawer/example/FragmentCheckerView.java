package nl.psdcompany.duonavigationdrawer.example;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bugfender.sdk.Bugfender;
import com.github.rubensousa.raiflatbutton.RaiflatImageButton;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.williamww.silkysignature.views.SignaturePad;

import org.json.JSONArray;
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
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import me.relex.circleindicator.CircleIndicator;

public class FragmentCheckerView extends Fragment {

    private View myView;
    private LayoutInflater mLayoutInflater;
    private ViewGroup mContainer;

    String branchID, ID;
    ProgressDialog dialog;
    String getorderno, getbranch, getcustomername, getcustomerphone, getservice, getproductname, getspecialdetail, getappointment, getexpress;
    int getcount;
    String myBarcode, type;
    Dialog dialog_logout;
    private GetIPAPI getIPAPI;
    private ArrayList<String>arrImg;
    private RequestPermissionHandler mRequestPermissionHandler;

    private ViewPager mPager;
    private int currentPage = 0;

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        MenuItem menuItem = menu.findItem(R.id.action_settings);
        Button scan = (Button) MenuItemCompat.getActionView(menuItem);
        scan.setVisibility(View.GONE);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mLayoutInflater = inflater;
        mContainer = container;
        myView = mLayoutInflater.inflate(R.layout.fragment_1, mContainer, false);

        getIPAPI = new GetIPAPI();
        //Log
       /* Bugfender.init(getActivity(), "RlG2SafK3kOHo2XvAfqwEZMMOnLl0yGB", BuildConfig.DEBUG);
        Bugfender.enableLogcatLogging();
        Bugfender.enableUIEventLogging(getActivity().getApplication());*/
        arrImg=new ArrayList<>();


        mRequestPermissionHandler = new RequestPermissionHandler();
        RaiflatImageButton btn_package = myView.findViewById(R.id.layout_package);
        btn_package.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(MainActivity.this, "Cliked", Toast.LENGTH_SHORT).show();
                scanPackage();
            }
        });

        myView.setFocusableInTouchMode(true);
        myView.requestFocus();
        myView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        return true;
                    }
                }
                return false;
            }
        });

        RaiflatImageButton btn_product = myView.findViewById(R.id.layout_product);
        btn_product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scanProduct();
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
                }
            }
        }


        return myView;
    }
    private void scanBarcodePackage() {
        Intent it = new Intent(getActivity(), Barcode.class);
        it.putExtra("IsActivity",""+1);
        it.putExtra("IsScan", "Package");
        startActivityForResult(it, 100);
    }

    public void scanPackage() {
        mRequestPermissionHandler.requestPermission(getActivity(), new String[]{
                Manifest.permission.CAMERA
        }, 123, new RequestPermissionHandler.RequestPermissionListener() {
            @Override
            public void onSuccess() {
                //Toast.makeText(getActivity(), "request permission success", Toast.LENGTH_SHORT).show();
                scanBarcodePackage();
            }

            @Override
            public void onFailed() {
                return;
            }
        });
    }
    private void scanBarcodeProduct() {
        Intent it = new Intent(getActivity(), Barcode.class);
        it.putExtra("IsActivity",""+1);
        it.putExtra("IsScan", "Product");
        startActivityForResult(it, 200);
    }

    public void scanProduct() {
        mRequestPermissionHandler.requestPermission(getActivity(), new String[]{
                Manifest.permission.CAMERA
        }, 123, new RequestPermissionHandler.RequestPermissionListener() {
            @Override
            public void onSuccess() {
                //Toast.makeText(getActivity(), "request permission success", Toast.LENGTH_SHORT).show();
                scanBarcodeProduct();
            }

            @Override
            public void onFailed() {
                return;
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 100 && resultCode == Activity.RESULT_OK) {
            try {
                final String contents = data.getStringExtra("barcode");
                String url=getIPAPI.IPAddress+"/CleanmateCheckerView/view.php?Type="+1+"&Barcode="+contents;
                new MyAsyncTask().execute(url,contents,"package");
            }catch (Exception ex){
                Log.e("ex : ",ex.getMessage());
            }

        }else if(requestCode == 200 && resultCode == Activity.RESULT_OK) {
            try {
                final String contents = data.getStringExtra("barcode");
                String url=getIPAPI.IPAddress+"/CleanmateCheckerView/view.php?Type="+2+"&Barcode="+contents;
                new MyAsyncTask().execute(url,contents,"product");
            }catch (Exception ex){
                Log.e("ex : ",ex.getMessage());
            }

        }
    }
    class MyAsyncTask extends AsyncTask<String,Void,String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(getActivity());
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
            String output = "";
            if (strings[2].equals("image")) {
                type = strings[2];
                myBarcode = strings[1];
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    output = "" + jsonArray.length();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObj = jsonArray.getJSONObject(i);
                        arrImg.add(jsonObj.getString("ImageFile"));
                    }
                } catch (Exception ex) {
                    System.out.println("Error2");
                }
            } else if (strings[2].equals("package")) {
                type = strings[2];
                myBarcode = strings[1];
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.optJSONObject(i);
                        output = jsonObject.optString("OrderNo");
                        getappointment = jsonObject.optString("AppointmentDate").substring(9, 20).trim();
                        getorderno = jsonObject.optString("OrderNo");
                        getbranch = jsonObject.optString("BranchNameTH");
                        getcustomername = jsonObject.optString("Name");
                        getcustomerphone = jsonObject.optString("TelephoneNo");
                    }
                    //JSONObject jsonObject = new JSONObject(response);

                } catch (Exception ex) {
                    System.out.println("Error22");
                }
            } else if (strings[2].equals("product")) {
                type = strings[2];
                myBarcode = strings[1];
                try {
                    getcount = 0;
                    JSONArray jsonArray = new JSONArray(response);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.optJSONObject(i);
                        if (Integer.parseInt(jsonObject.optString("IsExpressLevel")) == 1) {
                            getexpress = "ซักด่วน";
                        } else if (Integer.parseInt(jsonObject.optString("IsExpressLevel")) == 2) {
                            getexpress = "ซักด่วนพิเศษ";
                        } else if (Integer.parseInt(jsonObject.optString("IsExpressLevel")) == 0) {
                            getexpress = "ซักธรรมดา";
                        }
                        getappointment = jsonObject.optString("AppointmentDate").substring(9, 20).trim();
                        output = jsonObject.optString("OrderNo");
                        getorderno = jsonObject.optString("OrderNo");
                        getcount += Integer.parseInt(jsonObject.optString("counts"));
                        getbranch = jsonObject.optString("BranchNameTH");
                        getcustomername = jsonObject.optString("Name");
                        getservice = jsonObject.optString("ServiceNameTH");
                        getproductname = jsonObject.optString("ProductNameTH");
                        getspecialdetail = jsonObject.optString("SpecialDetial");
                    }
                } catch (Exception ex) {
                    System.out.println("Error2");
                }
            } else if (strings[1].equals("logout")) {
                type = strings[1];
                Intent i = new Intent(getActivity(), LoginActivity.class);
                startActivity(i);
                getActivity().finish();
                dialog_logout.dismiss();
            } else {

            }

            return output;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            dialog.dismiss();
            System.out.println("Type : " + type);
            if (type.equals("product")) {
                final Dialog dialog = new Dialog(getActivity());
                dialog.setContentView(R.layout.custom_alert_product1);
                dialog.show();
                Window window = dialog.getWindow();
                window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                TextView title = dialog.findViewById(R.id.tv_quit_learning);

                TextView tv_title0 = dialog.findViewById(R.id.tv_title0);
                TextView tv_name0 = dialog.findViewById(R.id.tv_name0);

                TextView tv_title7 = dialog.findViewById(R.id.tv_title7);
                TextView tv_title = dialog.findViewById(R.id.tv_title);
                TextView tv_title1 = dialog.findViewById(R.id.tv_title1);
                TextView tv_title2 = dialog.findViewById(R.id.tv_title2);
                TextView tv_title3 = dialog.findViewById(R.id.tv_title3);
                TextView tv_title4 = dialog.findViewById(R.id.tv_title4);
                TextView tv_title5 = dialog.findViewById(R.id.tv_title5);
                TextView tv_title6 = dialog.findViewById(R.id.tv_title6);
                TextView tv_title8 = dialog.findViewById(R.id.tv_title8);

                TextView tv_name7 = dialog.findViewById(R.id.tv_name7);
                TextView tv_name = dialog.findViewById(R.id.tv_name);
                TextView tv_name1 = dialog.findViewById(R.id.tv_name1);
                TextView tv_name2 = dialog.findViewById(R.id.tv_name2);
                TextView tv_name3 = dialog.findViewById(R.id.tv_name3);
                TextView tv_name4 = dialog.findViewById(R.id.tv_name4);
                TextView tv_name5 = dialog.findViewById(R.id.tv_name5);
                TextView tv_name6 = dialog.findViewById(R.id.tv_name6);
                ImageView img = dialog.findViewById(R.id.imageView);
                TextView tv_not = dialog.findViewById(R.id.tv_not);
                Button declineButton = dialog.findViewById(R.id.btn_cancel);
                declineButton.setVisibility(View.GONE);
                tv_not.setVisibility(View.GONE);
                Button okButton = dialog.findViewById(R.id.btn_ok);
                title.setText("รายการสินค้า");

                if (s.isEmpty()) {
                    tv_name.setVisibility(View.GONE);
                    tv_name1.setVisibility(View.GONE);
                    tv_not.setVisibility(View.VISIBLE);
                    tv_not.setText("ไม่พบข้อมูลรายการสินค้า : " + myBarcode);
                    tv_name2.setVisibility(View.GONE);
                    tv_name3.setVisibility(View.GONE);
                    tv_name4.setVisibility(View.GONE);
                    tv_name5.setVisibility(View.GONE);
                    tv_name6.setVisibility(View.GONE);
                    tv_name7.setVisibility(View.GONE);

                    tv_name0.setVisibility(View.GONE);
                    tv_title0.setVisibility(View.GONE);
                    tv_title.setVisibility(View.GONE);
                    tv_title1.setVisibility(View.GONE);
                    tv_title2.setVisibility(View.GONE);
                    tv_title3.setVisibility(View.GONE);
                    tv_title4.setVisibility(View.GONE);
                    tv_title5.setVisibility(View.GONE);
                    tv_title6.setVisibility(View.GONE);
                    tv_title7.setVisibility(View.GONE);
                    okButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });

                } else {
                    tv_title7.setText("วันที่นัดรับ : ");
                    tv_name7.setText(dateThai(getappointment));
                    tv_title.setText("ประเภทการซัก : ");
                    tv_name.setText(getexpress);
                    tv_title0.setText("สาขา : ");
                    tv_name0.setText(getbranch);
                    tv_title1.setText("เลขที่ออเดอร์ : ");
                    tv_name1.setText(getorderno);
                    tv_title2.setText("จำนวนสินค้า : ");
                    tv_name2.setText(""+getcount);
                    tv_title3.setText("ชื่อลูกค้า : ");
                    tv_name3.setText(getcustomername);
                    tv_title4.setText("ประเภทบริการ : ");
                    tv_name4.setText(getservice);
                    tv_title5.setText("ชนิดบริการ : ");
                    tv_name5.setText(getproductname);
                    tv_title6.setText("คำสั่งพิเศษ : ");
                    tv_name6.setText(getspecialdetail);
                    tv_title8.setText("รูปภาพสินค้า : ");
                    img.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            String url=getIPAPI.IPAddress+"/CleanmateCheckerIN/view_image.php?Barcode="+myBarcode;
                            new MyAsyncTask().execute(url,""+myBarcode,"image");


                        }
                    });

                    okButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });

                    declineButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                    MyFont myFont = new MyFont(getActivity());
                    okButton.setTypeface(myFont.setFont());
                    declineButton.setTypeface(myFont.setFont());
                    title.setTypeface(myFont.setFont());

                    tv_title.setTypeface(myFont.setFont());
                    tv_title1.setTypeface(myFont.setFont());
                    tv_title2.setTypeface(myFont.setFont());
                    tv_title3.setTypeface(myFont.setFont());
                    tv_title4.setTypeface(myFont.setFont());
                    tv_title5.setTypeface(myFont.setFont());
                    tv_title6.setTypeface(myFont.setFont());

                    tv_name.setTypeface(myFont.setFont());
                    tv_name1.setTypeface(myFont.setFont());
                    tv_name2.setTypeface(myFont.setFont());
                    tv_name3.setTypeface(myFont.setFont());
                    tv_name4.setTypeface(myFont.setFont());
                    tv_name5.setTypeface(myFont.setFont());
                    tv_name6.setTypeface(myFont.setFont());

                    tv_title0.setTypeface(myFont.setFont());
                    tv_name0.setTypeface(myFont.setFont());
                }
            }else if(type.equals("package")){
                final Dialog dialog = new Dialog(getActivity());
                dialog.setContentView(R.layout.custom_alert_package);
                dialog.show();
                Window window = dialog.getWindow();
                window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                TextView title = dialog.findViewById(R.id.tv_quit_learning);
                TextView tv_title1 = dialog.findViewById(R.id.tv_title1);
                TextView tv_title2 = dialog.findViewById(R.id.tv_title2);
                TextView tv_title3 = dialog.findViewById(R.id.tv_title3);
                TextView tv_title4 = dialog.findViewById(R.id.tv_title4);
                TextView tv_name1 = dialog.findViewById(R.id.tv_name1);
                TextView tv_name2 = dialog.findViewById(R.id.tv_name2);
                TextView tv_name3 = dialog.findViewById(R.id.tv_name3);
                TextView tv_name4 = dialog.findViewById(R.id.tv_name4);
                TextView tv_not = dialog.findViewById(R.id.tv_not);
                TextView tv_title0 = dialog.findViewById(R.id.tv_title0);
                TextView tv_name0 = dialog.findViewById(R.id.tv_name0);
                Button declineButton = dialog.findViewById(R.id.btn_cancel);
                declineButton.setVisibility(View.GONE);
                tv_not.setVisibility(View.GONE);
                Button okButton = dialog.findViewById(R.id.btn_ok);
                title.setText("รายการถุง");

                if (s.isEmpty()) {
                    tv_name1.setVisibility(View.GONE);
                    tv_not.setVisibility(View.VISIBLE);
                    tv_not.setText("ไม่พบข้อมูลรายการถุง : " + myBarcode);
                    tv_name2.setVisibility(View.GONE);
                    tv_name3.setVisibility(View.GONE);
                    tv_name4.setVisibility(View.GONE);

                    tv_title1.setVisibility(View.GONE);
                    tv_title2.setVisibility(View.GONE);
                    tv_title3.setVisibility(View.GONE);
                    tv_title4.setVisibility(View.GONE);

                    tv_name0.setVisibility(View.GONE);
                    tv_title0.setVisibility(View.GONE);
                    okButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });

                } else {
                    tv_title0.setText("วันที่นัดรับ : ");
                    tv_name0.setText(dateThai(getappointment));
                    tv_title1.setText("เลขที่ออเดอร์ : ");
                    tv_name1.setText(getorderno);
                    tv_title2.setText("สาขา : ");
                    tv_name2.setText(getbranch);
                    tv_title3.setText("ชื่อลูกค้า : ");
                    tv_name3.setText(getcustomername);
                    tv_title4.setText("เบอร์โทร : ");
                    tv_name4.setText(getcustomerphone);

                    okButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });

                    declineButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                    MyFont myFont = new MyFont(getActivity());
                    okButton.setTypeface(myFont.setFont());
                    declineButton.setTypeface(myFont.setFont());
                    title.setTypeface(myFont.setFont());
                    tv_title1.setTypeface(myFont.setFont());
                    tv_title2.setTypeface(myFont.setFont());
                    tv_title3.setTypeface(myFont.setFont());
                    tv_title4.setTypeface(myFont.setFont());
                    tv_name1.setTypeface(myFont.setFont());
                    tv_name2.setTypeface(myFont.setFont());
                    tv_name3.setTypeface(myFont.setFont());
                    tv_name4.setTypeface(myFont.setFont());
                    tv_title0.setTypeface(myFont.setFont());
                    tv_name0.setTypeface(myFont.setFont());
                }
            }else if(type.equals("image")){
                final Dialog dialog = new Dialog(getActivity());
                dialog.setContentView(R.layout.custom_image);
                dialog.show();
                Window window = dialog.getWindow();
                window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                TextView title = dialog.findViewById(R.id.tv_quit_learning);
                title.setText("รูปสินค้า");
                //des.setText("ยังไม่มีรายการในตะกร้าสินค้า");

                mPager = dialog.findViewById(R.id.pager);
                mPager.setAdapter(new MyAdapterSlideImage(getActivity(), arrImg));
                CircleIndicator indicator = dialog.findViewById(R.id.indicator);
                indicator.setViewPager(mPager);

                final Handler handler = new Handler();
                final Runnable Update = new Runnable() {
                    public void run() {
                        if (currentPage == arrImg.size()) {
                            currentPage = 0;
                        }
                        mPager.setCurrentItem(currentPage++, true);
                    }
                };
                Timer swipeTimer = new Timer();
                swipeTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        handler.post(Update);
                    }
                }, 4000, 4000);


                Button declineButton = dialog.findViewById(R.id.btn_cancel);
                declineButton.setVisibility(View.GONE);
                Button okButton = dialog.findViewById(R.id.btn_ok);
                okButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                MyFont myFont = new MyFont(getActivity());
                declineButton.setTypeface(myFont.setFont());
                //okButton.setTypeface(myFont.setFont());
                title.setTypeface(myFont.setFont());
            }
        }
    }
    /*@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //preventing default implementation previous to android.os.Build.VERSION_CODES.ECLAIR
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }*/
    public static String dateThai(String strDate)
    {
        String Months[] = {
                "ม.ค", "ก.พ", "มี.ค", "เม.ย",
                "พ.ค", "มิ.ย", "ก.ค", "ส.ค",
                "ก.ย", "ต.ค", "พ.ย", "ธ.ค"};

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

        int year=0,month=0,day=0;
        try {
            Date date = df.parse(strDate);
            Calendar c = Calendar.getInstance();
            c.setTime(date);

            year = c.get(Calendar.YEAR);
            month = c.get(Calendar.MONTH);
            day = c.get(Calendar.DATE);

        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return String.format("%s %s %s", day,Months[month],year+543);
    }
}
