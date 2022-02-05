package org.firstinspires.ftc.teamcode.Auto;

import org.firstinspires.ftc.robotcore.external.Predicate;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.robot.Hardware;

public class AUTO_HELPER_FSM {
    enum DEPOSIT_STATE {
        STATE_0,
        STATE_1,
        STATE_2,
        STATE_3,
        STATE_4,
        STATE_5,
        STATE_6,
        STATE_7,
        STATE_8
    }
    private DEPOSIT_STATE deposit_state = DEPOSIT_STATE.STATE_0;
    private Hardware Oscar;
    private boolean busy = true;
    private boolean deposited = false;
    private Telemetry telemetry;
    public AUTO_HELPER_FSM(Hardware hardware, Telemetry telemetry) {
        this.Oscar = hardware;
        this.telemetry = telemetry;
    }
    //Assumes starting position is grab position
    private double interval0 = 200; //Grab stone and move elbow horizontal, home slides
    private double interval1 = 100; //Move elbow horizontal, start extend slides out
    private double interval3 = 250; //Finishes extending slides out, rotates elbow up, rotates basket to deposit position
    private double interval4 = 250; //Deposits stone by opening basket
    private double interval5 = 100; //Rotates elbow back to horizontal, closes grabber, retracts slides until ready to rotate elbow down to grab position
    private double interval6 = 500; //Rotates basket to start position
    private double interval7 = 130; //Open basket

    private double PREVIOUS_CYCLE_TIMES = 0;

    public boolean isBusy() {return busy;}
    public boolean isDeposited() {return deposited;}
    public void reset() {
        busy = true;
        deposited = false;
        deposit_state = DEPOSIT_STATE.STATE_0;
        PREVIOUS_CYCLE_TIMES = 0;
    }

    //When you use this, reset a timer before depositing and repeatedly call this method with updated times
    //Returns true if cycle is finished
    public void doDepositTopAsync(double milliseconds) {
        telemetry.addData("DEPOSIT STATE: ", deposit_state);
        telemetry.update();
        milliseconds -= PREVIOUS_CYCLE_TIMES;
        switch(deposit_state) {
            case STATE_0:
                if(milliseconds > interval0) {
                    PREVIOUS_CYCLE_TIMES += milliseconds;
                    deposit_state = DEPOSIT_STATE.STATE_1;
                }
                else {
                    Oscar.grabber.closeGrab();
                    Oscar.grabber.goStart();
                    Oscar.slides.slidesHomeAsync();
                    busy = true;
                    deposited = false;
                }
                break;
            case STATE_1:
                if(milliseconds > interval1) {
                    PREVIOUS_CYCLE_TIMES += milliseconds;
                    deposit_state = DEPOSIT_STATE.STATE_2;
                }
                else {
                    Oscar.slides.slidesTop();
                    Oscar.elbow.moveStart();
                }
                break;
            case STATE_2:
                if(Oscar.slides.getMotorPosition() > Oscar.slides.TOP_SLIDE_TICKS - 600) {
                    Oscar.slides.slidesTop();
                    Oscar.elbow.moveTop();
                    Oscar.grabber.goTop();
                    PREVIOUS_CYCLE_TIMES += milliseconds;
                    deposit_state = DEPOSIT_STATE.STATE_3;
                }
                break;
            case STATE_3:
                if(milliseconds > interval3) {
                    PREVIOUS_CYCLE_TIMES += milliseconds;
                    deposit_state = DEPOSIT_STATE.STATE_4;
                }
                else {
                    Oscar.slides.slidesTop();
                    Oscar.elbow.moveTop();
                    Oscar.grabber.goTop();
                }
                break;
            case STATE_4:
                if(milliseconds > interval4) {
                    PREVIOUS_CYCLE_TIMES += milliseconds;
                    deposit_state = DEPOSIT_STATE.STATE_5;
                }
                else {
                    Oscar.slides.slidesTop();
                    Oscar.grabber.openGrab();
                    Oscar.grabber.openGrabExtra();
                }
                break;
            case STATE_5:
                if(milliseconds > interval5) {
                    PREVIOUS_CYCLE_TIMES += milliseconds;
                    deposit_state = DEPOSIT_STATE.STATE_6;
                }
                else {
                    Oscar.slides.slidesGrab();
                    Oscar.elbow.moveStart();
                    Oscar.grabber.closeGrabExtra();
                    Oscar.grabber.closeGrab();
                    deposited = true;
                }
                break;
            case STATE_6:
                if(milliseconds > interval6) {
                    PREVIOUS_CYCLE_TIMES += milliseconds;
                    deposit_state = DEPOSIT_STATE.STATE_7;
                }
                else {
                    Oscar.grabber.goStart();
                }
                break;
            case STATE_7:
                if(Oscar.slides.getMotorPosition() < 600) {
                    Oscar.slides.slidesGrab();
                    Oscar.grabber.goStart();
                    Oscar.elbow.goToGrabPos();
                    PREVIOUS_CYCLE_TIMES += milliseconds;
                    deposit_state = DEPOSIT_STATE.STATE_8;
                }
                break;
            case STATE_8:
                PREVIOUS_CYCLE_TIMES = 0;
                busy = false;
                break;
            default:
                deposit_state = DEPOSIT_STATE.STATE_8;
                break;
        }
    }
}
