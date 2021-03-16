package backend.transaction;

import backend.transaction.model.TransactionRequest;
import backend.transaction.model.TransactionResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@RequestMapping("/transaction")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @PostMapping
    public TransactionResponse saveTransaction(@RequestBody TransactionRequest request) {
        return transactionService.save(request);
    }

}
