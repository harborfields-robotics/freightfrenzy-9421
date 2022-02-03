package org.firstinspires.ftc.teamcode.Auto;

import org.firstinspires.ftc.teamcode.robot.Hardware;

public class AUTO_HELPER {
    private Hardware Oscar;
    private boolean busy = true;
    private boolean deposited = false;
    public AUTO_HELPER(Hardware hardware) {
        this.Oscar = hardware;
    }

    public boolean isBusy() {return busy;}
    public boolean isDeposited() {return deposited;}
    public void reset() {busy = true; deposited = false;}

    //When you use this, reset a timer before depositing and repeatedly call this method with updated times
    //Returns true if cycle is finished
    public void doDepositTopAsync(double milliseconds) {
        //Assumes starting position is grab position
        double interval0 = 200;
        double interval1 = 100;
        //Used for intervals where motor position is used instead of timers
        double correctedInterval2 = 0;
        double interval3 = 250;
        double interval4 = 250;
        double interval5 = 600;
        double correctedInterval6 = 0;
        double interval7 = 130;
        //INTERVAL 0
        if(milliseconds < interval0) {
            Oscar.grabber.closeGrab();
            Oscar.grabber.goStart();
            Oscar.slides.slidesHomeAsync();
            busy = true;
            deposited = false;
        }
        //INTERVAL 1
        else if(milliseconds < interval0 + interval1) {
            Oscar.elbow.moveStart();
            busy = true;
            deposited = false;
        }
        //INTERVAL 2 ***USES POSITION, NOT TIME***
        else if(Oscar.slides.getMotorPosition() < Oscar.slides.TOP_SLIDE_TICKS - 600) {
            Oscar.slides.slidesTop();
            correctedInterval2 = milliseconds - interval0 - interval1;
            busy = true;
            deposited = false;
        }
        //INTERVAL 3
        else if(milliseconds < interval0 + interval1 + correctedInterval2 + interval3) {
            Oscar.slides.slidesTop();
            Oscar.elbow.moveTop();
            Oscar.grabber.goTop();
            busy = true;
            deposited = false;
        }
        //INTERVAL 4
        else if(milliseconds < interval0 + interval1 + correctedInterval2 + interval3 + interval4) {
            Oscar.slides.slidesTop();
            Oscar.grabber.openGrab();
            Oscar.grabber.openGrabExtra();
            busy = true;
            deposited = false;
        }
        //INTERVAL 5
        else if(milliseconds < interval0 + interval1 + correctedInterval2 + interval3 + interval4 + interval5) {
            Oscar.slides.slidesGrab();
            Oscar.elbow.moveStart();
            Oscar.grabber.closeGrabExtra();
            Oscar.grabber.closeGrab();
            busy = true;
            deposited = true;
        }
        //INTERVAL 6
        else if(Oscar.slides.getMotorPosition() < 420) {
            Oscar.slides.slidesGrab();
            Oscar.grabber.goStart();
            Oscar.elbow.goToGrabPos();
            correctedInterval6 = milliseconds - interval0 - interval1 - correctedInterval2 - interval3 - interval4 - interval5;
            busy = true;
            deposited = true;
        }
        //INTERVAL 7
        else if(milliseconds < interval0 + interval1 + correctedInterval2 + interval3 + interval4 + interval5 + correctedInterval6 + interval7) {
            Oscar.grabber.openGrab();
            busy = true;
            deposited = true;
        }
        //CYCLE FINISHED
        else {busy = false; deposited = true;}
    }
}
