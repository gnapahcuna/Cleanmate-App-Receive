package nl.psdcompany.duonavigationdrawer.example;

import android.app.Activity;
import android.app.Application;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class InitialFonts extends Application{
    ProgressDialog dialog;
    int ID=0;
    private GetIPAPI getIPAPI;
    @Override
    public void onCreate() {
        super.onCreate();
        /*CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Ayuthaya.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );*/
        getIPAPI=new GetIPAPI();

        registerActivityLifecycleCallbacks(new AppLifecycleCallback());
    }
    class AppLifecycleCallback implements ActivityLifecycleCallbacks {
        private int numStarted = 0;

        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

        }

        @Override
        public void onActivityStarted(Activity activity) {
            if (numStarted == 0) {
                //Toast.makeText(getBaseContext(),"Start "+numStarted,Toast.LENGTH_SHORT).show();
            }
            numStarted++;
        }

        @Override
        public void onActivityResumed(Activity activity) {

        }

        @Override
        public void onActivityPaused(Activity activity) {

        }

        @Override
        public void onActivityStopped(Activity activity) {
            numStarted--;
            if (numStarted == 0) {
                SharedPreferences sharedPreferences2 = getBaseContext().getSharedPreferences("ID", Activity.MODE_PRIVATE);
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
                            ID = Integer.parseInt(getData2[i].substring(3).trim());
                        }
                    }
                }

                System.out.println(ID);
                //Toast.makeText(getBaseContext(),"Stopped "+numStarted,Toast.LENGTH_SHORT).show();
                String url = getIPAPI.IPAddress+"/CleanmateCheckerIN/Logout.php?IsSignOn=" + 0 + "&id=" + ID;
                new MyAsyncTask().execute(url, "logout");
            }
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

        }

        @Override
        public void onActivityDestroyed(Activity activity) {

        }
    }

    class MyAsyncTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            /*dialog = new ProgressDialog(getBaseContext());
            dialog.setIcon(R.mipmap.loading);
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setMessage("กำลังตรวจสอบข้อมูล");
            dialog.setIndeterminate(true);
            dialog.setCancelable(false);
            dialog.show();*/
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

            return output;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //dialog.dismiss();
        }
    }
}
