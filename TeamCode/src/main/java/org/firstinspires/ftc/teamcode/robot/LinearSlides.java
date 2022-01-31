package org.firstinspires.ftc.teamcode.robot;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.DigitalChannel;

import dashboard.RobotConstants;

public class LinearSlides {

    public DcMotor slideMotor1;
    public DcMotor slideMotor2;
    public DigitalChannel endstop;



    //position when the slides start
    public double ORIGINAL_POSITION = 0;
    //power motor uses for slides
    public static double SLIDE_POWER = -1;
    //Max length
    public static double MAX_LENGTH = RobotConstants.SLIDE_MAX_LENGTH;
    public double TOP_SLIDE_TICKS = 1700;
    private double MID_SLIDE_TICKS = 1000;
    private double BOTTOM_SLIDE_TICKS = 500;
    private double GRAB_SLIDE_TICKS = 0;


    //Must tune to get more efficient
    private double[] positions = {0.0, TOP_SLIDE_TICKS, MID_SLIDE_TICKS, BOTTOM_SLIDE_TICKS};

    private double currentPosition = 0.0;
    private int arrayPos = 0;

    public LinearSlides(HardwareMap ahwMap){
        slideMotor1 = ahwMap.get(DcMotor.class, "slideMotor1");
       // slideMotor2 = ahwMap.get(DcMotor.class, "slideMotor2");
        endstop = ahwMap.get(DigitalChannel.class, "lift_limit_switch");
        slideMotor1.setDirection(DcMotor.Direction.FORWARD);
        slideMotor1.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        slideMotor1.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
       // slideMotor2.setDirection(DcMotor.Direction.FORWARD);
       // slideMotor2.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
      //  slideMotor2.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        resetEncoder();



    }
    public void out(){

        slideMotor1.setPower(SLIDE_POWER);
        //slideMotor2.setPower(SLIDE_POWER);
    }
    public void in(){
        slideMotor1.setPower(SLIDE_POWER);
        //slideMotor2.setPower(-SLIDE_POWER);
    }
    public void stop(){
        slideMotor1.setPower(0);
       // slideMotor2.setPower(0);
    }

    public int getMotorPosition(){
        return slideMotor1.getCurrentPosition();
    }

    public void positionCorrection(){
        double error = positions[arrayPos] - getMotorPosition();

        slideMotor1.setPower(error * 0.001);
    }

    public void setEncoderPosition(int val) {
        slideMotor1.setTargetPosition(val);
    }

    public void resetEncoder(){
        slideMotor1.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        slideMotor1.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        //slideMotor2.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        //slideMotor2.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }
    //lets see if this works
    public void slidesTop(){
        currentPosition = TOP_SLIDE_TICKS; arrayPos = 1;
        slideMotor1.setTargetPosition((int)currentPosition);

        //slideMotor2.setTargetPosition((int)-currentPosition);

        //slideMotor2.setMode((DcMotor.RunMode.RUN_TO_POSITION));
        slideMotor1.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        out();
    }
    public void slidesMid(){ currentPosition = MID_SLIDE_TICKS; arrayPos = 2; slideMotor1.setTargetPosition((int)currentPosition);
        slideMotor1.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        out();
    }
    public void slidesBottom(){currentPosition = BOTTOM_SLIDE_TICKS; arrayPos = 3;slideMotor1.setTargetPosition((int)currentPosition);
        slideMotor1.setMode(DcMotor.RunMode.RUN_TO_POSITION);}

    public void slidesGrab(){
        currentPosition = GRAB_SLIDE_TICKS;
        arrayPos = 0;
        slideMotor1.setTargetPosition((int)currentPosition);
        slideMotor1.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        out();
    }


    public void slidesHome() {
        boolean run = true;
        while (run) {
            slideMotor1.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            slideMotor1.setPower(-.4);
            //slideMotor2.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            //slideMotor2.setPower(-.3);

            if (!endstop.getState()) {
                slideMotor1.setPower(0);
                //slideMotor2.setPower(0);
                currentPosition = 0;
                resetEncoder();
                run = false;
            }
        }
        slidesGrab();
    }


    public double getCurrentTargetPosition(){
        return positions[arrayPos];
    }

    public boolean getEndstop(){ return endstop.getState();}



}
