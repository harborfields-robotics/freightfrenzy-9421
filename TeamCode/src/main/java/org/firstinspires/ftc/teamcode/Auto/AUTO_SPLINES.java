package org.firstinspires.ftc.teamcode.Auto;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.acmerobotics.roadrunner.trajectory.Trajectory;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.util.ElapsedTime;

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

    private static final double Y_COORDINATE_FOR_BARRIER = -63.2;
    private static final double X_COORDINATE_FOR_BARRIER = 15.6;
    private static final double Y_COORDINATE_FOR_DEPOSIT = -58.2;
    private static final double X_COORDINATE_FOR_DEPOSIT = 9.5;
    private static final double HEADING_FOR_DEPOSIT = Math.toRadians(210);

    private double DEFAULT_BACK_BY_HOW_MUCH_TO_WAREHOUSE = 30;
    private double EXTRA_BACK_BY_HOW_MUCH_IN_WAREHOUSE = 1;

    public static Pose2d startPR = new Pose2d(6, -64, Math.toRadians(180));
    public static Pose2d depositPosition = new Pose2d( X_COORDINATE_FOR_DEPOSIT,Y_COORDINATE_FOR_DEPOSIT, HEADING_FOR_DEPOSIT);
    public static Pose2d barrierPosition = new Pose2d(X_COORDINATE_FOR_DEPOSIT, Y_COORDINATE_FOR_DEPOSIT, Math.toRadians(180));
    public static Pose2d parallelPosition = new  Pose2d(X_COORDINATE_FOR_BARRIER, Y_COORDINATE_FOR_BARRIER, Math.toRadians(180));

    //vectors for deposit

    public static Vector2d deposit = new Vector2d(-9.3,-64);
    public static Vector2d revert = new Vector2d(46.9,-64);
    private TrajectorySequence PRELOAD_TRAJECTORY;
    private TrajectorySequence DEPOSIT_TO_WAREHOUSE;
    private TrajectorySequence WAREHOUSE_TO_DEPOSIT;
    private Trajectory BACK_EXTRA;

    private boolean IT_DID_THE_FLIP = false;

    Hardware Oscar;

    private void CALIBRATE_WAREHOUSE_TO_DEPOSIT() {
        WAREHOUSE_TO_DEPOSIT = Oscar.drive.trajectorySequenceBuilder(Oscar.drive.getPoseEstimate())
                .lineToLinearHeading(barrierPosition)
                .lineToLinearHeading(parallelPosition)
                .lineToLinearHeading(depositPosition)
                .build();
    }

    @Override
    public void runOpMode() throws InterruptedException {
        Oscar = new Hardware(hardwareMap, telemetry);

        Oscar.drive.setPoseEstimate(startPR);

        DEPOSIT_FSM deposit_fsm = new DEPOSIT_FSM(Oscar, telemetry, gamepad1, gamepad2);
        INTAKE_FSM intake_fsm = new INTAKE_FSM(Oscar, telemetry, gamepad1, gamepad2);

        BarcodePositionDetector detector = new BarcodePositionDetector(telemetry);

        BarcodePositionDetector.BarcodePosition position = detector.getBarcodePosition();

        PRELOAD_TRAJECTORY = Oscar.drive.trajectorySequenceBuilder(startPR)
                .strafeRight(5)
                .lineToLinearHeading(depositPosition)
                .build();

        DEPOSIT_TO_WAREHOUSE = Oscar.drive.trajectorySequenceBuilder(PRELOAD_TRAJECTORY.end())
                .lineTo(deposit)
                .build();

        WAREHOUSE_TO_DEPOSIT = Oscar.drive.trajectorySequenceBuilder(DEPOSIT_TO_WAREHOUSE.end())
                .lineTo(revert)
                .build();

        BACK_EXTRA = Oscar.drive.trajectoryBuilder(Oscar.drive.getPoseEstimate())
                .back(EXTRA_BACK_BY_HOW_MUCH_IN_WAREHOUSE)
                .build();

        waitForStart();

        if (isStopRequested()) return;

        ElapsedTime AUTO_RUNTIME = new ElapsedTime();
        ElapsedTime time = new ElapsedTime();

        Oscar.drive.followTrajectorySequenceAsync(PRELOAD_TRAJECTORY);
        while (!deposit_fsm.isAnyDeposited()) {
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
            if(deposit_fsm.THE_THING_CAN_BE_DROPPED_NOW) {
                deposit_fsm.DROP_THE_THING_NOW = true;
            }
        }
        Oscar.drive.followTrajectorySequenceAsync(DEPOSIT_TO_WAREHOUSE);

        while(opModeIsActive() && !isStopRequested()) {

            telemetry.update();
            telemetry.addData("State: ", currentState);

            Oscar.drive.update();
            deposit_fsm.doDepositTopAsync();
            deposit_fsm.doDepositMiddleAsync();
            deposit_fsm.doDepositBottomAsync();

            if(((DistanceSensor) Oscar.colorBack).getDistance(DistanceUnit.CM) < 2) {
                intake_fsm.SET_EXEC_BACK_FLIP(true);
                IT_DID_THE_FLIP = true;
            }
            if(((DistanceSensor) Oscar.colorFront).getDistance(DistanceUnit.CM) < 2) {
                intake_fsm.SET_EXEC_FRONT_FLIP(true);
                IT_DID_THE_FLIP = true;
            }

            switch (currentState) {
                case AFTER_DEPOSIT_TO_WAREHOUSE:
                    Oscar.intake.backIn();
                    if(IT_DID_THE_FLIP) {
                        IT_DID_THE_FLIP = false;
                        CALIBRATE_WAREHOUSE_TO_DEPOSIT();
                        //moves to the warehouse to the deposit
                        Oscar.drive.followTrajectorySequenceAsync(WAREHOUSE_TO_DEPOSIT);
                        currentState = State.DRIVE_TO_DEPOSIT_AND_START_DEPOSIT;
                    }
                    if(!Oscar.drive.isBusy()) {
                        time.reset();
                        currentState = State.INTAKE_AND_ADJUST_UNTIL_THING_IN;
                    }
                    break;
                case INTAKE_AND_ADJUST_UNTIL_THING_IN:
                    if(IT_DID_THE_FLIP) {
                        IT_DID_THE_FLIP = false;
                        CALIBRATE_WAREHOUSE_TO_DEPOSIT();
                        Oscar.drive.followTrajectorySequenceAsync(WAREHOUSE_TO_DEPOSIT);
                        currentState = State.DRIVE_TO_DEPOSIT_AND_START_DEPOSIT;
                    }
                    if(time.milliseconds() > 150) {
                        Oscar.drive.followTrajectoryAsync(BACK_EXTRA);
                        time.reset();
                    }
                    break;
                case DRIVE_TO_DEPOSIT_AND_START_DEPOSIT:
                    if(Oscar.drive.getPoseEstimate().getX() < 20) {
                        deposit_fsm.startDeposittop = true;
                    }
                    if(deposit_fsm.THE_THING_CAN_BE_DROPPED_NOW) {
                        deposit_fsm.DROP_THE_THING_NOW = true;
                    }
                    if(deposit_fsm.isAnyDeposited()) {
                        Oscar.drive.followTrajectorySequenceAsync(DEPOSIT_TO_WAREHOUSE);
                        if(AUTO_RUNTIME.seconds() > 25) {
                            currentState = State.IDLE_BECAUSE_NOT_ENOUGH_TIME;
                        }
                        else {
                            currentState = State.AFTER_DEPOSIT_TO_WAREHOUSE;
                        }
                    }

            }
        }
        Oscar.drive.update();
    }
}
