package org.firstinspires.ftc.teamcode.Auto;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.trajectory.Trajectory;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DistanceSensor;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.drive.DriveConstants;
import org.firstinspires.ftc.teamcode.drive.SampleMecanumDrive;
import org.firstinspires.ftc.teamcode.robot.DEPOSIT_LINEAR;
import org.firstinspires.ftc.teamcode.robot.Hardware;
import org.firstinspires.ftc.teamcode.robot.INTAKE_FSM;

@Config
@Autonomous(group = "advanced")
public class LINEAR_AUTO extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        Hardware Oscar = new Hardware(hardwareMap, telemetry);
        DEPOSIT_LINEAR deposit_linear = new DEPOSIT_LINEAR(Oscar, telemetry, gamepad1, gamepad2);
        INTAKE_FSM intake_fsm = new INTAKE_FSM(Oscar, telemetry, gamepad1, gamepad2);

        Pose2d startPose = new Pose2d(19, -64, Math.toRadians(180));
        Pose2d depositPose = new Pose2d(-9.3, -69, Math.toRadians(180));
        Pose2d warehousePose = new Pose2d(47, -69, Math.toRadians(190));

        Oscar.drive.setPoseEstimate(startPose);

        Trajectory START_TO_DEPOSIT = Oscar.drive.trajectoryBuilder(startPose)
                .lineToLinearHeading(depositPose)
                .build();
        Trajectory DEPOSIT_TO_WAREHOUSE = Oscar.drive.trajectoryBuilder(START_TO_DEPOSIT.end())
                .lineToLinearHeading(warehousePose)
                .build();
        Trajectory WAREHOUSE_TO_DEPOSIT = Oscar.drive.trajectoryBuilder(DEPOSIT_TO_WAREHOUSE.end())
                .lineToLinearHeading(depositPose)
                .build();

        waitForStart();

        Oscar.drive.followTrajectory(START_TO_DEPOSIT);
        deposit_linear.DROP_THING_ON_TOP();
        Oscar.intake.forward();
        Oscar.drive.followTrajectory(DEPOSIT_TO_WAREHOUSE);
//        while (!Oscar.drive.isBusy()) {
//            Oscar.drive.update();
//            intake_fsm.doFlipBackAsync();
//            intake_fsm.doFlipFrontAsync();
////            if (((DistanceSensor) Oscar.colorBack).getDistance(DistanceUnit.CM) < 2) {
////                intake_fsm.SET_EXEC_BACK_FLIP(true);
////                break;
////            }
////            if (((DistanceSensor) Oscar.colorFront).getDistance(DistanceUnit.CM) < 2) {
////                intake_fsm.SET_EXEC_FRONT_FLIP(true);
////                break;
////            }
//        }
        Oscar.intake.reverse();
        Oscar.drive.followTrajectory(WAREHOUSE_TO_DEPOSIT);
        deposit_linear.DROP_THING_ON_TOP();
    }
}
