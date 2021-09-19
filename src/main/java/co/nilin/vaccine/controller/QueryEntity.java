package co.nilin.vaccine.controller;

import co.nilin.vaccine.dao.*;
import co.nilin.vaccine.dto.CreateVialsRequest;
import co.nilin.vaccine.dto.GeneralResponse;
import co.nilin.vaccine.dto.GenericResponse;
import co.nilin.vaccine.dto.VaccineException;
import co.nilin.vaccine.model.Account;
import co.nilin.vaccine.model.BalanceReport;
import co.nilin.vaccine.model.Lot;
import co.nilin.vaccine.model.Vial;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

@RestController
@RequestMapping("/vaccine/trader/query")
@Slf4j
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

    @GetMapping("/account/{accountId}")
    public Mono<GenericResponse> account(@PathVariable("accountId") long accountId) {

        return accountRepository.findById(accountId)
                .switchIfEmpty(Mono.error(new VaccineException("500", "invalid account")))
                .flatMap(r -> Mono.just(new GenericResponse("200", r)));
    }

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
                                      @PathVariable("manufacture") String manufacture) {

        return vialRepository.findByRefId(manufacture + "_" + vialRefId)
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
        return lotRepository.findByRefId(manufacture, lotRefId)
                .flatMap(l -> vialRepository.findByParentId(l.getId())
                        .map(c -> c.setRefId(c.getRefId().split("_")[1]))
                        .collectList())
                .flatMap(r -> Mono.just(new GenericResponse("200", r)));

    }


    @GetMapping("/tx/{manufacture}/{vialRefId}")
    public Mono<GenericResponse> tx(@PathVariable("manufacture") String manufacture,
                                    @PathVariable("vialRefId") String vialRefId) {
        return txRepository.findTxOfVial(manufacture + "_" + vialRefId)
                .map(c -> c.setVial(c.getVial().split("_")[1]))
                .collectList()
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
        return balanceRepository.findById(accountId)
                .flatMap(r -> Mono.just(new GenericResponse("200", r)));

    }

    @GetMapping("/balances")
    public Mono<GenericResponse> balances() {
        return balanceRepository.findAll()
                .collectList()
                .flatMap(r -> Mono.just(new GenericResponse("200", r)));

    }


}
