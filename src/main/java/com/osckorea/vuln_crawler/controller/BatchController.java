package com.osckorea.vuln_crawler.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/batch")
public class BatchController {

    private final JobLauncher jobLauncher;

    @Qualifier("nvdCveUpdateJob")
    private final Job nvdCveUpdateJob;
    @Qualifier("nvdCveJob")
    private final Job nvdCveJob;
    @Qualifier("nvdCveParseJob")
    private final Job nvdCveParseJob;
    @Qualifier("mitreCveJob")
    private final Job mitreCveJob;
    @Qualifier("mitreCveUpdateJob")
    private final Job mitreCveUpdateJob;

    @PostMapping("/nvd-cve")
    public ResponseEntity<String> runNvdCveJob() {
        try {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("timestamp", System.currentTimeMillis()) // 고유한 파라미터 (job 재실행을 위함)
                    .toJobParameters();
            JobExecution jobExecution = jobLauncher.run(nvdCveJob, jobParameters);
            return ResponseEntity.ok(jobExecution.getStatus().name());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @PostMapping("/nvd-update")
    public ResponseEntity<String> runNvdCveUpdateJob() {
        try {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("timestamp", System.currentTimeMillis()) // 고유한 파라미터 (job 재실행을 위함)
                    .toJobParameters();
            JobExecution jobExecution = jobLauncher.run(nvdCveUpdateJob, jobParameters);
            return ResponseEntity.ok(jobExecution.getStatus().name());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @PostMapping("/nvd-parse")
    public ResponseEntity<String> runNvdCveParseJob() {
        try {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("timestamp", System.currentTimeMillis()) // 고유한 파라미터 (job 재실행을 위함)
                    .toJobParameters();
            JobExecution jobExecution = jobLauncher.run(nvdCveParseJob, jobParameters);
            return ResponseEntity.ok(jobExecution.getStatus().name());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @PostMapping("/mitre-cve")
    public ResponseEntity<String> runMitreCveJob() {
        try {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("timestamp", System.currentTimeMillis()) // 고유한 파라미터 (job 재실행을 위함)
                    .toJobParameters();
            JobExecution jobExecution = jobLauncher.run(mitreCveJob, jobParameters);
            return ResponseEntity.ok(jobExecution.getStatus().name());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @PostMapping("/mitre-update")
    public ResponseEntity<String> runMitreCveUpdateJob() {
        try {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("timestamp", System.currentTimeMillis()) // 고유한 파라미터 (job 재실행을 위함)
                    .toJobParameters();
            JobExecution jobExecution = jobLauncher.run(mitreCveUpdateJob, jobParameters);
            return ResponseEntity.ok(jobExecution.getStatus().name());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }
}
