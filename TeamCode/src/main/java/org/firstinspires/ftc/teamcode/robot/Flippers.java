package org.firstinspires.ftc.teamcode.robot;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class Flippers {

    //f0 is front, f1 is back

    private Servo f0;
    private Servo f1;

    private double downPosition0 = 0;
    private double downPosition1 = 1;

    private double upPosition0 = .42;
    private double upPosition1 = .58;


    public Flippers(HardwareMap ahwMap) {
        //f0 = ahwMap.get(Servo.class, "Flipper 0");
        f1 = ahwMap.get(Servo.class, "Flipper 1");
    }
    public void moveDown(String frontOrBack) {
//        f0.setPosition((frontOrBack.equals("front") ? downPosition0 : upPosition0));
        f1.setPosition(frontOrBack.equals("back") ? downPosition1 : upPosition1);
    }
    public void moveUp(String frontOrBack) {
//        f0.setPosition((frontOrBack.equals("front") ? upPosition0 : downPosition0));
        f1.setPosition(frontOrBack.equals("back") ? upPosition1 : downPosition1);
    }
}
