package com.jms.assignment1.service;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectRateCalculatorTest {

    @Test
    void 제출자가_0명이면_null을_반환한다() {

        assertThat(CorrectRateCalculator.calculate(0, 0)).isNull();
    }

    @Test
    void 제출자가_15명이면_null을_반환한다() {
        assertThat(CorrectRateCalculator.calculate(15, 10)).isNull();
    }

    @Test
    void 제출자가_29명이면_null을_반환한다() {
        assertThat(CorrectRateCalculator.calculate(29, 20)).isNull();
    }

    @Test
    void 제출자가_30명이면_정답률을_반환한다() {
        assertThat(CorrectRateCalculator.calculate(30, 30)).isEqualTo(100);
    }

    @Test
    void 정답률은_소수점_첫째_자리에서_반올림한다() {
        assertThat(CorrectRateCalculator.calculate(30, 20)).isEqualTo(67);
    }

    @Test
    void 정답자가_0명이면_0을_반환한다() {
        assertThat(CorrectRateCalculator.calculate(30, 0)).isEqualTo(0);
    }
}
