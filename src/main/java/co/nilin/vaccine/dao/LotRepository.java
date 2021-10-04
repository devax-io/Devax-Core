package co.nilin.vaccine.dao;

import co.nilin.vaccine.model.Account;
import co.nilin.vaccine.model.Lot;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
@Component("lotRepo")
public interface LotRepository   extends ReactiveCrudRepository<Lot, Long> {

    @Query("select * from lot where ref_id= :refId and manufacture= :manufacture;")
    Mono<Lot> findByRefId(long manufacture,String refId);


    @Query(" select * from lot where  STR_TO_DATE(create_date, '%a %b %e %T IRST %Y') between  (now() - INTERVAL :duration SECOND) and now() ;")
    Flux<Lot> findAllByCreateDate(int duration);



}
