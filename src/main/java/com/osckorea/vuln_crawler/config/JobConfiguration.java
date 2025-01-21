package com.osckorea.vuln_crawler.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.osckorea.vuln_crawler.model.MitreCveItem;
import com.osckorea.vuln_crawler.model.NvdCveItem;
import com.osckorea.vuln_crawler.model.NvdCveParseItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class JobConfiguration {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    @Bean
    @Qualifier("nvdCveJob")
    public Job nvdCveJob(@Qualifier("nvdCveStep") Step nvdCveStep) {
        return new JobBuilder("nvdCveJob", jobRepository)
                .incrementer(new RunIdIncrementer()) //job 재실행 가능
                .start(nvdCveStep)
                .build();
    }

    @Bean
    @Qualifier("nvdCveUpdateJob")
    public Job nvdCveUpdateJob(@Qualifier("nvdCveUpdateStep") Step nvdCveUpdateStep) {
        return new JobBuilder("nvdCveUpdateJob", jobRepository)
                .incrementer(new RunIdIncrementer()) //job 재실행 가능
                .start(nvdCveUpdateStep)
                .build();
    }

    @Bean
    @Qualifier("nvdCveParseJob")
    public Job nvdCveParsedJob(@Qualifier("nvdCveParseStep") Step nvdCveParseStep) {
        return new JobBuilder("nvdCveParseJob", jobRepository)
                .incrementer(new RunIdIncrementer()) //job 재실행 가능
                .start(nvdCveParseStep)
                .build();
    }

    @Bean
    @Qualifier("mitreCveJob")
    public Job mitreCveJob(@Qualifier("mitreCveStep") Step mitreCveStep) {
        return new JobBuilder("mitreCveJob", jobRepository)
                .incrementer(new RunIdIncrementer()) //job 재실행 가능
                .start(mitreCveStep)
                .build();
    }

    @Bean
    @Qualifier("mitreCveUpdateJob")
    public Job mitreCveUpdateJob(@Qualifier("mitreCveUpdateStep") Step mitreCveStep) {
        return new JobBuilder("mitreCveUpdateJob", jobRepository)
                .incrementer(new RunIdIncrementer()) //job 재실행 가능
                .start(mitreCveStep)
                .build();
    }

    @Bean
    @Qualifier("nvdCveStep")
    public Step nvdCveStep(@Qualifier("nvdFeedReader") ItemReader<JsonNode> nvdFeedReader,
                           @Qualifier("nvdFeedProcessor") ItemProcessor<JsonNode, NvdCveItem> nvdFeedProcessor,
                           @Qualifier("nvdFeedWriter") ItemWriter<NvdCveItem> nvdFeedWriter) {
        return new StepBuilder("nvdCveStep", jobRepository)
                .<JsonNode, NvdCveItem>chunk(500, transactionManager)
                .reader(nvdFeedReader)
                .processor(nvdFeedProcessor)
                .writer(nvdFeedWriter)
                .build();
    }

    @Bean
    @Qualifier("nvdCveParseStep")
    public Step nvdCveParseStep(@Qualifier("nvdCveParseReader") ItemReader<JsonNode> nvdCveParseReader,
                           @Qualifier("nvdCveParseProcessor") ItemProcessor<JsonNode, NvdCveParseItem> nvdCveParseProcessor,
                           @Qualifier("nvdCveParseWriter") ItemWriter<NvdCveParseItem> nvdCveParseWriter) {
        return new StepBuilder("nvdCveParseStep", jobRepository)
                .<JsonNode, NvdCveParseItem>chunk(500, transactionManager)
                .reader(nvdCveParseReader)
                .processor(nvdCveParseProcessor)
                .writer(nvdCveParseWriter)
                .build();
    }

    @Bean
    @Qualifier("nvdCveUpdateStep")
    public Step nvdCveUpdateStep(@Qualifier("nvdUpdateReader") ItemReader<JsonNode> nvdUpdateReader,
                                 @Qualifier("nvdUpdateProcessor") ItemProcessor<JsonNode, NvdCveItem> nvdUpdateProcessor,
                                 @Qualifier("nvdUpdateWriter") ItemWriter<NvdCveItem> nvdUpdateWriter) {
        return new StepBuilder("nvdCveUpdateStep", jobRepository)
                .<JsonNode, NvdCveItem>chunk(500, transactionManager)
                .reader(nvdUpdateReader)
                .processor(nvdUpdateProcessor)
                .writer(nvdUpdateWriter)
                .build();
    }

    @Bean
    @Qualifier("mitreCveStep")
    public Step mitreCveStep(@Qualifier("mitreFeedReader") ItemReader<JsonNode> mitreFeedReader,
                                 @Qualifier("mitreFeedProcessor") ItemProcessor<JsonNode, MitreCveItem> mitreFeedProcessor,
                                 @Qualifier("mitreFeedWriter") ItemWriter<MitreCveItem> mitreFeedWriter) {
        return new StepBuilder("mitreCveStep", jobRepository)
                .<JsonNode, MitreCveItem>chunk(500, transactionManager)
                .reader(mitreFeedReader)
                .processor(mitreFeedProcessor)
                .writer(mitreFeedWriter)
                .build();
    }

    @Bean
    @Qualifier("mitreCveUpdateStep")
    public Step mitreCveUpdateStep(@Qualifier("mitreUpdateReader") ItemReader<JsonNode> mitreUpdateReader,
                             @Qualifier("mitreUpdateProcessor") ItemProcessor<JsonNode, MitreCveItem> mitreUpdateProcessor,
                             @Qualifier("mitreUpdateWriter") ItemWriter<MitreCveItem> mitreUpdateWriter) {
        return new StepBuilder("mitreCveUpdateStep", jobRepository)
                .<JsonNode, MitreCveItem>chunk(500, transactionManager)
                .reader(mitreUpdateReader)
                .processor(mitreUpdateProcessor)
                .writer(mitreUpdateWriter)
                .build();
    }
}
