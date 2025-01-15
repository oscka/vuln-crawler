package com.osckorea.vuln_crawler.step;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.osckorea.vuln_crawler.model.NvdCveItem;
import com.osckorea.vuln_crawler.model.NvdCveItemTest;
import com.osckorea.vuln_crawler.repository.NvdCveItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

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