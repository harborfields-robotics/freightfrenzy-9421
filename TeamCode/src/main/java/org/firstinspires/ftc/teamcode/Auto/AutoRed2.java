package org.firstinspires.ftc.teamcode.Auto;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.trajectory.Trajectory;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;


import org.firstinspires.ftc.teamcode.robot.Hardware;
import org.firstinspires.ftc.teamcode.trajectorysequence.TrajectorySequence;

import dashboard.RobotConstants;

    @Config
    @Autonomous(group = "drive")
    public class AutoRed2 extends LinearOpMode {
        private FtcDashboard dashboard;
        public static Pose2d startPR = new Pose2d(RobotConstants.STARTX,RobotConstants.STARTY,Math.toRadians(RobotConstants.HEADING));
        public static Pose2d deliverPos = new Pose2d(RobotConstants.DELIVERPOSX,RobotConstants.DELIVERPOSY,RobotConstants.DELIVERPOSANG);
        Hardware Oscar = new Hardware();

        @Override
        public void runOpMode() throws InterruptedException {


            Pose2d startPose = new Pose2d(RobotConstants.STARTPOSEX, RobotConstants.STARTPOSEY, RobotConstants.STARTPOSEANG);
            Oscar.init(hardwareMap);

            Oscar.drive.setPoseEstimate(startPR);


            TrajectorySequence autoTrajectory1 = Oscar.drive.trajectorySequenceBuilder(startPR)
                    .strafeRight(5)
                    .forward(41)
                    .build();
            TrajectorySequence autoTrajectory2 = Oscar.drive.trajectorySequenceBuilder(autoTrajectory1.end())
                    .back(70)
                    .strafeLeft(2)
                    .back(20)
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

            Oscar.drive.followTrajectorySequence(autoTrajectory1);

            Oscar.grabber.carousellOn();

            Thread.sleep(5000);


            Oscar.drive.followTrajectorySequence(autoTrajectory2);











        }
    }

