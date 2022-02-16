package org.firstinspires.ftc.teamcode.Teleop;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.robot.Hardware;
import org.firstinspires.ftc.teamcode.robot.controllers.ButtonState;
import org.firstinspires.ftc.teamcode.robot.controllers.ControllerState;
@TeleOp(group = "drive")
public class OdoFieldCentric extends LinearOpMode {

    public enum CycleState2 {
        CYCLE2_START,
        CYCLE2_UP,
        CYCLE2_EXTEND,
        CYCLE2_GRABBER_TOP,
        CYCLE2_DUMP,
        CYCLE2_RETRACT
    };

    CycleState2 cycleState = CycleState2.CYCLE2_START;



    ElapsedTime cycleTimer = new ElapsedTime();

    ElapsedTime stateTimer = new ElapsedTime();

    ElapsedTime grabberTimer = new ElapsedTime();

    ElapsedTime slideTimer = new ElapsedTime();

    private int startTime = 100;

    private int topGrabTime2 = 250;

    private int slideExtendTime = 1500;

    private int grabberTopTime = 300;

    private int retractTime = 500;


    @Override
    public void runOpMode() throws InterruptedException {

        Hardware Oscar = new Hardware(hardwareMap);

        Oscar.drive.setPoseEstimate(new Pose2d(6, -48, 180));

        ControllerState controller1 = new ControllerState(gamepad1);
        ControllerState controller2 = new ControllerState(gamepad2);



        controller1.addEventListener("x", ButtonState.HELD, () -> Oscar.grabber.carousellOn());
        controller1.addEventListener("x", ButtonState.OFF, () -> Oscar.grabber.carousellOff());


        //It all keeps heading up
        // When button y is pressed it will go through the entire top cycle
        controller2.addEventListener("a", ButtonState.PRESSED, () -> {
            Oscar.elbow.goToGrabPos();
            Oscar.grabber.goStart();
        });

        controller2.addEventListener("b", ButtonState.PRESSED, () -> {
            Oscar.elbow.moveStart();
            Oscar.grabber.goStart();
        });
        controller2.addEventListener("x", ButtonState.PRESSED, () -> {
            Oscar.grabber.grab();
        });
        controller2.addEventListener("dpad_up", ButtonState.PRESSED, () -> {
            Oscar.elbow.moveStart();
            Oscar.grabber.openGrab();
            Oscar.grabber.goStart();
            Thread.sleep(250);
            Oscar.slides.slidesHome();
            Oscar.slides.slidesAbsoluteOut();
            Thread.sleep(200);
            Oscar.elbow.goToGrabPos();
            Thread.sleep(180);
            Oscar.slides.slidesHome();
        });
        controller2.addEventListener("left_bumper", ButtonState.PRESSED, () -> {
            Oscar.slides.slidesHome();
        });
        controller2.addEventListener("dpad_left", ButtonState.PRESSED, () -> {
            Oscar.slides.slidesGrab();
        });
        controller2.addEventListener("dpad_down", ButtonState.PRESSED, () -> {
            Oscar.elbow.moveStart();
            Thread.sleep(500);
            Oscar.grabber.goStart();
            Oscar.grabber.grabberGrabExtra();
            Oscar.slides.slidesGrab();
            Thread.sleep(1500);
            Oscar.elbow.goToGrabPos();
            Oscar.grabber.openGrab();
        });

        Oscar.grabber.openGrab();
        Oscar.elbow.goToGrabPos();

        waitForStart();//

        while (opModeIsActive()) {
            Pose2d poseEstimate = Oscar.drive.getPoseEstimate();

            Vector2d input = new Vector2d(
                    -gamepad1.left_stick_y,
                    -gamepad1.left_stick_x
            ).rotated(poseEstimate.getHeading());

            telemetry.update();

            telemetry.addData("Slide Position", Oscar.slides.getMotorPosition());

            telemetry.addData("Endstop", !Oscar.slides.getEndstop());

            telemetry.addData("IS GRABBING? ", Oscar.grabber.getIsGrab());
            telemetry.addData("IS GRABBING EXTRA? ", Oscar.grabber.getIsGrabExtra());



            switch (cycleState) {
                case CYCLE2_START:
                    telemetry.addLine("CYCLE START");
                    if(gamepad2.y){
                        Oscar.grabber.closeGrab();
                        Oscar.grabber.goStart();
                        Oscar.slides.slidesHome();
                        cycleTimer.reset();
                        stateTimer.reset();
                        cycleState = CycleState2.CYCLE2_UP;
                    }
                    break;

                case CYCLE2_UP:
                    telemetry.addLine("CYCLE UP");
                    if(stateTimer.milliseconds() >= 100) {
                        if (cycleTimer.milliseconds() >= startTime + 100) {
                            Oscar.elbow.moveStart();
                            cycleTimer.reset();
                            stateTimer.reset();
                            cycleState = CycleState2.CYCLE2_EXTEND;
                        }
                    }
                    break;

                case CYCLE2_EXTEND:
                    telemetry.addLine("CYCLE EXTEND");
                    if(stateTimer.milliseconds() >= 500) {
                        Oscar.slides.slidesTop();
                        cycleTimer.reset();
                        stateTimer.reset();
                        cycleState = CycleState2.CYCLE2_DUMP;
                    }
                    break;

                case CYCLE2_DUMP:
                    telemetry.addLine("CYCLE DUMP");
                    if(Oscar.slides.getMotorPosition() > Oscar.slides.TOP_SLIDE_TICKS - 480) {
                        Oscar.elbow.moveTop();
                        Oscar.grabber.goTop();
                        cycleTimer.reset();
                        stateTimer.reset();
                        cycleState = CycleState2.CYCLE2_GRABBER_TOP;
                    }
                    break;

                case CYCLE2_GRABBER_TOP:
                    telemetry.addLine("GRABBER TOP");
                    if(stateTimer.milliseconds() >= 250) {
                        Oscar.grabber.openGrab();
                        Oscar.grabber.grabberGrabExtra();
                        cycleTimer.reset();
                        stateTimer.reset();
                        slideTimer.reset();
                        grabberTimer.reset();
                        cycleState = CycleState2.CYCLE2_RETRACT;
                    }
                    break;

                case CYCLE2_RETRACT:
                    telemetry.addLine("SLIDES RETRACT");
                    if(stateTimer.milliseconds() >= 600) {
                        Oscar.elbow.moveStart();
                        if(cycleTimer.milliseconds() >= 550 + retractTime) {
                            Oscar.grabber.goStart();
                            Oscar.grabber.closeGrab();
                            Oscar.slides.slidesGrab();
                            if(Oscar.slides.getMotorPosition() <= 420) {
                                Oscar.elbow.goToGrabPos();
                                Oscar.grabber.grabberGrabExtra();
                                Oscar.grabber.openGrab();
                                cycleTimer.reset();
                                stateTimer.reset();
                                slideTimer.reset();
                                grabberTimer.reset();
                                cycleState = CycleState2.CYCLE2_START;
                            }
                        }
                    }
                    break;

                default:
                    cycleState = CycleState2.CYCLE2_START;
            }

            if (gamepad1.a && cycleState != CycleState2.CYCLE2_START) {
                cycleState = CycleState2.CYCLE2_START;
            }

            controller1.updateControllerState();
            controller2.updateControllerState();

            controller1.handleEvents();
            controller2.handleEvents();

            if(Oscar.slides.getMotorPosition() <= 100) {
                if (gamepad2.left_trigger > .1) {
                    Oscar.intake.reverse();
                } else if (gamepad2.right_trigger > .1) {
                    Oscar.intake.forward();
                } else {
                    Oscar.intake.off();
                }

                if (gamepad2.left_bumper) {
                    Oscar.intake.reverse();
                } else if (gamepad2.right_bumper) {
                    Oscar.intake.forward();
                } else {
                    Oscar.intake.off();
                }
            }
            else {
                Oscar.intake.off();
            }

            Oscar.drive.setWeightedDrivePower(
                    new Pose2d(
                            input.getX() * 1,
                            input.getY() * 1,
                            -gamepad1.right_stick_x * .5
                    )
            );
        }
    }
}
