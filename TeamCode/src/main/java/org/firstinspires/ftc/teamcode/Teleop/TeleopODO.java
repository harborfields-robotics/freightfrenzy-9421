package org.firstinspires.ftc.teamcode.Teleop;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;
//hardware
import org.firstinspires.ftc.teamcode.robot.Hardware;
import org.firstinspires.ftc.teamcode.robot.controllers.AnalogCheck;
import org.firstinspires.ftc.teamcode.robot.controllers.ButtonState;
import org.firstinspires.ftc.teamcode.robot.controllers.ControllerState;

@Config
@TeleOp(group = "drive")
public class TeleopODO extends LinearOpMode {
    private FtcDashboard dashboard;


    public enum CycleState {
        CYCLE_START,
        CYCLE_UP,
        CYCLE_EXTEND,
        CYCLE_GRABBER_TOP,
        CYCLE_DUMP,
        CYCLE_RETRACT
    }

    ;

    CycleState cycleState = CycleState.CYCLE_START;

    ElapsedTime cycleTimer = new ElapsedTime();

    ElapsedTime stateTimer = new ElapsedTime();

    ElapsedTime grabberTimer = new ElapsedTime();

    ElapsedTime slideTimer = new ElapsedTime();

    private int startTime = 100;

    private int topGrabTime = 250;

    private int slideExtendTime = 1500;

    private int grabberTopTime = 300;

    private int retractTime = 500;


    @Override
    public void runOpMode() throws InterruptedException {

        Hardware Oscar = new Hardware(hardwareMap);

        Oscar.drive.setPoseEstimate(new Pose2d(6, -48, 180));

        ControllerState controller1 = new ControllerState(gamepad1);
        ControllerState controller2 = new ControllerState(gamepad2);

//        controller1.addEventListener("left_trigger", AnalogCheck.GREATER_THAN, 0.1, () -> {
//            positionTracker.setAngDirection(-1);
//        });
//
//        controller1.addEventListener("right_trigger", AnalogCheck.GREATER_THAN, 0.1, () -> {
//            positionTracker.setAngDirection(1);
//        });

//        controller1.addEventListener("dpad_left", ButtonState.HELD, () -> {Oscar.updateX(1); Oscar.setVel(new Pose2d(Oscar.getPoseX(),Oscar.getPoseY(),Oscar.getPoseHeading()));});
//        controller1.addEventListener("dpad_left", ButtonState.OFF, () -> {Oscar.updateX(0);});
//
//        controller1.addEventListener("dpad_down", ButtonState.HELD, () -> {Oscar.updateY(.7); Oscar.setVel(new Pose2d(Oscar.getPoseX(),Oscar.getPoseY(),Oscar.getPoseHeading()));});
//        controller1.addEventListener("dpad_down", ButtonState.OFF, () -> {Oscar.updateY(0);});
//
//        controller1.addEventListener("dpad_right", ButtonState.HELD, () -> {Oscar.updateX(-1); Oscar.setVel(new Pose2d(Oscar.getPoseX(),Oscar.getPoseY(),Oscar.getPoseHeading()));});
//        controller1.addEventListener("dpad_right", ButtonState.OFF, () -> {Oscar.updateX(0);});
//
//        controller1.addEventListener("dpad_up",ButtonState.HELD, () -> {Oscar.updateY(-.7); Oscar.setVel(new Pose2d(Oscar.getPoseX(),Oscar.getPoseY(),Oscar.getPoseHeading()));});
//        controller1.addEventListener("dpad_up", ButtonState.OFF, () -> {Oscar.updateY(0);});
//
//        controller1.addEventListener("left_trigger", AnalogCheck.GREATER_THAN, 0.1, () -> {Oscar.updateHeading(.3); Oscar.setVel(new Pose2d(Oscar.getPoseX(),Oscar.getPoseY(),Oscar.getPoseHeading()));});
//        controller1.addEventListener("left_trigger", AnalogCheck.LESS_THAN_EQUALS, 0.1, () -> {Oscar.updateHeading(0);});
//
//        controller1.addEventListener("right_trigger", AnalogCheck.GREATER_THAN, 0.1, () -> {Oscar.updateHeading(-.3); Oscar.setVel(new Pose2d(Oscar.getPoseX(),Oscar.getPoseY(),Oscar.getPoseHeading()));});
//        controller1.addEventListener("right_trigger", AnalogCheck.LESS_THAN_EQUALS, 0.1, () -> {Oscar.updateHeading(0);});

        controller1.addEventListener("x", ButtonState.HELD, () -> Oscar.grabber.carousellOn());
        controller1.addEventListener("x", ButtonState.OFF, () -> Oscar.grabber.carousellOff());

//        controller1.addEventListener("right_bumper",ButtonState.HELD,() ->{Oscar.intake.reverse();});
//        controller1.addEventListener("right_bumper",ButtonState.OFF,() ->{Oscar.intake.off();});
        /*controller 2
        sets intake on or off
        runs cycle for placing and releasing
         */
        // toggle intake on if the grabber is open and the elbow is at its home position
//        controller2.addEventListener("left_trigger", AnalogCheck.GREATER_THAN, 0.1, () -> {
//            Oscar.intake.on();
//        });
//        controller2.addEventListener("left_trigger", AnalogCheck.LESS_THAN, 0.1, () -> {
//            Oscar.intake.off();
//        });
        controller2.addEventListener("left_trigger", AnalogCheck.GREATER_THAN, 0.1,()-> {Oscar.slides.slidesHome();});
        //controller2.addEventListener("left_trigger",AnalogCheck.LESS_THAN, 0.1,() ->{Oscar.intake.off();});
        //IDK how this will work will test to see but may change

        //It all keeps heading up
        // When button y is pressed it will go through the entire top cycle
        controller2.addEventListener("a", ButtonState.PRESSED, () -> {
            Oscar.elbow.moveStart();
            Oscar.grabber.goStart();
            Thread.sleep(200);
            Oscar.slides.slidesRelativeOut(200);
            Thread.sleep(170);
            Oscar.slides.slidesHome();
            Oscar.elbow.goToGrabPos();
        });



        controller2.addEventListener("x", ButtonState.PRESSED, () -> {
            Oscar.grabber.grab();
        });

        controller2.addEventListener("left_bumper", ButtonState.PRESSED, () -> {
            Oscar.slides.slidesHome();
        });

        

        Oscar.grabber.openGrab();
        Oscar.elbow.goToGrabPos();

        waitForStart();//

        while (opModeIsActive()) {

            telemetry.update();
//            telemetry.addData("Wheel location", Oscar.drive.getWheelPositions());
//            telemetry.addData("Slide Position", Oscar.slides.getMotorPosition());
//            telemetry.addData("Slide Position", Oscar.slides.getCurrentTargetPosition());
            telemetry.addData("Endstop", !Oscar.slides.getEndstop());

            telemetry.addData("IS GRABBING? ", Oscar.grabber.getIsGrab());
            telemetry.addData("IS GRABBING EXTRA? ", Oscar.grabber.getIsGrabExtra());



            switch (cycleState) {
                case CYCLE_START:
                    telemetry.addLine("CYCLE START");
                    if(gamepad2.y){
                        Oscar.grabber.closeGrab();
                        Oscar.grabber.goStart();
                        Oscar.slides.slidesHome();
                        cycleTimer.reset();
                        stateTimer.reset();
                        cycleState = CycleState.CYCLE_UP;
                    }
                    break;

                case CYCLE_UP:
                    telemetry.addLine("CYCLE UP");
                    if(stateTimer.milliseconds() >= 100) {
                        if (cycleTimer.milliseconds() >= startTime + 100) {
                            Oscar.elbow.moveStart();
                            cycleTimer.reset();
                            stateTimer.reset();
                            cycleState = CycleState.CYCLE_EXTEND;
                        }
                    }
                    break;

                case CYCLE_EXTEND:
                    telemetry.addLine("CYCLE EXTEND");
                    if(stateTimer.milliseconds() >= 500) {
                        Oscar.slides.slidesTop();
                        cycleTimer.reset();
                        stateTimer.reset();
                        cycleState = CycleState.CYCLE_DUMP;
                    }
                    break;

                case CYCLE_DUMP:
                    telemetry.addLine("CYCLE DUMP");
                    if(Oscar.slides.getMotorPosition() > Oscar.slides.TOP_SLIDE_TICKS - 480) {
                        Oscar.elbow.moveTop();
                        Oscar.grabber.goTop();
                        cycleTimer.reset();
                        stateTimer.reset();
                        cycleState = CycleState.CYCLE_GRABBER_TOP;
                    }
                    break;

                case CYCLE_GRABBER_TOP:
                    telemetry.addLine("GRABBER TOP");
                    if(stateTimer.milliseconds() >= 450) {
                        Oscar.grabber.openGrab();
                        Oscar.grabber.grabberGrabExtra();
                        cycleTimer.reset();
                        stateTimer.reset();
                        slideTimer.reset();
                        grabberTimer.reset();
                        cycleState = CycleState.CYCLE_RETRACT;
                    }
                    break;

                case CYCLE_RETRACT:
                    telemetry.addLine("SLIDES RETRACT");
                    if(stateTimer.milliseconds() >= 600) {
                        Oscar.elbow.moveStart();
                        if(cycleTimer.milliseconds() >= 600 + retractTime) {
                            Oscar.grabber.goStart();
                            Oscar.grabber.closeGrab();
                            Oscar.slides.slidesGrab();
                            if(Oscar.slides.getMotorPosition() <= 385) {
                                Oscar.elbow.goToGrabPos();
                                Oscar.grabber.grabberGrabExtra();
                                Oscar.grabber.openGrab();
                                cycleTimer.reset();
                                stateTimer.reset();
                                slideTimer.reset();
                                grabberTimer.reset();
                                cycleState = CycleState.CYCLE_START;
                            }
                        }
                    }
                    break;

                default:
                    cycleState = CycleState.CYCLE_START;
            }

            if (gamepad1.a && cycleState != CycleState.CYCLE_START) {
                cycleState = CycleState.CYCLE_START;
            }

            controller1.updateControllerState();
            controller2.updateControllerState();

            controller1.handleEvents();
            controller2.handleEvents();

            if(Oscar.slides.getMotorPosition() <= 100) {


                if (gamepad2.dpad_left) {
                    Oscar.intake.reverse();
                } else if (gamepad2.dpad_up) {
                    Oscar.intake.forward();
                } else {
                    Oscar.intake.off();
                }
            }


            Oscar.drive.setWeightedDrivePower(
                    new Pose2d(
                            -gamepad1.left_stick_y * .7,
                            -gamepad1.left_stick_x * .7,
                            -gamepad1.right_stick_x * .5
                    )
            );
        }
    }
}
