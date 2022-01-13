package org.firstinspires.ftc.teamcode.robot;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class Grabber {
    public Servo g0;
    public Servo g1;

    private final double GRAB_POSITION_OFFSET = .1;

    //start positions, duh
    private final double initPos0 = 1;
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
    private final double TOP_ANG = -220;
    private final double MID_ANG = -230;
    private final double BOTTOM_ANG = -240;
    private final double GRAB_ANG = -90;

    public Grabber(HardwareMap ahwMap){
        g0 = ahwMap.get(Servo.class, "grabber1");
        g1 = ahwMap.get(Servo.class, "grabber2");

        g0.setPosition(initPos0);
        g1.setPosition(initPos1);

        position = "start";
    }
    //call this after changing tracking variables
    private void updatePositions(String newPosition) {
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
        updatePositions(position);
    }

    //Same comment
    public void stopGrab() {
        if(isGrab) {
            isGrab = false;
            cur0 -= grabOffset0;
            cur1 += grabOffset1;
            updatePositions(position);
            //curMid does not change
        }
    }



    //Accepts RELATIVE!!!!! angle in degrees RELATIVE!!!!!!! (refer to diagram)
    //IF YOU CALL THIS IN OTHER CLASS, BE SURE TO RESET TO ORIGINAL POSITION AFTER!!!!!!!!!
    public void moveByAngle(double ang, String newPosition) {
        ang /= 270;
        cur0 += ang;
        updatePositions(newPosition);
        cur1 -= ang;
        updatePositions(newPosition);
    }

    public void goTop() {
        if(position.equals("start")) {
            moveByAngle(TOP_ANG, "top");
        }
        if(position.equals("middle")) {
            moveByAngle(TOP_ANG - MID_ANG, "top");
        }
        if(position.equals("bottom")) {
            moveByAngle(TOP_ANG - BOTTOM_ANG, "top");
        }
    }

    public void goBottom() {
        if(position.equals("start")) {
            moveByAngle(BOTTOM_ANG, "bottom");
        }
        if(position.equals("top")) {
            moveByAngle(BOTTOM_ANG - TOP_ANG, "bottom");
        }
        if(position.equals("middle")) {
            moveByAngle(BOTTOM_ANG - MID_ANG, "bottom");
        }
    }
    public void goMiddle() {
        if(position.equals("start")) {
            moveByAngle(MID_ANG, "middle");
        }
        if(position.equals("top")) {
            moveByAngle(MID_ANG - TOP_ANG, "middle");
        }
        if(position.equals("bottom")) {
            moveByAngle(MID_ANG - BOTTOM_ANG, "middle");
        }
    }
    public void goStart() {
        if(position.equals("top")) {
            moveByAngle(-TOP_ANG, "start");
        }
        if(position.equals("middle")) {
            moveByAngle(-MID_ANG, "start");
        }
        if(position.equals("bottom")) {
            moveByAngle(-BOTTOM_ANG, "start");
        }
    }
    public double returnAngle(){
        return curMid;
    }
    public boolean getIsGrab() {
        return isGrab;
    }

    public double getCur0() {
        return cur0;
    }
    public double getCur1() {
        return cur1;
    }
    public String getPosition() {
        return(position);
    }
}
