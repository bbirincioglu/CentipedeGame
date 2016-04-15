package com.example.bbirincioglu.centipedegame;

/**
 * Created by bbirincioglu on 3/16/2016.
 */
public class GameResultEvaluator {
    public GameResultEvaluator() {

    }

    public double[] evaluate(GameResult gameResult) {
        double[] result = new double[2];
        double p1Payoff = Double.valueOf(gameResult.getP1Payoff());
        double p2Payoff = Double.valueOf(gameResult.getP2Payoff());
        double punishment = Double.valueOf(gameResult.getPunishment());
        int finalStepNumber = Integer.valueOf(gameResult.getFinalStepNumber());
        int p1Commitment = Integer.valueOf(gameResult.getP1Commitment());
        int p2Commitment = Integer.valueOf(gameResult.getP2Commitment());

        if (finalStepNumber > p1Commitment) {
            p1Payoff += punishment;
        }

        if (finalStepNumber > p2Commitment) {
            p2Payoff += punishment;
        }

        result[0] = p1Payoff;
        result[1] = p2Payoff;
        return result;
    }

    private int[] stringToIntArray(String text, char splitWith) {
        int[] result = new int[2];
        String temp = "";
        int length = text.length();
        int index = 0;

        for (int i = 0; i < length; i++) {
            char charAtI = text.charAt(i);

            if (charAtI == splitWith) {
                result[index] = Integer.valueOf(temp);
                temp = "";
                index++;
            } else {
                temp += charAtI;
            }
        }

        if (!temp.equals("")) {
            result[index] = Integer.valueOf(temp);
        }

        return result;
    }
}
