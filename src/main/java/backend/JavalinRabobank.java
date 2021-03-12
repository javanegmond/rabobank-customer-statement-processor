package backend;

import backend.transaction.*;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.Javalin;


public class JavalinRabobank {

	private static final TransactionRepository transactionRepository = new InMemoryTransactionRepository();
	private static final TransactionService transactionService = new TransactionService(transactionRepository);
	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
	private static Javalin app;

	public static void main(String[] args) {
		app = Javalin.create().start(8080);
		app.get("/", ctx -> ctx.result("Hello World"));

		app.post("/transaction", (context) -> {
			String transactionRequestString = context.body();
			TransactionRequest transactionRequest = OBJECT_MAPPER.readValue(transactionRequestString, TransactionRequest.class);
			TransactionResponse response = transactionService.save(transactionRequest);
			context.json(response);
		});

		app.exception(JsonParseException.class, (e, ctx) -> {
			ctx.status(400);
			TransactionResponse responseBody = new TransactionResponse();
			responseBody.setResult("BAD_REQUEST");
			try {
				ctx.result(OBJECT_MAPPER.writeValueAsString(responseBody));
			} catch (JsonProcessingException jsonProcessingException) {
				jsonProcessingException.printStackTrace();
			}
		});
	}

	public static void startApp() throws InterruptedException {
		main(new String[0]);
	}

	public static void stopApp() {
		app.stop();
	}
}
