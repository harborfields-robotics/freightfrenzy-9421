package org.firstinspires.ftc.teamcode.Auto;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.trajectory.Trajectory;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.drive.DriveConstants;
import org.firstinspires.ftc.teamcode.drive.SampleMecanumDrive;
import org.firstinspires.ftc.teamcode.robot.DEPOSIT_FSM;
import org.firstinspires.ftc.teamcode.robot.DEPOSIT_LINEAR;
import org.firstinspires.ftc.teamcode.robot.Hardware;
import org.firstinspires.ftc.teamcode.robot.INTAKE_FSM;

@Config
@Disabled
@Autonomous(group = "advanced")
public class CYCLE_BOTTOM_PRELOAD_RED extends LinearOpMode {
    Hardware Oscar;

    double adjustableWarehouseX = 48;
    //How much further back it goes each cycle, because minerals get pushed around
    double amountIncreaseWarehouseX = 2;

    double AMOUNT_ITERATE_Y = 2;

    Pose2d startPose = new Pose2d(19, -64, Math.toRadians(180));
    Pose2d depositPose = new Pose2d(-4, -69, Math.toRadians(180));
    Pose2d bottomDepositPose = new Pose2d(-.8, -69, Math.toRadians(180));
    Pose2d warehousePose = new Pose2d(adjustableWarehouseX, -69, Math.toRadians(180));

    Trajectory START_TO_DEPOSIT;
    Trajectory DEPOSIT_TO_WAREHOUSE;
    Trajectory WAREHOUSE_TO_DEPOSIT;
    Trajectory START_TO_DEPOSIT_BOTTOM;
    Trajectory DEPOSIT_BOTTOM_TO_WAREHOUSE;

    private void iterateWarehouseX() {
        adjustableWarehouseX += amountIncreaseWarehouseX;
        warehousePose = new Pose2d(adjustableWarehouseX, -69, Math.toRadians(180));
        DEPOSIT_TO_WAREHOUSE = Oscar.drive.trajectoryBuilder(depositPose)
                .lineToLinearHeading(warehousePose)
                .build();
        WAREHOUSE_TO_DEPOSIT = Oscar.drive.trajectoryBuilder(warehousePose)
                .lineToLinearHeading(depositPose)
                .build();
    }

    enum STATE {
        INIT,
        BACKWARD,
        INTAKE,
        FORWARD,
        IDLE
    }

    @Override
    public void runOpMode() throws InterruptedException {
        Oscar = new Hardware(hardwareMap, telemetry);
        DEPOSIT_FSM deposit_fsm = new DEPOSIT_FSM(Oscar, telemetry, gamepad1, gamepad2);
        INTAKE_FSM intake_fsm = new INTAKE_FSM(Oscar, telemetry, gamepad1, gamepad2);

        START_TO_DEPOSIT = Oscar.drive.trajectoryBuilder(startPose)
                .lineToLinearHeading(depositPose)
                .build();
        START_TO_DEPOSIT_BOTTOM = Oscar.drive.trajectoryBuilder(startPose)
                .lineToLinearHeading(bottomDepositPose)
                .build();
        DEPOSIT_BOTTOM_TO_WAREHOUSE = Oscar.drive.trajectoryBuilder(bottomDepositPose)
                .lineToLinearHeading(warehousePose)
                .build();
        DEPOSIT_TO_WAREHOUSE = Oscar.drive.trajectoryBuilder(START_TO_DEPOSIT.end())
                .lineToLinearHeading(warehousePose)
                .build();
        WAREHOUSE_TO_DEPOSIT = Oscar.drive.trajectoryBuilder(DEPOSIT_TO_WAREHOUSE.end())
                .lineToLinearHeading(depositPose)
                .build();

        Oscar.drive.setPoseEstimate(startPose);

        Oscar.elbow.goToGrabPos();
        Oscar.grabber.goStart();
        Oscar.grabber.openGrab();
        Oscar.slides.slidesHome();

        STATE state = STATE.INIT;

        ElapsedTime time = new ElapsedTime();
        ElapsedTime RUNTIME = new ElapsedTime();

        boolean ENSURE_ONE_DEPOSIT = false;

        double STOP_CYCLING_TIMEOUT = 26;

        Oscar.flippers.moveUp("front");
        Oscar.flippers.moveDown("back");

        waitForStart();

        Oscar.drive.followTrajectoryAsync(START_TO_DEPOSIT_BOTTOM);
        time.reset();
        RUNTIME.reset();

        while(isStarted() && !isStopRequested()) {
            switch (state) {
                case INIT:
                    if(!ENSURE_ONE_DEPOSIT && time.milliseconds() > 500) {
                        deposit_fsm.startDepositbot = true;
                        ENSURE_ONE_DEPOSIT = true;
                    }
                    else if(deposit_fsm.THE_THING_CAN_BE_DROPPED_NOW) {
                        deposit_fsm.DROP_THE_THING_NOW = true;
                        ENSURE_ONE_DEPOSIT = false;
                        Oscar.drive.followTrajectoryAsync(DEPOSIT_BOTTOM_TO_WAREHOUSE);
                        state = STATE.BACKWARD;
                        time.reset();
                    }
                    break;
                case BACKWARD:
                    if(((DistanceSensor) Oscar.colorBack).getDistance(DistanceUnit.CM) < 1.5 || !Oscar.drive.isBusy()) {
                        state = STATE.INTAKE;
                        time.reset();
                    }
                    else {
                        Oscar.intake.forward();
                    }
                    break;
                case INTAKE:
                    Oscar.drive.setWeightedDrivePower(new Pose2d(-.4,.2,0));
                    if(((DistanceSensor) Oscar.colorBack).getDistance(DistanceUnit.CM) < 1.5 && !deposit_fsm.isAnyBusy()) {
                        intake_fsm.SET_EXEC_BACK_FLIP(true);
                        Oscar.drive.setWeightedDrivePower(new Pose2d(0,0,0));
                        Oscar.drive.followTrajectoryAsync(WAREHOUSE_TO_DEPOSIT);
                        Oscar.drive.setPoseEstimate(new Pose2d(Oscar.drive.getPoseEstimate().getX(), Oscar.drive.getPoseEstimate().getY() + AMOUNT_ITERATE_Y, Oscar.drive.getPoseEstimate().getHeading()));
                        if(RUNTIME.seconds() < STOP_CYCLING_TIMEOUT) {
                            state = STATE.FORWARD;
                            time.reset();
                        }
                        else state = STATE.IDLE;
                    }
                    break;
                case FORWARD:
                    if(((DistanceSensor) Oscar.colorBack).getDistance(DistanceUnit.CM) > 1.5 && Oscar.drive.getPoseEstimate().getX() < 18 && !ENSURE_ONE_DEPOSIT) {
                        deposit_fsm.startDeposittop = true;
                        ENSURE_ONE_DEPOSIT = true;
                    }
                    else if(deposit_fsm.THE_THING_CAN_BE_DROPPED_NOW) {
                        iterateWarehouseX();
                        deposit_fsm.DROP_THE_THING_NOW = true;
                        ENSURE_ONE_DEPOSIT = false;
                        Oscar.drive.followTrajectoryAsync(DEPOSIT_TO_WAREHOUSE);
                        state = STATE.BACKWARD;
                        time.reset();
                    }
                    break;
                case IDLE:
                    Oscar.intake.off();
                    break;
            }
            deposit_fsm.doDepositTopAsync();
            deposit_fsm.doDepositMiddleAsync();
            deposit_fsm.doDepositBottomAsync();
            intake_fsm.doFlipBackAsync();
            Oscar.drive.update();
        }
    }
}
