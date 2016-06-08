package com.example.bbirincioglu.centipedegame;

/**
 * The class for evaluating final decisions of the players, and determining which player obtained which payoff.
 */
public class GameResultEvaluator {
    public GameResultEvaluator() {

    }

    public double[] evaluate(GameResult gameResult) {
        double[] result = new double[2];
        String playerFinished = gameResult.getPlayerFinished(); //get data of which player finished.
        double p1Payoff = Double.valueOf(gameResult.getP1Payoff()); //get first player final payoff.
        double p2Payoff = Double.valueOf(gameResult.getP2Payoff()); //get second player final payoff.
        double punishment = Double.valueOf(gameResult.getPunishment()); //get punishment value in case player finished didn't obey what he has commitment to.
        int finalStepNumber = Integer.valueOf(gameResult.getFinalStepNumber()); //get final step number for checking with the commitment value of the player finished.
        int p1Commitment = Integer.valueOf(gameResult.getP1Commitment()); //get first player commitment.
        int p2Commitment = Integer.valueOf(gameResult.getP2Commitment()); //get second player commitment.

        if (playerFinished.equals("P1")) { //If first player finishes, and he finishes the game when the game step number is smaller than what he has committed to,
                                            // he will be applied some punishment. (Punishment is negative)
            if (finalStepNumber  < p1Commitment) {
                p1Payoff += punishment;
            }
        } else if (playerFinished.equals("P2")) { //If second player finishes, and he finishes the game when the game step number is smaller than what he has committed to,
                                                    //he will be applied some punishment. (Punishment is negative)
            if (finalStepNumber < p2Commitment) {
                p2Payoff += punishment;
            }
        }

        //Save payoffs in an array.
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
