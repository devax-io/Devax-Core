package co.nilin.vaccine.controller;

import co.nilin.vaccine.dao.*;
import co.nilin.vaccine.dto.*;
import co.nilin.vaccine.model.Account;
import co.nilin.vaccine.model.BalanceReport;
import co.nilin.vaccine.model.Lot;
import co.nilin.vaccine.model.Vial;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.*;
import org.yaml.snakeyaml.util.EnumUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

@RestController
@RequestMapping("/vaccine/trader/query")
@Slf4j
@CrossOrigin

public class QueryEntity {

    @Autowired
    AccountRepository accountRepository;
    @Autowired
    LotRepository lotRepository;
    @Autowired
    VialRepository vialRepository;
    @Autowired
    BalanceRepository balanceRepository;
    @Autowired
    TransactionRepository txRepository;
    @Autowired
    SignRepository signRepository;

    @GetMapping("/account/{accountId}")
    public Mono<GenericResponse> account(@PathVariable("accountId") long accountId) {

        return accountRepository.findById(accountId)
                .switchIfEmpty(Mono.error(new VaccineException("500", "invalid account")))
                .flatMap(r -> Mono.just(new GenericResponse("200", r)));
    }

    @GetMapping("/account/type/{accountType}")
    public Mono<GenericResponse> accountType(@PathVariable("accountType") String accountType) {

        try {
            EnumUtils.findEnumInsensitiveCase(AccountType.class, accountType);
        } catch (Exception e) {
            throw new VaccineException("500", "invalid account type");
        }
        return accountRepository.findByType(accountType)
                .switchIfEmpty(Mono.error(new VaccineException("500", "invalid account")))
                .collectList()
                .flatMap(r -> Mono.just(new GenericResponse("200", r)));
    }

    @SendTo("/topic/account")
    @MessageMapping("/accounts")
    @GetMapping("/accounts")
    public Mono<GenericResponse> accounts() {
        return accountRepository.findAll()
                .collectList()
                .flatMap(r -> Mono.just(new GenericResponse("200", r)));

    }


    @GetMapping("/lot/{manufacture}/{lotRefId}")
    public Mono<GenericResponse> lot(@PathVariable("lotRefId") String lotRefId,
                                     @PathVariable("manufacture") long manufacture) {

        return lotRepository.findByRefId(manufacture, lotRefId)
                .switchIfEmpty(Mono.error(new VaccineException("500", "invalid lot")))
                .flatMap(r -> Mono.just(new GenericResponse("200", r)));
    }

    @GetMapping("/lots")
    public Mono<GenericResponse> lots() {
        return lotRepository.findAll()
                .collectList()
                .flatMap(r -> Mono.just(new GenericResponse("200", r)));

    }

    @GetMapping("/vial/{manufacture}/{vialRefId}")
    public Mono<GenericResponse> vial(@PathVariable("vialRefId") String vialRefId,
                                      @PathVariable("manufacture") long manufacture) {

        return validAccount(manufacture, AccountType.manufacture.name())
                .flatMap(a -> vialRepository.findByRefId(manufacture + "_" + vialRefId))
                .switchIfEmpty(Mono.error(new VaccineException("500", "invalid vial")))
                .flatMap(r -> Mono.just(new GenericResponse("200", r)));
    }


    @GetMapping("/vials")
    public Mono<GenericResponse> vials() {
        return vialRepository.findAll()
                .map(c -> c.setRefId(c.getRefId().split("_")[1]))
                .collectList()
                .flatMap(r -> Mono.just(new GenericResponse("200", r)));

    }


    @GetMapping("/lot/{manufacture}/{lotRefId}/vials")
    public Mono<GenericResponse> lotVials(@PathVariable("manufacture") long manufacture,
                                          @PathVariable("lotRefId") String lotRefId) {
        return validAccount(manufacture, AccountType.manufacture.name())
                .flatMap(a -> lotRepository.findByRefId(manufacture, lotRefId))
                .switchIfEmpty(Mono.error(new VaccineException("500", "invalid lot")))
                .flatMap(l -> vialRepository.findByParentId(l.getId())
                        .map(c -> c.setRefId(c.getRefId().split("_")[1]))
                        .collectList())
                .flatMap(r -> Mono.just(new GenericResponse("200", r)));

    }


    @GetMapping("/tx/{manufacture}/{vialRefId}")
    public Mono<GenericResponse> tx(@PathVariable("manufacture") long manufacture,
                                    @PathVariable("vialRefId") String vialRefId) {
        return validAccount(manufacture, AccountType.manufacture.name())
                .flatMap(a -> txRepository.findTxOfVial(manufacture + "_" + vialRefId)
                        .map(c -> c.setVial(c.getVial().split("_")[1]))
                        .collectList())
                .flatMap(r -> Mono.just(new GenericResponse("200", r)));

    }

    @GetMapping("/transactions")
    public Mono<GenericResponse> txs() {
        return txRepository.findAll()
                .map(c -> c.setVial(c.getVial().split("_")[1]))
                .collectList()
                .flatMap(r -> Mono.just(new GenericResponse("200", r)));

    }

    @GetMapping("/balance/{accountId}")
    public Mono<GenericResponse> balance(@PathVariable("accountId") long accountId) {
        return validAccount(accountId)
                .flatMap(a -> balanceRepository.findById(accountId))
                .flatMap(r -> Mono.just(new GenericResponse("200", r)));

    }

    @GetMapping("/balances")
    public Mono<GenericResponse> balances() {
        return balanceRepository.findAll()
                .collectList()
                .flatMap(r -> Mono.just(new GenericResponse("200", r)));

    }

    @GetMapping("/signs")
    public Mono<GenericResponse> signs() {
        return signRepository.findAll()
                .collectList()
                .flatMap(r -> Mono.just(new GenericResponse("200", r)));

    }


    @GetMapping("/signs/agent/{agent}")
    public Mono<GenericResponse> signer(@PathVariable("agent") long agent) {
        return signRepository.findByAgent(agent)
                .collectList()
                .flatMap(r -> Mono.just(new GenericResponse("200", r)));

    }

    @GetMapping("/signs/act/{actType}/{actId}")
    public Mono<GenericResponse> signed(@PathVariable("actType") String actType,
                                        @PathVariable("actId") long actId) {
        return signRepository.findByActAndType(actId, actType)
                .collectList()
                .flatMap(r -> Mono.just(new GenericResponse("200", r)));

    }

    @GetMapping("/general")
    public Mono<GenericResponse> general() {
        return lotRepository.count()
                .zipWhen(l -> vialRepository.count())
                .zipWhen(lv -> accountRepository.count())
                .zipWhen(lva -> txRepository.count())
                .flatMap(lvat -> Mono.just(new GenericResponse("200", GeneralQuery.builder()
                        .totalLots(lvat.getT1().getT1().getT1())
                        .totalVials(lvat.getT1().getT1().getT2())
                        .totalAccounts(lvat.getT1().getT2())
                        .totalTransactions(lvat.getT2()).build())));

    }


    public Mono<Account> validAccount(long accountId, String type) {
        return accountRepository.findByIdAndType(accountId, type)
                .switchIfEmpty(Mono.error(new VaccineException("500", "invalid " + type + " account ")));
    }

    public Mono<Account> validAccount(long accountId) {
        return accountRepository.findById(accountId)
                .switchIfEmpty(Mono.error(new VaccineException("500", "invalid  account ")));
    }


}
