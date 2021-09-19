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
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.web.bind.annotation.*;
import org.yaml.snakeyaml.util.EnumUtils;
import reactor.core.publisher.Mono;

import java.util.Date;

@RestController
@CrossOrigin
@RequestMapping("/vaccine/trader/sign")

public class SignController {
    @Autowired
    SignRepository signRepository;
    @Autowired
    AccountRepository accountRepository;

    @Autowired
    private ApplicationContext appContext;

    @RequestMapping("/{act}/{actId}")
    public Mono<GeneralResponse> sign(@RequestBody Sign signRequest,
                                      @PathVariable("act") ActType act,
                                      @PathVariable("actId") long actId) {

        try {
            EnumUtils.findEnumInsensitiveCase(ActType.class, act.name());
        } catch (Exception e) {
            throw new VaccineException("500", "invalid act");
        }

        if (act.name().equals(ActType.account.name()) && actId == signRequest.getAgent())
            throw new VaccineException("500", "invalid sign");

        return validAccount(signRequest.getAgent())
                .flatMap(a ->
                        appContext.getBean(act.name() + "Repo", ReactiveCrudRepository.class).findById(actId)
                ).switchIfEmpty(Mono.error(new VaccineException("500", "invalid act")))
                .flatMap(a -> signRepository.save(Sign.builder()
                        .act(actId)
                        .agent(signRequest.getAgent())
                        .type(act.name())
                        .createDate(new Date().toString())
                        .build()));


    }


    public Mono<Account> validAccount(long accountId, String type) {
        return accountRepository.findByIdAndType(accountId, type)
                .switchIfEmpty(Mono.error(new VaccineException(String.format("500", "invalid account {}", type))));
    }

    public Mono<Account> validAccount(long accountId) {
        return accountRepository.findById(accountId)
                .switchIfEmpty(Mono.error(new VaccineException("500", "invalid account")));
    }
}
