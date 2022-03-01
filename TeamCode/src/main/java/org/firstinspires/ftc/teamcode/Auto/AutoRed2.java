package org.firstinspires.ftc.teamcode.Auto;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.trajectory.Trajectory;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;


import org.firstinspires.ftc.teamcode.robot.Hardware;
import org.firstinspires.ftc.teamcode.trajectorysequence.TrajectorySequence;

import dashboard.RobotConstants;

    @Config
//    @Disabled
    @Autonomous(group = "drive")
    public class AutoRed2 extends LinearOpMode {
        private FtcDashboard dashboard;
        public static Pose2d startPR = new Pose2d(-39.1, -64, Math.toRadians(180));
        public static Pose2d DuckRed = new Pose2d(-60.3,-55.5, Math.toRadians(105));
        public static Pose2d returnToPos = new Pose2d(-39.1, -69, Math.toRadians(180));
        public static Pose2d driveToWarehouse = new Pose2d(44.8,-69,Math.toRadians(180));
        //Hardware Oscar = new Hardware(hardwareMap, telemetry);
        //test 2
        @Override
        public void runOpMode() throws InterruptedException {
            Hardware Oscar = new Hardware(hardwareMap, telemetry);



            Oscar.init(hardwareMap);

            Oscar.drive.setPoseEstimate(startPR);


            TrajectorySequence autoTrajectory1 = Oscar.drive.trajectorySequenceBuilder(startPR)
                    .forward(5)
                    .splineToLinearHeading(DuckRed, 140)
                    .build();
            TrajectorySequence autoTrajectory2 = Oscar.drive.trajectorySequenceBuilder(autoTrajectory1.end())
                    .lineToLinearHeading(returnToPos)
                    .build();


            TrajectorySequence rerunToWarehouse = Oscar.drive.trajectorySequenceBuilder(autoTrajectory2.end())
                    .lineToLinearHeading(driveToWarehouse)
                    .build();

            waitForStart();

            Oscar.drive.followTrajectorySequence(autoTrajectory1);

            Oscar.grabber.carousellOn();

            Thread.sleep(5000);

            Oscar.drive.followTrajectorySequence(autoTrajectory2);
            Oscar.drive.followTrajectorySequence(rerunToWarehouse);


        }
    }

