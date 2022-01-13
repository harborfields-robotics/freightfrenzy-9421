package org.firstinspires.ftc.teamcode.robot;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class Elgrab {
    //elbow
    public Servo e2;
    public Servo e3;

    private double curE2 = 0;
    private double curE3 = 1;

    private final double START_OFFSET = .26;
    private final double TOP_OFFSET = .65;
    private final double MID_OFFSET = .8;
    private final double BOTTOM_OFFSET = .9;
    private final double GRAB_POS_OFFSET = .12;


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

    //grabber variables
    public Servo g0;
    public Servo g1;

    private final double GRAB_POSITION_OFFSET = .1;

    //start positions, duh
    private final double initPos0 = .96;
    private final double initPos1 = .07;

    //how much each servo has to move in order to grab
    private final double grabOffset0 = -.15;
    private final double grabOffset1 = .15;

    //tracking position variables
    private boolean isGrab;
    //middle of cur0 and cur1 in servo tick thing (0-1)
    private double curMid;
    private double cur0 = initPos0;
    private double cur1 = initPos1;

    private String position;

    //ABSOLUTE!!!
    private final double TOP_ANG = -200;
    private final double MID_ANG = -230;
    private final double BOTTOM_ANG = -240;
    private final double GRAB_ANG = -90;

    public Elgrab(HardwareMap ahwMap){
        g0 = ahwMap.get(Servo.class, "grabber1");
        g1 = ahwMap.get(Servo.class, "grabber2");

        g0.setPosition(initPos0);
        g1.setPosition(initPos1);

        position = "start";

        e2 = ahwMap.get(Servo.class, "Elbow 2");
        e3 = ahwMap.get(Servo.class, "Elbow 3");

    }

    //GRABBER!!!!!!!!!!!
    //call this after changing tracking variables
    private void grabberUpdatePositions(String newPosition) {
        g0.setPosition(cur0);
        g1.setPosition(cur1);
        position = newPosition;
    }


    //DO NOT CHANGE THESE SIGNS!!!!!!!!!!!
    public void grab() {
        if(!isGrab) {
            isGrab = true;
            cur0 += grabOffset0;
            cur1 -= grabOffset1;
            //dont change position because its just grabbing
            //curMid does not change
        }
        else {
            isGrab = false;
            cur0 -= grabOffset0;
            cur1 += grabOffset1;
            //curMid does not change
        }
        grabberUpdatePositions(position);
    }

    //Same comment
    public void stopGrab() {
        if(isGrab) {
            isGrab = false;
            cur0 -= grabOffset0 - .15;
            cur1 += grabOffset1 + .15;
            grabberUpdatePositions(position);
            //curMid does not change
        }
    }



    //Accepts RELATIVE!!!!! angle in degrees RELATIVE!!!!!!! (refer to diagram)
    //IF YOU CALL THIS IN OTHER CLASS, BE SURE TO RESET TO ORIGINAL POSITION AFTER!!!!!!!!!
    public void grabberMoveByAngle(double ang, String newPosition) {
        ang /= 270;
        cur0 += ang;
        grabberUpdatePositions(newPosition);
        cur1 -= ang;
        grabberUpdatePositions(newPosition);
    }

    public void grabberGoTop() {
        if(position.equals("start")) {
            grabberMoveByAngle(TOP_ANG, "top");
        }
        if(position.equals("middle")) {
            grabberMoveByAngle(TOP_ANG - MID_ANG, "top");
        }
        if(position.equals("bottom")) {
            grabberMoveByAngle(TOP_ANG - BOTTOM_ANG, "top");
        }
    }

    public void grabberGoBottom() {
        if(position.equals("start")) {
            grabberMoveByAngle(BOTTOM_ANG, "bottom");
        }
        if(position.equals("top")) {
            grabberMoveByAngle(BOTTOM_ANG - TOP_ANG, "bottom");
        }
        if(position.equals("middle")) {
            grabberMoveByAngle(BOTTOM_ANG - MID_ANG, "bottom");
        }
    }
    public void grabberGoMiddle() {
        if(position.equals("start")) {
            grabberMoveByAngle(MID_ANG, "middle");
        }
        if(position.equals("top")) {
            grabberMoveByAngle(MID_ANG - TOP_ANG, "middle");
        }
        if(position.equals("bottom")) {
            grabberMoveByAngle(MID_ANG - BOTTOM_ANG, "middle");
        }
    }
    public void grabberGoStart() {
        if(position.equals("top")) {
            grabberMoveByAngle(-TOP_ANG, "start");
        }
        if(position.equals("middle")) {
            grabberMoveByAngle(-MID_ANG, "start");
        }
        if(position.equals("bottom")) {
            grabberMoveByAngle(-BOTTOM_ANG, "start");
        }
    }
    public double grabberReturnAngle(){
        return curMid;
    }
    public boolean grabberGetIsGrab() {
        return isGrab;
    }

    public double getCur0() {
        return cur0;
    }
    public double getCur1() {
        return cur1;
    }
    public String grabberGetPosition() {
        return(position);
    }


    //Elbow

    private void elbowUpdatePositions() {
        e2.setPosition(curE2);
        e3.setPosition(curE3);
    }

    public void elbowMoveStart() {
        elbowGoToGrabPos();
        curE2 = startE2;
        curE3 = startE3;
        elbowUpdatePositions();
    }
    public void elbowMoveTop() {
        curE2 = topE2;
        curE3 = topE3;
        elbowUpdatePositions();
    }
    public void elbowMoveMid() {
        curE2 = midE2;
        curE3 = midE3;
        elbowUpdatePositions();
    }
    public void elbowMoveBottom() {
        curE2 = bottomE2;
        curE3 = bottomE3;
        elbowUpdatePositions();
    }
    public void elbowGoToGrabPos(){
        curE2 = grabPosE2;
        curE3 = grabPosE3;
        elbowUpdatePositions();
    }

    public double getElbowPosition(){
        return curE2;
    }

    public void grabSequence(){
        curE2 = grabPosE2;
        curE3 = grabPosE3;
        grabberMoveByAngle(GRAB_POS_OFFSET, "start");
        elbowGoToGrabPos();
    }

}
