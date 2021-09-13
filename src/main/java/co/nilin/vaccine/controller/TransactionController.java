package co.nilin.vaccine.controller;

import co.nilin.vaccine.dao.*;
import co.nilin.vaccine.dto.GeneralResponse;

import co.nilin.vaccine.dto.TransferRequest;
import co.nilin.vaccine.model.Transaction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.websocket.server.PathParam;
import java.time.Duration;
import java.util.Date;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/vaccine/trader/transfer")
public class TransactionController {
    @Autowired
    AccountRepository accountRepository;

    @Autowired
    TransactionRepository transactionRepository;

    @Autowired
    BalanceRepository balanceRepository;

    @Autowired
    VialRepository vialRepository;

    @Autowired
    LotRepository lotRepository;

    @RequestMapping("/{from}/to/{to}")
    public Mono<GeneralResponse> transfer(@RequestBody TransferRequest request,
                                          @PathVariable("from") long src,
                                          @PathVariable("to") long dest) {

        long[] total_value = {0};
        request.getTransfers().stream().map(b -> total_value[0] += b.getValue());

        return accountRepository.findById(src)
                .switchIfEmpty(Mono.error(new RuntimeException("invalid sender")))
                .zipWhen(d -> accountRepository.findById(dest)
                        .switchIfEmpty(Mono.error(new RuntimeException("invalid receiver"))))
                .zipWhen(b -> balanceRepository.findBalance(src))
                .flatMap(gl -> gl.getT2().getBalance() < total_value[0] ? Mono.error(new RuntimeException("low balance")) : Mono.just(gl))
                .flatMap(p -> Mono.just(request.getTransfers())
                        .flatMapMany(Flux::fromIterable)
                        .flatMap(c -> Mono.just(c)
                                .zipWhen(k ->
                                        vialRepository.findById(c.getVial())
                                                .log()
                                                .switchIfEmpty(Mono.error(new RuntimeException("invalid vial")))
                                )
                                .flatMap(r -> r.getT2().getCurrentOwner() != src ? Mono.error(new RuntimeException("unauthorize")) : Mono.just(r))
                                .flatMap(v -> transactionRepository.save(Transaction.builder().from(src).to(dest).value(v.getT1().getValue()).vial(v.getT1().getVial()).createDate(new Date().toString()).build())
                                        .zipWhen(vt -> vialRepository.save(v.getT2().setCurrentOwner(dest)))
                                        .flatMap(vt -> balanceRepository.updateBalance(dest, vt.getT1().getValue(), new Date().toString())
                                                .map(t -> {
                                                    log.info("===========================");
                                                   return balanceRepository.updateBalance(src, -(vt.getT1().getValue()), new Date().toString());
                                                })
                                        )
                                )).collectList()
                ).flatMap(t -> Mono.just(new GeneralResponse("done")));


    }


}
