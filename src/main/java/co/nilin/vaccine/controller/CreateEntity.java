package co.nilin.vaccine.controller;

import co.nilin.vaccine.dao.AccountRepository;
import co.nilin.vaccine.dao.LotRepository;
import co.nilin.vaccine.dao.VialRepository;
import co.nilin.vaccine.dto.CreateVialsRequest;
import co.nilin.vaccine.dto.GeneralResponse;
import co.nilin.vaccine.dto.VaccineException;
import co.nilin.vaccine.model.Account;
import co.nilin.vaccine.model.Lot;
import co.nilin.vaccine.model.Vial;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.xml.crypto.Data;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/vaccine/trader/create")
public class CreateEntity {

    @Autowired
    AccountRepository accountRepository;
    @Autowired
    LotRepository lotRepository;
    @Autowired
    VialRepository vialRepository;

    @RequestMapping("/account")
    public Mono<GeneralResponse> createAccount(Account account) {
        account.setAddress(UUID.randomUUID().toString());
        account.setCreateDate(new Date().toString());
        return accountRepository.save(account)
                .flatMap(u -> Mono.just(new GeneralResponse("account " + account.getId() + " created")));

    }

    @RequestMapping("/lot")
    public Mono<GeneralResponse> createLot(Lot lot) {
        lot.setCreateDate(new Date().toString());
        return accountRepository.findById(lot.getAgent())
                .switchIfEmpty(Mono.error(new VaccineException(500, "invalid agent")))
                .flatMap(ac -> lotRepository.save(lot))
                .flatMap(u -> Mono.just(new GeneralResponse("account " + lot.getId() + " created")));

    }

    @RequestMapping("/vial")
    public Mono<GeneralResponse> createVial(CreateVialsRequest vials) {
        return lotRepository.findById(vials.getAgent())
                .switchIfEmpty(Mono.error(new VaccineException(500, "invalid lot")))
                .map(p -> Mono.just(vials.getVials())
                        .flatMapMany(Flux::fromIterable)
                        .map(c -> c.setCreateDate(new Date().toString()).setParentId(vials.getParentId()))
                        .flatMap(v -> vialRepository.save(v)))
                .flatMap(u -> Mono.just(new GeneralResponse("vials created")));

    }


}
