package org.firstinspires.ftc.teamcode.robot;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import dashboard.RobotConstants;

public class Elbow {
    public Servo e2;
    public Servo e3;

    private double curE2 = .62;
    private double curE3 = .72;

    private double startOffset = -.2;
    private double topOffset = .33;
    private double midOffset = .45;
    private double bottomOffset = .7;
    private double grabPosOffset = RobotConstants.GRAB_POS_OFFSET;


    private final double startE2 = curE2 + startOffset;
    private final double startE3 = curE3 - startOffset;

    private final double topE2 = curE2 + topOffset;
    private final double topE3 = curE3 - topOffset;

    private final double midE2 = curE2 + midOffset;
    private final double midE3 = curE3 - midOffset;

    private final double bottomE2 = curE2 + bottomOffset;
    private final double bottomE3 = curE3 - bottomOffset;

    private final double grabPosE2 = curE2 + grabPosOffset;
    private final double grabPosE3 = curE3 - grabPosOffset;


    public Elbow(HardwareMap ahwMap) {
        e2 = ahwMap.get(Servo.class, "Elbow 2");
        e3 = ahwMap.get(Servo.class, "Elbow 3");

    }

    private void updatePositions() {
        e2.setPosition(curE2);
        e3.setPosition(curE3);
    }

    public void moveStart() {
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
        return curE3;
    }

}
