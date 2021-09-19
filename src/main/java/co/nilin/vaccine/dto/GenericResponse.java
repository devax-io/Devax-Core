package co.nilin.vaccine.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class GenericResponse<T> extends GeneralResponse{

    T data;

    public GenericResponse(String code,  T data) {
        super(code);
        this.data = data;
    }

    public GenericResponse(T data) {
        this.data = data;
    }

    public GenericResponse(String code, String message, T data) {
        super(code, message);
        this.data = data;
    }
}
