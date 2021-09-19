package co.nilin.vaccine.controller;

import co.nilin.vaccine.dto.GeneralResponse;
import co.nilin.vaccine.dto.VaccineException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import reactor.core.publisher.Mono;

import java.util.Date;

@Slf4j
@RestControllerAdvice
@CrossOrigin

public class ExceptionController {
    @ExceptionHandler(value = {VaccineException.class})
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public GeneralResponse vaccineException(VaccineException ex) {
        return new GeneralResponse(
                new Date().getTime(),
                ex.getCode(),
                ex.getMessage());
    }
}