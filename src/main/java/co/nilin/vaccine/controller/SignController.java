package co.nilin.vaccine.controller;

import co.nilin.vaccine.dao.AccountRepository;
import co.nilin.vaccine.dao.SignRepository;
import co.nilin.vaccine.dto.AccountType;
import co.nilin.vaccine.dto.ActType;
import co.nilin.vaccine.dto.GeneralResponse;
import co.nilin.vaccine.dto.VaccineException;
import co.nilin.vaccine.model.Account;
import co.nilin.vaccine.model.Lot;
import co.nilin.vaccine.model.Sign;
import jdk.jfr.internal.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Date;

@RestController
public class SignController {
    @Autowired
    SignRepository signRepository;
    @Autowired
    AccountRepository accountRepository;

//    @RequestMapping("/sign/{act}/{actId}")
//    public Mono<GeneralResponse> sign(@RequestBody Sign signRequest,
//                                      @PathVariable("act") ActType act,
//                                      @PathVariable("actId") long actId) {
//        validAccount(signRequest.getId())
//                .flatMap(s -> {
//
//                    if (act.equals(ActType.account))
//                        return validAccount(actId)
//                                .flatMap(signRepository.save(signRequest.builder().act(actId)
//                                ))
//
//                })
//
//    }

    public Mono<Account> validAccount(long accountId, String type) {
        return accountRepository.findByIdAndType(accountId, type)
                .switchIfEmpty(Mono.error(new VaccineException(String.format("500", "invalid account {}", type))));
    }

    public Mono<Account> validAccount(long accountId) {
        return accountRepository.findById(accountId)
                .switchIfEmpty(Mono.error(new VaccineException("500", "invalid account")));
    }
}
