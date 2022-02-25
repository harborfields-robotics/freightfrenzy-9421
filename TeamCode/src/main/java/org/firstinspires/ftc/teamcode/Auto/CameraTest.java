package org.firstinspires.ftc.teamcode.Auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.robot.CV.BarcodePositionDetector;
import org.firstinspires.ftc.teamcode.robot.Hardware;

@Autonomous(group = "drive")
@Disabled

public class CameraTest extends LinearOpMode {

    Hardware Oscar;

    @Override
    public void runOpMode() throws InterruptedException {

        Oscar = new Hardware(hardwareMap, telemetry);
        int counterTop = 0;
        int counterBottom = 0;
        int counterMid = 0;
        Oscar.cvUtil.init();

        BarcodePositionDetector.BarcodePosition barcodePosition = Oscar.cvUtil.getBarcodePosition();
        String position = "";

        while (!isStopRequested() && !opModeIsActive()) {
            barcodePosition = Oscar.cvUtil.getBarcodePosition();
            telemetry.addData("Barcode position", barcodePosition);
            if(barcodePosition == BarcodePositionDetector.BarcodePosition.LEFT){
                counterBottom++;
            }
            else if( barcodePosition == BarcodePositionDetector.BarcodePosition.MIDDLE){
                counterMid++;
            }
            else if( barcodePosition == BarcodePositionDetector.BarcodePosition.RIGHT){
                counterTop++;
            }

            telemetry.update();
        }

        waitForStart();
        if (counterBottom > counterMid && counterBottom > counterTop){
            position = "LEFT";
        }
        if(counterMid > counterTop && counterMid > counterBottom){
            position = "MID";

        }
        if (counterTop > counterBottom && counterTop > counterMid){
            position = "RIGHT";
        }
        while(opModeIsActive() && !isStopRequested()) {
            telemetry.addData("WHAT IS DETECTED", position);
            telemetry.addData("TOP COUNT", counterTop);
            telemetry.addData("MID COUNT", counterMid);
            telemetry.addData("BOT COUNT", counterBottom);
            telemetry.update();
        }
        //test


    }
}
