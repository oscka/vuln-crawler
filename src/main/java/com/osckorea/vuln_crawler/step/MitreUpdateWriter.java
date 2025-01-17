package com.osckorea.vuln_crawler.step;


import com.osckorea.vuln_crawler.model.MitreCveItem;
import com.osckorea.vuln_crawler.repository.MitreCveItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@Qualifier("mitreUpdatewriter")
@RequiredArgsConstructor
@Slf4j
public class MitreUpdateWriter implements ItemWriter<MitreCveItem> {

    private final MitreCveItemRepository repository;

    @Override
    @Transactional
    public void write(Chunk<? extends MitreCveItem> chunk) {
        log.info("====================Writer 시작====================");
        List<MitreCveItem> itemsToSave = new ArrayList<>();

        for (MitreCveItem item : chunk) {
            Optional<MitreCveItem> existingItem = repository.findOptionalByCveName(item.getCveName());
            if(existingItem.isPresent()) {
                MitreCveItem updateItem = updateExistingItem(existingItem.get(), item);
                itemsToSave.add(updateItem);
            } else {
                itemsToSave.add(item);
            }
        }

        repository.saveAll(itemsToSave);
    }

    // 기본적으로 Update는 새로운 데이터를 기반으로 기존 데이터를 덮어버린다.
    private MitreCveItem updateExistingItem(MitreCveItem existingItem, MitreCveItem newItem) {

        if (newItem.getCveName() != null) {
            existingItem.setCveName(newItem.getCveName());
        }
        if (newItem.getDescription() != null) {
            existingItem.setDescription(newItem.getDescription());
        }
        if (newItem.getProblemTypes() != null) {
            existingItem.setProblemTypes(newItem.getProblemTypes());
        }
        if (newItem.getBaseScore() != null) {
            existingItem.setBaseScore(newItem.getBaseScore());
        }
        if (newItem.getBaseSeverity() != null) {
            existingItem.setBaseSeverity(newItem.getBaseSeverity());
        }
        if (newItem.getCveJson() != null) {
            existingItem.setCveJson(newItem.getCveJson());
        }
        if (newItem.getCveAdpJson() != null) {
            existingItem.setCveAdpJson(newItem.getCveAdpJson());
        }
        return existingItem;
    }

}
