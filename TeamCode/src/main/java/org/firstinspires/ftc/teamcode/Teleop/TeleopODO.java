package org.firstinspires.ftc.teamcode.Teleop;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.util.ElapsedTime;
//hardware
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.robot.DEPOSIT_FSM;
import org.firstinspires.ftc.teamcode.robot.Hardware;
import org.firstinspires.ftc.teamcode.robot.INTAKE_FSM;
import org.firstinspires.ftc.teamcode.robot.LOGIC;
import org.firstinspires.ftc.teamcode.robot.controllers.AnalogCheck;
import org.firstinspires.ftc.teamcode.robot.controllers.ButtonState;
import org.firstinspires.ftc.teamcode.robot.controllers.ControllerState;
//
@Config
@TeleOp(group = "drive")
public class TeleopODO extends LinearOpMode {

    private FtcDashboard dashboard;

    @Override
    public void runOpMode() throws InterruptedException {

        Hardware Oscar = new Hardware(hardwareMap, telemetry);

        Oscar.drive.setPoseEstimate(new Pose2d(6, -48, 180));

        ControllerState controller1 = new ControllerState(gamepad1);
        ControllerState controller2 = new ControllerState(gamepad2);

        controller1.addEventListener("x", ButtonState.HELD, () -> Oscar.grabber.carousellOn());
        controller1.addEventListener("x", ButtonState.OFF, () -> Oscar.grabber.carousellOff());

        controller2.addEventListener("left_bumper", ButtonState.PRESSED,()-> {Oscar.slides.slidesHome();});
        controller2.addEventListener("right_bumper", ButtonState.PRESSED,()-> {Oscar.grabber.goTop(); Thread.sleep(200); Oscar.grabber.goStart();});

        controller1.addEventListener("dpad_left", ButtonState.HELD, () -> LOGIC.IS_THING_IN_DA_ROBOT = false);

        controller2.addEventListener("dpad_left", ButtonState.HELD, () -> Oscar.slides.relativeMove(-5));
        controller2.addEventListener("dpad_right", ButtonState.HELD, () -> Oscar.slides.relativeMove(5));

        DEPOSIT_FSM deposit_fsm = new DEPOSIT_FSM(Oscar, telemetry, gamepad1, gamepad2);
        INTAKE_FSM intake_fsm = new INTAKE_FSM(Oscar, telemetry, gamepad1, gamepad2);

        controller2.addEventListener("left_bumper", ButtonState.PRESSED, () -> {
            Oscar.slides.slidesHome();
        });

        Gamepad.RumbleEffect customRumbleEffect = new Gamepad.RumbleEffect.Builder()
                .addStep(0.0, 1.0, 50)  //  Rumble right motor 100% for 500 mSec
                .addStep(0.0, 0.0, 50)  //  Pause for 300 mSec
                .addStep(1.0, 0.0, 50)  //  Rumble left motor 100% for 250 mSec
                .addStep(0.0, 0.0, 50)  //  Pause for 250 mSec
                .addStep(1.0, 0.0, 50)  //  Rumble left motor 100% for 250 mSec
                .build();

        Oscar.elbow.goToGrabPos();
        Oscar.grabber.goStart();
        Oscar.grabber.openGrab();
        Oscar.grabber.moveByAngle(-90, "start");
        Thread.sleep(1800);
        Oscar.grabber.moveByAngle(90,"start");
        Oscar.slides.slidesHome();

        waitForStart();//

        while (opModeIsActive()) {

            telemetry.update();

            controller1.updateControllerState();
            controller2.updateControllerState();

            controller1.handleEvents();
            controller2.handleEvents();

            deposit_fsm.doDepositTopAsync();
            deposit_fsm.doDepositMiddleAsync();
            deposit_fsm.doDepositBottomAsync();

            if(((DistanceSensor) Oscar.colorBack).getDistance(DistanceUnit.CM) < 2) {
                intake_fsm.SET_EXEC_BACK_FLIP(true);
                gamepad1.runRumbleEffect(customRumbleEffect);
            }
            if(((DistanceSensor) Oscar.colorFront).getDistance(DistanceUnit.CM) < 2) {
                intake_fsm.SET_EXEC_FRONT_FLIP(true);
                gamepad1.runRumbleEffect(customRumbleEffect);
            }

            intake_fsm.doFlipBackAsync();
            intake_fsm.doFlipFrontAsync();

            if(Oscar.slides.getMotorPosition() <= 200 && !intake_fsm.isBackBusy() && !intake_fsm.isFrontBusy()) {
                if (gamepad2.left_trigger > .1 || gamepad1.left_trigger > .1) Oscar.intake.reverse();
                else if (gamepad2.right_trigger > .1 || gamepad1.right_trigger > .1) Oscar.intake.forward();
                else Oscar.intake.off();
            }

            telemetry.addData("IS THING IN DA ROBOT? ", LOGIC.IS_THING_IN_DA_ROBOT);

            Oscar.drive.setWeightedDrivePower(new Pose2d(-gamepad1.left_stick_y * 1,-gamepad1.left_stick_x * 1,-gamepad1.right_stick_x * .5));
        }
    }
}