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

        DEPOSIT_FSM deposit_fsm = new DEPOSIT_FSM(Oscar, telemetry, gamepad1, gamepad2);
        INTAKE_FSM intake_fsm = new INTAKE_FSM(Oscar, telemetry, gamepad1, gamepad2);

        controller2.addEventListener("left_bumper", ButtonState.PRESSED, () -> {
            Oscar.slides.slidesHome();
        });

        Oscar.elbow.goToGrabPos();
        Oscar.grabber.goStart();
        Oscar.grabber.openGrab();
        Oscar.grabber.moveByAngle(-90, "start");
        Oscar.flippers.moveDown("front");
        Oscar.flippers.moveDown("back");
        Thread.sleep(1800);
        Oscar.flippers.moveDown("front");
        Oscar.flippers.moveDown("back");
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
            deposit_fsm.doDepositSharedAsync();

            intake_fsm.handleEvents();

            if(Oscar.slides.getMotorPosition() <= 200 && !intake_fsm.isBackBusy() && !intake_fsm.isFrontBusy()) {
                if (gamepad2.left_trigger > .1 || gamepad1.left_trigger > .1) Oscar.intake.reverse();
                else if (gamepad2.right_trigger > .1 || gamepad1.right_trigger > .1) Oscar.intake.forward();
                else Oscar.intake.off();
            }

            telemetry.addData("IS THING IN DA ROBOT? ", LOGIC.IS_THING_IN_DA_ROBOT);
            telemetry.addData("IS THE ENCODER OK", Oscar.slides.getMotorPosition());

            Oscar.drive.setWeightedDrivePower(new Pose2d(-gamepad1.left_stick_x * 1,gamepad1.left_stick_y * 1,-gamepad1.right_stick_x * .5));
        }
    }
}