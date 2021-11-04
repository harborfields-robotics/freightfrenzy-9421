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
         *
         */
        //inputs from joysticks
        double y = -gamepad1.left_stick_y;
        double x = gamepad1.left_stick_x;
        double turn = gamepad1.right_stick_x;

        //total joystick displacement
        double q = Math.hypot(x,y);


        // y and z represent components of the vectors of the mecanum wheels
        double FLVal = q * (y + x) + turn;
        double FRVal = q * (y - x) - turn;
        double BLVal = q * (y - x) + turn;
        double BRVal = q * (y + x) - turn;


        // if wheel power is greater than 1, divides each wheel power by highest

        double[] wheelPowers = {FLVal, FRVal, BLVal, BRVal};
        Arrays.sort(wheelPowers);
        if (wheelPowers[3] > 1){
            FLVal /= wheelPowers[3];
            FRVal /= wheelPowers[3];
            BLVal /= wheelPowers[3];
            BRVal /= wheelPowers[3];
        }




    }




}
