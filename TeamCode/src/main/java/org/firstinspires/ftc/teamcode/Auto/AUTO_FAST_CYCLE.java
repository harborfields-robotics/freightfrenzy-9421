package org.firstinspires.ftc.teamcode.Auto;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.trajectory.Trajectory;
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
        CYCLE_1, //Intake first (non-preloaded) stone(MINERAL), move to deposit position, deposit, move to warehouse
        CYCLE_2, //Intake second stone(MINERAL), move to deposit position, deposit, move to warehouse
        CYCLE_3, //Intake third stone(MINERAL), move to deposit position, deposit, move to warehouse
        IDLE     //When finished, stop everything
    }

    private State currentState = State.CYCLE_0;

    public static Pose2d startPR = new Pose2d(RobotConstants.STARTX,RobotConstants.STARTY,Math.toRadians(RobotConstants.HEADING));

    Hardware Oscar = new Hardware();
    AUTO_HELPER_FSM helper = new AUTO_HELPER_FSM(Oscar, telemetry);

    @Override
    public void runOpMode() throws InterruptedException {

        Oscar.init(hardwareMap);

        Trajectory autoTrajectory0 = Oscar.drive.trajectoryBuilder(startPR)
                .back(65)
                .build();

        Trajectory autoTrajectory1 = Oscar.drive.trajectoryBuilder(autoTrajectory0.end())
                .forward(65)
                .build();

        Trajectory autoTrajectory2 = Oscar.drive.trajectoryBuilder(autoTrajectory1.end())
                .back(65)
                .build();
        Trajectory autoTrajectory3 = Oscar.drive.trajectoryBuilder(autoTrajectory2.end())
                .forward(65)
                .build();

        Trajectory autoTrajectory4 = Oscar.drive.trajectoryBuilder(autoTrajectory3.end())
                .back(65)
                .build();

        waitForStart();

        if (isStopRequested()) return;

        ElapsedTime time = new ElapsedTime();

        while(opModeIsActive() && !isStopRequested()) {

            switch (currentState) {

                case CYCLE_0:
                    telemetry.addData("CYCLE 0", currentState);
                    if(helper.isBusy()) {
                        helper.doDepositTopAsync(time.milliseconds());
                    }
                    //After finishes deposit
                    else if(helper.isDeposited()) {
                        Oscar.intake.forward();
                        Oscar.drive.followTrajectoryAsync(autoTrajectory0);
                    }
                    if(!helper.isBusy() && !Oscar.drive.isBusy()) {
                        currentState = State.CYCLE_1;
                        helper.reset();
                        time.reset();
                    }
                    telemetry.update();
                    break;
                case CYCLE_1:
                    telemetry.addData("CYCLE 1", currentState);
                    if(time.milliseconds() > 600) {
                        Oscar.intake.reverse();
                        helper.doDepositTopAsync(time.milliseconds());
                    }
                    if(!helper.isDeposited()) {
                        Oscar.drive.followTrajectoryAsync(autoTrajectory1);
                    }
                    else if(helper.isDeposited()) {
                        Oscar.intake.forward();
                        Oscar.drive.followTrajectoryAsync(autoTrajectory2);
                    }
                    if(helper.isDeposited() && !Oscar.drive.isBusy()) {
                        currentState = State.CYCLE_2;
                        helper.reset();
                        time.reset();
                    }
                    break;
                case CYCLE_2:
                    telemetry.addLine("CYCLE 2");
                    if(time.milliseconds() > 600) {
                        Oscar.intake.reverse();
                        helper.doDepositTopAsync(time.milliseconds());
                    }
                    if(!helper.isDeposited()) {
                        Oscar.drive.followTrajectoryAsync(autoTrajectory1);
                    }
                    else if(helper.isDeposited()) {
                        Oscar.intake.forward();
                        Oscar.drive.followTrajectoryAsync(autoTrajectory2);
                    }
                    if(helper.isDeposited() && !Oscar.drive.isBusy()) {
                        currentState = State.CYCLE_2;
                        helper.reset();
                        time.reset();
                    }
                    break;
                case CYCLE_3:
                    telemetry.addLine("CYCLE 3");
                    if(time.milliseconds() > 600) {
                        Oscar.intake.reverse();
                        helper.doDepositTopAsync(time.milliseconds());
                    }
                    if(!helper.isDeposited()) {
                        Oscar.drive.followTrajectoryAsync(autoTrajectory1);
                    }
                    else if(helper.isDeposited()) {
                        Oscar.intake.forward();
                        Oscar.drive.followTrajectoryAsync(autoTrajectory2);
                    }
                    if(helper.isDeposited() && !Oscar.drive.isBusy()) {
                        currentState = State.CYCLE_2;
                        helper.reset();
                        time.reset();
                    }
                    break;
                case IDLE:
                    break;
            }
        }
        Oscar.drive.update();
    }
}
