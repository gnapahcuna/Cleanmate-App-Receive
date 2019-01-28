package nl.psdcompany.duonavigationdrawer.example;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.TextView;

import com.bugfender.sdk.Bugfender;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Scanner;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class LoginActivity extends AppCompatActivity {

    MaterialEditText user,pass;
    Button signIn;
    private Boolean isInternetPresent = false;
    private ConnectionDetector cd;
    TextView mVersion;
    ProgressDialog dialog;

    String firstname="",lastname="",id_control="",id_branch="",branchGroup="",branchName="",IsSignOn="",ipAddress="";
    ArrayList<String>arrrole_id,arrrole_name,arrrole_create,arrrole_read,arrrole_update,arrrole_delete;

    private SQLiteHelper mSQLite;
    private SQLiteDatabase mDb;
    private GetIPAPI getIPAPI;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login2);

        getIPAPI=new GetIPAPI();
        //Log
       /* Bugfender.init(this, "RlG2SafK3kOHo2XvAfqwEZMMOnLl0yGB", BuildConfig.DEBUG);
        Bugfender.enableLogcatLogging();
        Bugfender.enableUIEventLogging(getApplication());*/

        arrrole_id=new ArrayList<>();
        arrrole_name=new ArrayList<>();
        arrrole_create=new ArrayList<>();
        arrrole_read=new ArrayList<>();
        arrrole_update=new ArrayList<>();
        arrrole_delete=new ArrayList<>();

        mSQLite = SQLiteHelper.getInstance(LoginActivity.this);
        mDb = mSQLite.getReadableDatabase();
        String sqlDeleteProduct = "Delete FROM tb_barcode";
        mDb.execSQL(sqlDeleteProduct);

        cd = new ConnectionDetector(getApplicationContext());
        isInternetPresent = cd.isConnectingToInternet();


        System.out.println("IP Device : "+getDeviceIpAddress());
        System.out.println("IP Wifi : "+getWifiIp());
        System.out.println("Network : "+getNetworkInterfaceIpAddress());

        if (isInternetPresent) {

            mVersion = findViewById(R.id.text_version);
            MyFont myFont = new MyFont(LoginActivity.this);
            mVersion.setTypeface(myFont.setFont());
            user = findViewById(R.id.edUsername);
            user.setImeOptions(EditorInfo.IME_ACTION_NEXT);
            user.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_NEXT) {
                        pass.requestFocus();
                        return true;
                    }
                    return false;
                }
            });
            pass = findViewById(R.id.edPassword);
            pass.setImeOptions(EditorInfo.IME_ACTION_GO);
            pass.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_GO) {
                        if (user.getText().toString().isEmpty() || pass.getText().toString().isEmpty()) {
                            final Dialog dialog = new Dialog(LoginActivity.this);
                            dialog.setContentView(R.layout.custon_alert_dialog);
                            dialog.show();
                            Window window = dialog.getWindow();
                            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                            TextView title = dialog.findViewById(R.id.tv_quit_learning);
                            TextView des = dialog.findViewById(R.id.tv_description);
                            title.setText("แจ้งเตือน");
                            des.setText("กรุณากรอก Username และ Password");
                            Button okButton = dialog.findViewById(R.id.btn_ok);
                            okButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.dismiss();
                                }
                            });
                            Button declineButton = dialog.findViewById(R.id.btn_cancel);
                            declineButton.setVisibility(View.GONE);
                            MyFont myFont = new MyFont(LoginActivity.this);
                            okButton.setTypeface(myFont.setFont());
                            declineButton.setTypeface(myFont.setFont());
                            title.setTypeface(myFont.setFont());
                            des.setTypeface(myFont.setFont());
                        } else {
                            android.text.format.DateFormat df = new android.text.format.DateFormat();
                            final String date_today = "" + df.format("yyyy-MM-dd", new java.util.Date());
                            String url=getIPAPI.IPAddress+"/CleanmateCheckerIN/Login1.php?user="+user.getText().toString().trim();
                            url+="&pass="+pass.getText().toString().trim()+"&ip="+getDeviceIpAddress()+"&IsSignOn="+1+"&dates="+date_today;
                            new MyAsyncTask().execute(url);
                            return true;
                        }
                    }
                    return false;
                }
            });

            signIn = findViewById(R.id.btnSignin);
            signIn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    //checkFile();
                    if (user.getText().toString().isEmpty() || pass.getText().toString().isEmpty()) {
                        final Dialog dialog = new Dialog(LoginActivity.this);
                        dialog.setContentView(R.layout.custon_alert_dialog);
                        dialog.show();
                        Window window = dialog.getWindow();
                        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                        TextView title = dialog.findViewById(R.id.tv_quit_learning);
                        TextView des = dialog.findViewById(R.id.tv_description);
                        title.setText("แจ้งเตือน");
                        des.setText("กรุณากรอก Username และ Password");
                        Button okButton = dialog.findViewById(R.id.btn_ok);
                        okButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                        Button declineButton = dialog.findViewById(R.id.btn_cancel);
                        declineButton.setVisibility(View.GONE);
                                                    /*declineButton.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            dialog.dismiss();
                                                        }
                                                    });*/
                        MyFont myFont = new MyFont(LoginActivity.this);
                        okButton.setTypeface(myFont.setFont());
                        declineButton.setTypeface(myFont.setFont());
                        title.setTypeface(myFont.setFont());
                        des.setTypeface(myFont.setFont());
                    } else {
                        android.text.format.DateFormat df = new android.text.format.DateFormat();
                        final String date_today = "" + df.format("yyyy-MM-dd", new java.util.Date());
                        String url=getIPAPI.IPAddress+"/CleanmateCheckerIN/Login1.php?user="+user.getText().toString().trim();
                        url+="&pass="+pass.getText().toString().trim()+"&ip="+getDeviceIpAddress()+"&IsSignOn="+1+"&dates="+date_today;
                        new MyAsyncTask().execute(url);
                    }
                }
            });

        }else {
            new MyToast(LoginActivity.this, "ไม่มีการเชื่อมต่อ Internet", 0);
        }

    }
    @NonNull
    private String getDeviceIpAddress() {
        String actualConnectedToNetwork = null;
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connManager != null) {
            NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (mWifi.isConnected()) {
                actualConnectedToNetwork = getWifiIp();
            }
        }
        if (TextUtils.isEmpty(actualConnectedToNetwork)) {
            actualConnectedToNetwork = getNetworkInterfaceIpAddress();
        }
        if (TextUtils.isEmpty(actualConnectedToNetwork)) {
            actualConnectedToNetwork = "127.0.0.1";
        }
        return actualConnectedToNetwork;
    }

    @Nullable
    private String getWifiIp() {
        final WifiManager mWifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (mWifiManager != null && mWifiManager.isWifiEnabled()) {
            int ip = mWifiManager.getConnectionInfo().getIpAddress();
            return (ip & 0xFF) + "." + ((ip >> 8) & 0xFF) + "." + ((ip >> 16) & 0xFF) + "."
                    + ((ip >> 24) & 0xFF);
        }
        return null;
    }


    @Nullable
    public String getNetworkInterfaceIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface networkInterface = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = networkInterface.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                        String host = inetAddress.getHostAddress();
                        if (!TextUtils.isEmpty(host)) {
                            return host;
                        }
                    }
                }
            }
        } catch (Exception ex) {
            Log.e("IP Address", "getLocalIpAddress", ex);
        }
        return null;
    }
    class MyAsyncTask extends AsyncTask<String,Void,String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(LoginActivity.this);
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
                String sanitized = response.replaceAll("[\uFEFF-\uFFFF]", "");
                if(sanitized.trim().equals("#2")) {
                    output=sanitized;
                }else {
                    JSONArray jsonArray = new JSONArray(response);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObj = jsonArray.getJSONObject(i);

                        JSONArray jsonArray_user = new JSONArray(jsonObj.getString("Data_User"));
                        if (jsonArray_user.length() > 0) {
                            for (int j = 0; j < jsonArray_user.length(); j++) {
                                JSONObject jsonObj_user = jsonArray_user.getJSONObject(j);
                                firstname = jsonObj_user.getString("FirstName");
                                lastname = jsonObj_user.getString("LastName");
                                id_control = jsonObj_user.getString("AccountCode");
                                id_branch = jsonObj_user.getString("BranchID");
                                branchGroup = jsonObj_user.getString("BranchGroupID");
                                branchName = jsonObj_user.getString("BranchNameTH");
                                IsSignOn = jsonObj_user.getString("IsSignOn");
                                ipAddress = jsonObj_user.getString("SignOnIPAddress");
                            }
                        }

                        JSONArray jsonArray_role = new JSONArray(jsonObj.getString("Data_Role"));
                        if (jsonArray_role.length() > 0) {
                            for (int j = 0; j < jsonArray_role.length(); j++) {
                                JSONObject jsonObj_role = jsonArray_role.getJSONObject(j);
                                arrrole_id.add(jsonObj_role.getString("ProgramCode"));
                                arrrole_name.add(jsonObj_role.getString("MenuName"));
                                arrrole_create.add(jsonObj_role.getString("IsCreate"));
                                arrrole_read.add(jsonObj_role.getString("IsRead"));
                                arrrole_update.add(jsonObj_role.getString("IsUpdate"));
                                arrrole_delete.add(jsonObj_role.getString("IsDelete"));
                            }
                        }
                    }
                }
            }catch (Exception ex){
                System.out.println("Error2 : "+ex.getMessage());
            }
            return output;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            dialog.dismiss();
            if (s.trim().equals("#2")) {
                final Dialog dialog = new Dialog(LoginActivity.this);
                dialog.setContentView(R.layout.custon_alert_dialog);
                dialog.show();
                Window window = dialog.getWindow();
                window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                TextView title = dialog.findViewById(R.id.tv_quit_learning);
                TextView des = dialog.findViewById(R.id.tv_description);
                title.setText("แจ้งเตือน");
                des.setText("User ของคุณหมดอายุการใช้งานแล้ว");
                Button okButton = dialog.findViewById(R.id.btn_ok);
                okButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                Button declineButton = dialog.findViewById(R.id.btn_cancel);
                declineButton.setVisibility(View.GONE);
                declineButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                MyFont myFont = new MyFont(LoginActivity.this);
                okButton.setTypeface(myFont.setFont());
                declineButton.setTypeface(myFont.setFont());
                title.setTypeface(myFont.setFont());
                des.setTypeface(myFont.setFont());
            } else {
                if (IsSignOn.equals("1") && !ipAddress.equals(getDeviceIpAddress())) {
                    final Dialog dialog = new Dialog(LoginActivity.this);
                    dialog.setContentView(R.layout.custon_alert_dialog);
                    dialog.show();
                    Window window = dialog.getWindow();
                    window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                    TextView title = dialog.findViewById(R.id.tv_quit_learning);
                    TextView des = dialog.findViewById(R.id.tv_description);
                    title.setText("แจ้งเตือน");
                    des.setText("User นี้กำลัง Login อยู่");
                    Button okButton = dialog.findViewById(R.id.btn_ok);
                    okButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                    Button declineButton = dialog.findViewById(R.id.btn_cancel);
                    declineButton.setVisibility(View.GONE);
                                                    /*declineButton.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            dialog.dismiss();
                                                        }/
                                                    });*/
                    MyFont myFont = new MyFont(LoginActivity.this);
                    okButton.setTypeface(myFont.setFont());
                    declineButton.setTypeface(myFont.setFont());
                    title.setTypeface(myFont.setFont());
                    des.setTypeface(myFont.setFont());
                } else {
                    if (firstname.trim().isEmpty() || id_branch.trim().isEmpty()) {
                        final Dialog dialog = new Dialog(LoginActivity.this);
                        dialog.setContentView(R.layout.custon_alert_dialog);
                        dialog.show();
                        Window window = dialog.getWindow();
                        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                        TextView title = dialog.findViewById(R.id.tv_quit_learning);
                        TextView des = dialog.findViewById(R.id.tv_description);
                        title.setText("แจ้งเตือน");
                        des.setText("Username หรือ Password ไม่ถูกต้อง");
                        Button okButton = dialog.findViewById(R.id.btn_ok);
                        okButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                        Button declineButton = dialog.findViewById(R.id.btn_cancel);
                        declineButton.setVisibility(View.GONE);
                                                    /*declineButton.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            dialog.dismiss();
                                                        }
                                                    });*/
                        MyFont myFont = new MyFont(LoginActivity.this);
                        okButton.setTypeface(myFont.setFont());
                        declineButton.setTypeface(myFont.setFont());
                        title.setTypeface(myFont.setFont());
                        des.setTypeface(myFont.setFont());
                    } else {
                        SharedPreferences sp = LoginActivity.this.getSharedPreferences("ID", Context.MODE_PRIVATE);
                        SharedPreferences sp_role = LoginActivity.this.getSharedPreferences("Role", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sp.edit();
                        SharedPreferences.Editor editor_role = sp_role.edit();
                        sp.edit().clear().apply();
                        sp_role.edit().clear().apply();

                        //user
                        ArrayList<String> arr_proTH = new ArrayList<>();
                        arr_proTH.add("id");
                        HashSet<String> mSet = new HashSet<>();
                        for (int i = 0; i < arr_proTH.size(); i++) {
                            mSet.add("<a>" + id_control);
                            mSet.add("<b>" + id_branch);
                            mSet.add("<c>" + firstname);
                            mSet.add("<d>" + lastname);
                            mSet.add("<e>" + branchGroup);
                            mSet.add("<g>" + branchName);
                        }
                        editor.putStringSet("id", mSet);
                        editor.apply();

                        //role
                        HashSet<String> mSet_role = new HashSet<>();
                        for (int i = 0; i < arrrole_id.size(); i++) {
                            mSet_role.add("<a>" + arrrole_id.get(i));
                            mSet_role.add("<b>" + arrrole_name.get(i));
                            mSet_role.add("<c>" + arrrole_create.get(i));
                            mSet_role.add("<d>" + arrrole_read.get(i));
                            mSet_role.add("<e>" + arrrole_update.get(i));
                            mSet_role.add("<f>" + arrrole_delete.get(i));
                            editor_role.putStringSet(arrrole_id.get(i), mSet_role);
                        }
                        editor_role.apply();

                        getWindow().setSoftInputMode(
                                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
                        );

                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    }
                }
            }
        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            /*if (this.getIntent().getExtras() == null){
                finish();
            }else{

            }*/
            final Dialog dialog = new Dialog(LoginActivity.this);
            dialog.setContentView(R.layout.custon_alert_dialog);
            dialog.show();
            TextView title = dialog.findViewById(R.id.tv_quit_learning);
            TextView des = dialog.findViewById(R.id.tv_description);
            title.setText("แจ้งเตือน");
            des.setText("ต้องการออกจากแอพพลิเคชั่น?");
            Button declineButton = dialog.findViewById(R.id.btn_cancel);
            declineButton.setVisibility(View.GONE);
            Button okButton = dialog.findViewById(R.id.btn_ok);
            okButton.setOnClickListener(new View.OnClickListener() {
                @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    finishAffinity();
                    System.exit(0);
                }
            });
            MyFont myFont = new MyFont(LoginActivity.this);
            declineButton.setTypeface(myFont.setFont());
            okButton.setTypeface(myFont.setFont());
            title.setTypeface(myFont.setFont());
            des.setTypeface(myFont.setFont());
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
