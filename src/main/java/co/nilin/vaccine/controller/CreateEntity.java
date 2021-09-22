package co.nilin.vaccine.controller;

import co.nilin.vaccine.dao.AccountRepository;
import co.nilin.vaccine.dao.BalanceRepository;
import co.nilin.vaccine.dao.LotRepository;
import co.nilin.vaccine.dao.VialRepository;
import co.nilin.vaccine.dto.AccountType;
import co.nilin.vaccine.dto.CreateVialsRequest;
import co.nilin.vaccine.dto.GeneralResponse;
import co.nilin.vaccine.dto.VaccineException;
import co.nilin.vaccine.model.Account;
import co.nilin.vaccine.model.BalanceReport;
import co.nilin.vaccine.model.Lot;
import co.nilin.vaccine.model.Vial;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.*;
import org.yaml.snakeyaml.util.EnumUtils;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import java.time.Duration;
import java.util.Date;
import java.util.UUID;

@RestController
@RequestMapping("/vaccine/trader/create")
@Slf4j
@CrossOrigin

public class CreateEntity {

    @Autowired
    AccountRepository accountRepository;
    @Autowired
    LotRepository lotRepository;
    @Autowired
    VialRepository vialRepository;
    @Autowired
    BalanceRepository balanceRepository;

    @PostMapping("/account")
    @SendTo("/topic/account")
    public Mono<GeneralResponse> createAccount(@RequestBody Account account) {
        try {
            EnumUtils.findEnumInsensitiveCase(AccountType.class, account.getType());
        } catch (Exception e) {
            throw new VaccineException("500", "invalid account type");
        }
        account.setAddress(UUID.randomUUID().toString());
        account.setCreateDate(new Date().toString());
        return accountRepository.save(account)
                .onErrorResume(e -> Mono.error(new VaccineException("500", e.getMessage())))
                .zipWhen(a -> balanceRepository.save(BalanceReport.builder().owner(a.getId()).balance(0).lastModified(new Date().toString()).build()))
                .flatMap(u -> Mono.just(new GeneralResponse("200", "account " + account.getId() + " created")));

    }


    @PostMapping("/lot")
    public Mono<GeneralResponse> createLot(@RequestBody Lot lot) {
        lot.setCreateDate(new Date().toString());
        return validAccount(lot.getManufacture(), AccountType.manufacture.name())
                .flatMap(m ->validAccount(lot.getAgent(),AccountType.level1.name()))
                .flatMap(ac -> lotRepository.save(lot))
                .onErrorResume(e -> Mono.error(new VaccineException("500", e.getMessage())))
                .flatMap(u -> Mono.just(new GeneralResponse("200", "lot " + lot.getId() + " created")));

    }

    @PostMapping("/vial")
    public Mono<GeneralResponse> createVial(@RequestBody CreateVialsRequest request) {
        if (request.getManufacture() == 0 && request.getParentRefId().isEmpty() && request.getParentRefId() == null)
            throw new VaccineException("500", "manufacture and lot reference Id are required");
        return validAccount(request.getManufacture(), AccountType.manufacture.name())
                .flatMap(m -> lotRepository.findByRefId(request.getManufacture(), request.getParentRefId()))
                .switchIfEmpty(Mono.error(new VaccineException("500", "invalid lot")))
                .flatMap(p -> Mono.just(request.getVials())
                        .flatMapMany(Flux::fromIterable)
                        .flatMap(v -> vialRepository.save(Vial.builder()
                                .parentId(p.getId())
                                .refId(p.getManufacture() + "_" + v.getRefId())
                                .currentOwner(p.getAgent())
                                .createDate(new Date().toString()).build())
                                .onErrorContinue((throwable, o) ->
                                        log.error("Error while create vial {}. Cause: {}", o, throwable.getMessage())
                                )
                                .flatMap(vc ->
                                {
                                    if (vc.getId() != null)

                                        return balanceRepository.updateBalance(p.getAgent(), 1.0, new Date().toString());
                                    else
                                        return Mono.empty();
                                })
                        ).collectList()
                )
                .flatMap(u -> Mono.just(new GeneralResponse("200", "vials created")));

    }

    public Mono<Account> validAccount(long accountId, String type) {
        return accountRepository.findByIdAndType(accountId, type)
                .switchIfEmpty(Mono.error(new VaccineException("500",  "invalid "+type+ " account ")));
    }
}
