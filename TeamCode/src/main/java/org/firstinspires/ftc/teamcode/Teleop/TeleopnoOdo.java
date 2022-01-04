package org.firstinspires.ftc.teamcode.Teleop;

import org.firstinspires.ftc.teamcode.robot.Hardware;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import java.util.Arrays;

@TeleOp(name ="Simple Teleop No ODO")
public class TeleopnoOdo extends OpMode {

    // makes and instance of the Hardware class
    Hardware robot = new Hardware();

    public void init(){robot.init(hardwareMap);}

    public void init_loop(){}

    public void start(){}

    public void loop(){
        /** Controls
         * GamePad 1
         * Left Joystick: moving around
         * Right Joystick: turning in place
         * Right Trigger: intake
         */
        //inputs from joysticks
        double y = -gamepad1.left_stick_y;
        double x = gamepad1.left_stick_x;
        double turn = gamepad1.right_stick_x;

        //total joystick displacement
        double r = Math.hypot(x,y);


        // y and x represent components of the vectors of the mecanum wheels
        double FrontLeftVal = r * (y + x) + turn;
        double FrontRightVal = r * (y - x) - turn;
        double BackLeftVal = r * (y - x) + turn;
        double BackRightVal = r * (y + x) - turn;


        // if wheel power is greater than 1, divides each wheel power by highest
        double[] wheelPowers = {FrontLeftVal, FrontRightVal, BackLeftVal, BackRightVal};
        Arrays.sort(wheelPowers);
        if (wheelPowers[3] > 1){
            FrontLeftVal /= wheelPowers[3];
            FrontRightVal /= wheelPowers[3];
            BackLeftVal /= wheelPowers[3];
            BackRightVal /= wheelPowers[3];
        }
        if(gamepad1.left_bumper) {
            robot.dt.setMotorPower(FrontLeftVal / 4, FrontRightVal / 4, BackLeftVal / 4, BackRightVal / 4);
        }
        else robot.dt.setMotorPower(FrontLeftVal * 1 , FrontRightVal * 1 , BackLeftVal * 1, BackRightVal * 1);
        //intake controls
        //may change them to gamepad2 when we use 2 gamepads

        if(gamepad1.right_trigger > 0.5) robot.intake.on();
        else if(gamepad1.left_trigger > 0.5) robot.intake.reverse();
        else robot.intake.off();




    }





}
