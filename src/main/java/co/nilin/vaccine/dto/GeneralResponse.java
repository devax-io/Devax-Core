package co.nilin.vaccine.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class GeneralResponse {

    public GeneralResponse(String code, String message) {
        this.code = code;
        this.message = message;
        this.timestamp=new Date().getTime();
    }


    public GeneralResponse(String code) {
        this.code = code;
        this.timestamp=new Date().getTime();
    }

    long timestamp  ;
    String code;
    String message;


    public void setTimestamp(long timestamp) {
        this.timestamp = new Date().getTime();
    }
}
