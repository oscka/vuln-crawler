package com.osckorea.vuln_crawler.config;

import com.osckorea.vuln_crawler.model.NvdCveItemTest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.io.File;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class JobConfiguration {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    @Bean
    public Job nvdCveJob(Step nvdCveStep) {
        return new JobBuilder("nvdCveJob", jobRepository)
                .start(nvdCveStep)
                .build();
    }

    @Bean
    public Step nvdCveStep(ItemReader<File> nvdFeedReader,
                           ItemProcessor<File, NvdCveItemTest> nvdFeedProcessor,
                           ItemWriter<NvdCveItemTest> nvdFeedWriter) {
        return new StepBuilder("nvdCveStep", jobRepository)
                .<File, NvdCveItemTest>chunk(100, transactionManager)
                .reader(nvdFeedReader)
                .processor(nvdFeedProcessor)
                .writer(nvdFeedWriter)
                .build();
    }


//    @Bean
//    public Job job(JobRepository jobRepository, Step step) {
//        return new JobBuilder("job-chunk", jobRepository) //job name 설정
//                .start(step)
//                .build();
//    }
//
//    @Bean
//    public Step step(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager) {
//        final ItemReader<Integer> itemReader = new ItemReader<>() {
//
//            private int count = 0;
//            @Override
//            public Integer read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
//                count++;
//                log.info("count {}", count);
//                if(count == 15) return null;
//                return count;
//            }
//        };
//
//        return new StepBuilder("step", jobRepository)
//                .chunk(10, platformTransactionManager)
//                .reader(itemReader)
////                .processor()
//                .writer(read -> {})
//                .build();
//    }
}
