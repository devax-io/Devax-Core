package co.nilin.vaccine.dao;

import co.nilin.vaccine.model.Transaction;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface TransactionRepository extends ReactiveCrudRepository<Transaction, Long> {
    @Query("select * from tx where vial= :vialRefId order by create_date;")
    Flux<Transaction> findTxOfVial(String vialRefId);
}