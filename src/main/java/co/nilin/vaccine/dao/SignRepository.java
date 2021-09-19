package co.nilin.vaccine.dao;

import co.nilin.vaccine.model.Lot;
import co.nilin.vaccine.model.Sign;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
@Component("signRepo")

public interface SignRepository  extends ReactiveCrudRepository<Sign, Long> {

    @Query("select * from sign where agent= :agent;")
    Flux<Sign> findByAgent(long agent);

    @Query("select * from sign where act= :act and type= :type;")
    Flux<Sign> findByActAndType(long act,String type);

}
