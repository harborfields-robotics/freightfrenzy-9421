package org.firstinspires.ftc.teamcode.Teleop;

import android.widget.Button;

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
    };

    CycleState cycleState = CycleState.CYCLE_START;

    ElapsedTime cycleTimer = new ElapsedTime();

    ElapsedTime stateTimer = new ElapsedTime();

    ElapsedTime grabberTimer = new ElapsedTime();

    ElapsedTime slideTimer = new ElapsedTime();

    private int startTime = 300;

    private int topGrabTime = 250;

    private int elbowTopTime = 1500;

    private int grabberTopTime = 300;

    private int retractTime = 500;






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
        controller1.addEventListener("dpad_left", ButtonState.HELD, () -> {Oscar.updateX(1); Oscar.setVel(new Pose2d(Oscar.getPoseX(),Oscar.getPoseY(),Oscar.getPoseHeading()));});
        controller1.addEventListener("dpad_left", ButtonState.OFF, () -> {Oscar.updateX(0);});

        controller1.addEventListener("dpad_down", ButtonState.HELD, () -> {Oscar.updateY(.7); Oscar.setVel(new Pose2d(Oscar.getPoseX(),Oscar.getPoseY(),Oscar.getPoseHeading()));});
        controller1.addEventListener("dpad_down", ButtonState.OFF, () -> {Oscar.updateY(0);});

        controller1.addEventListener("dpad_right", ButtonState.HELD, () -> {Oscar.updateX(-1); Oscar.setVel(new Pose2d(Oscar.getPoseX(),Oscar.getPoseY(),Oscar.getPoseHeading()));});
        controller1.addEventListener("dpad_right", ButtonState.OFF, () -> {Oscar.updateX(0);});

        controller1.addEventListener("dpad_up",ButtonState.HELD, () -> {Oscar.updateY(-.7); Oscar.setVel(new Pose2d(Oscar.getPoseX(),Oscar.getPoseY(),Oscar.getPoseHeading()));});
        controller1.addEventListener("dpad_up", ButtonState.OFF, () -> {Oscar.updateY(0);});

        controller1.addEventListener("left_trigger", AnalogCheck.GREATER_THAN, 0.1, () -> {Oscar.updateHeading(.3); Oscar.setVel(new Pose2d(Oscar.getPoseX(),Oscar.getPoseY(),Oscar.getPoseHeading()));});
        controller1.addEventListener("left_trigger", AnalogCheck.LESS_THAN_EQUALS, 0.1, () -> {Oscar.updateHeading(0);});

        controller1.addEventListener("right_trigger", AnalogCheck.GREATER_THAN, 0.1, () -> {Oscar.updateHeading(-.3); Oscar.setVel(new Pose2d(Oscar.getPoseX(),Oscar.getPoseY(),Oscar.getPoseHeading()));});
        controller1.addEventListener("right_trigger", AnalogCheck.LESS_THAN_EQUALS, 0.1, () -> {Oscar.updateHeading(0);});

        controller1.addEventListener("x", ButtonState.HELD, () -> Oscar.grabber.carousellOn());
        controller1.addEventListener("x", ButtonState.OFF, () -> Oscar.grabber.carousellOff());
        //controller1.addEventListener("right_bumper",ButtonState.HELD,() ->{Oscar.intake.setIntakeDirection(false); Oscar.intake.setIntakeMode(true);});
       //controller1.addEventListener("right_bumper",ButtonState.OFF,() ->{Oscar.intake.setIntakeMode(false);});
        /*controller 2
        sets intake on or off
        runs cycle for placing and releasing
         */
        // toggle intake on if the grabber is open and the elbow is at its home position
        controller2.addEventListener("left_trigger",AnalogCheck.GREATER_THAN, 0.1,() ->{Oscar.intake.on();});
        controller2.addEventListener("left_trigger",AnalogCheck.LESS_THAN, 0.1,() ->{Oscar.intake.off();});
       // controller2.addEventListener("right_trigger", AnalogCheck.GREATER_THAN, 0.1,()-> {Oscar.intake.reverse();});
        //controller2.addEventListener("right_trigger",AnalogCheck.LESS_THAN, 0.1,() ->{Oscar.intake.off();});
        //IDK how this will work will test to see but may change

        //It all keeps heading up
        // When button y is pressed it will go through the entire top cycle
        controller2.addEventListener("a", ButtonState.PRESSED, () ->{Oscar.grabber.goStart(); Oscar.grabber.moveByAngle(-.1, "start"); Thread.sleep(200); Oscar.elbow.goToGrabPos(); Oscar.grabber.moveByAngle(.1, "start"); Oscar.grabber.openGrab();});
        controller2.addEventListener("y", ButtonState.PRESSED,() -> {Oscar.elbow.moveBottom(); Oscar.grabber.goBottom();});
        controller2.addEventListener("b", ButtonState.PRESSED, () -> {Oscar.elbow.moveStart(); Oscar.grabber.goStart();});
        controller2.addEventListener("x", ButtonState.PRESSED, () -> {Oscar.grabber.grab();});
        controller2.addEventListener("dpad_up", ButtonState.PRESSED, () -> {Oscar.elbow.moveStart(); Thread.sleep(250); Oscar.slides.slidesTop(); Thread.sleep(1500); Oscar.elbow.moveTop(); Oscar.grabber.goTop(); Oscar.grabber.grabberGrabExtra();});
        controller2.addEventListener("left_bumper", ButtonState.PRESSED, () -> {Oscar.slides.slidesHome();});
        controller2.addEventListener("dpad_left", ButtonState.PRESSED, () -> {Oscar.slides.slidesGrab();});
        controller2.addEventListener("dpad_down", ButtonState.PRESSED, () -> {Oscar.elbow.moveStart(); Thread.sleep(500); Oscar.grabber.goStart(); Oscar.grabber.grabberGrabExtra(); Oscar.slides.slidesGrab();});


        waitForStart();//

        while(opModeIsActive()){

            telemetry.update();
            telemetry.addData("Wheel location", Oscar.drive.getWheelPositions());
            telemetry.addData("Slide Position", Oscar.slides.getMotorPosition());
            telemetry.addData("Slide Position", Oscar.slides.getCurrentTargetPosition());
            telemetry.addData("Endstop", !Oscar.slides.getEndstop());


            switch(cycleState){
                case CYCLE_START:
                    telemetry.addLine("in cycle start");
                    if(gamepad1.y){


                        Oscar.grabber.goStart();
                        Oscar.grabber.moveByAngle(-.1, "start");
                        cycleTimer.reset();
                        stateTimer.reset();
                        cycleState = CycleState.CYCLE_UP;






                    }
                    break;

                case CYCLE_UP:

                    telemetry.addLine("in cycle up");
                    if(stateTimer.milliseconds() >= 1000) {
                        if (cycleTimer.milliseconds() >= startTime) {
                            Oscar.elbow.goToGrabPos();

                            telemetry.addData("timer1", cycleTimer.milliseconds());

                            Oscar.grabber.moveByAngle(.1, "start");
                            Oscar.grabber.openGrab();
                            cycleTimer.reset();

                            stateTimer.reset();

                            cycleState = CycleState.CYCLE_EXTEND;

                        }
                    }



                    break;
                case CYCLE_EXTEND:
                    //if(gamepad1.y) {

                    telemetry.addLine("in cycle extend");
                    telemetry.addData("timer2", cycleTimer.milliseconds());
                    if(stateTimer.milliseconds() >= 1500) {
                        Oscar.elbow.moveStart();
                        if (cycleTimer.milliseconds() >= topGrabTime) {
                            Oscar.slides.slidesTop();
                            cycleTimer.reset();
                            stateTimer.reset();
                            cycleState = CycleState.CYCLE_DUMP;

                        }
                    }


                        //cycleState = cycleState.CYCLE_RETRACT;
                    //}
                    break;
                case CYCLE_DUMP:
                    telemetry.addLine("In cycle dump");
                    if(stateTimer.milliseconds() >= 1500) {
                        if ((cycleTimer.milliseconds()) >= elbowTopTime) {
                            Oscar.elbow.moveTop();
                            Oscar.grabber.goTop();
                            cycleTimer.reset();
                            stateTimer.reset();
                            cycleState = CycleState.CYCLE_GRABBER_TOP;
                        }
                    }
                    break;
                case CYCLE_GRABBER_TOP:
                    telemetry.addLine("Grabber Top");
                    if(stateTimer.milliseconds() >= 1000) {
                        if (cycleTimer.milliseconds() >= grabberTopTime) {
                            //Oscar.grabber.goTop();
                            Oscar.grabber.grabberGrabExtra();
                            cycleTimer.reset();
                            stateTimer.reset();
                            grabberTimer.reset();
                            cycleState = CycleState.CYCLE_RETRACT;

                        }
                    }
                    break;


                case CYCLE_RETRACT:
                    //if(gamepad1.y) {
                        telemetry.addLine("retracting slides");
                        telemetry.addData("in retract timer", cycleTimer.milliseconds());
                        if(stateTimer.milliseconds() >= 1000){
                        //Oscar.elbow.moveStart();
                        if (cycleTimer.milliseconds() >= retractTime) {
                            Oscar.elbow.moveStart();
                           // Oscar.grabber.goStart();
                            //Oscar.grabber.grabberGrabExtra();
                            if(grabberTimer.milliseconds() >= 1000 + retractTime){
                                Oscar.grabber.goStart();
                                Oscar.grabber.grabberGrabExtra();
                                slideTimer.reset();
                                Oscar.slides.slidesGrab();
                                if(slideTimer.milliseconds() >= 200) {
                                    Oscar.elbow.goToGrabPos();
                                    Oscar.grabber.grab();
                                    cycleTimer.reset();
                                    cycleState = CycleState.CYCLE_START;


                                }



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


            Oscar.setVel(new Pose2d(
                    -Math.pow(controller1.getAnalogValue("left_stick_x"),3),
                    Math.pow(controller1.getAnalogValue("left_stick_y"),3),
                    -Math.pow(controller1.getAnalogValue("right_stick_x"),3)

            ));


            controller1.handleEvents();
            controller2.handleEvents();

            if(gamepad2.left_trigger > .5){
                Oscar.intake.reverse();


            }
            else if(gamepad2.right_trigger > .5){
                Oscar.intake.on();
            }
            else{
                Oscar.intake.off();
            }

            //Allow Dpad override
            Oscar.drive.setDrivePower(Oscar.getVel());
            Oscar.drive.update();



            Oscar.setVel(new Pose2d(Oscar.getPoseX(),Oscar.getPoseY(),Oscar.getPoseHeading()));





        }


    }

}
