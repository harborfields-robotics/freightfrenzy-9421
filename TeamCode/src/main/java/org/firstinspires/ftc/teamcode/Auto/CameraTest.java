package org.firstinspires.ftc.teamcode.Auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.robot.CV.BarcodePositionDetector;
import org.firstinspires.ftc.teamcode.robot.Hardware;

@Autonomous(group = "drive")

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


        while (!isStopRequested() && !opModeIsActive()) {
            telemetry.addData("Barcode position", Oscar.cvUtil.getBarcodePosition());
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
            barcodePosition = BarcodePositionDetector.BarcodePosition.LEFT;
        }
        if(counterMid > counterTop && counterMid > counterBottom){
            barcodePosition = BarcodePositionDetector.BarcodePosition.MIDDLE;

        }
        if (counterTop > counterBottom && counterTop > counterMid){
            barcodePosition = BarcodePositionDetector.BarcodePosition.RIGHT;
        }

        telemetry.addData("WHAT IS DETECTED", barcodePosition);


    }
}
