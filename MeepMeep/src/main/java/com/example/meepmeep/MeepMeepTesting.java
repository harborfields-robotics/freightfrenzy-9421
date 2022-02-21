package com.example.meepmeep;


import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.noahbres.meepmeep.MeepMeep;
import com.noahbres.meepmeep.core.colorscheme.scheme.ColorSchemeBlueDark;
import com.noahbres.meepmeep.core.colorscheme.scheme.ColorSchemeRedDark;
import com.noahbres.meepmeep.roadrunner.DefaultBotBuilder;
import com.noahbres.meepmeep.roadrunner.entity.RoadRunnerBotEntity;

public class MeepMeepTesting {
    public static void main(String[] args) {
        System.setProperty("sun.java2d.opengl", "true");

        MeepMeep meepMeep = new MeepMeep(650);

        // Declare our first bot
        Pose2d splineCV = new Pose2d(9.5,-55.2,Math.toRadians(210));
        Pose2d splineTest = new Pose2d(9.5,-55.2, Math.toRadians(210));
        Pose2d RevertTest = new Pose2d(15.6,-63.2, Math.toRadians(180));
        Vector2d vectorTest = new Vector2d(9.5,-55.2);
        RoadRunnerBotEntity myFirstBot = new DefaultBotBuilder(meepMeep)
                // We set this bot to be blue
                .setColorScheme(new ColorSchemeBlueDark())
                .setConstraints(140.63964888286645, 52.48291908330528, Math.toRadians(180), Math.toRadians(180), 12.6)
                .followTrajectorySequence(drive ->
                        drive.trajectorySequenceBuilder(new Pose2d(6, -64, Math.toRadians(180)))
                                .lineToLinearHeading(splineCV)
                                .lineToLinearHeading(RevertTest)
                                .back(30)
                                .forward(30)
                                .lineToLinearHeading(splineTest)
                                .lineToLinearHeading(RevertTest)
                                .back(30)
                                .forward(30)
                                .lineToLinearHeading(splineTest)
                                .lineToLinearHeading(RevertTest)
                                .back(30)
                                .forward(30)
                                .lineToLinearHeading(splineTest)
                                .lineToLinearHeading(RevertTest)
                                .back(30)
                                .forward(30)
                                .lineToLinearHeading(splineTest)
                                .lineToLinearHeading(RevertTest)
                                .back(30)
                                .forward(30)
                                .lineToLinearHeading(splineTest)
                                .lineToLinearHeading(RevertTest)
                                .back(40)
                                .build()
                );



        // Declare out second bot
        RoadRunnerBotEntity mySecondBot = new DefaultBotBuilder(meepMeep)
                // We set this bot to be red
                .setColorScheme(new ColorSchemeRedDark())
                .setConstraints(60, 60, Math.toRadians(180), Math.toRadians(180), 12.6)
                .followTrajectorySequence(drive ->
                        drive.trajectorySequenceBuilder(new Pose2d(30, 30, Math.toRadians(180)))
                                .forward(30)
                                .turn(Math.toRadians(90))
                                .forward(30)
                                .turn(Math.toRadians(90))
                                .forward(30)
                                .turn(Math.toRadians(90))
                                .forward(30)
                                .turn(Math.toRadians(90))
                                .build()
                );

        meepMeep.setBackground(MeepMeep.Background.FIELD_FREIGHTFRENZY_ADI_DARK)
                .setDarkMode(false)
                .setBackgroundAlpha(0.95f)

                // Add both of our declared bot entities
                .addEntity(myFirstBot)
                .addEntity(mySecondBot)
                .start();
    }


}