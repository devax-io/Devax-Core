package co.nilin.vaccine.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class VaccineException extends RuntimeException {

    private String code;

    public VaccineException(String message) {
        super(message);
    }

    public VaccineException(String code, String message) {
        super(message);
        this.setCode(code);
    }

    public VaccineException(String code, String message, Throwable cause) {
        super(message, cause);
        this.setCode(code);
    }

}
