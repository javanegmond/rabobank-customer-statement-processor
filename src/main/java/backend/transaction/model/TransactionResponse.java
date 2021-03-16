package backend.transaction.model;

import java.util.ArrayList;
import java.util.List;

public class TransactionResponse {

    private String result;

    private final List<ErrorRecord> errorRecords = new ArrayList<>();

    public boolean addError(ErrorRecord errorRecord) {
        return errorRecords.add(errorRecord);
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public List<ErrorRecord> getErrorRecords() {
        return errorRecords;
    }

}
