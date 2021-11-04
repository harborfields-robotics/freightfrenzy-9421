package org.firstinspires.ftc.teamcode.robot;

import com.qualcomm.robotcore.hardware.HardwareMap;

public class Hardware {
    HardwareMap hwMap = null;
    public Drivetrain dt = null;
    public Hardware(){}

    //inits harware for opmode
    public void init(HardwareMap ahwMap){
        hwMap = ahwMap;
        dt = new Drivetrain(hwMap);
    }
}
