package co.nilin.vaccine.dao;

import co.nilin.vaccine.model.Lot;
import co.nilin.vaccine.model.Sign;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SignRepository  extends ReactiveCrudRepository<Sign, Long> {
}
