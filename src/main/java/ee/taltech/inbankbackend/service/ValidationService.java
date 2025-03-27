package ee.taltech.inbankbackend.service;

import com.github.vladislavgoltjajev.personalcode.locale.estonia.EstonianPersonalCodeValidator;
import ee.taltech.inbankbackend.config.DecisionEngineConstants;
import ee.taltech.inbankbackend.exceptions.InvalidCustomerAgeException;
import ee.taltech.inbankbackend.exceptions.InvalidLoanAmountException;
import ee.taltech.inbankbackend.exceptions.InvalidLoanPeriodException;
import ee.taltech.inbankbackend.exceptions.InvalidPersonalCodeException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class ValidationService {

    // Used to check for the validity of the presented ID code.
    private static final EstonianPersonalCodeValidator validator = new EstonianPersonalCodeValidator();

    /**
     * Verify that all inputs are valid according to business rules.
     * If inputs are invalid, then throws corresponding exceptions.
     *
     * @param personalCode Provided personal ID code
     * @param loanAmount   Requested loan amount
     * @param loanPeriod   Requested loan period
     * @throws InvalidPersonalCodeException If the provided personal ID code is invalid
     * @throws InvalidLoanAmountException   If the requested loan amount is invalid
     * @throws InvalidLoanPeriodException   If the requested loan period is invalid
     */
    protected static void verifyInputs(String personalCode, Long loanAmount, int loanPeriod)
            throws InvalidPersonalCodeException, InvalidLoanAmountException, InvalidLoanPeriodException {

        if (!validator.isValid(personalCode)) {
            throw new InvalidPersonalCodeException("Invalid personal ID code!");
        }

        if(!customerWithinAgeRangeByBalticPersonalCode(personalCode)){
            throw new InvalidCustomerAgeException("Invalid loan amount!");
        }

        if (!(DecisionEngineConstants.MINIMUM_LOAN_AMOUNT <= loanAmount)
                || !(loanAmount <= DecisionEngineConstants.MAXIMUM_LOAN_AMOUNT)) {
            throw new InvalidLoanAmountException("Invalid loan amount!");
        }
        if (!(DecisionEngineConstants.MINIMUM_LOAN_PERIOD <= loanPeriod)
                || !(loanPeriod <= DecisionEngineConstants.MAXIMUM_LOAN_PERIOD)) {
            throw new InvalidLoanPeriodException("Invalid loan period!");
        }
    }

    protected static boolean customerWithinAgeRangeByBalticPersonalCode(String personalCode) {
        int yearPrefix = Integer.parseInt(personalCode.substring(0, 1));
        int year = Integer.parseInt(personalCode.substring(1, 3));
        int currentYear = LocalDate.now().getYear();
        int birthYear;

        if (yearPrefix == 1 || yearPrefix == 2) {
            birthYear = 1800 + year;
        } else if (yearPrefix == 3 || yearPrefix == 4) {
            birthYear = 1900 + year;
        } else if (yearPrefix == 5 || yearPrefix == 6) {
            birthYear = 2000 + year;
        } else {
            return false; // Invalid prefix
        }

        int age = currentYear - birthYear;

        // Adjust age if birthday hasn't occurred yet this year.
        int birthMonth = Integer.parseInt(personalCode.substring(3, 5));
        int birthDay = Integer.parseInt(personalCode.substring(5, 7));
        int currentMonth = LocalDate.now().getMonthValue();
        int currentDay = LocalDate.now().getDayOfMonth();

        if (currentMonth < birthMonth || (currentMonth == birthMonth && currentDay < birthDay)) {
            age--;
        }

        return age >= 18 && age <= (DecisionEngineConstants.CURRENT_AVERAGE_LIFESPAN_IN_EUROPE - DecisionEngineConstants.MAXIMUM_LOAN_PERIOD / 12);
    }
}
