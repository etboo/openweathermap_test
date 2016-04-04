package com.etb.mainsoftweather;

import android.content.Context;
import android.os.Build;

import java.util.Random;

/**
 * Created by etb on 04.04.16.
 */
public class MaterialColorPicker {

    private static int[] sColors = {R.color.material_blue, R.color.material_green,
            R.color.material_orange, R.color.material_pink,
            R.color.material_red, R.color.material_yellow};

    private static Random sGenerator = new Random();
    private static int sPrev;

    public static int getMaterialColor(Context context){

        int color = sColors[getRandomValue()];

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return context.getColor(color);
        } else{
            return context.getResources().getColor(color);
        }
    }

    public static int getRandomValue(){
        while (true){
            int value = sGenerator.nextInt(sColors.length);
            if(value != sPrev){
                sPrev = value;
                return value;
            }
        }
    }
}