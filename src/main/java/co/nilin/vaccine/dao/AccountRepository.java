package co.nilin.vaccine.dao;

import co.nilin.vaccine.model.Account;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface AccountRepository extends ReactiveCrudRepository<Account, Long> {
    @Query("select * from account where name = :name; ")
    Mono<Account> findByName(String name);
}
