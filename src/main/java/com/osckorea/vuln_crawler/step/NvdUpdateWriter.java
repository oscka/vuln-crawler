package com.osckorea.vuln_crawler.step;

import com.osckorea.vuln_crawler.model.NvdCveItem;
import com.osckorea.vuln_crawler.repository.NvdCveItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@Qualifier("nvdUpdatewriter")
@RequiredArgsConstructor
public class NvdUpdateWriter implements ItemWriter<NvdCveItem> {

    private final NvdCveItemRepository repository;

    @Override
    @Transactional
    public void write(Chunk<? extends NvdCveItem> chunk) {
        List<NvdCveItem> itemsToSave = new ArrayList<>();

        for (NvdCveItem item : chunk) {
            Optional<NvdCveItem> existingItem = repository.findOptionalByCveName(item.getCveName());

            if (existingItem.isPresent()) {
                NvdCveItem updatedItem = updateExistingItem(existingItem.get(), item);
                itemsToSave.add(updatedItem);
            } else {
                itemsToSave.add(item);
            }
        }

        repository.saveAll(itemsToSave);
    }

    // 기본적으로 Update는 새로운 데이터를 기반으로 기존 데이터를 덮어버린다.
    // Modify가 아닌 Add 일 경우도 생각해서 추후 수정(Add면 기존 데이터를 보존하고 수정값만 추가해야하니..)
    private NvdCveItem updateExistingItem(NvdCveItem existingItem, NvdCveItem newItem) {
        if (newItem.getCveName() != null) {
            existingItem.setCveName(newItem.getCveName());
        }
        if (newItem.getDescription() != null) {
            existingItem.setDescription(newItem.getDescription());
        }
        if (newItem.getBaseScore() != null) {
            existingItem.setBaseScore(newItem.getBaseScore());
        }
        if (newItem.getBaseSeverity() != null) {
            existingItem.setBaseSeverity(newItem.getBaseSeverity());
        }
        if (newItem.getNvdJson() != null) {
            existingItem.setNvdJson(newItem.getNvdJson());
        }
        if (newItem.getNvdUpdatedJson() != null) {
            existingItem.setNvdUpdatedJson(newItem.getNvdUpdatedJson());
        }
        return existingItem;
    }
}
