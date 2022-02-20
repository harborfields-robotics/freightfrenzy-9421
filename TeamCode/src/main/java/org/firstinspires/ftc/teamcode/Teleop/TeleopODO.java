package org.firstinspires.ftc.teamcode.Teleop;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.util.ElapsedTime;
//hardware
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.robot.DEPOSIT_FSM;
import org.firstinspires.ftc.teamcode.robot.Hardware;
import org.firstinspires.ftc.teamcode.robot.INTAKE_FSM;
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

        controller1.addEventListener("dpad_left", ButtonState.HELD, () -> Oscar.slides.relativeMove(-5));
        controller1.addEventListener("dpad_right", ButtonState.HELD, () -> Oscar.slides.relativeMove(5));

        controller2.addEventListener("dpad_left", ButtonState.HELD, () -> Oscar.slides.relativeMove(-5));
        controller2.addEventListener("dpad_right", ButtonState.HELD, () -> Oscar.slides.relativeMove(5));

        DEPOSIT_FSM deposit_fsm = new DEPOSIT_FSM(Oscar, telemetry, gamepad1, gamepad2);
        INTAKE_FSM intake_fsm = new INTAKE_FSM(Oscar, telemetry, gamepad1, gamepad2);

       // controller2.addEventListener("x", ButtonState.PRESSED, () -> {
           // Oscar.grabber.grab();
       // });

        controller2.addEventListener("left_bumper", ButtonState.PRESSED, () -> {
            Oscar.slides.slidesHome();
        });

        Oscar.grabber.openGrab();
        Oscar.elbow.goToGrabPos();

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

            telemetry.addData("Distance (cm)", "%.3f", ((DistanceSensor) Oscar.colorBack).getDistance(DistanceUnit.CM));
            if(((DistanceSensor) Oscar.colorBack).getDistance(DistanceUnit.CM) < 1) {
                intake_fsm.SET_EXEC_BACK_FLIP(true);
            }

            intake_fsm.doFlipBackAsync();
            intake_fsm.doFlipFrontAsync();

            if(Oscar.slides.getMotorPosition() <= 200 && !intake_fsm.isBackBusy()) {
                if (gamepad2.left_trigger > .1 || gamepad1.left_trigger > .1) Oscar.intake.reverse();
                else if (gamepad2.right_trigger > .1 || gamepad1.right_trigger > .1) Oscar.intake.forward();
                else Oscar.intake.off();
            }

            Oscar.drive.setWeightedDrivePower(new Pose2d(-gamepad1.left_stick_y * 1,-gamepad1.left_stick_x * 1,-gamepad1.right_stick_x * .5));
        }
    }
}