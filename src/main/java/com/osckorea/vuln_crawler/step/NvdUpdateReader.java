package com.osckorea.vuln_crawler.step;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;

@Slf4j
@Component
@RequiredArgsConstructor
@Getter
@Qualifier("nvdUpdateReader")
public class NvdUpdateReader implements ItemReader<JsonNode> {
    private final RestTemplate restTemplate;
    private final String baseUrl = "https://services.nvd.nist.gov/rest/json/cves/2.0/";
    private ObjectMapper objectMapper = new ObjectMapper();
    private Iterator<JsonNode> vulnerabilitiesIterator;
    private boolean apiCalled = false;

    //테스트 용
    private JsonNode totalResult;

    @Override
    public JsonNode read() throws Exception {
        if (!apiCalled) {
            log.info("==================Read Start==================");
            if (!fetchVulnerabilities()) {
                log.info("==================Read End: No vulnerabilities found==================");
                return null;
            }
            apiCalled = true;
        }

        if (vulnerabilitiesIterator != null && vulnerabilitiesIterator.hasNext()) {
            JsonNode vulnerability = vulnerabilitiesIterator.next();
//            log.info("Processing vulnerability: {}", vulnerability.get("cve").get("id").asText());
            return vulnerability;
        }

        log.info("==================Read End: All vulnerabilities processed==================");
        return null;
    }

    private boolean fetchVulnerabilities() {
        LocalDate today = LocalDate.now();
        LocalDateTime endDate = today.atStartOfDay();
        LocalDateTime startDate = today.minusDays(1).atStartOfDay();

        String url = buildUrl(startDate, endDate);
        log.info("Fetching data from URL: {}", url);

        int maxRetries = 3;
        int retryDelay = 10000; // 10초

        for (int attempt = 0; attempt < maxRetries; attempt++) {
            try {
                String response = restTemplate.getForObject(url, String.class);
                JsonNode rootNode = objectMapper.readTree(response);
                totalResult = rootNode.path("totalResults");
                log.info("totalResult : {}", totalResult);
                JsonNode vulnerabilities = rootNode.path("vulnerabilities");

                if (vulnerabilities.size() > 0) {
                    vulnerabilitiesIterator = vulnerabilities.elements();
                    log.info("Fetched {} vulnerabilities", vulnerabilities.size());
                    return true;
                } else {
                    log.info("No vulnerabilities found in the current time range");
                    return false;
                }
            } catch (Exception e) {
                log.error("Error fetching data from NVD API, attempt {} of {}", attempt + 1, maxRetries, e);
                if (attempt < maxRetries - 1) {
                    log.info("Retrying in {} seconds...", retryDelay / 1000);
                    try {
                        Thread.sleep(retryDelay);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        log.error("Retry interrupted", ie);
                        return false;
                    }
                }
            }
        }
        log.error("Failed to fetch data after {} attempts", maxRetries);
        return false;
    }

    private String buildUrl(LocalDateTime startDate, LocalDateTime endDate) {
        log.info("================startDate {}, endDate {}================", startDate, endDate);
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
        return baseUrl + "?lastModStartDate=" + startDate.format(formatter) + "&lastModEndDate=" + endDate.format(formatter);
    }

}
