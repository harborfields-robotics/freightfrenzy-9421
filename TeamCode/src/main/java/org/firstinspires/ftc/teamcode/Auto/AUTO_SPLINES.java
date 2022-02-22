package org.firstinspires.ftc.teamcode.Auto;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.acmerobotics.roadrunner.trajectory.Trajectory;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Predicate;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.drive.DriveConstants;
import org.firstinspires.ftc.teamcode.drive.SampleMecanumDrive;
import org.firstinspires.ftc.teamcode.robot.DEPOSIT_FSM;
import org.firstinspires.ftc.teamcode.robot.Hardware;
import org.firstinspires.ftc.teamcode.robot.INTAKE_FSM;
import org.firstinspires.ftc.teamcode.trajectorysequence.TrajectorySequence;
import org.firstinspires.ftc.teamcode.CV.BarcodePositionDetector;

@Config
@Autonomous(group = "advanced")
public class AUTO_SPLINES extends LinearOpMode {

    enum State {
        INTAKE_AND_ADJUST_UNTIL_THING_IN,
        DRIVE_FROM_INTAKE_POSITION_TO_WAREHOUSE_LINE,
        DRIVE_TO_DEPOSIT_AND_START_DEPOSIT,
        AFTER_DEPOSIT_TO_WAREHOUSE,
        IDLE_BECAUSE_NOT_ENOUGH_TIME
    }

    private State currentState = State.INTAKE_AND_ADJUST_UNTIL_THING_IN;

    private static final double Y_COORDINATE_FOR_BARRIER = -63.2;
    private static final double X_COORDINATE_FOR_BARRIER = 15.6;
    private static final double Y_COORDINATE_FOR_DEPOSIT = -58.2;
    private static final double X_COORDINATE_FOR_DEPOSIT = 9.5;
    private static final double HEADING_FOR_DEPOSIT = Math.toRadians(210);

    private double DEFAULT_BACK_BY_HOW_MUCH_TO_WAREHOUSE = 30;
    private double EXTRA_BACK_BY_HOW_MUCH_IN_WAREHOUSE = 1;

    private boolean ENSURE_ONE_DEPOSIT = false;

    private TrajectorySequence PRELOAD_TRAJECTORY;
    private TrajectorySequence DEPOSIT_TO_WAREHOUSE;
    private TrajectorySequence WAREHOUSE_TO_DEPOSIT;
    private Trajectory BACK_EXTRA;

    private boolean IT_DID_THE_FLIP = false;

    private final Pose2d startPose = new Pose2d(6.4, -64, Math.toRadians(180));

    private final Pose2d parallelDepositPosition = new Pose2d(-9.3,-64, Math.toRadians(180));

    private final Pose2d warehousePosition = new Pose2d(42,-64,Math.toRadians(180));

    private final Pose2d intakeExtraPosition = new Pose2d(54,-64,Math.toRadians(180));

    Hardware Oscar;

    @Override
    public void runOpMode() throws InterruptedException {
        Oscar = new Hardware(hardwareMap, telemetry);

        Oscar.drive.setPoseEstimate(startPose);

        DEPOSIT_FSM deposit_fsm = new DEPOSIT_FSM(Oscar, telemetry, gamepad1, gamepad2);
        INTAKE_FSM intake_fsm = new INTAKE_FSM(Oscar, telemetry, gamepad1, gamepad2);

        BarcodePositionDetector detector = new BarcodePositionDetector(telemetry);

        BarcodePositionDetector.BarcodePosition position = detector.getBarcodePosition();

        telemetry.addData("position", position);




        Trajectory DEPOSIT_TO_WAREHOUSE = Oscar.drive.trajectoryBuilder(parallelDepositPosition)
                .lineToLinearHeading(warehousePosition)
                .build();

        Trajectory WAREHOUSE_TO_DEPOSIT = Oscar.drive.trajectoryBuilder(warehousePosition)
                .lineToLinearHeading(parallelDepositPosition)
                .build();

        Trajectory WAREHOUSE_TO_INTAKE_EXTRA = Oscar.drive.trajectoryBuilder(warehousePosition)
                .lineToLinearHeading(
                        intakeExtraPosition,
                        SampleMecanumDrive.getVelocityConstraint(40, DriveConstants.MAX_ANG_VEL, DriveConstants.TRACK_WIDTH),
                        SampleMecanumDrive.getAccelerationConstraint(DriveConstants.MAX_ACCEL))
                .build();

        Trajectory INTAKE_EXTRA_TO_DEPOSIT = Oscar.drive.trajectoryBuilder(intakeExtraPosition)
                .lineToLinearHeading(parallelDepositPosition)
                .build();

        Trajectory INTAKE_EXTRA_TO_WAREHOUSE = Oscar.drive.trajectoryBuilder(intakeExtraPosition)
                .lineToLinearHeading(warehousePosition)
                .build();




        TrajectorySequence PRELOAD_TRAJECTORY = Oscar.drive.trajectorySequenceBuilder(startPose)
                .lineToLinearHeading(new Pose2d(6.4,-57,Math.toRadians(210)))
                .build();

        TrajectorySequence PRELOAD_TRAJECTORY_SECOND = Oscar.drive.trajectorySequenceBuilder(PRELOAD_TRAJECTORY.end())
                .lineToLinearHeading(new Pose2d(6.4,-64, Math.toRadians(180)))
                .lineToLinearHeading(warehousePosition)
                .build();

        waitForStart();

        if (isStopRequested()) return;

        ElapsedTime AUTO_RUNTIME = new ElapsedTime();
        ElapsedTime time = new ElapsedTime();

        Oscar.drive.followTrajectorySequence(PRELOAD_TRAJECTORY);
        boolean RUN_DEPOSIT = true;
        deposit_fsm.startDeposittop = true;
        while(RUN_DEPOSIT) {
            deposit_fsm.doDepositTopAsync();
            if(deposit_fsm.THE_THING_CAN_BE_DROPPED_NOW) {
                deposit_fsm.DROP_THE_THING_NOW = true;
            }
            if(!deposit_fsm.isAnyBusy()) {
                RUN_DEPOSIT = false;
            }
        }
        Oscar.drive.followTrajectorySequence(PRELOAD_TRAJECTORY_SECOND);
        Oscar.drive.followTrajectoryAsync(WAREHOUSE_TO_INTAKE_EXTRA);

        while(opModeIsActive() && !isStopRequested()) {

            telemetry.addData("State: ", currentState);
            telemetry.update();

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

            intake_fsm.doFlipBackAsync();
            intake_fsm.doFlipFrontAsync();

            switch (currentState) {
                case INTAKE_AND_ADJUST_UNTIL_THING_IN:
                    if(!IT_DID_THE_FLIP) {
                        Oscar.intake.forward();
                    }
                    else {
                        Oscar.intake.reverse();
                    }
                    if(!Oscar.drive.isBusy()) {
                        if(IT_DID_THE_FLIP) {
                            IT_DID_THE_FLIP = false;
                            Oscar.drive.followTrajectoryAsync(INTAKE_EXTRA_TO_DEPOSIT);
                            ENSURE_ONE_DEPOSIT = false;
                            currentState = State.DRIVE_TO_DEPOSIT_AND_START_DEPOSIT;
                        }
                        else {
                            Oscar.drive.followTrajectoryAsync(INTAKE_EXTRA_TO_WAREHOUSE);
                            currentState = State.DRIVE_FROM_INTAKE_POSITION_TO_WAREHOUSE_LINE;
                        }
                    }
                    break;
                case DRIVE_FROM_INTAKE_POSITION_TO_WAREHOUSE_LINE:
                    if(!Oscar.drive.isBusy()) {
                        Oscar.drive.followTrajectoryAsync(WAREHOUSE_TO_INTAKE_EXTRA);
                        currentState = State.INTAKE_AND_ADJUST_UNTIL_THING_IN;
                    }
                    break;
                case DRIVE_TO_DEPOSIT_AND_START_DEPOSIT:
                    if(Oscar.drive.getPoseEstimate().getX() < 0 && !ENSURE_ONE_DEPOSIT) {
                        deposit_fsm.startDeposittop = true;
                        ENSURE_ONE_DEPOSIT = true;
                    }
                    if(deposit_fsm.THE_THING_CAN_BE_DROPPED_NOW) {
                       deposit_fsm.DROP_THE_THING_NOW = true;
                       ENSURE_ONE_DEPOSIT = false;
                       Oscar.drive.followTrajectoryAsync(DEPOSIT_TO_WAREHOUSE);
                       currentState = State.AFTER_DEPOSIT_TO_WAREHOUSE;
                    }
                    break;
                case AFTER_DEPOSIT_TO_WAREHOUSE:
                    if(!IT_DID_THE_FLIP) {
                        Oscar.intake.forward();
                    }
                    else {
                        Oscar.intake.reverse();
                    }
                    if(!Oscar.drive.isBusy()) {
                        if(AUTO_RUNTIME.seconds() > 26) {currentState = State.IDLE_BECAUSE_NOT_ENOUGH_TIME;}
                        else {
                            if(!IT_DID_THE_FLIP) {
                                Oscar.drive.followTrajectoryAsync(WAREHOUSE_TO_INTAKE_EXTRA);
                                currentState = State.INTAKE_AND_ADJUST_UNTIL_THING_IN;
                            }
                            else {
                                IT_DID_THE_FLIP = false;
                                Oscar.drive.followTrajectoryAsync(WAREHOUSE_TO_DEPOSIT);
                                currentState = State.DRIVE_TO_DEPOSIT_AND_START_DEPOSIT;
                            }
                        }
                    }
                    break;
                case IDLE_BECAUSE_NOT_ENOUGH_TIME:
                    break;
                default:
                    currentState = State.INTAKE_AND_ADJUST_UNTIL_THING_IN;
                    break;
            }
            Oscar.drive.update();
        }
    }
}
