package org.firstinspires.ftc.teamcode.Auto;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.trajectory.Trajectory;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.robot.Hardware;

import dashboard.RobotConstants;

@Config
@Autonomous(group = "drive")
public class AutoRed extends LinearOpMode {
    private FtcDashboard dashboard;
    public static Pose2d startPR = new Pose2d(-60,-48,Math.toRadians(180));
    public static Pose2d deliverPos = new Pose2d(RobotConstants.DELIVERPOSX,RobotConstants.DELIVERPOSY,RobotConstants.DELIVERPOSANG);
    Hardware Oscar = new Hardware();

    @Override
    public void runOpMode() throws InterruptedException {

        Pose2d startPose = new Pose2d(RobotConstants.STARTPOSEX, RobotConstants.STARTPOSEY, RobotConstants.STARTPOSEANG);
        Oscar.init(hardwareMap);
        Trajectory straitPark = Oscar.drive.trajectoryBuilder(new Pose2d())
                .back(17)
                .build();

        Trajectory hitWall = Oscar.drive.trajectoryBuilder(new Pose2d())
                .strafeLeft(1)
                .build();

        Trajectory finishPark = Oscar.drive.trajectoryBuilder(new Pose2d())
                .back(5)
                .build();

        Trajectory carousell2 = Oscar.drive.trajectoryBuilder(new Pose2d())
                .forward(9.35)
                .build();

        Trajectory carousell1 = Oscar.drive.trajectoryBuilder(new Pose2d())
                .strafeRight(1.5)
                .build();


        waitForStart();

        // When auto starts
        Oscar.elbow.goToGrabPos();
        Thread.sleep(1000);

        Oscar.grabber.grab();
        Thread.sleep(200);

        Oscar.elbow.moveStart();

        Thread.sleep(500);
        Oscar.slides.slidesTop();

        Thread.sleep(1500);
        Oscar.elbow.moveTop();

        Oscar.grabber.goTop();

        Oscar.grabber.grabberGrabExtra();

        Thread.sleep(1200);
        Oscar.grabber.grab();

        Thread.sleep(500);


        Oscar.elbow.moveStart();
        Thread.sleep(500);
        Oscar.grabber.goStart();
        Oscar.grabber.grabberGrabExtra();

        Oscar.slides.slidesHome();

        Oscar.drive.followTrajectory(carousell1);

        Oscar.grabber.carousellOn();


        Oscar.drive.followTrajectory(carousell2);
        Thread.sleep(5000);


        Oscar.drive.followTrajectory(straitPark);

        Oscar.drive.followTrajectory(hitWall);

        Oscar.drive.followTrajectory(finishPark);









    }
}
