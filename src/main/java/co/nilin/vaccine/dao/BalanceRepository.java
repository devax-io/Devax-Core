package co.nilin.vaccine.dao;

import co.nilin.vaccine.model.BalanceReport;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface BalanceRepository extends ReactiveCrudRepository<BalanceReport, Long> {

    @Query("select balance from balance_report where owner=:owner ")
    Mono<Double> findBalance(long owner);
}
