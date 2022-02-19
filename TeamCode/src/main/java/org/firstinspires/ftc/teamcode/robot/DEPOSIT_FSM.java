package org.firstinspires.ftc.teamcode.robot;

import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class DEPOSIT_FSM {
    enum DEPOSIT_STATE {
        INIT,
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
    enum MID_DEPOSIT_STATE {
        INIT,
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
    enum BOTTOM_DEPOSIT_STATE {
        INIT,
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
    private DEPOSIT_STATE deposit_state = DEPOSIT_STATE.INIT;
    private MID_DEPOSIT_STATE mid_deposit_state = MID_DEPOSIT_STATE.INIT;
    private BOTTOM_DEPOSIT_STATE bottom_deposit_state = BOTTOM_DEPOSIT_STATE.INIT;
    private final Hardware Oscar;
    private final Gamepad gamepad1;
    private final Gamepad gamepad2;
    private boolean topBusy = false;
    private boolean midBusy = false;
    private boolean bottomBusy = false;
    private boolean midDeposited = false;
    private boolean bottomDeposited = false;

    private boolean deposited = false;
    private final Telemetry telemetry;

    public DEPOSIT_FSM(Hardware hardware, Telemetry telemetry, Gamepad c1, Gamepad c2) {
        this.Oscar = hardware;
        this.telemetry = telemetry;
        gamepad1 = c1;
        gamepad2 = c2;
    }

    private final ElapsedTime time = new ElapsedTime();

    public boolean isTopBusy() {return topBusy;}
    public boolean isDeposited() {return deposited;}

    public void reset() {
        topBusy = false;
        deposited = false;
        deposit_state = DEPOSIT_STATE.INIT;
        time.reset();
    }

    public void doDepositTopAsync() {
        telemetry.addData("DEPOSIT STATE: ", deposit_state);
//        telemetry.update();
        switch(deposit_state) {
            case INIT:
                if((gamepad2.triangle || gamepad1.triangle) && !midBusy) {
                    deposit_state = DEPOSIT_STATE.STATE_0;
                    topBusy = true;
                    deposited = false;
                    time.reset();
                }
                else {
                    topBusy = false;
                    deposited = false;
                    if(topBusy) {}
                }
                break;
            case STATE_0:
                if(time.milliseconds() > 50) {
                    deposit_state = DEPOSIT_STATE.STATE_1;
                    time.reset();
                }
                else {
                    Oscar.slides.slidesTop();
                    Oscar.grabber.closeGrab();
                    Oscar.grabber.goStart();
                    Oscar.elbow.moveStart();
                }
                break;
            case STATE_1:
                if(Oscar.slides.getMotorPosition() > 300) {
                    Oscar.slides.slidesTop();
                    Oscar.elbow.moveTop();
                    Oscar.grabber.goTop();
                    deposit_state = DEPOSIT_STATE.STATE_2;
                    time.reset();
                }
                else {
                    Oscar.slides.slidesTop();
                }
                break;
            case STATE_2:
                if(gamepad1.triangle || gamepad2.triangle) {
                    deposit_state = DEPOSIT_STATE.STATE_3;
                    time.reset();
                }
                else {
                    Oscar.slides.slidesTop();
                }
                break;
            case STATE_3:
                if(time.milliseconds() > 300) {
                    deposit_state = DEPOSIT_STATE.STATE_4;
                    deposited = true;
                    time.reset();
                }
                else {
                    Oscar.slides.slidesHold();
                    Oscar.grabber.openGrab();
                    Oscar.grabber.openGrabExtra();
                }
                break;
            case STATE_4:
                if(time.milliseconds() > 150) {
                    deposit_state = DEPOSIT_STATE.STATE_5;
                    time.reset();
                }
                else {
                    Oscar.slides.slidesHold();
                    Oscar.elbow.goToGrabPos();
                    Oscar.grabber.closeGrabExtra();
                }
                break;
            case STATE_5:
                if(Oscar.slides.getMotorPosition() < 380) {
                    deposit_state = DEPOSIT_STATE.STATE_6;
                    Oscar.grabber.goStart();
                    time.reset();
                }
                else {
                    Oscar.slides.slidesGrab();
                }
                break;
            case STATE_6:
                if(time.milliseconds() > 500) {
                    reset();
                    deposit_state = DEPOSIT_STATE.INIT;
                }
                else {
                    Oscar.slides.slidesGrab();
                }
                break;
            default:
                deposit_state = DEPOSIT_STATE.INIT;
                break;
        }
    }
    public void doDepositMiddleAsync() {
        telemetry.addData("DEPOSIT STATE (MIDDLE): ", mid_deposit_state);
        switch(mid_deposit_state) {
            case INIT:
                if(gamepad2.square && !topBusy) {
                    mid_deposit_state = MID_DEPOSIT_STATE.STATE_0;
                    midBusy = true;
                    midDeposited = false;
                    time.reset();
                }
                else {
                    midBusy = false;
                    midDeposited = false;
                }
                break;
            case STATE_0:
                if(time.milliseconds() > 50) {
                    mid_deposit_state = MID_DEPOSIT_STATE.STATE_1;
                    time.reset();
                }
                else {
                    Oscar.slides.slidesOutABit();
                    Oscar.grabber.closeGrab();
                    Oscar.grabber.goStart();
                    Oscar.elbow.moveStart();
                }
                break;
            case STATE_1:
                if(time.milliseconds() > 700) {
                    Oscar.slides.slidesMid();
                    mid_deposit_state = MID_DEPOSIT_STATE.STATE_2;
                    time.reset();
                }
                else {
                    Oscar.slides.slidesOutABit();
                    Oscar.elbow.moveMid();
                    Oscar.grabber.goMiddle();
                }
                break;
            case STATE_2:
                if(gamepad2.square) {
                    mid_deposit_state = MID_DEPOSIT_STATE.STATE_3;
                    time.reset();
                }
                else {
                    Oscar.slides.slidesMid();
                }
                break;
            case STATE_3:
                if(time.milliseconds() > 200) {
                    mid_deposit_state = MID_DEPOSIT_STATE.STATE_4;
                    midDeposited = true;
                    time.reset();
                }
                else {
                    Oscar.slides.slidesHold();
                    Oscar.grabber.openGrab();
                    Oscar.grabber.openGrabExtra();
                }
                break;
            case STATE_4:
                if(time.milliseconds() > 800) {
                    mid_deposit_state = MID_DEPOSIT_STATE.STATE_5;
                    time.reset();
                }
                else {
                    Oscar.slides.slidesHold();
                    Oscar.elbow.goToGrabPos();
                    Oscar.grabber.closeGrabExtra();
                    Oscar.slides.slidesOutABit();
                }
                break;
            case STATE_5:
                if(Oscar.slides.getMotorPosition() < 250) {
                    mid_deposit_state = MID_DEPOSIT_STATE.STATE_6;
                    Oscar.grabber.goStart();
                    time.reset();
                }
                else {
                    Oscar.slides.slidesGrab();
                }
                break;
            case STATE_6:
                if(time.milliseconds() > 500) {
                    reset();
                    mid_deposit_state = MID_DEPOSIT_STATE.INIT;
                }
                else {
                    Oscar.slides.slidesGrab();
                }
                break;
            default:
                mid_deposit_state = MID_DEPOSIT_STATE.INIT;
                break;
        }
    }
    public void doDepositBottomAsync() {
        telemetry.addData("DEPOSIT STATE (MIDDLE): ", mid_deposit_state);
        switch(bottom_deposit_state) {
            case INIT:
                if(gamepad2.cross && !topBusy && !midBusy) {
                    bottom_deposit_state = BOTTOM_DEPOSIT_STATE.STATE_0;
                    bottomBusy = true;
                    bottomDeposited = false;
                    time.reset();
                }
                else {
                    bottomBusy = false;
                    bottomDeposited = false;
                }
                break;
            case STATE_0:
                if(time.milliseconds() > 50) {
                    bottom_deposit_state = BOTTOM_DEPOSIT_STATE.STATE_1;
                    time.reset();
                }
                else {
                    //very interesting name( SLides out a bit)
                    Oscar.slides.slidesOutABit();
                    Oscar.grabber.closeGrab();
                    Oscar.grabber.goStart();
                    Oscar.elbow.moveStart();
                }
                break;
            case STATE_1:
                if(time.milliseconds() > 600) {
                    //TODO: Change the value for slidesBottom
                    Oscar.slides.slidesBottom();
                    bottom_deposit_state = BOTTOM_DEPOSIT_STATE.STATE_2;
                    time.reset();
                }
                else {
                    Oscar.slides.slidesOutABit();
                    //TODO: Change these values
                    Oscar.elbow.moveBottom();
                    Oscar.grabber.goBottom();
                }
                break;
            case STATE_2:
                if(gamepad2.cross) {
                    bottom_deposit_state = BOTTOM_DEPOSIT_STATE.STATE_3;
                    time.reset();
                }
                else {
                    Oscar.slides.slidesBottom();
                }
                break;
            case STATE_3:
                if(time.milliseconds() > 175) {
                    bottom_deposit_state = BOTTOM_DEPOSIT_STATE.STATE_4;
                    bottomDeposited = true;
                    time.reset();
                }
                else {
                    Oscar.slides.slidesHold();
                    Oscar.grabber.openGrab();
                    Oscar.grabber.openGrabExtra();
                }
                break;
            case STATE_4:
                if(time.milliseconds() > 800) {
                    bottom_deposit_state = BOTTOM_DEPOSIT_STATE.STATE_5;
                    time.reset();
                }
                else {
                    Oscar.slides.slidesHold();
                    Oscar.elbow.goToGrabPos();
                    Oscar.grabber.closeGrabExtra();
                    Oscar.slides.slidesOutABit();
                }
                break;
            case STATE_5:
                if(Oscar.slides.getMotorPosition() < 150) {
                    bottom_deposit_state = BOTTOM_DEPOSIT_STATE.STATE_6;
                    Oscar.grabber.goStart();
                    time.reset();
                }
                else {
                    Oscar.slides.slidesGrab();
                }
                break;
            case STATE_6:
                if(time.milliseconds() > 450) {
                    reset();
                    bottom_deposit_state = BOTTOM_DEPOSIT_STATE.INIT;
                }
                else {
                    Oscar.slides.slidesGrab();
                }
                break;
            default:
                bottom_deposit_state = BOTTOM_DEPOSIT_STATE.INIT;
                break;
        }
    }
}
