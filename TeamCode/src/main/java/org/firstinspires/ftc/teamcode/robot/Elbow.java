package org.firstinspires.ftc.teamcode.robot;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class Elbow {
    public Servo e2;
    public Servo e3;

    private double curE2 = 0;
    private double curE3 = 1;

    private final double START_OFFSET = .26;
    private final double TOP_OFFSET = .65;
    private final double MID_OFFSET = .8;
    private final double BOTTOM_OFFSET = .9;
    private final double GRAB_POS_OFFSET = .2;


    private final double startE2 = curE2 + START_OFFSET;
    private final double startE3 = curE3 - START_OFFSET;

    private final double topE2 = curE2 + TOP_OFFSET;
    private final double topE3 = curE3 - TOP_OFFSET;

    private final double midE2 = curE2 + MID_OFFSET;
    private final double midE3 = curE3 - MID_OFFSET;

    private final double bottomE2 = curE2 + BOTTOM_OFFSET;
    private final double bottomE3 = curE3 - BOTTOM_OFFSET;

    private final double grabPosE2 = curE2 + GRAB_POS_OFFSET;
    private final double grabPosE3 = curE3 - GRAB_POS_OFFSET;


    public Elbow(HardwareMap ahwMap) {
        e2 = ahwMap.get(Servo.class, "Elbow 2");
        e3 = ahwMap.get(Servo.class, "Elbow 3");

    }

    private void updatePositions() {
        e2.setPosition(curE2);
        e3.setPosition(curE3);
    }

    public void moveStart() {
        goToGrabPos();
        curE2 = startE2;
        curE3 = startE3;
        updatePositions();
    }
    public void moveTop() {
        curE2 = topE2;
        curE3 = topE3;
        updatePositions();
    }
    public void moveMid() {
        curE2 = midE2;
        curE3 = midE3;
        updatePositions();
    }
    public void moveBottom() {
        curE2 = bottomE2;
        curE3 = bottomE3;
        updatePositions();
    }
    public void goToGrabPos(){
        curE2 = grabPosE2;
        curE3 = grabPosE3;
        updatePositions();
    }

    public double getElbowPosition(){
        return curE2;
    }

}
