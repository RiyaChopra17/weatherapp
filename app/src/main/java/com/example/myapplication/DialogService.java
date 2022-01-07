package com.example.myapplication;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;



import java.util.ArrayList;
import java.util.List;



/**
 * Created by vicky on 4/28/2016.
 */

public class DialogService {

    static BussinessTypecallbackinterface onPostInterface;

    public static  Dialog dialog;
    public static Dialog yesNoDialog;
    public  static EditText et_dialog;
    public  static TextView error_dialog;
    public static void customDialog(Context dialogContext, String message, final boolean flag, final Intent intent) {

        dialog = new Dialog(dialogContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        dialog.setContentView(R.layout.dialog_custom);
        Button btn = ((Button) dialog.findViewById(R.id.btnDialogOk));
        //TextView dailogTitleTxtVw = ((TextView) dialog.findViewById(R.id.dailogTitleTxtVw));
        TextView txtDialogMessage = ((TextView) dialog.findViewById(R.id.txtDialogMessage));
        ImageView dialogheaderIconImagevw= ((ImageView) dialog.findViewById(R.id.dialogheaderIconImagevw));

//            CommonFonts.setFonts(txtDialogMessage,dialogContext,1);
//        CommonFonts.setFonts(dailogTitleTxtVw,dialogContext,4);
//        CommonFonts.setFonts(btn,dialogContext,1);
        if(intent!=null){
            if(flag){
                dialog.setCanceledOnTouchOutside(false);
                dialogheaderIconImagevw.setImageDrawable(null);
               // dailogTitleTxtVw.setText("Success");
                dialogheaderIconImagevw.setBackgroundResource(R.drawable.ic_cloud_queue_dark);
            }

        }

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                dialog.dismiss();
                if(intent!=null){
                   /* UserPreference userPreference = new UserPreference(dialogContext);
                    userPreference.logoutDone();

                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    act.startActivity(intent);
                    act.finish();*/
                    Activity act = (Activity) dialogContext;
                   // logoutService(act);
                }else{
                    if (flag) {
                        Activity act = (Activity) dialogContext;
                        act.finish();
                    }
                }

            }
        });

        ((TextView) dialog.findViewById(R.id.txtDialogMessage)).setText(message);
        //CommonFonts.setFonts(btn,dialogContext,1);

        dialog.show();
    }






    public interface BussinessTypecallbackinterface {
        void onPostExecute(String name, int id);
    }
    public void onCallBack(BussinessTypecallbackinterface onpost) {

        onPostInterface = onpost;
    }




    public static void dismissYesNoDailog(){
        yesNoDialog.dismiss();
    }


}
