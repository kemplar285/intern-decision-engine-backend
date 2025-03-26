package ee.taltech.inbankbackend.service;


import ee.taltech.inbankbackend.config.DecisionEngineConstants;
import ee.taltech.inbankbackend.exceptions.InvalidLoanAmountException;
import ee.taltech.inbankbackend.exceptions.InvalidLoanPeriodException;
import ee.taltech.inbankbackend.exceptions.InvalidPersonalCodeException;
import ee.taltech.inbankbackend.exceptions.NoValidLoanException;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * A service class that provides a method for calculating an approved loan amount and period for a customer.
 * The loan amount is calculated based on the customer's credit modifier,
 * which is determined by the last four digits of their ID code.
 */
@Service
public class DecisionEngine {

    private static final Long HIGHEST_VALID_LOAN_AMOUNT_CALCULATOR_STEP = 100L;
    private int creditModifier = 0;

    /**
     * Calculates the maximum loan amount and period for the customer based on their ID code,
     * the requested loan amount and the loan period.
     * The loan period must be between 12 and 48 months (inclusive).
     * The loan amount must be between 2000 and 10000€ months (inclusive).
     *
     * @param personalCode        ID code of the customer that made the request.
     * @param loanRequestedAmount Requested loan amount
     * @param loanRequestedPeriod Requested loan period
     * @return A Decision object containing the approved loan amount and period, and an error message (if any)
     * @throws InvalidPersonalCodeException If the provided personal ID code is invalid
     * @throws InvalidLoanAmountException   If the requested loan amount is invalid
     * @throws InvalidLoanPeriodException   If the requested loan period is invalid
     * @throws NoValidLoanException         If there is no valid loan found for the given ID code, loan amount and loan period
     */
    public Decision calculateApprovedLoan(String personalCode, Long loanRequestedAmount, int loanRequestedPeriod)
            throws InvalidPersonalCodeException, InvalidLoanAmountException, InvalidLoanPeriodException,
            NoValidLoanException {
        try {
            ValidationService.verifyInputs(personalCode, loanRequestedAmount, loanRequestedPeriod);
        } catch (Exception e) {
            return new Decision(null, null, e.getMessage());
        }

        creditModifier = CreditInfoService.getCreditModifier(personalCode);

        if (creditModifier == 0) {
            throw new NoValidLoanException("No valid loan found!");
        }

        Map<String, Integer> suggestedLoanAmountAndPeriod = suggestedLoanAmountAndPeriod(loanRequestedPeriod, loanRequestedAmount);
        int highestValidLoanAmount = suggestedLoanAmountAndPeriod.get("loanAmount");
        int suggestedLoanPeriod = suggestedLoanAmountAndPeriod.get("loanPeriod");

        if (highestValidLoanAmount < DecisionEngineConstants.MINIMUM_LOAN_AMOUNT) {
            throw new NoValidLoanException("No valid loan found!");
        }

        return new Decision(Math.min(DecisionEngineConstants.MAXIMUM_LOAN_AMOUNT, highestValidLoanAmount), suggestedLoanPeriod, null);
    }



    /**
     * Calculates the largest valid loan for the current credit modifier and loan period.
     *
     * @return Largest valid loan amount or 0 if there are no valid loans
     */
    private int highestValidLoanAmount(int loanPeriod) {
        double creditScoreForMinimumAllowedLoan = CreditInfoService.calculateCreditScore(creditModifier, DecisionEngineConstants.MINIMUM_LOAN_AMOUNT, loanPeriod);
        if (creditScoreForMinimumAllowedLoan >= 0.1) {
            int highestValidAmount = DecisionEngineConstants.MINIMUM_LOAN_AMOUNT;

            while (CreditInfoService.calculateCreditScore(creditModifier, highestValidAmount, loanPeriod) > 0.1) {
                highestValidAmount += HIGHEST_VALID_LOAN_AMOUNT_CALCULATOR_STEP;
            }

            return highestValidAmount;
        }
        return 0;
    }


    private Map<String, Integer> suggestedLoanAmountAndPeriod(Integer loanRequestedPeriod, Long loanRequestedAmount){
        Map<String, Integer> suggestedLoanAmountAndPeriod = new HashMap<>();
        int loanAmount = 0;
        int loanPeriod = 0;

        int highestValidLoanAmountForUserRequestedPeriod = highestValidLoanAmount(loanRequestedPeriod);

        //If we are ready to approve higher loan or the same amount for user requested period
        if (highestValidLoanAmountForUserRequestedPeriod >= loanRequestedAmount) {
            loanAmount = highestValidLoanAmountForUserRequestedPeriod;
            loanPeriod = loanRequestedPeriod;
        }
        // Else - calculate how many months would go to get the same or maximum amount possible
        else {
            loanPeriod = DecisionEngineConstants.MINIMUM_LOAN_PERIOD;

            for (int period = DecisionEngineConstants.MINIMUM_LOAN_PERIOD; period <= DecisionEngineConstants.MAXIMUM_LOAN_PERIOD; period++) {
                loanAmount = highestValidLoanAmount(period);
                loanPeriod = period;
                if (loanAmount >= loanRequestedAmount) {
                    break;
                }
            }
        }

        suggestedLoanAmountAndPeriod.put("loanAmount", loanAmount);
        suggestedLoanAmountAndPeriod.put("loanPeriod", loanPeriod);
        return suggestedLoanAmountAndPeriod;
    }



}
