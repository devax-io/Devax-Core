//package co.nilin.vaccine.config;
//
//import co.nilin.vaccine.dao.LotRepository;
//import co.nilin.vaccine.dao.TransactionRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//public class RepoConfig {
//    @Autowired
//    TransactionRepository txRepo;
//    @Autowired
//    LotRepository lotRepo;
//
//    @Bean("txRepo")
//    public TransactionRepository txRepo(){
//        return txRepo;
//    }
//
//
//    @Bean("lotRepo")
//    public LotRepository lotRepo(){
//        return lotRepo;
//    }
//}
