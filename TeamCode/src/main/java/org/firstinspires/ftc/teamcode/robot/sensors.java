package org.firstinspires.ftc.teamcode.robot;

import android.app.Activity;
import android.view.View;

import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class sensors {
    NormalizedColorSensor frSensor;
    NormalizedColorSensor bkSensor;
    View relativeLayout;

    public sensors( HardwareMap ahwMap){
        frSensor = ahwMap.get(NormalizedColorSensor.class, "fr_sensor_color");
        bkSensor = ahwMap.get(NormalizedColorSensor.class,"bk_sensor_color");
        // Get a reference to the RelativeLayout so we can later change the background
        // color of the Robot Controller app to match the hue detected by the RGB sensor.
        int relativeLayoutId = ahwMap.appContext.getResources().getIdentifier("RelativeLayout", "id", ahwMap.appContext.getPackageName());
        relativeLayout = ((Activity) ahwMap.appContext).findViewById(relativeLayoutId);

    }

}
