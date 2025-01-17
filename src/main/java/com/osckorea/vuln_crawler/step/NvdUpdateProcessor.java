package com.osckorea.vuln_crawler.step;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.osckorea.vuln_crawler.model.NvdCveItem;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@Qualifier("nvdUpdateProcessor")
@RequiredArgsConstructor
public class NvdUpdateProcessor implements ItemProcessor<JsonNode, NvdCveItem>  {

    private static final Logger log = LoggerFactory.getLogger(NvdUpdateProcessor.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    public NvdCveItem process(JsonNode cveJson) throws Exception {
        NvdCveItem item = new NvdCveItem();

        // CVE ID 설정
        item.setCveName(safeGetText(cveJson.path("cve"), "id"));

        // 설명 설정
        item.setDescription(getDescription(cveJson.path("cve")));

        // CVSS 점수 및 심각도 설정
        setBaseScoreAndSeverity(item, cveJson.path("cve"));

        // 원본 JSON 저장
        item.setNvdUpdatedJson(objectMapper.writeValueAsString(cveJson));

        return item;
    }

    // 안전하게 텍스트 값을 가져오는 유틸리티 함수
    private String safeGetText(JsonNode node, String fieldName) {
        JsonNode valueNode = node.path(fieldName);
        return valueNode.isMissingNode() || valueNode.isNull() ? null : valueNode.asText();
    }

    // CVE 설명 가져오기
    private String getDescription(JsonNode cveJson) {
        JsonNode descriptions = cveJson.path("descriptions");
        if (descriptions.isArray()) {
            for (JsonNode desc : descriptions) {
                if ("en".equals(safeGetText(desc, "lang"))) {
                    return safeGetText(desc, "value");
                }
            }
        }
        return "No description available.";
    }

    // CVSS 점수 및 심각도 설정
    private void setBaseScoreAndSeverity(NvdCveItem item, JsonNode cveJson) {

        JsonNode metrics = cveJson.path("metrics");

        // CVSS v3.1
        JsonNode cvssV31 = getFirstElement(metrics, "cvssMetricV31");
        if (cvssV31 != null) {
            setCvssData(item, cvssV31);
            return;
        }

        // CVSS v2.0
        JsonNode cvssV2 = getFirstElement(metrics, "cvssMetricV2");
        if (cvssV2 != null) {
            setCvssData(item, cvssV2);
        }
    }

    // 첫 번째 요소 가져오기
    private JsonNode getFirstElement(JsonNode parentNode, String fieldName) {
        JsonNode arrayNode = parentNode.path(fieldName);
        return arrayNode.isArray() && arrayNode.size() > 0 ? arrayNode.get(0) : null;
    }

    // CVSS 데이터 설정
    private void setCvssData(NvdCveItem item, JsonNode cvssNode) {
        JsonNode cvssData = cvssNode.path("cvssData");
        // baseScore를 문자열로 저장
        item.setBaseScore(safeGetText(cvssData, "baseScore"));
        // baseSeverity 설정
        item.setBaseSeverity(safeGetText(cvssData, "baseSeverity"));
    }
}
