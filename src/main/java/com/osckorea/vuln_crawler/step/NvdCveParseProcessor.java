package com.osckorea.vuln_crawler.step;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.osckorea.vuln_crawler.model.NvdCveParseItem;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
@Qualifier("nvdCveParseProcessor")
public class NvdCveParseProcessor implements ItemProcessor<JsonNode, NvdCveParseItem> {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public NvdCveParseItem process(JsonNode jsonNode) throws Exception {
        NvdCveParseItem item = new NvdCveParseItem();

        // CVE Name
        item.setCveName(jsonNode.path("cve").path("CVE_data_meta").path("ID").asText());

        // Description
        item.setDescription(Optional.ofNullable(jsonNode.path("cve").path("description").path("description_data").get(0))
                .map(node -> node.path("value").asText())
                .orElse(""));

        // Problem Type
        item.setProblemType(Optional.ofNullable(jsonNode.path("cve").path("problemtype").path("problemtype_data").get(0))
                .map(node -> node.path("description").get(0))
                .map(node -> node.path("value").asText())
                .orElse(""));

        // References JSON
        item.setReferencesJson(jsonNode.path("cve").path("references").toString());

        // NVD Configuration JSON
        item.setNvdConfJson(jsonNode.path("configurations").toString());

        // Impact JSON
        item.setImpactJson(jsonNode.path("impact").toString());

        // Reference Site
        item.setReferenceSite("NVD");

        return item;
    }
}
