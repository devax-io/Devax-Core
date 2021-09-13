package co.nilin.vaccine.dao;

import co.nilin.vaccine.model.Lot;
import co.nilin.vaccine.model.Vial;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface VialRepository   extends ReactiveCrudRepository<Vial, Long> {
}
