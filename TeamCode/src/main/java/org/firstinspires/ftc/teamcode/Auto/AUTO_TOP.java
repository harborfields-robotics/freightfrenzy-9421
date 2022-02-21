package org.firstinspires.ftc.teamcode.Auto;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.trajectory.Trajectory;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.robot.DEPOSIT_FSM;
import org.firstinspires.ftc.teamcode.robot.Hardware;
import org.firstinspires.ftc.teamcode.robot.INTAKE_FSM;
import org.firstinspires.ftc.teamcode.robot.LOGIC;
import org.firstinspires.ftc.teamcode.trajectorysequence.TrajectorySequence;
import org.firstinspires.ftc.teamcode.trajectorysequence.TrajectorySequenceBuilder;
import org.firstinspires.ftc.teamcode.CV.BarcodePositionDetector;

import dashboard.RobotConstants;

@Config
@Autonomous(group = "advanced")
public class AUTO_TOP extends LinearOpMode {

    enum State {
        PRELOAD_CYCLE,
        CYCLE_0, //Deposit preload, move to warehouse
        CYCLE_1, //Intake first (non-preloaded) stone(MINERAL), move to deposit position, deposit, move to warehouse
        CYCLE_2, //Intake second stone(MINERAL), move to deposit position, deposit, move to warehouse
        CYCLE_3 //Intake third stone(MINERAL), move to deposit position, deposit, move to warehouse
    }

    private State currentState = State.PRELOAD_CYCLE;


    public static Pose2d startPR = new Pose2d(RobotConstants.STARTX,RobotConstants.STARTY,Math.toRadians(RobotConstants.HEADING));
    public static Pose2d depositSpline = new Pose2d( 9.5,-55.2, Math.toRadians(210));
    public static Pose2d revertSpline = new  Pose2d(15.6,-63.2, Math.toRadians(180));
    Hardware Oscar = new Hardware(hardwareMap, telemetry);

    DEPOSIT_FSM deposit_fsm = new DEPOSIT_FSM(Oscar, telemetry, gamepad1, gamepad2);
    INTAKE_FSM intake_fsm = new INTAKE_FSM(Oscar, telemetry, gamepad1, gamepad2);

    BarcodePositionDetector detector = new BarcodePositionDetector(telemetry);



    @Override
    public void runOpMode() throws InterruptedException {

        Oscar.init(hardwareMap);

        detector.getBarcodePosition();
        // test
        Trajectory autoTrajectory0 = Oscar.drive.trajectoryBuilder(startPR)
                .back(30)
                .build();

        TrajectorySequence CycleTrajectory = Oscar.drive.trajectorySequenceBuilder(new Pose2d(6, -64, Math.toRadians(180)))
                .forward(40)
                .lineToLinearHeading(depositSpline)
                .lineToLinearHeading(revertSpline)
                .build();
        Trajectory autoTrajectory3 = Oscar.drive.trajectoryBuilder(CycleTrajectory.end())
                .back(35)
                .build();

        TrajectorySequence autoTrajectory4 = Oscar.drive.trajectorySequenceBuilder(autoTrajectory3.end())
                .back(65)
                .build();


        waitForStart();

        if (isStopRequested()) return;

        ElapsedTime time = new ElapsedTime();

        while(opModeIsActive() && !isStopRequested()) {

            deposit_fsm.doDepositTopAsync();
            deposit_fsm.doDepositMiddleAsync();
            deposit_fsm.doDepositBottomAsync();

            if(((DistanceSensor) Oscar.colorBack).getDistance(DistanceUnit.CM) < 2) {
                intake_fsm.SET_EXEC_BACK_FLIP(true);
            }
            if(((DistanceSensor) Oscar.colorFront).getDistance(DistanceUnit.CM) < 2) {
                intake_fsm.SET_EXEC_FRONT_FLIP(true);
            }

            switch (currentState) {
                case PRELOAD_CYCLE:
                    if(PRELOAD_BOTTOM) {
                        deposit_fsm.startDeposittop = true;
                    }
                    else if(PRELOAD_MID) {
                        deposit_fsm.startDepositmid = true;
                    }
                    else {
                        deposit_fsm.startDeposittop = true;
                    }
                    Oscar.drive.followTrajectorySequenceAsync(preloadTrajectory);
                    if(LOGIC.IS_THING_IN_DA_ROBOT) {

                    }
                    break;
                case CYCLE_0:
                    if() {

                    }
            }
        }
        Oscar.drive.update();
    }
}
