package org.firstinspires.ftc.teamcode.Auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.robot.CV.BarcodePositionDetector;
import org.firstinspires.ftc.teamcode.robot.Hardware;

@Autonomous(group = "drive")
public class CameraTest extends LinearOpMode {

    Hardware Oscar;
    @Override
    public void runOpMode() throws InterruptedException {

        Oscar = new Hardware(hardwareMap, telemetry);

        Oscar.cvUtil.init();

        BarcodePositionDetector.BarcodePosition barcodePosition = Oscar.cvUtil.getBarcodePosition();


        while (!isStopRequested() && !opModeIsActive()) {
            Oscar.drive.update();
            telemetry.addData("Barcode position", barcodePosition);
            telemetry.update();
        }

        waitForStart();

        Oscar.cvUtil.stopCamera();

    }
}
