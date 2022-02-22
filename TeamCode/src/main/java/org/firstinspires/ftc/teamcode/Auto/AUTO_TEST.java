package org.firstinspires.ftc.teamcode.Auto;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.roadrunner.geometry.Pose2d;
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
import org.firstinspires.ftc.teamcode.trajectorysequence.sequencesegment.WaitSegment;

@Config
@Autonomous(group = "advanced")
public class AUTO_TEST extends LinearOpMode {

    enum State {
        AFTER_DEPOSIT_TO_WAREHOUSE,
        INTAKE_AND_ADJUST_UNTIL_THING_IN,
        DRIVE_TO_DEPOSIT_AND_START_DEPOSIT,
        IDLE_BECAUSE_NOT_ENOUGH_TIME
    }

    private State currentState = State.AFTER_DEPOSIT_TO_WAREHOUSE;

    private static final double Y_COORDINATE_FOR_BARRIER = -64;
    private static final double X_COORDINATE_FOR_BARRIER = 8.8;
    private static final double Y_COORDINATE_FOR_DEPOSIT = -55.2;
    private static final double X_COORDINATE_FOR_DEPOSIT = 4.1;
    private static final double HEADING_FOR_DEPOSIT = Math.toRadians(210);

    private double DEFAULT_BACK_BY_HOW_MUCH_TO_WAREHOUSE = 30;
    private double EXTRA_BACK_BY_HOW_MUCH_IN_WAREHOUSE = 1;

    public static Pose2d startPR = new Pose2d(6.4, -64, Math.toRadians(180));
    public static Pose2d depositPosition = new Pose2d( X_COORDINATE_FOR_DEPOSIT,Y_COORDINATE_FOR_DEPOSIT, HEADING_FOR_DEPOSIT);
    public static Pose2d barrierPosition = new Pose2d(X_COORDINATE_FOR_DEPOSIT, Y_COORDINATE_FOR_DEPOSIT, Math.toRadians(180));
    public static Pose2d parallelPosition = new  Pose2d(X_COORDINATE_FOR_BARRIER, Y_COORDINATE_FOR_BARRIER, Math.toRadians(180));
    public static Pose2d splineCV = new Pose2d(4.1,-55.2,Math.toRadians(210));
    public static Pose2d splineTest = new Pose2d(4.1,-55.2, Math.toRadians(210));
    public static Pose2d RevertTest = new Pose2d(4.1,-63.2, Math.toRadians(180));

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
                .lineToLinearHeading(splineCV)
                .lineToLinearHeading(RevertTest)
                .build();

        DEPOSIT_TO_WAREHOUSE = Oscar.drive.trajectorySequenceBuilder(PRELOAD_TRAJECTORY.end())
                .back(30)
                .forward(30)
                .build();

        WAREHOUSE_TO_DEPOSIT = Oscar.drive.trajectorySequenceBuilder(DEPOSIT_TO_WAREHOUSE.end())
                .lineToLinearHeading(splineTest)
                .lineToLinearHeading(RevertTest)
                .build();




        BACK_EXTRA = Oscar.drive.trajectoryBuilder(WAREHOUSE_TO_DEPOSIT.end())
                .back(EXTRA_BACK_BY_HOW_MUCH_IN_WAREHOUSE)
                .build();

        waitForStart();

        if (isStopRequested()) return;

        ElapsedTime AUTO_RUNTIME = new ElapsedTime();
        ElapsedTime time = new ElapsedTime();

        Oscar.drive.followTrajectorySequence(PRELOAD_TRAJECTORY);
        Oscar.drive.followTrajectorySequence(DEPOSIT_TO_WAREHOUSE);
        Oscar.drive.followTrajectorySequence(WAREHOUSE_TO_DEPOSIT);
    }
}

