package co.nilin.vaccine.controller;

import co.nilin.vaccine.dao.*;
import co.nilin.vaccine.dto.GeneralResponse;

import co.nilin.vaccine.model.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Date;
import java.util.List;

@RestController
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
    public Mono<GeneralResponse> transfer(@RequestBody List<Transaction> request,
                                          @RequestParam("from") long src,
                                          @RequestParam("to") long dest) {

        long[] total_value = {0};
        request.stream().map(b -> total_value[0] += b.getValue());

        return accountRepository.findById(src)
                .switchIfEmpty(Mono.error(new RuntimeException("invalid sender")))
                .zipWhen(d -> accountRepository.findById(dest)
                        .switchIfEmpty(Mono.error(new RuntimeException("invalid receiver"))))
                .zipWhen(b -> balanceRepository.findBalance(src))
                .map(gl -> gl.getT2().getBalance() < total_value[0] ? Mono.error(new RuntimeException("low balance")) : gl)
                .map(p -> Mono.just(request).flatMapMany(Flux::fromIterable)
                        .flatMap(c -> vialRepository.findById(c.getId())
                                .switchIfEmpty(Mono.error(new RuntimeException("invalid vial")))
                                .map(r -> r.getParentId() != src ? Mono.error(new RuntimeException("unauthorize")) : r)
                                .map(d -> c)
                        )
                        .map(c -> c.setFrom(src).setTo(dest).setCreateDate(new Date().toString()))
                        .map(t -> transactionRepository.save(t)
                                .zipWhen(ct -> balanceRepository.findBalance(src)
                                        .map(c -> balanceRepository.save(c.setBalance(c.getBalance() - t.getValue()).setLastModified(new Date().toString()))))
                                .delayElement(Duration.ofSeconds(5))
                                .zipWhen(ct -> balanceRepository.findBalance(dest)
                                        .map(c -> balanceRepository.save(c.setBalance(c.getBalance() + t.getValue()).setLastModified(new Date().toString()))))
                        )


                ).flatMap(t -> Mono.just(new GeneralResponse("done")));

    }


}
