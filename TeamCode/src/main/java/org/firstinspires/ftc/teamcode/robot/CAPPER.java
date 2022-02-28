package org.firstinspires.ftc.teamcode.robot;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;


public class CAPPER {

    public Servo UPDOWN;
    public Servo LR;
    public CRServo INOUT;

    private final Gamepad gamepad2;
    private final Telemetry telemetry;

    //starting positions
    private final double INITPOSUPDOW = .5;
    private final double INITPOSLR = .5;

    //current position
    private double CURUPDOWN = .5;
    private double CURLR = .5;

    private final double INITINOUT = .5;

    private ElapsedTime time = new ElapsedTime();

    public CAPPER (HardwareMap ahwMap, Telemetry telemetry, Gamepad c1){
        UPDOWN = ahwMap.get(Servo.class, "UPDOWN");
        LR = ahwMap.get(Servo.class, "LR");
        INOUT = ahwMap.get(CRServo.class,"INOUT");
        this.telemetry = telemetry;
        gamepad2 = c1;

        UPDOWN.setPosition(INITPOSUPDOW);
        LR.setPosition(INITPOSLR);
        INOUT.setPower(0);




    }

    private void LRupdatePositions() {
        LR.setPosition(CURLR);

    }
    private void UPDOWNupdatePositions() {
        UPDOWN.setPosition(CURUPDOWN);

    }

    public void LRmoveRelative(double deltaTicks) {
        CURLR += deltaTicks;

        LRupdatePositions();
    }

    public void UPDOWNmoveRelative(double deltaTicks){
        CURUPDOWN += deltaTicks;

        UPDOWNupdatePositions();
    }


    public void CapOut (){
        INOUT.setPower(1);
    }

    public void CapIn(){
        INOUT.setPower(-1);
    }

    public void CapOff(){
        INOUT.setPower(0);
    }

    public void pitchAsync(){
        if(gamepad2.left_stick_y > .2 && time.milliseconds() > 15){
            UPDOWNmoveRelative(.001);
            time.reset();
        }
        if(gamepad2.left_stick_y < -.2 && time.milliseconds() > 15){
            UPDOWNmoveRelative(-.001);
            time.reset();
        }
    }

    public void yawSurgeAsync(){
        if (gamepad2.right_stick_x > .2 && time.milliseconds() > 15){
            LRmoveRelative(.001);
            time.reset();
        }
        if(gamepad2.right_stick_x < -.2 && time.milliseconds() > 15){
            LRmoveRelative(.001);
            time.reset();
        }

        if(gamepad2.right_stick_y > .2 ){
            CapOut();
        }
        else if (gamepad2.right_stick_y < -.2){
            CapIn();
        }
        else{
            CapOff();
        }
    }




    //while button on dpad is pressed slowly increment the angle at which it facing(up and down)
    //while button on dpad is pressed slowly reverse the increment
    //while button is pressed move the angle to the right
    //while button is pressed move the angle to the left
    //while right Bumper is pressed move the tape measure out







}
