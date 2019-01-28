package nl.psdcompany.duonavigationdrawer.example;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;

import com.fxn.cue.Cue;
import com.fxn.cue.enums.Duration;
import com.fxn.cue.enums.Type;

/**
 * Created by anucha on 2/7/2018.
 */

public class MyToast {
    public MyToast(Context context, String text, int type){

        if(type==0){
            Cue.init()
                    .with(context)
                    .setMessage(text)
                    .setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER)
                    .setType(Type.CUSTOM)
                    .setDuration(Duration.LONG)
                    .setBorderWidth(1)
                    .setCornerRadius(10)
                    .setCustomFontColor(Color.parseColor("#ff4d4d"),
                            Color.parseColor("#ffffff"),
                            Color.parseColor("#ff4d4d"))
                    .setFontFace("fonts/Abel_Regular.ttf")
                    .setPadding(30)
                    .setTextSize(20)
                    .show();
        }else if(type==1){
            Cue.init()
                    .with(context)
                    .setMessage(text)
                    .setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER)
                    .setType(Type.CUSTOM)
                    .setDuration(Duration.LONG)
                    .setBorderWidth(1)
                    .setCornerRadius(10)
                    .setCustomFontColor(Color.parseColor("#2eb82e"),
                            Color.parseColor("#ffffff"),
                            Color.parseColor("#2eb82e"))
                    .setFontFace("fonts/Abel_Regular.ttf")
                    .setPadding(30)
                    .setTextSize(20)
                    .show();
        } else if(type==2){
            Cue.init()
                    .with(context)
                    .setMessage(text)
                    .setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER)
                    .setType(Type.CUSTOM)
                    .setDuration(Duration.LONG)
                    .setBorderWidth(1)
                    .setCornerRadius(10)
                    .setCustomFontColor(Color.parseColor("#2eb82e"),
                            Color.parseColor("#ffffff"),
                            Color.parseColor("#2eb82e"))
                    .setFontFace("fonts/Abel_Regular.ttf")
                    .setPadding(30)
                    .setTextSize(20)
                    .show();
        }

    }
}
