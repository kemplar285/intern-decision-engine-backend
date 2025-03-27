package ee.taltech.inbankbackend.endpoint;

import ee.taltech.inbankbackend.exceptions.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class DecisionEngineControllerAdvice {

    private final DecisionResponse response;

    public DecisionEngineControllerAdvice() {
        this.response = new DecisionResponse();
        response.setLoanAmount(null);
        response.setLoanPeriod(null);
    }

    @ExceptionHandler(value = { InvalidLoanAmountException.class })
    public ResponseEntity<DecisionResponse> handleInvalidLoanAmountException(InvalidLoanAmountException ex) {
        response.setErrorMessage(ex.getMessage());
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(value = { InvalidLoanPeriodException.class })
    public ResponseEntity<DecisionResponse> handleInvalidLoanPeriodException(InvalidLoanPeriodException ex) {
        response.setErrorMessage(ex.getMessage());
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(value = { InvalidPersonalCodeException.class })
    public ResponseEntity<DecisionResponse> handleInvalidPersonalCodeException(InvalidPersonalCodeException ex) {
        response.setErrorMessage(ex.getMessage());
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(value = { NoValidLoanException.class })
    public ResponseEntity<DecisionResponse> handleNoValidLoanException(NoValidLoanException ex) {
        response.setErrorMessage(ex.getMessage());
        return ResponseEntity.ok().body(response);
    }

    @ExceptionHandler(value = { InvalidCustomerAgeException.class })
    public ResponseEntity<DecisionResponse> InvalidCustomerAgeException(InvalidCustomerAgeException ex) {
        response.setErrorMessage(ex.getMessage());
        return ResponseEntity.ok().body(response);
    }


    @ExceptionHandler(value = { Exception.class })
    public ResponseEntity<DecisionResponse> handleOtherExceptions(Exception ex) {
        response.setErrorMessage("An unexpected error occurred");
        return ResponseEntity.internalServerError().body(response);
    }

}
