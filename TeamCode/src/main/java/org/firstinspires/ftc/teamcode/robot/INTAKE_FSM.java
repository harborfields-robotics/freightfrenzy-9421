package org.firstinspires.ftc.teamcode.robot;

import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

import dashboard.RobotConstants;

public class INTAKE_FSM {
    enum BACK_FLIP_STATE {
        INIT,
        STATE_0,
        STATE_01,
        STATE_1,
        STATE_2,
        STATE_3,
        SPIT
    }
    enum FRONT_FLIP_STATE {
        INIT,
        STATE_0,
        STATE_01,
        STATE_1,
        STATE_2,
        STATE_3,
        SPIT
    }
    enum BACK_PULSE_STATE {
        IDLE,
        OFF,
        ON
    }
    enum FRONT_PULSE_STATE {
        IDLE,
        OFF,
        ON
    }
    private BACK_FLIP_STATE back_flip_state = BACK_FLIP_STATE.INIT;
    private FRONT_FLIP_STATE front_flip_state = FRONT_FLIP_STATE.INIT;
    private BACK_PULSE_STATE back_pulse_state = BACK_PULSE_STATE.IDLE;
    private FRONT_PULSE_STATE front_pulse_state = FRONT_PULSE_STATE.IDLE;

    public boolean doBackPulseNow = false;
    public boolean doFrontPulseNow = false;

    private final Hardware Oscar;
    private final Gamepad gamepad1;
    private final Gamepad gamepad2;
    private boolean frontBusy = false;
    private boolean backBusy = false;
    private final Telemetry telemetry;

    private boolean EXEC_BACK_FLIP = false;
    private boolean EXEC_FRONT_FLIP = false;

    private boolean FRONT_DETECTED = false;
    private boolean BACK_DETECTED = false;
    private boolean FRONT_PRIORITY = false;
    private boolean BACK_PRIORITY = false;

    public INTAKE_FSM(Hardware hardware, Telemetry telemetry, Gamepad c1, Gamepad c2) {
        this.Oscar = hardware;
        this.telemetry = telemetry;
        gamepad1 = c1;
        gamepad2 = c2;
    }

    private final ElapsedTime time = new ElapsedTime();
    private final ElapsedTime wiggleTime = new ElapsedTime();
    private final ElapsedTime frontPulseTime = new ElapsedTime();
    private final ElapsedTime backPulseTime = new ElapsedTime();

    private Gamepad.RumbleEffect flipRumble = new Gamepad.RumbleEffect.Builder()
            .addStep(1.0, 0.0, 200)  //  Rumble left motor 100% for 300 mSec
            .addStep(0.0, 1.0, 200)  //  Rumble left motor 100% for 300 mSec
            .build();

    public boolean isBackBusy() {return backBusy;}
    public boolean isFrontBusy() {return frontBusy;}

    public void reset() {
        backBusy = false;
        frontBusy = false;
        back_flip_state = BACK_FLIP_STATE.INIT;
        front_flip_state = FRONT_FLIP_STATE.INIT;
        time.reset();
    }

    public void SET_EXEC_BACK_FLIP(boolean EXEC) {EXEC_BACK_FLIP = EXEC;}
    public void SET_EXEC_FRONT_FLIP(boolean EXEC) {EXEC_FRONT_FLIP = EXEC;}

    private void doPulseFrontAsync() {
        switch (front_pulse_state) {
            case IDLE:
                if(doFrontPulseNow) {
                    front_pulse_state = FRONT_PULSE_STATE.ON;
                    frontPulseTime.reset();
                }
                break;
            case ON:
                Oscar.intake.frontIn();
                if(frontPulseTime.milliseconds() > RobotConstants.PULSE_FREQUENCY_ON) {
                    front_pulse_state = FRONT_PULSE_STATE.OFF;
                    frontPulseTime.reset();
                }
                if(!doFrontPulseNow) {
                    front_pulse_state = FRONT_PULSE_STATE.IDLE;
                    Oscar.intake.off();
                }
                break;
            case OFF:
                Oscar.intake.off();
                if(frontPulseTime.milliseconds() > RobotConstants.PULSE_FREQUENCY_OFF) {
                    front_pulse_state = FRONT_PULSE_STATE.ON;
                    frontPulseTime.reset();
                }
                if(!doFrontPulseNow) {
                    front_pulse_state = FRONT_PULSE_STATE.IDLE;
                    Oscar.intake.off();
                }
                break;
        }
    }
    private void doPulseBackAsync() {
        telemetry.addData("PULSE BACK:", back_pulse_state);
        switch (back_pulse_state) {
            case IDLE:
                if(doBackPulseNow) {
                    back_pulse_state = BACK_PULSE_STATE.ON;
                    backPulseTime.reset();
                }
                break;
            case ON:
                Oscar.intake.backIn();
                if(backPulseTime.milliseconds() > RobotConstants.PULSE_FREQUENCY_ON) {
                    back_pulse_state = BACK_PULSE_STATE.OFF;
                    backPulseTime.reset();
                }
                if(!doBackPulseNow) {
                    back_pulse_state = BACK_PULSE_STATE.IDLE;
                    Oscar.intake.off();
                }
                break;
            case OFF:
                Oscar.intake.off();
                if(backPulseTime.milliseconds() > RobotConstants.PULSE_FREQUENCY_OFF) {
                    back_pulse_state = BACK_PULSE_STATE.ON;
                    backPulseTime.reset();
                }
                if(!doBackPulseNow) {
                    back_pulse_state = BACK_PULSE_STATE.IDLE;
                    Oscar.intake.off();
                }
                break;
        }
    }

    public void handleEvents(boolean isDepositBusy, boolean disableFront, boolean disableBack) {
        FRONT_DETECTED = ((DistanceSensor) Oscar.colorFront).getDistance(DistanceUnit.CM) < RobotConstants.DISTANCE_THRESHOLD_FOR_FLIP;
        BACK_DETECTED = ((DistanceSensor) Oscar.colorBack).getDistance(DistanceUnit.CM) < RobotConstants.DISTANCE_THRESHOLD_FOR_FLIP;

        if(!frontBusy && !backBusy) {
            Oscar.flippers.moveDown("front");
            Oscar.flippers.moveDown("back");
        }
        if(FRONT_DETECTED) {
            FRONT_PRIORITY = !BACK_PRIORITY;
            EXEC_FRONT_FLIP = FRONT_PRIORITY;
            if(FRONT_PRIORITY) {
                Oscar.flippers.moveUp("back");
            }
        }
        if(BACK_DETECTED) {
            BACK_PRIORITY = !FRONT_PRIORITY;
            EXEC_BACK_FLIP = BACK_PRIORITY;
            if(BACK_PRIORITY) {
                Oscar.flippers.moveUp("front");
            }
        }
        if(FRONT_DETECTED && BACK_DETECTED) {
            if(FRONT_PRIORITY) {
                back_flip_state = BACK_FLIP_STATE.SPIT;
            }
            if(BACK_PRIORITY) {
                front_flip_state = FRONT_FLIP_STATE.SPIT;
            }
        }
        if(!FRONT_DETECTED && !BACK_DETECTED) {
            FRONT_PRIORITY = false;
            BACK_PRIORITY = false;
        }
        if(!isDepositBusy) {
            if(!disableBack) {
                doFlipBackAsync();
//                doPulseBackAsync();
            }
            if(!disableFront) {
                doFlipFrontAsync();
//                doPulseFrontAsync();
            }
        }
        else {
            front_flip_state = FRONT_FLIP_STATE.INIT;
            back_flip_state = BACK_FLIP_STATE.INIT;
            EXEC_BACK_FLIP = false;
            EXEC_FRONT_FLIP = false;
            Oscar.elbow.START_STOP_WIGGLE = false;
            doFlipFrontAsync();
            doFlipBackAsync();
        }
    }

    public void doFlipFrontAsync() {
//        telemetry.addData("FRONT FLIPPER STATE: ", front_state);
        switch(front_flip_state) {
            case SPIT:
                EXEC_FRONT_FLIP = false;
                Oscar.intake.frontOut();
                if(!Oscar.intake.STALLED){
                    front_flip_state = FRONT_FLIP_STATE.INIT;
                }
                Oscar.flippers.moveDown("front");

                break;
            case INIT:
                if(EXEC_FRONT_FLIP) {
                    EXEC_FRONT_FLIP = false;
                    front_flip_state = FRONT_FLIP_STATE.STATE_01;
                    frontBusy = true;
                    gamepad1.runRumbleEffect(flipRumble);
                    time.reset();
                }
                else {
                    frontBusy = false;
                }
                break;
            case STATE_01:
                if(time.milliseconds() > 10) {
                    front_flip_state = FRONT_FLIP_STATE.STATE_0;
                    Oscar.elbow.START_STOP_WIGGLE = true;
                }
                else {
                    Oscar.intake.frontOut();
                }
                break;
            case STATE_0:
                if(time.milliseconds() > 800) {
                    front_flip_state = FRONT_FLIP_STATE.STATE_2;
                    Oscar.flippers.moveUp("front");
                    Oscar.elbow.START_STOP_WIGGLE = false;
                    time.reset();
                }
                if(wiggleTime.milliseconds() > 200) {
                    front_flip_state = FRONT_FLIP_STATE.STATE_1;
                    wiggleTime.reset();
                }
                else {
                    Oscar.flippers.moveUp("front");
                    Oscar.intake.frontOut();
                }
                break;
            case STATE_1:
                if(wiggleTime.milliseconds() > 200) {
                    front_flip_state = FRONT_FLIP_STATE.STATE_0;
                    wiggleTime.reset();
                }
                else {
                    Oscar.flippers.moveWiggle("front");
                    Oscar.intake.frontOut();
                }
                break;
            case STATE_2:
                if(time.milliseconds() > 100) {
                    front_flip_state = FRONT_FLIP_STATE.STATE_3;
                    Oscar.flippers.moveDown("front");
                    time.reset();
                }
                if(((DistanceSensor) Oscar.colorFront).getDistance(DistanceUnit.CM) > 2) {
                    LOGIC.IS_THING_IN_DA_ROBOT = true;
                }
                else {
                    Oscar.intake.frontOut();
                }
                break;
            case STATE_3:
                if(time.milliseconds() > 20) {
                    front_flip_state = FRONT_FLIP_STATE.INIT;
                    if(((DistanceSensor) Oscar.colorBack).getDistance(DistanceUnit.CM) < RobotConstants.DISTANCE_THRESHOLD_FOR_FLIP) {
                        LOGIC.IS_THING_IN_DA_ROBOT = false;
                    }
                    EXEC_FRONT_FLIP = false;
                    Oscar.intake.off();
                    reset();
                }
                break;
            default:
                front_flip_state = FRONT_FLIP_STATE.INIT;
                break;
        }
        Oscar.elbow.doWiggleAsync();
    }

    public void doFlipBackAsync() {
        switch(back_flip_state) {
            case SPIT:
                EXEC_BACK_FLIP = false;
                Oscar.intake.backOut();
                Oscar.flippers.moveDown("back");
                if(!Oscar.intake.STALLED){
                    front_flip_state = FRONT_FLIP_STATE.INIT;
                }

                break;
            case INIT:
                if(EXEC_BACK_FLIP) {
                    EXEC_BACK_FLIP = false;
                    back_flip_state = BACK_FLIP_STATE.STATE_01;
                    backBusy = true;
                    gamepad1.runRumbleEffect(flipRumble);
                    time.reset();
                }
                else {
                    backBusy = false;
                }
                break;
            case STATE_01:
                if(time.milliseconds() > 10) {
                    back_flip_state = BACK_FLIP_STATE.STATE_0;
                    Oscar.elbow.START_STOP_WIGGLE = true;
                }
                else {
                    Oscar.intake.backIn();
                }
                break;
            case STATE_0:
                if(time.milliseconds() > 800) {
                    back_flip_state = BACK_FLIP_STATE.STATE_2;
                    Oscar.flippers.moveUp("back");
                    Oscar.elbow.START_STOP_WIGGLE = false;
                    time.reset();
                }
                if(wiggleTime.milliseconds() > 200) {
                    back_flip_state = BACK_FLIP_STATE.STATE_1;
                    wiggleTime.reset();
                }
                else {
                    Oscar.flippers.moveUp("back");
                    Oscar.intake.backOut();
                }
                break;
            case STATE_1:
                if(wiggleTime.milliseconds() > 200) {
                    back_flip_state = BACK_FLIP_STATE.STATE_0;
                    wiggleTime.reset();
                }
                else {
                    Oscar.flippers.moveWiggle("back");
                    Oscar.intake.backOut();
                }
                break;
            case STATE_2:
                if(time.milliseconds() > 1200) {
                    back_flip_state = BACK_FLIP_STATE.STATE_3;
                    Oscar.flippers.moveDown("back");
                    time.reset();
                }
                if(((DistanceSensor) Oscar.colorBack).getDistance(DistanceUnit.CM) > 2) {
                    LOGIC.IS_THING_IN_DA_ROBOT = true;
                }
                else{
                    Oscar.intake.backOut();
                }
                break;
            case STATE_3:
                if(time.milliseconds() > 20) {
                    if(((DistanceSensor) Oscar.colorBack).getDistance(DistanceUnit.CM) < RobotConstants.DISTANCE_THRESHOLD_FOR_FLIP) {
                        LOGIC.IS_THING_IN_DA_ROBOT = false;
                    }
                    EXEC_BACK_FLIP = false;
                    back_flip_state = BACK_FLIP_STATE.INIT;
                    Oscar.intake.off();
                    reset();
                }
                break;
            default:
                back_flip_state = BACK_FLIP_STATE.INIT;
                break;
        }
        Oscar.elbow.doWiggleAsync();
    }
}
