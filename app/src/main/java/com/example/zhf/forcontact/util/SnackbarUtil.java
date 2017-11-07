package com.example.zhf.forcontact.util;

import android.support.design.widget.Snackbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

/**
 * Created by zhf on 2017/11/7.
 */

public class SnackbarUtil {

    public static Snackbar longSnackbar(View view, String message ){
        Snackbar snackbar = Snackbar.make(view,message,Snackbar.LENGTH_LONG);
        return snackbar;
    }

    public static Snackbar shortSnackbar(View view, String message ){
        Snackbar snackbar = Snackbar.make(view,message,Snackbar.LENGTH_SHORT);
        return snackbar;
    }

    /**
     * 向Snackbar中添加view
     * @param snackbar
     * @param layoutId
     * @param index 新加布局在Snackbar中的位置
     */
    public static void SnackbarAddView( Snackbar snackbar,int layoutId,int index) {
        View snackbarview = snackbar.getView();
        Snackbar.SnackbarLayout snackbarLayout=(Snackbar.SnackbarLayout)snackbarview;
        View add_view = LayoutInflater.from(snackbarview.getContext()).inflate(layoutId,null);
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams( LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        p.gravity= Gravity.CENTER_VERTICAL;
//        snackbarLayout.addView(add_view,index,p);
        snackbarLayout.addView(add_view,p);
    }

}
