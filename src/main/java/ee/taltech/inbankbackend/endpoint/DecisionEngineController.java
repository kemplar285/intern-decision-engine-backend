package ee.taltech.inbankbackend.endpoint;

import ee.taltech.inbankbackend.exceptions.InvalidLoanAmountException;
import ee.taltech.inbankbackend.exceptions.InvalidLoanPeriodException;
import ee.taltech.inbankbackend.exceptions.InvalidPersonalCodeException;
import ee.taltech.inbankbackend.exceptions.NoValidLoanException;
import ee.taltech.inbankbackend.service.Decision;
import ee.taltech.inbankbackend.service.DecisionEngine;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/loan")
@CrossOrigin
public class DecisionEngineController {

    private final DecisionEngine decisionEngine;

    public DecisionEngineController(DecisionEngine decisionEngine) {
        this.decisionEngine = decisionEngine;
    }

    /**
     * A REST endpoint that handles requests for loan decisions.
     * The endpoint accepts POST requests with a request body containing the customer's personal ID code,
     * requested loan amount, and loan period.<br><br>
     * <p>
     * Exception handling is now done through controller advice.
     *
     * @param request The request body containing the customer's personal ID code, requested loan amount, and loan period
     * @return A ResponseEntity with a DecisionResponse body containing the approved loan amount and period, and an error message (if any)
     */
    @PostMapping("/decision")
    public ResponseEntity<DecisionResponse> requestDecision(@RequestBody DecisionRequest request)
            throws InvalidLoanPeriodException, NoValidLoanException, InvalidPersonalCodeException, InvalidLoanAmountException {
        DecisionResponse response = new DecisionResponse();

        Decision decision = decisionEngine.calculateApprovedLoan(request.getPersonalCode(), request.getLoanAmount(), request.getLoanPeriod());
        response.setLoanAmount(decision.getLoanAmount());
        response.setLoanPeriod(decision.getLoanPeriod());
        response.setErrorMessage(decision.getErrorMessage());

        return ResponseEntity.ok(response);
    }
}
