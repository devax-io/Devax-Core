package co.nilin.vaccine.config;

import co.nilin.vaccine.controller.CreateEntity;
import co.nilin.vaccine.dto.CreateVialsRequest;
import co.nilin.vaccine.model.Lot;
import co.nilin.vaccine.model.Vial;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Configuration
@Slf4j
@Profile("server")
public class BotConfiguration {
    @Autowired
    CreateEntity createEntity;

    @PostConstruct
    public void init() {
        log.info("scheduler1 called");
        long baseTime = 1633341429;
        int[] manufactures = {1, 8};
        Flux.interval(Duration.ofSeconds(10))
                .onBackpressureDrop()
                .map(k -> Lot.builder()
                        .refId(UUID.randomUUID().toString())
                        .manufacture(manufactures[ThreadLocalRandom.current().nextInt(0, manufactures.length)])
                        .agent(2)
                        .pod(String.valueOf(baseTime - ThreadLocalRandom.current().nextInt(0, 100)))
                        .exp(String.valueOf(baseTime - ThreadLocalRandom.current().nextInt(0, 100)))
                        .build())
                .flatMap(l -> createEntity.createLot(l).map(b -> {
                    List vials = new ArrayList<>();
                    int lotVials = ThreadLocalRandom.current().nextInt(5, 15);
                    int i = 0;
                    while (i < lotVials) {
                        vials.add(Vial.builder().refId(UUID.randomUUID().toString()).build());
                        i += 1;
                    }
                    log.info(vials.size() + "=========");
                    log.info(l.getRefId());
                    return Tuples.of(l, vials);
                })).log()
                .flatMap(l -> createEntity.createVial(CreateVialsRequest.builder()
                        .manufacture(l.getT1().getManufacture())
                        .parentRefId(l.getT1().getRefId())
                        .vials(l.getT2()).build()))
                .subscribeOn(Schedulers.boundedElastic())
                .subscribe();
    }

//    @Scheduled(fixedRate = 3L)
//    public void insertLot() {
//        log.info("scheduler called");
//        long baseTime=1633341752;
//        createEntity.createLot(Lot.builder()
//                .refId(UUID.randomUUID().toString())
//                .manufacture(1)
//                .agent(2)
//                .pod(String.valueOf(baseTime - ThreadLocalRandom.current().nextInt(0, 100)))
//                .exp(String.valueOf(baseTime - ThreadLocalRandom.current().nextInt(0, 100)))
//                .build()).subscribeOn(Schedulers.parallel()).then().block();
//    }
}
