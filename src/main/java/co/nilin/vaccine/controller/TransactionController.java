package co.nilin.vaccine.controller;

import co.nilin.vaccine.dao.*;
import co.nilin.vaccine.dto.AccountType;
import co.nilin.vaccine.dto.GeneralResponse;

import co.nilin.vaccine.dto.TransferRequest;
import co.nilin.vaccine.dto.VaccineException;
import co.nilin.vaccine.model.Account;
import co.nilin.vaccine.model.BalanceReport;
import co.nilin.vaccine.model.Transaction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Date;

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
                                          @PathVariable("to") long dest) throws VaccineException {

//        long[] total_value = {0};
//        request.getTransfers().stream().map(b -> total_value[0] += b.getValue());

        if (src == dest)
            throw new VaccineException("500", "invalid transfer (sender is equal receiver)");

        return validAccount(request.getManufacture(), AccountType.manufacture.name())
                        .flatMap(m -> accountRepository.findById(src))
                        .switchIfEmpty(Mono.error(new VaccineException("500", "invalid sender")))
                        .flatMap(a -> a.getType().equals(AccountType.end_user.name()) ? Mono.error(new VaccineException("500", "invalid transfer(sender is end user)")) : Mono.just(a))
                        .zipWhen(d -> accountRepository.findById(dest)
                                .switchIfEmpty(Mono.error(new VaccineException("500", "invalid receiver")))
                                .flatMap(a -> a.getType().equals(AccountType.end_user.name()) ? Mono.error(new VaccineException("500", "invalid transfer(receiver is end user)")) : Mono.just(a))
                        )
                        .zipWhen(b -> balanceRepository.findBalance(src))
                        // .flatMap(gl -> gl.getT2().getBalance() < total_value[0] ? Mono.error(new RuntimeException("low balance")) : Mono.just(gl))
                        .flatMap(p -> Mono.just(request.getTransfers())
                                .flatMapMany(Flux::fromIterable)
                                .flatMap(c -> Mono.just(c)
                                        .zipWhen(k ->
                                                vialRepository.findByRefId(request.getManufacture() + "_" + c.getVial())
                                                        .log()
                                                        .switchIfEmpty(Mono.empty())
                                        )
                                        .flatMap(r -> {
                                            if (r.getT2().getId() != null)
                                                if (r.getT2().getCurrentOwner() != src)
                                                    return Mono.empty();
                                                else
                                                    return Mono.just(r);
                                            else
                                                return Mono.empty();
                                        })
                                        .flatMap(v ->
                                        {
                                            if (v.getT2().getId() != null)
                                                return transactionRepository.save(Transaction.builder().from(src).to(dest).value(1).vial(request.getManufacture()+"_"+v.getT1().getVial()).createDate(new Date().toString()).build())
                                                        .zipWhen(vt -> vialRepository.save(v.getT2().setCurrentOwner(dest)))
                                                        .flatMap(vt -> {
                                                            Mono<BalanceReport> b = balanceRepository.updateBalance(dest, 1, new Date().toString());
                                                            Mono<BalanceReport> b1 = balanceRepository.updateBalance(src, -1, new Date().toString());
                                                            return Mono.zip(b, b1);
                                                        });
                                            else
                                                return Mono.empty();
                                        })).collectList()
                        ).flatMap(t -> Mono.just(new GeneralResponse("200", "done")));


    }

    public Mono<Account> validAccount(long accountId, String type) {
        return accountRepository.findByIdAndType(accountId, type)
                .switchIfEmpty(Mono.error(new VaccineException("500", "invalid "+type+ " account ")));
    }
}
