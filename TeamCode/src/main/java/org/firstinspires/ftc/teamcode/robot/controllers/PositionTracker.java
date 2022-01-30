package org.firstinspires.ftc.teamcode.robot.controllers;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.qualcomm.robotcore.util.ElapsedTime;

import java.util.ArrayList;
import java.util.Arrays;

//CLASS ONLY WORKS FOR DPAD VALUES!! DON'T USE WITH JOYSTICKS!!!
public class PositionTracker {

    //These values are constant acceleration values
    //inches per second
    private final double X_MAX_ACCEL = 20;
    private final double Y_MAX_ACCEL = 20;
    //radians per second
    private final double ANG_MAX_ACCEL = Math.toRadians(180);

    private final double X_MAX_SPEED = 50;
    private final double Y_MAX_SPEED = 50;
    private final double ANG_MAX_SPEED = ANG_MAX_ACCEL * 2;

    private double X_ACCEL = 0;
    private double Y_ACCEL = 0;
    private double ANG_ACCEL = 0;

    private double X_VELOCITY = 0;
    private double Y_VELOCITY = 0;
    private double ANG_VELOCITY = 0;

    //if velocity within threshold, then acceleration = 0, and velocity = 0
    private final double X_VELOCITY_THRESHOLD = 1;
    private final double Y_VELOCITY_THRESHOLD = 1;
    private final double ANG_VELOCITY_THRESHOLD = .1;

    private boolean X_POSITIVE_ANALOG;
    private boolean Y_POSITIVE_ANALOG;
    private boolean ANG_POSITIVE_ANALOG;
    private boolean X_NEGATIVE_ANALOG;
    private boolean Y_NEGATIVE_ANALOG;
    private boolean ANG_NEGATIVE_ANALOG;

    //used to do numerical integration
    private double DELTA_TIME = 0;

    private Pose2d POSITION;
    private Pose2d PREVIOUS_POSITION;

    private ElapsedTime INTERNAL_TIMER;

    //assumed that initial velocities and accelerations are 0, therefore only taking in initial position.
    public PositionTracker(Pose2d INITIAL_POSITION) {
        POSITION = INITIAL_POSITION;
        PREVIOUS_POSITION = INITIAL_POSITION;
        INTERNAL_TIMER = new ElapsedTime();
    }

    //positive is true, negative is false, 0 is 0
    public void setXDirection(int direction) {
        X_POSITIVE_ANALOG = direction > 0;
        X_NEGATIVE_ANALOG = direction < 0;
    }

    public void setYDirection(int direction) {
        Y_POSITIVE_ANALOG = direction > 0;
        Y_NEGATIVE_ANALOG = direction < 0;
    }

    //positive / counter clockwise is true, cw / negative is false, 0 is 0
    public void setAngDirection(int direction) {
        ANG_POSITIVE_ANALOG = direction > 0;
        ANG_NEGATIVE_ANALOG = direction < 0;
    }

    //these booleans describe which dpad buttons are pressed
    public void iterate() {

        X_ACCEL = (X_POSITIVE_ANALOG ? 1 : 0) * X_MAX_ACCEL;
        X_ACCEL += (X_NEGATIVE_ANALOG ? -1 : 0) * X_MAX_ACCEL;
        X_ACCEL += (X_VELOCITY > X_VELOCITY_THRESHOLD ? -1 : (X_VELOCITY < -X_VELOCITY_THRESHOLD ? 1 : 0)) * X_MAX_ACCEL;

        Y_ACCEL = (Y_POSITIVE_ANALOG ? 1 : 0) * Y_MAX_ACCEL;
        Y_ACCEL += (Y_NEGATIVE_ANALOG ? -1 : 0) * Y_MAX_ACCEL;
        Y_ACCEL += (Y_VELOCITY > Y_VELOCITY_THRESHOLD ? -1 : (Y_VELOCITY < -Y_VELOCITY_THRESHOLD ? 1 : 0)) * Y_MAX_ACCEL;

        ANG_ACCEL = (ANG_POSITIVE_ANALOG ? 1 : 0) * ANG_MAX_ACCEL;
        ANG_ACCEL += (ANG_NEGATIVE_ANALOG ? -1 : 0) * ANG_MAX_ACCEL;
        ANG_ACCEL += (ANG_VELOCITY > ANG_VELOCITY_THRESHOLD ? -1 : (ANG_VELOCITY < -ANG_VELOCITY_THRESHOLD ? 1 : 0)) * ANG_MAX_ACCEL;
        
        DELTA_TIME = .1;

        X_VELOCITY += X_ACCEL * DELTA_TIME;
        X_VELOCITY = (X_ACCEL == 0) ? 0 : X_VELOCITY;
        X_VELOCITY = (Math.abs(X_VELOCITY) < X_MAX_SPEED) ? X_VELOCITY : (Math.signum(X_VELOCITY) * X_MAX_SPEED);

        Y_VELOCITY += Y_ACCEL * DELTA_TIME;
        Y_VELOCITY = (Y_ACCEL == 0) ? 0 : Y_VELOCITY;
        Y_VELOCITY = (Math.abs(Y_VELOCITY) < Y_MAX_SPEED) ? Y_VELOCITY : (Math.signum(Y_VELOCITY) * Y_MAX_SPEED);

        ANG_VELOCITY += ANG_ACCEL * DELTA_TIME;
        ANG_VELOCITY = (ANG_ACCEL == 0) ? 0 : ANG_VELOCITY;
        ANG_VELOCITY = (Math.abs(ANG_VELOCITY) < ANG_MAX_SPEED) ? ANG_VELOCITY : (Math.signum(ANG_VELOCITY) * ANG_MAX_SPEED);

        PREVIOUS_POSITION = POSITION;
        POSITION = new Pose2d(POSITION.getX() + (X_VELOCITY * DELTA_TIME), POSITION.getY() + (Y_VELOCITY * DELTA_TIME), POSITION.getHeading() + (ANG_VELOCITY * DELTA_TIME));

        INTERNAL_TIMER.reset();
    }

    public Pose2d getPose() {
        return(POSITION);
    }
    public Pose2d getPreviousPose() {return(PREVIOUS_POSITION);}
    public ArrayList<Double> getAccels() {
        ArrayList<Double> accels = new ArrayList<>(Arrays.asList(X_ACCEL, Y_ACCEL, ANG_ACCEL));
        return(accels);
    }
    public ArrayList<Double> getVelocities() {
        ArrayList<Double> velocities = new ArrayList<>(Arrays.asList(X_VELOCITY, Y_VELOCITY, ANG_VELOCITY));
        return(velocities);
    }
}
