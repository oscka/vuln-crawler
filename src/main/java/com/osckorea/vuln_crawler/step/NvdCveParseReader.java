package com.osckorea.vuln_crawler.step;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.osckorea.vuln_crawler.model.NvdCveItem;
import com.osckorea.vuln_crawler.repository.NvdCveItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Iterator;

@Slf4j
@Component
@RequiredArgsConstructor
@Qualifier("nvdCveParseReader")
public class NvdCveParseReader implements ItemReader<JsonNode> {

    private final NvdCveItemRepository nvdCveItemRepository;
    private final ObjectMapper objectMapper;

    private Iterator<String> nvdJsonIterator;
    private int rowNum = 0;

    @Override
    public JsonNode read() throws Exception {
        if (nvdJsonIterator == null) {
            initializeNvdJsonIterator();
        }

        if (!nvdJsonIterator.hasNext()) {
            log.info("Finished reading {} rows", rowNum);
            return null; // End of data
        }

        rowNum++;
        String nvdJson = nvdJsonIterator.next();
        return objectMapper.readTree(nvdJson);
    }

    private void initializeNvdJsonIterator() {
        Iterable<String> allNvdJsons = nvdCveItemRepository.findAllNvdJsons();
        this.nvdJsonIterator = allNvdJsons.iterator();
        log.info("Initialized NvdJsonIterator for NvdCveParseReader");
    }
}
