package org.firstinspires.ftc.teamcode.robot;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class Intake {
    public DcMotor intakeFront;
    public DcMotor intakeBack;




    public static final double INTAKE_POWER = 1;
    public static final double INTAKE_POWER_SLOW = 0.25;

    public Intake(HardwareMap ahwMap){
        intakeFront = ahwMap.get(DcMotor.class, "IntakeLeft");
        intakeBack = ahwMap.get(DcMotor.class, "IntakeRight");

        intakeFront.setDirection(DcMotor.Direction.FORWARD);
        intakeBack.setDirection(DcMotor.Direction.FORWARD);


    }

    public void on(){
        intakeFront.setPower(INTAKE_POWER);
        intakeBack.setPower(INTAKE_POWER);
    }
    public void reverse(){
        intakeFront.setPower(-INTAKE_POWER_SLOW);
        intakeBack.setPower(-INTAKE_POWER_SLOW);
    }
    public void off(){
        intakeFront.setPower(0);
        intakeBack.setPower(0);
    }
}
