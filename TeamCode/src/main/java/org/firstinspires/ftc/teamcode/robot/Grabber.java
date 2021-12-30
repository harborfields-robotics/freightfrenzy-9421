package org.firstinspires.ftc.teamcode.robot;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class Grabber {
    public Servo g0;
    public Servo g1;

    //start positions, duh
    private final double initPos0 = .98;
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
    private double topAng = -220;
    private double midAng = -230;
    private double bottomAng = -240;
    private double startAng = -90;

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
        if(isGrab == false) {
            isGrab = true;
            cur0 += grabOffset0;
            cur1 -= grabOffset1;
            //dont change position because its just grabbing
            updatePositions(position);
            //curMid does not change
        }
    }
    //Same comment
    public void stopGrab() {
        if(isGrab == true) {
            isGrab = false;
            cur0 -= grabOffset0;
            cur1 += grabOffset1;
            updatePositions(position);
            //curMid does not change
        }
    }



    //Accepts RELATIVE!!!!! angle in degrees RELATIVE!!!!!!! (refer to diagram)
    private void moveByAngle(double ang, String newPosition) {
        ang /= 270;
        cur0 += ang;
        updatePositions(newPosition);
        cur1 -= ang;
        updatePositions(newPosition);
    }

    public void goTop() {
        if(position.equals("start")) {
            moveByAngle(topAng, "top");
        }
        if(position.equals("middle")) {
            moveByAngle(topAng - midAng, "top");
        }
        if(position.equals("bottom")) {
            moveByAngle(topAng - bottomAng, "top");
        }
    }

    public void goBottom() {
        if(position.equals("start")) {
            moveByAngle(bottomAng, "bottom");
        }
        if(position.equals("top")) {
            moveByAngle(bottomAng - topAng, "bottom");
        }
        if(position.equals("middle")) {
            moveByAngle(bottomAng - midAng, "bottom");
        }
    }
    public void goMiddle() {
        if(position.equals("start")) {
            moveByAngle(midAng, "middle");
        }
        if(position.equals("top")) {
            moveByAngle(midAng - topAng, "middle");
        }
        if(position.equals("bottom")) {
            moveByAngle(midAng - bottomAng, "middle");
        }
    }
    public void goStart() {
        if(position.equals("top")) {
            moveByAngle(-topAng, "start");
        }
        if(position.equals("middle")) {
            moveByAngle(-midAng, "start");
        }
        if(position.equals("bottom")) {
            moveByAngle(-bottomAng, "start");
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
