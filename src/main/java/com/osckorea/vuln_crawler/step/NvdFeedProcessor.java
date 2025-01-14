package com.osckorea.vuln_crawler.step;


import com.fasterxml.jackson.databind.JsonNode;
import com.osckorea.vuln_crawler.model.NvdCveItemTest;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;

@Component
@RequiredArgsConstructor
public class NvdFeedProcessor implements ItemProcessor<File, NvdCveItemTest> {

    private final ObjectMapper objectMapper;

    @Override
    public NvdCveItemTest process(File item) throws Exception {
        JsonNode rootNode = objectMapper.readTree(item);
        JsonNode cveItems = rootNode.get("result").get("CVE_Items");

        NvdCveItemTest nvdCveItem = new NvdCveItemTest();
        nvdCveItem.setNvdJson(cveItems.toString());
        return nvdCveItem;
    }

}
