package com.osckorea.vuln_crawler.step;

import com.osckorea.vuln_crawler.model.NvdCveItem;
import com.osckorea.vuln_crawler.repository.NvdCveItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class NvdFeedWriter implements ItemWriter<NvdCveItem> {

    private final NvdCveItemRepository repository;

    @Override
    @Transactional
    public void write(Chunk<? extends NvdCveItem> chunk) throws Exception {
        repository.saveAll(chunk);
    }
}