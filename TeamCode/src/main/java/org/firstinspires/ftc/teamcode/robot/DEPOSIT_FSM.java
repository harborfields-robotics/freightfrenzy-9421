package org.firstinspires.ftc.teamcode.robot;

import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.robot.controllers.ControllerState;

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
    private DEPOSIT_STATE deposit_state = DEPOSIT_STATE.INIT;
    private final Hardware Oscar;
    private final Gamepad gamepad1;
    private final Gamepad gamepad2;
    private boolean busy = false;
    private boolean deposited = false;
    private final Telemetry telemetry;

    public DEPOSIT_FSM(Hardware hardware, Telemetry telemetry, Gamepad c1, Gamepad c2) {
        this.Oscar = hardware;
        this.telemetry = telemetry;
        gamepad1 = c1;
        gamepad2 = c2;
    }

    private final ElapsedTime time = new ElapsedTime();

    public boolean isBusy() {return busy;}
    public boolean isDeposited() {return deposited;}

    public void reset() {
        busy = false;
        deposited = false;
        deposit_state = DEPOSIT_STATE.INIT;
        time.reset();
    }

    public void doDepositTopAsync() {
        telemetry.addData("DEPOSIT STATE: ", deposit_state);
//        telemetry.update();
        switch(deposit_state) {
            case INIT:
                if(gamepad1.y || gamepad2.y) {
                    deposit_state = DEPOSIT_STATE.STATE_0;
                    busy = true;
                    deposited = false;
                    time.reset();
                }
                else {
                    busy = false;
                    deposited = false;
                    Oscar.slides.slidesGrab();
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
                if(gamepad1.y || gamepad2.y) {
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
}
