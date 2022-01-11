package org.firstinspires.ftc.teamcode.Teleop;

import android.widget.Button;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
//hardware
import org.firstinspires.ftc.teamcode.robot.Hardware;
import org.firstinspires.ftc.teamcode.robot.controllers.AnalogCheck;
import org.firstinspires.ftc.teamcode.robot.controllers.ButtonState;
import org.firstinspires.ftc.teamcode.robot.controllers.ControllerState;

@Config
@TeleOp(group = "drive")
public class TeleopODO extends LinearOpMode {
    private FtcDashboard dashboard;



    @Override
    public void runOpMode() throws InterruptedException {

        Hardware Oscar = new Hardware(hardwareMap);

        Oscar.drive.setPoseEstimate(new Pose2d(0,0,0));

        ControllerState controller1 = new ControllerState(gamepad1);
        ControllerState controller2 = new ControllerState(gamepad2);


        /*
        controller 1- For max most likely
        any small changes in robot position with d pad, will override the joysticks
         */
        //TODO:tune these
        controller1.addEventListener("dpad_up", ButtonState.HELD, () -> Oscar.setVel(new Pose2d(0.25,0,0)));
        controller1.addEventListener("dpad_down", ButtonState.HELD, () -> Oscar.setVel(new Pose2d(-0.25,0,0)));
        controller1.addEventListener("dpad_left", ButtonState.HELD, () -> Oscar.setVel(new Pose2d(0,0.25,0)));
        controller1.addEventListener("dpad_right",ButtonState.HELD, () -> Oscar.setVel(new Pose2d(0,-0.25,0)));
        controller1.addEventListener("left_trigger", AnalogCheck.GREATER_THAN, 0.1, () -> Oscar.setVel(new Pose2d(0,0,0.2)));
        controller1.addEventListener("right_trigger", AnalogCheck.GREATER_THAN, 0.1, () -> Oscar.setVel(new Pose2d(0,0,-0.2)));

        /*controller 2
        sets intake on or off
        runs cycle for placing and releasing
         */
        // toggle intake on if the grabber is open and the elbow is at its home position
        controller2.addEventListener("right_trigger",AnalogCheck.GREATER_THAN, 0.1,() ->{Oscar.intake.setIntakeDirection(false); Oscar.intake.setIntakeMode(true);});
        controller2.addEventListener("right_trigger",AnalogCheck.LESS_THAN_EQUALS, 0.1,() ->{Oscar.intake.setIntakeMode(false);});
        //IDK how this will work will test to see but may change
        //It all keeps heading up
        // When button y is pressed it will go through the entire top cycle
        controller2.addEventListener("a", ButtonState.PRESSED, () ->{Oscar.elbow.goToGrabPos();});
        controller2.addEventListener("y", ButtonState.PRESSED,() -> {Oscar.elbow.moveTop(); Oscar.grabber.goTop();});
        controller2.addEventListener("b", ButtonState.PRESSED, () -> {Oscar.elbow.moveStart(); Oscar.grabber.goStart();});
        controller2.addEventListener("x", ButtonState.PRESSED, () -> {Oscar.grabber.grab();});
        controller2.addEventListener("dpad_up", ButtonState.PRESSED, () -> {Oscar.elbow.moveStart(); Thread.sleep(250); Oscar.slides.slidesTop(); Thread.sleep(1500); Oscar.elbow.moveTop(); Oscar.grabber.goTop();});
        controller2.addEventListener("left_bumper", ButtonState.PRESSED, () -> {Oscar.slides.slidesHome(); Oscar.slides.slidesGrab();});
        controller2.addEventListener("dpad_left", ButtonState.PRESSED, () -> {Oscar.slides.slidesGrab();});
        controller2.addEventListener("dpad_down", ButtonState.PRESSED, () -> { Oscar.slides.slidesGrab(); Thread.sleep(350); Oscar.elbow.moveStart(); Oscar.grabber.goStart();});

        waitForStart();

        while(opModeIsActive()){
            controller1.updateControllerState();
            controller2.updateControllerState();
            //

            Oscar.setVel(new Pose2d(
                    -Math.pow(controller1.getAnalogValue("left_stick_y"),3),
                    -Math.pow(controller1.getAnalogValue("left_stick_x"),3),
                    -Math.pow(controller1.getAnalogValue("right_stick_x"),3)

            ));
            controller1.handleEvents();
            controller2.handleEvents();

            //Allow Dpad override
            Oscar.drive.setDrivePower(Oscar.getVel());
            Oscar.drive.update();

            telemetry.update();
            telemetry.addData("Wheel location", Oscar.drive.getWheelPositions());
            telemetry.addData("Slide Position", Oscar.slides.getMotorPosition());
            telemetry.addData("Slide Position", Oscar.slides.getCurrentTargetPosition());

            Pose2d myPose = Oscar.drive.getPoseEstimate();



        }


    }

}
