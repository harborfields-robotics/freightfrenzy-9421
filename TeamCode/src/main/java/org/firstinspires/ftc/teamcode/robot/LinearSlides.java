package org.firstinspires.ftc.teamcode.robot;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.DigitalChannel;

import dashboard.RobotConstants;

public class LinearSlides {

    public DcMotor slideMotor;
    public DigitalChannel endstop;



    //position when the slides start
    public double ORIGINAL_POSITION = 0;
    //power motor uses for slides
    public static double SLIDE_POWER = .5;
    //Max length
    public static double MAX_LENGTH = RobotConstants.SLIDE_MAX_LENGTH;
    private double TOP_SLIDE_TICKS = -3667;
    private double MID_SLIDE_TICKS = -3056;
    private double BOTTOM_SLIDE_TICKS = -2445;
    private double GRAB_SLIDE_TICKS = -100;


    //Must tune to get more efficient
    private double[] positions = {0.0, TOP_SLIDE_TICKS, MID_SLIDE_TICKS, BOTTOM_SLIDE_TICKS};

    private double currentPosition = 0.0;
    private int arrayPos = 0;

    public LinearSlides(HardwareMap ahwMap){
        slideMotor = ahwMap.get(DcMotor.class, "slideMotor");
        endstop = ahwMap.get(DigitalChannel.class, "lift_limit_switch");
        slideMotor.setDirection(DcMotor.Direction.FORWARD);
        slideMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        slideMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        resetEncoder();



    }
    public void out(){
        slideMotor.setPower(SLIDE_POWER);
    }
    public void in(){
        slideMotor.setPower(-SLIDE_POWER);
    }
    public void stop(){
        slideMotor.setPower(0);
    }

    public double getMotorPosition(){
        return slideMotor.getCurrentPosition();
    }

    public void positionCorrection(){
        double error = positions[arrayPos] - getMotorPosition();

        slideMotor.setPower(error * 0.001);
    }



    public void resetEncoder(){ slideMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        slideMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }
    //lets see if this works
    public void slidesTop(){ currentPosition = TOP_SLIDE_TICKS; arrayPos = 1; slideMotor.setTargetPosition((int)currentPosition);
        slideMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    out();}
    public void slidesMid(){ currentPosition = MID_SLIDE_TICKS; arrayPos = 2; slideMotor.setTargetPosition((int)currentPosition);
        slideMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        out();
    }
    public void slidesBottom(){currentPosition = BOTTOM_SLIDE_TICKS; arrayPos = 3;slideMotor.setTargetPosition((int)currentPosition);
        slideMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);}

    public void slidesGrab(){currentPosition = GRAB_SLIDE_TICKS; arrayPos = 0;slideMotor.setTargetPosition((int)currentPosition);
        slideMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);}


    public void slidesHome() {
        boolean run = true;
        while (run) {
            slideMotor.setPower(-.1);
            if (endstop.getState()) {
                slideMotor.setPower(0);
                currentPosition = 0;
                resetEncoder();
                run = false;
            }
        }
    }


    public double getCurrentTargetPosition(){
        return positions[arrayPos];
    }



}
