package com.osckorea.vuln_crawler.step;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.osckorea.vuln_crawler.model.MitreCveItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Qualifier("mitreFeedProcessor")
@Slf4j
public class MitreUpdateProcessor implements ItemProcessor<JsonNode, MitreCveItem> {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public MitreCveItem process(JsonNode jsonNode) throws Exception {
        log.info("====================Processor 시작====================");
        MitreCveItem item = new MitreCveItem();

        item.setCveName(jsonNode.path("cveMetadata").path("cveId").asText());

        // 영어 설명 설정 (항상 존재한다고 가정)
        JsonNode descriptions = jsonNode.path("containers").path("cna").path("descriptions");
        if (descriptions.isArray() && descriptions.size() > 0) {
            item.setDescription(descriptions.get(0).path("value").asText());
        }

        // Problem Types 설정
        JsonNode problemTypes = jsonNode.path("containers").path("cna").path("problemTypes");
        item.setProblemTypes(objectMapper.writeValueAsString(problemTypes));

        // CVSS 점수 및 심각도 설정
        JsonNode metrics = jsonNode.path("containers").path("cna").path("metrics");
        if (metrics.isArray() && metrics.size() > 0) {
            JsonNode cvssV3_1 = metrics.get(0).path("cvssV3_1");
            JsonNode cvssV3_0 = metrics.get(0).path("cvssV3_0");

            if (!cvssV3_1.isMissingNode()) {
                item.setBaseScore(cvssV3_1.path("baseScore").asText());
                item.setBaseSeverity(cvssV3_1.path("baseSeverity").asText());
            } else if (!cvssV3_0.isMissingNode()) {
                item.setBaseScore(cvssV3_0.path("baseScore").asText());
                item.setBaseSeverity(cvssV3_0.path("baseSeverity").asText());
            }
        }

        // CNA JSON 설정
        item.setCveJson(objectMapper.writeValueAsString(jsonNode.path("containers").path("cna")));

        // ADP JSON 설정 (전체 ADP 배열)
        JsonNode adpNode = jsonNode.path("containers").path("adp");
        item.setCveAdpJson(objectMapper.writeValueAsString(adpNode));

        return item;
    }
}
