package org.firstinspires.ftc.teamcode.Auto;

import org.firstinspires.ftc.teamcode.robot.Hardware;

public class AUTO_HELPER {
    private Hardware Oscar;
    private boolean busy = true;
    private boolean deposited = false;
    public AUTO_HELPER(Hardware hardware) {
        this.Oscar = hardware;
    }

    //Assumes starting position is grab position
    private double interval0 = 200; //Grab stone and move elbow horizontal, home slides
    //Interval5 is unreasonably high because next interval relies on position, so timer should never reach this value.
    private double interval1 = 3000; //Move elbow horizontal, start extend slides out
    //Used for intervals where motor position is used instead of timers, combines both interval1 and interval2
    private double correctedInterval2 = 0; //Extend slides until ready to rotate elbow up
    private double interval3 = 250; //Finishes extending slides out, rotates elbow up, rotates basket to deposit position
    private double interval4 = 250; //Deposits stone by opening basket
    //Interval5 is unreasonably high because next interval relies on position, so timer should never reach this value.
    private double interval5 = 3000; //Rotates elbow back to horizontal, closes grabber, retracts slides until ready to rotate elbow down to grab position
    private double correctedInterval6 = 0; //Rotate elbow down to grab position, combines both interval5 and interval6
    private double interval7 = 130; //Open basket

    public boolean isBusy() {return busy;}
    public boolean isDeposited() {return deposited;}
    public void reset() {
        busy = true;
        deposited = false;
        interval1 = 3000;
        interval5 = 3000;
    }

    //When you use this, reset a timer before depositing and repeatedly call this method with updated times
    //Returns true if cycle is finished
    public void doDepositTopAsync(double milliseconds) {

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
            //INTERVAL 2 ***USES POSITION, NOT TIME***
            if(Oscar.slides.getMotorPosition() > Oscar.slides.TOP_SLIDE_TICKS - 600) {
                Oscar.slides.slidesTop();
                correctedInterval2 = milliseconds;
                interval1 = 0; //So next call of doDepositTopAsync does not run INTERVAL 1
            }
            else {
                Oscar.slides.slidesTop();
                Oscar.elbow.moveStart();
            }
            busy = true;
            deposited = false;
        }
        //INTERVAL 3
        else if(milliseconds < correctedInterval2 + interval3) {
            Oscar.slides.slidesTop();
            Oscar.elbow.moveTop();
            Oscar.grabber.goTop();
            busy = true;
            deposited = false;
        }
        //INTERVAL 4
        else if(milliseconds < correctedInterval2 + interval3 + interval4) {
            Oscar.slides.slidesTop();
            Oscar.grabber.openGrab();
            Oscar.grabber.openGrabExtra();
            busy = true;
            deposited = false;
        }
        //INTERVAL 5
        else if(milliseconds < correctedInterval2 + interval3 + interval4 + interval5) {
            //INTERVAL 6 ***USES POSITION, NOT TIME***
            if(Oscar.slides.getMotorPosition() < 420) {
                Oscar.slides.slidesGrab();
                Oscar.grabber.goStart();
                Oscar.elbow.goToGrabPos();
                correctedInterval6 = milliseconds;
                interval5 = 0; //So next call of doDepositTopAsync does not run this INTERVAL 5
            }
            else {
                Oscar.slides.slidesGrab();
                Oscar.elbow.moveStart();
                Oscar.grabber.closeGrabExtra();
                Oscar.grabber.closeGrab();
                //Too lazy to add another interval, this is so the grabber is facing down when elbow rotates down to grab position in INTERVAL 6
                if(milliseconds > correctedInterval2 + interval3 + interval4 + 300) {
                    Oscar.grabber.goStart();
                }
            }
            busy = true;
            deposited = true;
        }
        //INTERVAL 7
        else if(milliseconds < correctedInterval6) {
            Oscar.grabber.openGrab();
            busy = true;
            deposited = true;
        }
        //CYCLE FINISHED
        else {busy = false; deposited = true;}
    }
}
