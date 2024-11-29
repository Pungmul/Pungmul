package pungmul.pungmul.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

//@RequiredArgsConstructor
//@Configuration
//public class TaskSchedulerConfig {
//
//    @Bean
//    public TaskScheduler taskScheduler() {
//        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
//        scheduler.setPoolSize(10); // 동시 실행 가능한 작업 수
//        scheduler.setThreadNamePrefix("TaskScheduler-");
//        scheduler.initialize();
//        return scheduler;
//    }
//}
