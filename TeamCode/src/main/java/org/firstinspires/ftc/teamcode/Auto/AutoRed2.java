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
    @Disabled
    @Autonomous(group = "drive")
    public class AutoRed2 extends LinearOpMode {
        private FtcDashboard dashboard;
        public static Pose2d startPR = new Pose2d(-39.1, -64, Math.toRadians(180));
        public static Pose2d DuckRed = new Pose2d(-57.2,-53.7, Math.toRadians(130));
        Hardware Oscar = new Hardware(null, null);

        @Override
        public void runOpMode() throws InterruptedException {



            Oscar.init(hardwareMap);

            Oscar.drive.setPoseEstimate(startPR);


            TrajectorySequence autoTrajectory1 = Oscar.drive.trajectorySequenceBuilder(startPR)
                    .forward(5)
                    .splineToLinearHeading(DuckRed, 140)
                    .build();
            //TrajectorySequence autoTrajectory2 = Oscar.drive.trajectorySequenceBuilder(autoTrajectory1.end())
             //       .back(70)
               //     .strafeLeft(2)
               //     .back(20)
                //    .build();

            waitForStart();

            Oscar.drive.followTrajectorySequence(autoTrajectory1);

            Oscar.grabber.carousellOn();


        }
    }

