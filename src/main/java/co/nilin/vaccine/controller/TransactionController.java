package co.nilin.vaccine.controller;

import co.nilin.vaccine.dao.AccountRepository;
import co.nilin.vaccine.dao.BalanceRepository;
import co.nilin.vaccine.dao.TransactionRepository;
import co.nilin.vaccine.dto.GeneralResponse;
import co.nilin.vaccine.dto.VaccineException;
import co.nilin.vaccine.model.Account;
import co.nilin.vaccine.model.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import javax.swing.plaf.basic.BasicLookAndFeel;
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

    @RequestMapping("/{from}/to/{to}")
    public Mono<GeneralResponse> transfer(List<Transaction> request,
                                          @RequestParam("from") long src,
                                          @RequestParam("to") long dest) {

        long[] total_value = {0};
        request.stream().map(b -> total_value[0] += b.getValue());



        return accountRepository.findById(src)
                .switchIfEmpty(Mono.error(new VaccineException(500, "invalid sender")))
                .zipWhen(d -> accountRepository.findById(dest)
                        .switchIfEmpty(Mono.error(new VaccineException(500, "invalid receiver"))))
                .zipWhen(b->balanceRepository.findBalance(src))
                .map(gl -> checkBalance(gl, request))
                .map(p -> Mono.just(request).flatMapMany(Flux::fromIterable)
                        .map(c -> c.setFrom(src).setTo(dest).setCreateDate(new Date().toString()))
                        .map(t -> transactionRepository.save(t)))
                .flatMap(t -> Mono.just(new GeneralResponse()));

    }

    public Tuple2<Account, Account> checkBalance(Tuple2<Account, Account> gl, List<Transaction> request) {
        long[] req = {0};
        request.stream().map(b -> req[0] += b.getValue());
        return Mono.just(gl).zipWhen(balanceRepository.findBalance(gl.getT2().getId()))
                .map(b -> b < req[0] ? Mono.error(new VaccineException(500, "low balance")) : Mono.empty());


    }

}
