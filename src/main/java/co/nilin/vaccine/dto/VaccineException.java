package co.nilin.vaccine.dto;

public class VaccineException extends RuntimeException {
    protected String code;

    public VaccineException(String code, String message) {
        super(message);
        this.code = code;
    }
}
