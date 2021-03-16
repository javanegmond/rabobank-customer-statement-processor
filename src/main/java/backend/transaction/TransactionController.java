package backend.transaction;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

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
