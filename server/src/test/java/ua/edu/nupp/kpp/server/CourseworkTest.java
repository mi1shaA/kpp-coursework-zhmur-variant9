package ua.edu.nupp.kpp.server;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CourseworkTest {

    @Test
    void integralIndexForDemoFactorsIs71() {
        assertEquals(71.0, MeasureCalculator.calculate(75, 60, 80), 0.001);
    }

    @Test
    void integralIndexForZeroFactorsIsZero() {
        assertEquals(0.0, MeasureCalculator.calculate(0, 0, 0), 0.001);
    }

    @Test
    void integralIndexForMaxFactorsIs100() {
        assertEquals(100.0, MeasureCalculator.calculate(100, 100, 100), 0.001);
    }

    @Test
    void integralIndexIsRoundedToTwoDecimals() {
        assertEquals(49.17, MeasureCalculator.calculate(33.33, 66.67, 50), 0.001);
    }

    @Test
    void calculatorRejectsFactorAbove100() {
        assertThrows(IllegalArgumentException.class, () -> MeasureCalculator.calculate(101, 10, 10));
    }

    @Test
    void calculatorRejectsNegativeFactor() {
        assertThrows(IllegalArgumentException.class, () -> MeasureCalculator.calculate(10, -1, 10));
    }

    @Test
    void proposedToApprovedIsAllowed() {
        assertTrue(MeasureService.isAllowedTransition(MeasureState.PROPOSED, MeasureState.APPROVED));
    }

    @Test
    void approvedToImplementationIsAllowed() {
        assertTrue(MeasureService.isAllowedTransition(MeasureState.APPROVED, MeasureState.IMPLEMENTATION));
    }

    @Test
    void implementationToCompletedIsAllowed() {
        assertTrue(MeasureService.isAllowedTransition(MeasureState.IMPLEMENTATION, MeasureState.COMPLETED));
    }

    @Test
    void proposedToCompletedIsRejected() {
        assertFalse(MeasureService.isAllowedTransition(MeasureState.PROPOSED, MeasureState.COMPLETED));
    }

    @Test
    void backwardTransitionIsRejected() {
        assertFalse(MeasureService.isAllowedTransition(MeasureState.APPROVED, MeasureState.PROPOSED));
    }

    @Test
    void completedMeasureHasNoTransitions() {
        for (MeasureState next : MeasureState.values()) {
            assertFalse(MeasureService.isAllowedTransition(MeasureState.COMPLETED, next));
        }
    }

    @Test
    void measureCalculatesIntegralIndexFromFactors() {
        Measure measure = new Measure();
        measure.setFactors(75, 60, 80);
        assertEquals(71.0, measure.getIntegralIndex(), 0.001);
    }

    @Test
    void measureRejectsFactorOutOfRange() {
        Measure measure = new Measure();
        assertThrows(IllegalArgumentException.class, () -> measure.setFactors(150, 50, 50));
    }

    @Test
    void textValidationRejectsBlankValue() {
        assertThrows(IllegalArgumentException.class, () -> ObjectItemService.requireText("   ", "Назва", 50));
    }

    @Test
    void textValidationRejectsTooLongValue() {
        assertThrows(IllegalArgumentException.class, () -> ObjectItemService.requireText("x".repeat(51), "Назва", 50));
    }

    @Test
    void textValidationAcceptsNormalValue() {
        assertDoesNotThrow(() -> ObjectItemService.requireText("Головний корпус", "Назва", 50));
    }
}