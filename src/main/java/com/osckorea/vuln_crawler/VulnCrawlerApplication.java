package com.osckorea.vuln_crawler;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
// Spring Batch 5 이후 이슈 존재
//@EnableBatchProcessing
public class VulnCrawlerApplication {

	public static void main(String[] args) {
		SpringApplication.run(VulnCrawlerApplication.class, args);
	}

}
