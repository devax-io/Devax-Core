package co.nilin.vaccine.dao;

import co.nilin.vaccine.model.Lot;
import co.nilin.vaccine.model.Vial;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
@Repository
@Component("vialRepo")
public interface VialRepository   extends ReactiveCrudRepository<Vial, Long> {

    @Query("select * from vial where ref_id= :refId;")
    Mono<Vial> findByRefId(String refId);

    @Query("select * from vial where parent_id= :parenId;")
    Flux<Vial> findByParentId(long parentId);

}
