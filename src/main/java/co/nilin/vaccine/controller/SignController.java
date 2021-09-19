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
import org.springframework.web.bind.annotation.*;
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

//    @RequestMapping("/{act}/{actId}")
//    public Mono<GeneralResponse> sign(@RequestBody Sign signRequest,
//                                      @PathVariable("act") ActType act,
//                                      @PathVariable("actId") long actId)  {
//
//
//          validAccount(signRequest.getId())
//                  .flatMap(a -> {
//                      try {
//                          return chooseRepo()
//                      } catch (ClassNotFoundException e) {
//                          e.printStackTrace();
//                      }
//                  })
//
//
//
//    })
//
//
//}



    public Mono<Account> validAccount(long accountId, String type) {
        return accountRepository.findByIdAndType(accountId, type)
                .switchIfEmpty(Mono.error(new VaccineException(String.format("500", "invalid account {}", type))));
    }

    public Mono<Account> validAccount(long accountId) {
        return accountRepository.findById(accountId)
                .switchIfEmpty(Mono.error(new VaccineException("500", "invalid account")));
    }
}
