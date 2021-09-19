package co.nilin.vaccine.dao;

import co.nilin.vaccine.model.Account;
import co.nilin.vaccine.model.Lot;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface LotRepository   extends ReactiveCrudRepository<Lot, Long> {

    @Query("select * from lot where ref_id= :refId and manufacture= :manufacture;")
    Mono<Lot> findByRefId(long manufacture,String refId);



}
