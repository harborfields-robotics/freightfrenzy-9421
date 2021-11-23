package org.firstinspires.ftc.teamcode.robot;

import com.qualcomm.robotcore.hardware.HardwareMap;


public class Hardware {
    HardwareMap hwMap = null;
    public Drivetrain dt = null;
    public Hardware(){}

    //Inits hardware for opmode
    public void init(HardwareMap ahwMap){
        //ahwMap is hwMap
        hwMap = ahwMap;
        dt = new Drivetrain(hwMap);
    }
}
