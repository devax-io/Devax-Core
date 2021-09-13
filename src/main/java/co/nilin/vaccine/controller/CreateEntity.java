package co.nilin.vaccine.controller;

import co.nilin.vaccine.dao.AccountRepository;
import co.nilin.vaccine.dao.BalanceRepository;
import co.nilin.vaccine.dao.LotRepository;
import co.nilin.vaccine.dao.VialRepository;
import co.nilin.vaccine.dto.CreateVialsRequest;
import co.nilin.vaccine.dto.GeneralResponse;
import co.nilin.vaccine.dto.VaccineException;
import co.nilin.vaccine.model.Account;
import co.nilin.vaccine.model.BalanceReport;
import co.nilin.vaccine.model.Lot;
import co.nilin.vaccine.model.Vial;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Date;
import java.util.UUID;

@RestController
@RequestMapping("/vaccine/trader/create")
@Slf4j
public class CreateEntity {

    @Autowired
    AccountRepository accountRepository;
    @Autowired
    LotRepository lotRepository;
    @Autowired
    VialRepository vialRepository;
    @Autowired
    BalanceRepository balanceRepository;

    @RequestMapping("/account")
    public Mono<GeneralResponse> createAccount(@RequestBody Account account) {
        account.setAddress(UUID.randomUUID().toString());
        account.setCreateDate(new Date().toString());
        return accountRepository.save(account)
                .zipWhen(a -> balanceRepository.save(BalanceReport.builder().owner(a.getId()).balance(0).lastModified(new Date().toString()).build()))
                .flatMap(u -> Mono.just(new GeneralResponse("account " + account.getId() + " created")));

    }

    @RequestMapping("/lot")
    public Mono<GeneralResponse> createLot(@RequestBody Lot lot) {
        lot.setCreateDate(new Date().toString());
        return accountRepository.findById(lot.getAgent())
                .switchIfEmpty(Mono.error(new RuntimeException("invalid agent")))
                .flatMap(ac -> lotRepository.save(lot))
                .flatMap(u -> Mono.just(new GeneralResponse("account " + lot.getId() + " created")));

    }

    @RequestMapping("/vial")
    public Mono<GeneralResponse> createVial(@RequestBody CreateVialsRequest vials) {
        return lotRepository.findById(vials.getParentId())
                .switchIfEmpty(Mono.error(new VaccineException("500", "invalid lot")))
                .flatMap(p -> Mono.just(vials.getVials())
                        .flatMapMany(Flux::fromIterable)
                        .flatMap(v -> vialRepository.save(Vial.builder()
                                .parentId(vials.getParentId())
                                .refId(v.getRefId())
                                .currentOwner(p.getAgent())
                                .createDate(new Date().toString()).build())
                                .zipWhen(vc -> {
                                    log.info(p.getAgent()+"");
                                    log.info("================");
                                    return balanceRepository.updateBalance(p.getAgent(),1.0,new Date().toString());
                                }).log())
                        .collectList()
                )
                .flatMap(u -> Mono.just(new GeneralResponse("vials created")));

    }


}
