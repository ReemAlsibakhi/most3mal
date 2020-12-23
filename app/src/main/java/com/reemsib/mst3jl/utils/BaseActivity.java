package com.reemsib.mst3jl.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ContextThemeWrapper;

import com.reemsib.mst3jl.R;

public  class BaseActivity extends AppCompatActivity {
    public  static  AlertDialog loading(Context context){
//        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.myDialog));

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.layout_custom_progress_bar, null);
        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        dialog.show();
        return dialog;
    }
    public void loadingDialog(Context c,Boolean b){
        AlertDialog dialog=loading(c);
        if (b){
           if (!dialog.isShowing()){
               dialog.show();
           }
        }else {
            if (dialog.isShowing()){
                dialog.dismiss();
            }
        }
    }

}

