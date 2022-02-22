package org.firstinspires.ftc.teamcode.Auto;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.trajectory.Trajectory;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Predicate;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.robot.DEPOSIT_FSM;
import org.firstinspires.ftc.teamcode.robot.Hardware;
import org.firstinspires.ftc.teamcode.robot.INTAKE_FSM;
import org.firstinspires.ftc.teamcode.trajectorysequence.TrajectorySequence;
import org.firstinspires.ftc.teamcode.CV.BarcodePositionDetector;

@Config
@Autonomous(group = "advanced")
public class AUTO_SPLINES extends LinearOpMode {

    enum State {
        AFTER_DEPOSIT_TO_WAREHOUSE,
        INTAKE_AND_ADJUST_UNTIL_THING_IN,
        DRIVE_TO_DEPOSIT_AND_START_DEPOSIT,
        IDLE_BECAUSE_NOT_ENOUGH_TIME
    }

    private State currentState = State.AFTER_DEPOSIT_TO_WAREHOUSE;

    private Pose2d startPose = new Pose2d(6.4, -64, Math.toRadians(180));

    Hardware Oscar;

    @Override
    public void runOpMode() throws InterruptedException {
        Oscar = new Hardware(hardwareMap, telemetry);

        Oscar.drive.setPoseEstimate(startPose);

        DEPOSIT_FSM deposit_fsm = new DEPOSIT_FSM(Oscar, telemetry, gamepad1, gamepad2);
        INTAKE_FSM intake_fsm = new INTAKE_FSM(Oscar, telemetry, gamepad1, gamepad2);

        BarcodePositionDetector detector = new BarcodePositionDetector(telemetry);

        BarcodePositionDetector.BarcodePosition position = detector.getBarcodePosition();

        TrajectorySequence PRELOAD_TRAJECTORY = Oscar.drive.trajectorySequenceBuilder(startPose)
                .strafeRight(7)
                .turn(Math.toRadians(30))
                .build();
//46.9, -64
        TrajectorySequence PRELOAD_TRAJECTORY_SECOND = Oscar.drive.trajectorySequenceBuilder(PRELOAD_TRAJECTORY.end())
                .turn(Math.toRadians(-30))
                .strafeLeft(7)
                .lineToLinearHeading(new Pose2d(46.9,-64,Math.toRadians(180)))
                .build();

        waitForStart();

        if (isStopRequested()) return;

        ElapsedTime AUTO_RUNTIME = new ElapsedTime();
        ElapsedTime time = new ElapsedTime();

        boolean RUN_PRELOAD = true;

        while (RUN_PRELOAD) {
            Oscar.drive.update();
            deposit_fsm.doDepositTopAsync();
            deposit_fsm.doDepositMiddleAsync();
            deposit_fsm.doDepositBottomAsync();
            if(!deposit_fsm.isAnyBusy()) {
                if (position == BarcodePositionDetector.BarcodePosition.LEFT) {
                    deposit_fsm.startDeposittop = true;
                } else if (position == BarcodePositionDetector.BarcodePosition.MIDDLE) {
                    deposit_fsm.startDepositmid = true;
                } else {
                    deposit_fsm.startDeposittop = true;
                }
            }
            if(!deposit_fsm.isAnyDeposited()) {
                Oscar.drive.followTrajectorySequenceAsync(PRELOAD_TRAJECTORY);
            }
            else {
                Oscar.drive.followTrajectorySequenceAsync(PRELOAD_TRAJECTORY_SECOND);
                if(!Oscar.drive.isBusy()) {
                    RUN_PRELOAD = false;
                }
            }
            if(deposit_fsm.THE_THING_CAN_BE_DROPPED_NOW) {
                deposit_fsm.DROP_THE_THING_NOW = true;
            }
        }
        while(opModeIsActive() && !isStopRequested()) {

            Oscar.drive.update();
        }
    }
}
