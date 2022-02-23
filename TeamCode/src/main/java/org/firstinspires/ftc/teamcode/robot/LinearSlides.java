package org.firstinspires.ftc.teamcode.robot;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.DigitalChannel;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import dashboard.RobotConstants;

public class LinearSlides {

    public DcMotor slideMotor1;
    public DigitalChannel endstop;

    //position when the slides start
    public double ORIGINAL_POSITION = 0;
    //power motor uses for slides
    public static double SLIDE_POWER = -1;
    //Max length
    public static double MAX_LENGTH = RobotConstants.SLIDE_MAX_LENGTH;
    public double TOP_SLIDE_TICKS = 420;
    private double MID_SLIDE_TICKS = 222;
    private double OUT_A_BIT_SLIDE_TICKS = 100;
    private double BOTTOM_SLIDE_TICKS = 218;
    private double GRAB_SLIDE_TICKS = 15;

    private double ADJUSTABLE_TOP_TICKS = TOP_SLIDE_TICKS;

    private Telemetry telemetry;

    private double THRESHOLD = 1;

    private double currentPosition = 0.0;
    private int arrayPos = 0;

    public LinearSlides(HardwareMap ahwMap, Telemetry telemetry){
        slideMotor1 = ahwMap.get(DcMotor.class, "slideMotor1");
        slideMotor1.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        // slideMotor2 = ahwMap.get(DcMotor.class, "slideMotor2");
        endstop = ahwMap.get(DigitalChannel.class, "lift_limit_switch");
        slideMotor1.setDirection(DcMotor.Direction.FORWARD);
        slideMotor1.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        slideMotor1.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        this.telemetry = telemetry;

        resetEncoder();
    }

    private boolean isItInThreshold(double desiredPosition) {
        return(slideMotor1.getCurrentPosition() > desiredPosition - THRESHOLD && slideMotor1.getCurrentPosition() < desiredPosition + THRESHOLD);
    }

    public void out(){

        slideMotor1.setPower(SLIDE_POWER);

    }
    public void in(){
        slideMotor1.setPower(SLIDE_POWER);

    }
    public void stop(){
        slideMotor1.setPower(0);

    }

    public void CHANGE_ADJUSTABLE_TOP_TICKS(double DELTA_TICKS) {
        ADJUSTABLE_TOP_TICKS += DELTA_TICKS;
    }

    public void RESET_ADJUSTABLE_TOP_TICKS() {
        ADJUSTABLE_TOP_TICKS = TOP_SLIDE_TICKS;
    }

    public void GO_TO_ADJUSTABLE_TOP_POSITION(){
        currentPosition = ADJUSTABLE_TOP_TICKS;
        slideMotor1.setTargetPosition((int)currentPosition);
        slideMotor1.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        if(isItInThreshold(currentPosition)){
            slideMotor1.setPower(0);
        }
        else out();
    }

    public int getMotorPosition(){
        return slideMotor1.getCurrentPosition();
    }

    public void setEncoderPosition(int val) {
        slideMotor1.setTargetPosition(val);
    }

    public void resetEncoder(){
        slideMotor1.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        slideMotor1.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    public void slidesTop(){
        currentPosition = TOP_SLIDE_TICKS;
        slideMotor1.setTargetPosition((int)currentPosition);
        slideMotor1.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        if(isItInThreshold(currentPosition)){
            slideMotor1.setPower(0);
        }
        else out();
    }
    public void slidesMid(){
        currentPosition = MID_SLIDE_TICKS;
        slideMotor1.setTargetPosition((int)currentPosition);
        slideMotor1.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        if(isItInThreshold(currentPosition)){
            slideMotor1.setPower(0);

        }
        else out();
    }
    public void slidesOutABit(){
        currentPosition = OUT_A_BIT_SLIDE_TICKS;
        slideMotor1.setTargetPosition((int)currentPosition);
        slideMotor1.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        if(isItInThreshold(currentPosition)){
            slideMotor1.setPower(0);

        }
        else out();
    }

    public void slidesBottom(){
        currentPosition = BOTTOM_SLIDE_TICKS;
        slideMotor1.setTargetPosition((int)currentPosition);
        slideMotor1.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        if(isItInThreshold(currentPosition)){
            slideMotor1.setPower(0);

        }
        else out();
    }

    public void slidesGrab(){
        currentPosition = GRAB_SLIDE_TICKS;
        arrayPos = 0;
        slideMotor1.setTargetPosition((int)currentPosition);
        slideMotor1.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        if(isItInThreshold(currentPosition)){
            slideMotor1.setPower(0);
        }
        else out();
    }

    public void relativeMove(int ticks) {
        currentPosition += ticks;
        slideMotor1.setTargetPosition((int) currentPosition);
        slideMotor1.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        if(isItInThreshold(currentPosition)){
            slideMotor1.setPower(0);
        }
        out();
    }

    public void slidesRelativeOut(int ticks) {
        slideMotor1.setTargetPosition(ticks);
        slideMotor1.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        out();
    }

    public void slidesAbsoluteOut(){
        slideMotor1.setTargetPosition((int)GRAB_SLIDE_TICKS + 200);
        slideMotor1.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        if(isItInThreshold(currentPosition)){
            slideMotor1.setPower(0);
        }
        out();
    }
    public void slidesAbsolutIn(){
        slideMotor1.setTargetPosition((int)GRAB_SLIDE_TICKS);
        slideMotor1.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        if(isItInThreshold(currentPosition)){
            slideMotor1.setPower(0);
        }
        out();
    }

    public void slidesHome() {
        boolean run = true;
        while (run) {
            slideMotor1.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            slideMotor1.setPower(-.8);
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

    public void slidesHomeAsync() {
        //if not clicked
        if(endstop.getState()) {
            slideMotor1.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            slideMotor1.setPower(-.4);
            resetEncoder();
        }
        //if clicked
        else {
            slideMotor1.setPower(0);
        }
    }

    public void slidesHold() {
        slideMotor1.setPower(0);
    }

    public boolean getEndstop(){ return endstop.getState();}

}
