package com.osckorea.vuln_crawler.step;


import com.fasterxml.jackson.databind.JsonNode;
import com.osckorea.vuln_crawler.model.NvdCveItem;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
@Qualifier("nvdFeedProcessor")
public class NvdFeedProcessor implements ItemProcessor<JsonNode, NvdCveItem> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public NvdCveItem process(JsonNode jsonNode) throws Exception {
        NvdCveItem item = new NvdCveItem();
        item.setCveName(jsonNode.path("cve").path("CVE_data_meta").path("ID").asText());
        item.setDescription(jsonNode.path("cve").path("description").path("description_data").get(0).path("value").asText());

        JsonNode impactNode = jsonNode.path("impact").path("baseMetricV3").path("cvssV3");
        item.setBaseScore(impactNode.path("baseScore").asText());
        item.setBaseSeverity(impactNode.path("baseSeverity").asText());

        item.setNvdJson(objectMapper.writeValueAsString(jsonNode));

        return item;
    }
}
