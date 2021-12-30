package org.firstinspires.ftc.teamcode.robot;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.qualcomm.robotcore.hardware.HardwareMap;


public class Hardware {
    HardwareMap hwMap = null;
    public Drivetrain dt = null;
    public Intake intake = null;
    public Grabber grabber = null;
    public Elbow elbow = null;
    private Pose2d vel;

    public SampleMecanumDrive drive;

    public Hardware(){
    }

    //Inits hardware for opmode
    public void init(HardwareMap ahwMap){
        //ahwMap is hwMap
        hwMap = ahwMap;
        dt = new Drivetrain(hwMap);
        drive = new SampleMecanumDrive(hwMap);
        intake = new Intake(hwMap);
        grabber = new Grabber(hwMap);
        elbow = new Elbow(hwMap);
        vel = new Pose2d(0,0,0);

    }
    //too lazy to make a new class( vel method will be here)

    public Pose2d getVel(){return vel;}
    public void setVel(Pose2d v){vel = v;}

}
