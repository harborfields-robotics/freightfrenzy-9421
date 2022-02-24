package org.firstinspires.ftc.teamcode.Auto;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.acmerobotics.roadrunner.trajectory.Trajectory;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.robot.Robot;

import org.firstinspires.ftc.teamcode.robot.Hardware;

import dashboard.RobotConstants;

@Config
@Disabled
@Autonomous(group = "drive")
public class AutoRed extends LinearOpMode {
    private FtcDashboard dashboard;
    public static Pose2d startPR = new Pose2d(RobotConstants.STARTX,RobotConstants.STARTY,Math.toRadians(RobotConstants.HEADING));
    public static Pose2d deliverPos = new Pose2d(RobotConstants.DELIVERPOSX,RobotConstants.DELIVERPOSY,RobotConstants.DELIVERPOSANG);
    public static Pose2d splineCV = new Pose2d(4.1,-55.2,Math.toRadians(210));
    public static Pose2d splineTest = new Pose2d(4.1,-55.2, Math.toRadians(210));
    public static Pose2d RevertTest = new Pose2d(4.1,-63.2, Math.toRadians(180));
    public static Vector2d vectorTest = new Vector2d(9.5,-55.2);
    public static Vector2d returnVector = new Vector2d();
    Hardware Oscar;

    @Override
    public void runOpMode() throws InterruptedException {

        Pose2d startPose = new Pose2d(6.4, -64, Math.toRadians(180));
        Oscar = new Hardware(hardwareMap, telemetry);
        Trajectory startCV = Oscar.drive.trajectoryBuilder(startPose)
                .lineToLinearHeading(splineCV)
                .build();

        Trajectory revertCV = Oscar.drive.trajectoryBuilder(startCV.end())
                .lineToLinearHeading(RevertTest)
                .build();

        Trajectory inWarehouse = Oscar.drive.trajectoryBuilder(revertCV.end())
                .back(30)
                .build();

        Trajectory outWarehouse = Oscar.drive.trajectoryBuilder(inWarehouse.end())
                .forward(30)

                .build();

        Trajectory deposit = Oscar.drive.trajectoryBuilder(outWarehouse.end())
                .lineToLinearHeading(splineTest)
                .build();

        Trajectory revert = Oscar.drive.trajectoryBuilder(deposit.end())
                .lineToLinearHeading(RevertTest)
                .build();




        waitForStart();
        Oscar.drive.setPoseEstimate(startPose);

        Oscar.drive.followTrajectory(startCV);

        Oscar.drive.followTrajectory(revertCV);

        Oscar.drive.followTrajectory(inWarehouse);
        Oscar.drive.followTrajectory(outWarehouse);
        Oscar.drive.followTrajectory(deposit);
        Oscar.drive.followTrajectory(revert);

        //

        Oscar.drive.followTrajectory(inWarehouse);
        Oscar.drive.followTrajectory(outWarehouse);
        Oscar.drive.followTrajectory(deposit);
        Oscar.drive.followTrajectory(revert);

        Oscar.drive.followTrajectory(inWarehouse);
        Oscar.drive.followTrajectory(outWarehouse);
        Oscar.drive.followTrajectory(deposit);
        Oscar.drive.followTrajectory(revert);

        Oscar.drive.followTrajectory(inWarehouse);
        Oscar.drive.followTrajectory(outWarehouse);
        Oscar.drive.followTrajectory(deposit);
        Oscar.drive.followTrajectory(revert);

        Oscar.drive.followTrajectory(inWarehouse);
        Oscar.drive.followTrajectory(outWarehouse);
        Oscar.drive.followTrajectory(deposit);
        Oscar.drive.followTrajectory(revert);


        Oscar.drive.followTrajectory(inWarehouse);
        Oscar.drive.followTrajectory(outWarehouse);
        Oscar.drive.followTrajectory(deposit);
        Oscar.drive.followTrajectory(revert);
    }
}
