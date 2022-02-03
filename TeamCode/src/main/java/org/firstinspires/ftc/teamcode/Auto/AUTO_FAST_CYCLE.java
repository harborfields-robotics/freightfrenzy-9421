package org.firstinspires.ftc.teamcode.Auto;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.robot.Hardware;
import org.firstinspires.ftc.teamcode.trajectorysequence.TrajectorySequence;

import dashboard.RobotConstants;

@Config
@Autonomous(group = "advanced")
public class AUTO_FAST_CYCLE extends LinearOpMode {

    enum State {
        CYCLE_0, //Deposit preload, move to warehouse
        CYCLE_1, //Intake first (non-preloaded) stone, move to deposit position, deposit, move to warehouse
        CYCLE_2, //Intake second stone, move to deposit position, deposit, move to warehouse
        CYCLE_3, //Intake third stone, move to deposit position, deposit, move to warehouse
        IDLE     //When finished, stop everything
    }

    State currentState = State.CYCLE_0;

    public static Pose2d startPR = new Pose2d(RobotConstants.STARTX,RobotConstants.STARTY,Math.toRadians(RobotConstants.HEADING));

    Hardware Oscar = new Hardware();
    AUTO_HELPER helper = new AUTO_HELPER(Oscar);

    @Override
    public void runOpMode() throws InterruptedException {

        Oscar.init(hardwareMap);

        TrajectorySequence autoTrajectory0 = Oscar.drive.trajectorySequenceBuilder(startPR)
                .back(65)
                .build();

        TrajectorySequence autoTrajectory1 = Oscar.drive.trajectorySequenceBuilder(autoTrajectory0.end())
                .forward(65)
                .build();

        TrajectorySequence autoTrajectory2 = Oscar.drive.trajectorySequenceBuilder(autoTrajectory1.end())
                .back(65)
                .build();
        TrajectorySequence autoTrajectory3 = Oscar.drive.trajectorySequenceBuilder(autoTrajectory2.end())
                .forward(65)
                .build();

        TrajectorySequence autoTrajectory4 = Oscar.drive.trajectorySequenceBuilder(autoTrajectory3.end())
                .back(65)
                .build();

        ElapsedTime time = new ElapsedTime();

        while(opModeIsActive() && !isStopRequested()) {

            switch (currentState) {

                case CYCLE_0:
                    if(helper.isBusy()) {
                        helper.doDepositTopAsync(time.milliseconds());
                    }
                    //After finishes deposit
                    else if(!helper.isBusy()) {
                        Oscar.intake.forward();
                        Oscar.drive.followTrajectorySequenceAsync(autoTrajectory0);
                    }
                    if(!helper.isBusy() && !Oscar.drive.isBusy()) {
                        currentState = State.CYCLE_1;
                        helper.reset();
                        time.reset();
                    }

                case CYCLE_1:
                    if(time.milliseconds() > 600) {
                        Oscar.intake.reverse();
                        helper.doDepositTopAsync(time.milliseconds());
                    }
                    if(!helper.isDeposited()) {
                        Oscar.drive.followTrajectorySequenceAsync(autoTrajectory1);
                    }
                    else if(helper.isDeposited()) {
                        Oscar.intake.forward();
                        Oscar.drive.followTrajectorySequenceAsync(autoTrajectory2);
                    }
                    if(helper.isDeposited() && !Oscar.drive.isBusy()) {
                        currentState = State.CYCLE_2;
                        helper.reset();
                        time.reset();
                    }

                case CYCLE_2:
                    if(time.milliseconds() > 600) {
                        Oscar.intake.reverse();
                        helper.doDepositTopAsync(time.milliseconds());
                    }
                    if(!helper.isDeposited()) {
                        Oscar.drive.followTrajectorySequenceAsync(autoTrajectory1);
                    }
                    else if(helper.isDeposited()) {
                        Oscar.intake.forward();
                        Oscar.drive.followTrajectorySequenceAsync(autoTrajectory2);
                    }
                    if(helper.isDeposited() && !Oscar.drive.isBusy()) {
                        currentState = State.CYCLE_2;
                        helper.reset();
                        time.reset();
                    }

                case CYCLE_3:
                    if(time.milliseconds() > 600) {
                        Oscar.intake.reverse();
                        helper.doDepositTopAsync(time.milliseconds());
                    }
                    if(!helper.isDeposited()) {
                        Oscar.drive.followTrajectorySequenceAsync(autoTrajectory1);
                    }
                    else if(helper.isDeposited()) {
                        Oscar.intake.forward();
                        Oscar.drive.followTrajectorySequenceAsync(autoTrajectory2);
                    }
                    if(helper.isDeposited() && !Oscar.drive.isBusy()) {
                        currentState = State.CYCLE_2;
                        helper.reset();
                        time.reset();
                    }

                case IDLE:
                    break;
            }
        }
        Oscar.drive.update();
    }
}
