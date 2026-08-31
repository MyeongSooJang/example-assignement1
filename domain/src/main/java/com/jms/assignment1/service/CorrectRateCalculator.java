package com.jms.assignment1.service;

public class CorrectRateCalculator {

    public Integer calculate(long totalAttempts, long correctCount) {
        if (totalAttempts < 30) {
            return null;
        }
        double rate = (double) correctCount / totalAttempts * 100;
        return (int) Math.round(rate);
    }
}
