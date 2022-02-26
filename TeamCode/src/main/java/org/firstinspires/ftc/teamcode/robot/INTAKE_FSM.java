package org.firstinspires.ftc.teamcode.robot;

import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

public class INTAKE_FSM {
    enum BACK_STATE {
        INIT,
        STATE_0,
        STATE_01,
        STATE_1,
        STATE_2,
        STATE_3,
        SPIT
    }
    enum FRONT_STATE {
        INIT,
        STATE_0,
        STATE_01,
        STATE_1,
        STATE_2,
        STATE_3,
        SPIT
    }
    private BACK_STATE back_state = BACK_STATE.INIT;
    private FRONT_STATE front_state = FRONT_STATE.INIT;
    private final Hardware Oscar;
    private final Gamepad gamepad1;
    private final Gamepad gamepad2;
    private boolean frontBusy = false;
    private boolean backBusy = false;
    private final Telemetry telemetry;

    private boolean EXEC_BACK_FLIP = false;
    private boolean EXEC_FRONT_FLIP = false;

    private boolean IS_THE_THING_BEING_GRABBED = false;
    private boolean FRONT_DETECTED = false;
    private boolean BACK_DETECTED = false;
    private boolean FRONT_PRIORITY = false;
    private boolean BACK_PRIORITY = false;

    private boolean ENSURE_ONE_GRAB_RUMBLE = false;

    public INTAKE_FSM(Hardware hardware, Telemetry telemetry, Gamepad c1, Gamepad c2) {
        this.Oscar = hardware;
        this.telemetry = telemetry;
        gamepad1 = c1;
        gamepad2 = c2;
    }

    private final ElapsedTime time = new ElapsedTime();
    private final ElapsedTime wiggleTime = new ElapsedTime();

    private Gamepad.RumbleEffect flipRumble = new Gamepad.RumbleEffect.Builder()
            .addStep(1.0, 0.0, 500)  //  Rumble left motor 100% for 500 mSec
            .build();

    private Gamepad.RumbleEffect grabbedRumble = new Gamepad.RumbleEffect.Builder()
            .addStep(0.0, 1.0, 500)  //  Rumble right motor 100% for 500 mSec
            .build();


    public boolean isBackBusy() {return backBusy;}
    public boolean isFrontBusy() {return frontBusy;}

    public void reset() {
        backBusy = false;
        frontBusy = false;
        back_state = BACK_STATE.INIT;
        front_state = FRONT_STATE.INIT;
        time.reset();
    }

    public void SET_EXEC_BACK_FLIP(boolean EXEC) {EXEC_BACK_FLIP = EXEC;}
    public void SET_EXEC_FRONT_FLIP(boolean EXEC) {EXEC_FRONT_FLIP = EXEC;}

    public void handleEvents() {
        FRONT_DETECTED = ((DistanceSensor) Oscar.colorFront).getDistance(DistanceUnit.CM) < 2;
        BACK_DETECTED = ((DistanceSensor) Oscar.colorBack).getDistance(DistanceUnit.CM) < 2;
        if(FRONT_DETECTED) {
            FRONT_PRIORITY = !BACK_PRIORITY;
            EXEC_FRONT_FLIP = true;
        }
        if(BACK_DETECTED) {
            BACK_PRIORITY = !FRONT_PRIORITY;
            EXEC_BACK_FLIP = true;
        }
        if(FRONT_DETECTED && BACK_DETECTED) {
            if(FRONT_PRIORITY) {
                back_state = BACK_STATE.SPIT;
            }
            if(BACK_PRIORITY) {
                front_state = FRONT_STATE.SPIT;
            }
        }
        if(!FRONT_DETECTED && !BACK_DETECTED) {
            FRONT_PRIORITY = false;
            BACK_PRIORITY = false;
            Oscar.grabber.closeGrab();
            if(ENSURE_ONE_GRAB_RUMBLE) {
                ENSURE_ONE_GRAB_RUMBLE = false;
                gamepad1.runRumbleEffect(grabbedRumble);
            }
        }
        else {
            ENSURE_ONE_GRAB_RUMBLE = true;
            Oscar.grabber.openGrab();
        }
        doFlipBackAsync();
        doFlipFrontAsync();
    }

    public void doFlipFrontAsync() {
        telemetry.addData("FRONT FLIPPER STATE: ", front_state);
        switch(front_state) {
            case SPIT:
                EXEC_FRONT_FLIP = false;
                Oscar.intake.frontOut();
                Oscar.flippers.moveDown("front");
                if(!BACK_DETECTED) {
                    front_state = FRONT_STATE.INIT;
                }
                break;
            case INIT:
                if(((gamepad2.dpad_up || gamepad1.dpad_up) && !backBusy) || EXEC_FRONT_FLIP) {
                    EXEC_FRONT_FLIP = false;
                    front_state = FRONT_STATE.STATE_01;
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
                    front_state = FRONT_STATE.STATE_0;
                    Oscar.slides.START_STOP_WIGGLE = true;
                }
                else {
                    Oscar.intake.frontOut();
                }
                break;
            case STATE_0:
                if(time.milliseconds() > 1200) {
                    front_state = FRONT_STATE.STATE_2;
                    Oscar.flippers.moveUp("front");
                    Oscar.slides.START_STOP_WIGGLE = false;
                    time.reset();
                }
                if(wiggleTime.milliseconds() > 200) {
                    front_state = FRONT_STATE.STATE_1;
                    wiggleTime.reset();
                }
                else {
                    Oscar.flippers.moveUp("front");
                    Oscar.intake.frontOut();
                }
                break;
            case STATE_1:
                if(wiggleTime.milliseconds() > 200) {
                    front_state = FRONT_STATE.STATE_0;
                    wiggleTime.reset();
                }
                else {
                    Oscar.flippers.moveWiggle("front");
                    Oscar.intake.frontOut();
                }
                break;
            case STATE_2:
                if(time.milliseconds() > 100) {
                    front_state = FRONT_STATE.STATE_3;
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
                    front_state = FRONT_STATE.INIT;
                    if(((DistanceSensor) Oscar.colorBack).getDistance(DistanceUnit.CM) < 2) {
                        LOGIC.IS_THING_IN_DA_ROBOT = false;
                    }
                    EXEC_FRONT_FLIP = false;
                    Oscar.intake.off();
                    reset();
                }
                break;
            default:
                front_state = FRONT_STATE.INIT;
                break;
        }
        Oscar.slides.doWiggleAsync();
    }

    public void doFlipBackAsync() {
        telemetry.addData("BACK FLIPPER STATE: ", back_state);
        switch(back_state) {
            case SPIT:
                EXEC_BACK_FLIP = false;
                Oscar.intake.backOut();
                Oscar.flippers.moveDown("back");
                if(!FRONT_DETECTED) {
                    back_state = BACK_STATE.INIT;
                }
                break;
            case INIT:
                if(EXEC_BACK_FLIP) {
                    EXEC_BACK_FLIP = false;
                    back_state = BACK_STATE.STATE_01;
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
                    back_state = BACK_STATE.STATE_0;
                    Oscar.slides.START_STOP_WIGGLE = true;
                }
                else {
                    Oscar.intake.backOut();
                }
                break;
            case STATE_0:
                if(time.milliseconds() > 1200) {
                    back_state = BACK_STATE.STATE_2;
                    Oscar.flippers.moveUp("back");
                    Oscar.slides.START_STOP_WIGGLE = false;
                    time.reset();
                }
                if(wiggleTime.milliseconds() > 200) {
                    back_state = BACK_STATE.STATE_1;
                    wiggleTime.reset();
                }
                else {
                    Oscar.flippers.moveUp("back");
                    Oscar.intake.backOut();
                }
                break;
            case STATE_1:
                if(wiggleTime.milliseconds() > 200) {
                    back_state = BACK_STATE.STATE_0;
                    wiggleTime.reset();
                }
                else {
                    Oscar.flippers.moveWiggle("back");
                    Oscar.intake.backOut();
                }
                break;
            case STATE_2:
                if(time.milliseconds() > 1200) {
                    back_state = BACK_STATE.STATE_3;
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
                    if(((DistanceSensor) Oscar.colorBack).getDistance(DistanceUnit.CM) < 2) {
                        LOGIC.IS_THING_IN_DA_ROBOT = false;
                    }
                    EXEC_BACK_FLIP = false;
                    back_state = BACK_STATE.INIT;
                    Oscar.intake.off();
                    reset();
                }
                break;
            default:
                back_state = BACK_STATE.INIT;
                break;
        }
        Oscar.slides.doWiggleAsync();
    }
}
