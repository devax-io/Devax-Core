package co.nilin.vaccine.service;

import co.nilin.vaccine.controller.CreateEntity;
import co.nilin.vaccine.controller.QueryEntity;
import co.nilin.vaccine.model.Lot;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.annotation.Schedules;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.function.Tuples;

import javax.management.Query;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Service
@Slf4j
public class ReactiveScheduledPushMessages implements InitializingBean {

    private SimpMessagingTemplate simpMessagingTemplate;
    @Autowired
    QueryEntity queryEntity;
    @Autowired
    CreateEntity createEntity;

    @Autowired
    public ReactiveScheduledPushMessages(SimpMessagingTemplate simpMessagingTemplate) {
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
//        Flux.interval(Duration.ofSeconds(1L))
//                // discard the incoming Long, replace it by an OutputMessage
//                .flatMap(n -> queryEntity.accounts())
//                .subscribe(message -> simpMessagingTemplate.convertAndSend("/topic/accounts", message));

        int schedule = 3;
        Flux.interval(Duration.ofSeconds(schedule))
                // discard the incoming Long, replace it by an OutputMessage
                .flatMap(t -> queryEntity.lots(schedule))
                .subscribe(message -> simpMessagingTemplate.convertAndSend("/topic/lots", message));


    }


}