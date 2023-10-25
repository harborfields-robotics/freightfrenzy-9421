/*package org.firstinspires.ftc.teamcode.Auto;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.robot.Hardware;
import org.firstinspires.ftc.teamcode.trajectorysequence.TrajectorySequence;

@Autonomous
// public class RED_DUCK_STORAGE extends LinearOpMode {

    private FtcDashboard dashboard;
    public static Pose2d startPR = new Pose2d(-33,-59,Math.toRadians(0));
    public static Pose2d DuckRed = new Pose2d(-57.2, -53.7, Math.toRadians(130));
    public static Pose2d StoragePark = new Pose2d(-58.7, -34.7, Math.toRadians((90)));
    public static Pose2d MoveRight1 = new Pose2d(-55.0, -53.7, Math.toRadians(130));
    public static Pose2d MoveLeft1 = new Pose2d(-47, -52, Math.toRadians(-45));
    public static Pose2d MoveDownLeft1 = new Pose2d(-59, -59,Math.toRadians(-45));
    public static Pose2d Shimmy1 = new Pose2d(-56, -60,Math.toRadians(-85));
    public static Pose2d Shimmy2 = new Pose2d(-62, -61,Math.toRadians(-95));

    /*
    @Override
    public void runOpMode() throws InterruptedException {
        Hardware Oscar = new Hardware(hardwareMap, telemetry);

        Oscar.init(hardwareMap);

        Oscar.drive.setPoseEstimate(startPR);


        TrajectorySequence autoTrajectory1 = Oscar.drive.trajectorySequenceBuilder(startPR)
                .forward(5)
                .splineToLinearHeading(DuckRed, 140)
                .build();

        TrajectorySequence autoTrajectory2 = Oscar.drive.trajectorySequenceBuilder(startPR)
                .splineToLinearHeading(MoveRight1, 140)
                .splineToLinearHeading(StoragePark, 90)
                .build();

        TrajectorySequence autoToDeposit = Oscar.drive.trajectorySequenceBuilder(startPR)
                .lineToLinearHeading(MoveLeft1)
                .waitSeconds(3)
                .build();
        TrajectorySequence depositToDuck = Oscar.drive.trajectorySequenceBuilder((autoToDeposit.end()))
                .lineToLinearHeading(MoveDownLeft1)
                .turn(Math.toRadians(-45))
                .build();

        TrajectorySequence duckShimmy = Oscar.drive.trajectorySequenceBuilder()
                .lineToLinearHeading(Shimmy1)
                .lineToLinearHeading(Shimmy2)
                        .build();

        //TrajectorySequence autoTrajectory2 = Oscar.drive.trajectorySequenceBuilder(autoTrajectory1.end())
        //       .back(70)
        //     .strafeLeft(2)
        //     .back(20)
        //    .build();

        waitForStart();

        Oscar.drive.followTrajectorySequence(autoTrajectory1);


        Oscar.grabber.carousellOn();

        Thread.sleep(5000);

        Oscar.drive.followTrajectorySequence(autoTrajectory2);
    }
    */ /*
}
*/