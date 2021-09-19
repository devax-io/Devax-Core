package co.nilin.vaccine.dao;

import co.nilin.vaccine.model.Account;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
@Component("accountRepo")
public interface AccountRepository extends ReactiveCrudRepository<Account, Long> {
    @Query("select * from account where name = :name; ")
    Mono<Account> findByName(String name);


    @Query("select * from account where id = :id and type= :type; ")
    Mono<Account> findByIdAndType(long id,String type);


    @Query("select * from account where type= :type; ")
    Flux<Account> findByType(String type);
}
