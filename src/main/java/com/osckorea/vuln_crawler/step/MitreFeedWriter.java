package com.osckorea.vuln_crawler.step;

import com.osckorea.vuln_crawler.model.MitreCveItem;
import com.osckorea.vuln_crawler.repository.MitreCveItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
@Qualifier("mitreFeedWriter")
public class MitreFeedWriter implements ItemWriter<MitreCveItem> {

    private final MitreCveItemRepository repository;

    @Override
    @Transactional
    public void write(Chunk<? extends MitreCveItem> chunk) throws Exception {
        repository.saveAll(chunk);
    }
}
