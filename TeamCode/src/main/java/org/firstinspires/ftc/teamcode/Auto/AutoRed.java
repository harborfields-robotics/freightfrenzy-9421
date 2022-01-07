package org.firstinspires.ftc.teamcode.Auto;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.robot.Robot;

import org.firstinspires.ftc.teamcode.R;
import org.firstinspires.ftc.teamcode.robot.Hardware;
import org.firstinspires.ftc.teamcode.robot.SampleMecanumDrive;

import dashboard.RobotConstants;

@Config

public class AutoRed extends LinearOpMode {
    private FtcDashboard dashboard;
    public static Pose2d startPR = new Pose2d(-60,-48,Math.toRadians(180));
    public static Pose2d deliverPos = new Pose2d(RobotConstants.DELIVERPOSX,RobotConstants.DELIVERPOSY,RobotConstants.DELIVERPOSANG);

    @Override
    public void runOpMode() throws InterruptedException {
        Hardware Oscar = new Hardware(hardwareMap);

        Pose2d startPose = new Pose2d(RobotConstants.STARTPOSEX, RobotConstants.STARTPOSEY, RobotConstants.STARTPOSEANG);

    }
}
