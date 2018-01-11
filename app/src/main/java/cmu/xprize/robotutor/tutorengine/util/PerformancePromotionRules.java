package cmu.xprize.robotutor.tutorengine.util;


import android.util.Log;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * RoboTutor
 * <p>
 * Created by kevindeland on 1/11/18.
 */

public class PerformancePromotionRules extends PromotionRules {

    List<String> nonAssessableActivities = Arrays.asList("story.read", "story.echo", "story.hear", "countingx", "write");


    static final double LOW_PERFORMANCE_THRESHOLD = 0.4;
    static final double MID_PERFORMANCE_THRESHOLD = 0.7;
    static final double HIGH_PERFORMANCE_THRESHOLD = 0.9;

    // what if there are not enough total questions???
    // for reference... here is a simulation of percentages for #correct out of #problems {1,7}... and the respective promotion
    double[] p1 = {0.0, 1.0}; // {PREV, HIGH}
    double[] p2 = {0.0, 0.5, 1.0}; // {PREV, STAY, HIGH}
    double[] p3 = {0.0, 0.333, 0.667, 1.0}; // {PREV, PREV, STAY, HIGH}
    double[] p4 = {0.0, 0.25, 0.5, 0.75, 1.0}; // {PREV, PREV, STAY, NEXT, HIGH}
    double[] p5 = {0.0, 0.20, 0.40, 0.60, 0.80, 0.90}; // {PREV, PREV, STAY, STAY, NEXT, HIGH}
    double[] p6 = {0.0, 0.167, 0.333, 0.50, 0.667, 0.833, 1.0}; // {PREV, PREV, PREV, STAY, STAY, NEXT, HIGH}
    double[] p7 = {0.0, 0.14, 0.28, 0.43, 0.57, 0.72, 0.86, 1.0}; // {PREV, PREV, PREV, STAY, STAY, NEXT, NEXT, HIGH}


    @Override
    public SelectedActivity selectActivityByPerformance(PerformanceData performance) {

        // if they want to play again, repeat no matter what
        if (performance.getSelfAssessment() == PerformanceData.StudentSelfAssessment.PLAY_AGAIN) {
            return SelectedActivity.SAME;
        }

        // test Non-assessable activities
        if (performance.getActivityType() != null) {
            for (String type : nonAssessableActivities) {
                if (performance.getActivityType().startsWith(type)) {
                    return SelectedActivity.NEXT;
                }
            }
        }

        // now we get into assessment

        // if they start an activity but don't like it.... what do?
        if (performance.getNumberAttempts() <= 2 && performance.getTotalNumberQuestions() > 3) {
            return SelectedActivity.NEXT;
        }

        // prevent divide by zero
        if(performance.getTotalNumberQuestions() == 0) {
            return SelectedActivity.NEXT;
        }

        // percentage is #correct / #totalQuestions.... NOT #correct / #numAttempts
        // buuuuuut if they back out???

        double percentCorrect = (double) performance.getNumberCorrect() / (double) performance.getTotalNumberQuestions();
        Log.d("PERCENT_CORRECT", "" + percentCorrect);

        if (percentCorrect >= HIGH_PERFORMANCE_THRESHOLD) {
            // 50/50 probability
            return Math.random() > 0.5 ? SelectedActivity.NEXT : SelectedActivity.DOUBLE_NEXT;
        } else if (percentCorrect >= MID_PERFORMANCE_THRESHOLD) {
            // pass to next
            return SelectedActivity.NEXT;
        } else if (percentCorrect >= LOW_PERFORMANCE_THRESHOLD) {
            // repeat
            return SelectedActivity.SAME;
        } else {
            // drop down to lower level
            return SelectedActivity.PREVIOUS;
        }
    }
}