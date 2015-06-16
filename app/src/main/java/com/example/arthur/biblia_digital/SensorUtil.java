package com.example.arthur.biblia_digital;

import android.content.Context;
import android.hardware.SensorEvent;
import android.view.Display;
import android.view.Surface;
import android.view.WindowManager;

/**
 * Created by Arthur on 10/06/2015.
 */
public class SensorUtil {
    public static float[] fixAcelerometro(Context context, SensorEvent event) {
        float x = 0;
        float y = 0;
        float z = 0;

        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        switch (display.getRotation()) {
            case Surface.ROTATION_0:
                //Vertical
                x = -event.values[0];
                y = event.values[1];
                break;

            case Surface.ROTATION_90:
                //Horizontal - botoes para a direita
                x = event.values[1];
                y = event.values[0];
                break;

            case Surface.ROTATION_180:
                //Vertical - de ponta-cabeça
                x = -event.values[0];
                y = -event.values[1];

                break;
            case Surface.ROTATION_270:
                //Horizontal - botoes para a esquerda
                x = -event.values[1];
                y = -event.values[0];
                break;

        }//switch

        float[] values = new float[3];
        values[0] = x;
        values[1] = y;
        values[2] = z;

        return values;
    }

    public static String getRotationString(Context c) {
        WindowManager wm = (WindowManager)c.getSystemService(Context.WINDOW_SERVICE);
        Display d = wm.getDefaultDisplay();

        switch (d.getRotation()) {
            case Surface.ROTATION_0:
                return "Rotation_0";
            case Surface.ROTATION_90:
                return "Rotation_90";
            case Surface.ROTATION_180:
                return "Rotation_180";
            case Surface.ROTATION_270:
                return "Rotation_270";
        }

        return null;

    }
}
