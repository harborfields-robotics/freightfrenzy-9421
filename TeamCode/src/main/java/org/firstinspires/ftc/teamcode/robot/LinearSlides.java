package org.firstinspires.ftc.teamcode.robot;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import dashboard.RobotConstants;

public class LinearSlides {

    public DcMotor slideMotor;



    //position when the slides start
    public static double ORIGINAL_POSITION = 0;
    //power motor uses for slides
    public static double SLIDE_POWER = 1;
    //Max length
    public static double MAX_LENGTH = RobotConstants.SLIDE_MAX_LENGTH;
    //Must tune to get more efficient
    public static double[]positions = {0, RobotConstants.TOP_SLIDE_LENGTH, RobotConstants.MID_SLIDE_LENGTH, RobotConstants.BOTTOM_SLIDE_LENGTH};

    public int currentPosition = 0;

    public LinearSlides(HardwareMap ahwMap){
        slideMotor = ahwMap.get(DcMotor.class, "slideMotor");
        slideMotor.setDirection(DcMotor.Direction.FORWARD);
        slideMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        slideMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);



    }


}
