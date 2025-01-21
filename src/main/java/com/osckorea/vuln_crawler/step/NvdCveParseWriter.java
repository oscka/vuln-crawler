package com.osckorea.vuln_crawler.step;

import com.osckorea.vuln_crawler.model.NvdCveItem;
import com.osckorea.vuln_crawler.model.NvdCveParseItem;
import com.osckorea.vuln_crawler.repository.NvdCveParseItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class NvdCveParseWriter implements ItemWriter<NvdCveParseItem> {

    private final NvdCveParseItemRepository repository;

    @Override
    @Transactional
    public void write(Chunk<? extends NvdCveParseItem> chunk) throws Exception {
        repository.saveAll(chunk);
    }
}
