package co.nilin.vaccine.dao;

import co.nilin.vaccine.model.BalanceReport;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.Date;

@Repository
@Component("balanceRepo")
public interface BalanceRepository extends ReactiveCrudRepository<BalanceReport, Long> {

    @Query("select * from balance_report where owner=:owner ")
    Mono<BalanceReport> findBalance(long owner);

    @Modifying()
    @Query("update balance_report set balance = balance + :value , last_modified = :lastModified where owner = :owner ")
    Mono<BalanceReport> updateBalance(long owner,double value,String lastModified);
}
