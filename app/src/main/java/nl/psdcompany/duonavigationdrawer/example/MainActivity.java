package nl.psdcompany.duonavigationdrawer.example;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bugfender.sdk.Bugfender;
import com.luseen.simplepermission.permissions.Permission;
import com.luseen.simplepermission.permissions.PermissionActivity;
import com.luseen.simplepermission.permissions.PermissionUtils;
import com.luseen.simplepermission.permissions.SinglePermissionCallback;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import nl.psdcompany.duonavigationdrawer.views.DuoDrawerLayout;
import nl.psdcompany.duonavigationdrawer.views.DuoMenuView;
import nl.psdcompany.duonavigationdrawer.widgets.DuoDrawerToggle;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends PermissionActivity implements DuoMenuView.OnMenuClickListener {
    private ViewHolder mViewHolder;
    private TextView text_title, text_subtitle, text_subtitle1;
    private Button logout, signature, scan,checkerview;
    private String type, ID, branchID, firstname, latname, branchGroup, sTitle, title, branchName;
    private ProgressDialog dialog;
    private Dialog dialog_logout;
    private LinearLayout layout;
    private ArrayList<MyItemProduct> items;
    private ListView list;
    private RelativeLayout head;

    private SQLiteHelper mSQLite;
    private SQLiteDatabase mDb;

    private GetIPAPI getIPAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Log
        /*Bugfender.init(this, "RlG2SafK3kOHo2XvAfqwEZMMOnLl0yGB", BuildConfig.DEBUG);
        Bugfender.enableLogcatLogging();
        Bugfender.enableUIEventLogging(getApplication());*/
        layout=findViewById(R.id.layout);
        if(this.getIntent().getExtras() != null && this.getIntent().getExtras().containsKey("IsCheckerView")){
            //if(getIntent().getExtras().get("IsCheckerView").toString().equals("1")) {
            FragmentCheckerView fragmentCheckerView = new FragmentCheckerView();
            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, fragmentCheckerView)
                    .commit();
            layout.setVisibility(View.GONE);
            //scan.setEnabled(false);
            //}
        }


        getIPAPI=new GetIPAPI();


        mSQLite = SQLiteHelper.getInstance(MainActivity.this);
        mDb = mSQLite.getReadableDatabase();

        items = new ArrayList<>();
        list = findViewById(R.id.list);

        SharedPreferences sharedPreferences2 = MainActivity.this.getSharedPreferences("ID", Activity.MODE_PRIVATE);
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
        if (Integer.parseInt(branchID) == 1) {
            String url = getIPAPI.IPAddress+"/CleanmateCheckerIN/viewall.php";
            new MyAsyncTask().execute(url, "package");
            //Toast.makeText(MainActivity.this,"User โรงงาน",Toast.LENGTH_SHORT).show();
        } else {
            String url = getIPAPI.IPAddress+"/CleanmateCheckerIN/viewbranch.php?branchID=" + branchID;
            new MyAsyncTask().execute(url, "package");
            //Toast.makeText(MainActivity.this,"User สาขา",Toast.LENGTH_SHORT).show();
        }

        text_title = findViewById(R.id.duo_view_header_text_title);
        text_subtitle = findViewById(R.id.duo_view_header_text_sub_title);
        text_subtitle1 = findViewById(R.id.duo_view_header_text_sub_title2);

        head = findViewById(R.id.layout_head);
        head.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, MainActivity.class);
                //intent.setFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                finish();
            }
        });


        text_title.setText("เจ้าหน้าที่");
        text_subtitle.setText(firstname + " " + latname);
        text_subtitle1.setText(branchName);
        mViewHolder = new ViewHolder();
        handleToolbar();
        handleDrawer();



    }

    private void scanBarcodePackage() {
        Intent it = new Intent(MainActivity.this, Barcode.class);
        it.putExtra("Type",""+1);
        startActivityForResult(it, 100);
    }
    private void scanBarcodePackage1() {
        Intent it = new Intent(MainActivity.this, Barcode.class);
        it.putExtra("Type",""+2);
        startActivityForResult(it, 100);
    }

    public void scanPackage() {
        if (PermissionUtils.isMarshmallowOrHigher()) {
            requestPermission(Permission.CAMERA, new SinglePermissionCallback() {
                @Override
                public void onPermissionResult(boolean permissionGranted,
                                               boolean isPermissionDeniedForever) {

                    if (!permissionGranted) {
                        return;
                    } else {
                        scanBarcodePackage();
                    }
                }
            });
        }
    }
    public void scanPackage1() {
        if (PermissionUtils.isMarshmallowOrHigher()) {
            requestPermission(Permission.CAMERA, new SinglePermissionCallback() {
                @Override
                public void onPermissionResult(boolean permissionGranted,
                                               boolean isPermissionDeniedForever) {

                    if (!permissionGranted) {
                        return;
                    } else {
                        scanBarcodePackage1();
                    }
                }
            });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 100 && resultCode == Activity.RESULT_OK) {
            try{
                android.text.format.DateFormat df = new android.text.format.DateFormat();
                final String dates = "" + df.format("yyyy-MM-dd", new java.util.Date());
                final String contents = data.getStringExtra("barcode");

                for (int i = 0; i < items.size(); i++) {
                    if (contents.equals(items.get(i).getBarcode().trim())) {
                        final Dialog dialog = new Dialog(MainActivity.this);
                        dialog.setContentView(R.layout.custon_alert_dialog);
                        dialog.setCancelable(false);
                        dialog.show();
                        Window window = dialog.getWindow();
                        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                        TextView title = dialog.findViewById(R.id.tv_quit_learning);
                        TextView des = dialog.findViewById(R.id.tv_description);
                        title.setText("แจ้งเตือน");
                        des.setText("ต้องการรับผ้าหมายเลข : " + contents);
                        Button okButton = dialog.findViewById(R.id.btn_ok);
                        okButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (Integer.parseInt(branchID) == 1) {
                                    String url = getIPAPI.IPAddress+"/CleanmateCheckerIN/IsChecker.php?ID=" + ID.trim() + "&barcode=" + contents;
                                    new MyAsyncTask().execute(url, "check");
                                    dialog.dismiss();
                                    System.out.println("size1 : "+items.size()+" get "+contents);
                                    for (int i = 0; i < items.size(); i++) {
                                        if (contents.equals(items.get(i).getBarcode().trim())) {
                                            insert_barcode(items.get(i).getBarcode());
                                            MyAdapterProduct myAdapter = new MyAdapterProduct(MainActivity.this, R.layout.list_product, items, list);
                                            myAdapter.remove(items.get(i));
                                            myAdapter.notifyDataSetChanged();
                                            list.setAdapter(myAdapter);
                                            //Toast.makeText(MainActivity.this, "รับถุง " + contents + " แล้ว", Toast.LENGTH_SHORT).show();

                                        }
                                    }
                                }else{
                                    String url = getIPAPI.IPAddress+"/CleanmateCheckerIN/IsCheckerEmp.php?ID=" + ID.trim() + "&barcode=" + contents;
                                    new MyAsyncTask().execute(url, "check");
                                    dialog.dismiss();

                                    for (int i = 0; i < items.size(); i++) {
                                        System.out.println(items.get(i).getBarcode() + " = " + contents);
                                        if (contents.equals(items.get(i).getBarcode())) {
                                            insert_barcode(items.get(i).getBarcode());
                                            MyAdapterProduct myAdapter = new MyAdapterProduct(MainActivity.this, R.layout.list_product, items, list);
                                            myAdapter.remove(items.get(i));
                                            myAdapter.notifyDataSetChanged();
                                            list.setAdapter(myAdapter);
                                            //Toast.makeText(MainActivity.this, "รับถุง " + contents + " แล้ว", Toast.LENGTH_SHORT).show();
                                        }
                                    }
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
                        MyFont myFont = new MyFont(MainActivity.this);
                        okButton.setTypeface(myFont.setFont());
                        declineButton.setTypeface(myFont.setFont());
                        title.setTypeface(myFont.setFont());
                        des.setTypeface(myFont.setFont());
                    }else{
                        //new MyToast(MainActivity.this,"หมายเลขถุงไม่ตรง",0);
                    }
                }
            }catch (Exception ex){
                Log.e("", ex.getMessage());
            }

        }
    }

    private void insert_barcode(String barcode) {
        ContentValues cv = new ContentValues();
        cv.put("Bracode", barcode);
        mDb.insert("tb_barcode", null, cv);

        //showMessage("บันทึกข้อมูลแล้ว");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);

        MenuItem menuItem = menu.findItem(R.id.action_settings);
        scan = (Button) MenuItemCompat.getActionView(menuItem);
        scan.setWidth((getResources().getDisplayMetrics().widthPixels*35)/100);
        scan.setTextColor(Color.parseColor("#0099cc"));
        scan.setBackgroundResource(R.drawable.relative_layout_background_back);
        scan.setText("แกนบาร์โค้ด");
        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Integer.parseInt(branchID) == 1) {
                    scanPackage();
                }else{
                    scanPackage1();
                }
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // action with ID action_refresh was selected

            case R.id.action_settings:

                Toast.makeText(this, "Skip selected", Toast.LENGTH_SHORT)
                        .show();
                break;
            default:
                break;
        }
        return true;
    }

    private void handleToolbar() {
        setSupportActionBar(mViewHolder.mToolbar);
    }

    private void handleDrawer() {
        DuoDrawerToggle duoDrawerToggle = new DuoDrawerToggle(this,
                mViewHolder.mDuoDrawerLayout,
                mViewHolder.mToolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);

        mViewHolder.mDuoDrawerLayout.setDrawerListener(duoDrawerToggle);
        duoDrawerToggle.syncState();

    }

    @Override
    public void onFooterClicked() {
        Toast.makeText(this, "onFooterClicked", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onHeaderClicked() {
        Toast.makeText(this, "onHeaderClicked", Toast.LENGTH_SHORT).show();
    }

    private void goToFragment(Fragment fragment, boolean addToBackStack) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        if (addToBackStack) {
            transaction.addToBackStack(null);
        }

        transaction.add(R.id.container, fragment).commit();
    }

    @Override
    public void onOptionClicked(int position, Object objectClicked) {
    }

    private class ViewHolder {
        private DuoDrawerLayout mDuoDrawerLayout;
        private DuoMenuView mDuoMenuView;
        private Toolbar mToolbar;

        ViewHolder() {
            mDuoDrawerLayout = (DuoDrawerLayout) findViewById(R.id.drawer);
            mDuoMenuView = (DuoMenuView) mDuoDrawerLayout.getMenuView();
            mToolbar = (Toolbar) findViewById(R.id.toolbar);
        }
    }

    class MyAsyncTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(MainActivity.this);
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
            System.out.println(strings[1]);
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
            if (strings[1].equals("logout")) {
                type = strings[1];
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
                ;
                dialog_logout.dismiss();
            } else if (strings[1].equals("package")) {
                type = strings[1];
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    output = "" + jsonArray.length();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObj = jsonArray.getJSONObject(i);
                        items.add(new MyItemProduct(jsonObj.getString("Barcode"),
                                jsonObj.getString("BranchNameTH"), jsonObj.getString("IsCheckerVerify")));

                    }
                } catch (Exception ex) {
                    System.out.println("Error21 : " + ex);
                }
            } else if (strings[1].equals("check")) {
                type = strings[1];
                try {
                    System.out.println(response);
                } catch (Exception ex) {
                    System.out.println("Error31 : " + ex);
                }
            }

            return output;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            dialog.dismiss();

            if (type.equals("package")) {
                if (Integer.parseInt(s) == 0) {
                    //new MyToast(MainActivity.this, "ไม่มีรายการรับถุง", 0);

                } else {
                    MyAdapterProduct myAdapter = new MyAdapterProduct(MainActivity.this, R.layout.list_product, items, list);
                    list.setAdapter(myAdapter);
                }
            }/*else if(type.equals("check")){
                new MyToast(MainActivity.this,"บันทึกรายการเรียบร้อยแล้ว",2);
                Intent intent = new Intent(MainActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }*/

        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //preventing default implementation previous to android.os.Build.VERSION_CODES.ECLAIR
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onStart() {
        super.onStart();


        logout = findViewById(R.id.duo_view_footer_text);
        logout.setText("ออกจากระบบ");
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final ArrayList<String> arrBarcode = new ArrayList<>();
                mDb = mSQLite.getReadableDatabase();
                String sql = "SELECT * FROM tb_barcode";
                Cursor cursor = mDb.rawQuery(sql, null);
                while (cursor.moveToNext()) {
                    System.out.println(cursor.getString(1));
                    arrBarcode.add(cursor.getString(1));
                }
                if (arrBarcode.size() > 0) {
                    new MyToast(MainActivity.this, "กรุณณาเซ็นรับผ้าก่อน", 0);
                } else {
                    dialog_logout = new Dialog(MainActivity.this);
                    dialog_logout.setContentView(R.layout.custon_alert_dialog);
                    dialog_logout.show();
                    Window window = dialog_logout.getWindow();
                    window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                    TextView title = dialog_logout.findViewById(R.id.tv_quit_learning);
                    TextView des = dialog_logout.findViewById(R.id.tv_description);
                    title.setText("แจ้งเตือน");
                    des.setText("คุณต้องการออกจากระบบใช่หรือไม่");
                    Button declineButton = dialog_logout.findViewById(R.id.btn_cancel);
                    declineButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog_logout.dismiss();
                        }
                    });
                    Button okButton = dialog_logout.findViewById(R.id.btn_ok);
                    okButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String url = getIPAPI.IPAddress+"/CleanmateCheckerIN/Logout1.php?IsSignOn=" + 0 + "&id=" + ID;
                            new MyAsyncTask().execute(url, "logout");
                        }
                    });
                    MyFont myFont = new MyFont(MainActivity.this);
                    okButton.setTypeface(myFont.setFont());
                    declineButton.setTypeface(myFont.setFont());
                    title.setTypeface(myFont.setFont());
                    des.setTypeface(myFont.setFont());
                }
            }
        });

        checkerview =findViewById(R.id.checkerView);
        checkerview.setText("ดูข้อมูลสินค้า");
        checkerview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentCheckerView fragmentCheckerView = new FragmentCheckerView();
                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.container, fragmentCheckerView)
                        .commit();
                layout.setVisibility(View.GONE);
                scan.setEnabled(false);
                mViewHolder.mDuoDrawerLayout.closeDrawer();
            }
        });

        signature = findViewById(R.id.btn_signature);
        signature.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final ArrayList<String> arrBarcode = new ArrayList<>();
                mDb = mSQLite.getReadableDatabase();
                String sql = "SELECT * FROM tb_barcode";
                Cursor cursor = mDb.rawQuery(sql, null);
                while (cursor.moveToNext()) {
                    System.out.println(cursor.getString(1));
                    arrBarcode.add(cursor.getString(1));
                }

                if(arrBarcode.size()==0){
                    new MyToast(MainActivity.this,"ยังไม่มีรายการรับผ้า",0);
                }else {
                    FragmentSignature fragmentSignature = new FragmentSignature();
                    getFragmentManager()
                            .beginTransaction()
                            .replace(R.id.container, fragmentSignature)
                            .commit();
                    layout.setVisibility(View.GONE);
                    scan.setEnabled(false);
                }
            }
        });
    }
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
