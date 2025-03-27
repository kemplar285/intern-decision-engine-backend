package ee.taltech.inbankbackend.service;

import ee.taltech.inbankbackend.config.DecisionEngineConstants;
import org.springframework.stereotype.Service;

/**
 *
 * A service class that provides a method for calculating credit score of an offer.
 * customer's credit modifier is determined by the last four digits of their ID code.
 */

@Service
public class CreditInfoService {


    /**
     *
     * @param creditModifier Customer credit modifier. Mock implementation is provided in getCreditModifier.
     * @param loanAmount Loan amount. 2000 <= Amount <= 10000
     * @param loanPeriod Loan period. 12 <= Period <= 48
     * @return Double credit modifier. A value less than 0.1 will be rejected during loan calculation.
     */


    protected static double calculateCreditScore(int creditModifier, int loanAmount, int loanPeriod) {
        return (((double) creditModifier / (double) loanAmount) * loanPeriod) / 10;
    }

    /**
     * Calculates the credit modifier of the customer to according to the last four digits of their ID code.
     * Debt - 0000...2499
     * Segment 1 - 2500...4999
     * Segment 2 - 5000...7499
     * Segment 3 - 7500...9999
     *
     * @param personalCode ID code of the customer that made the request.
     * @return Segment to which the customer belongs.
     */
    protected static int getCreditModifier(String personalCode) {
        int segment = Integer.parseInt(personalCode.substring(personalCode.length() - 4));

        if (segment < 2500) {
            return DecisionEngineConstants.DEBT;
        } else if (segment < 5000) {
            return DecisionEngineConstants.SEGMENT_1_CREDIT_MODIFIER;
        } else if (segment < 7500) {
            return DecisionEngineConstants.SEGMENT_2_CREDIT_MODIFIER;
        }

        return DecisionEngineConstants.SEGMENT_3_CREDIT_MODIFIER;
    }

}
