package co.nilin.vaccine.config;

import co.nilin.vaccine.controller.CreateEntity;
import co.nilin.vaccine.model.Lot;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Configuration
@Slf4j
@Profile("server")
public class ApplicationConfiguration {
@Autowired
    CreateEntity createEntity;
    @PostConstruct
    public void init() {
        log.info("scheduler1 called");
       long baseTime=1633341429;
       int[] manufactures={1,8};
        Flux.interval(Duration.ofSeconds(5))
                .onBackpressureDrop()
                .flatMap( t->createEntity.createLot(Lot.builder()
                        .refId(UUID.randomUUID().toString())
                        .manufacture(manufactures[ThreadLocalRandom.current().nextInt(0, 2)])
                        .agent(2)
                        .pod(String.valueOf(baseTime - ThreadLocalRandom.current().nextInt(0, 100)))
                        .exp(String.valueOf(baseTime - ThreadLocalRandom.current().nextInt(0, 100)))
                        .build()))
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
