package com.osckorea.vuln_crawler.step;

import com.osckorea.vuln_crawler.model.NvdCveItemTest;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class NvdFeedWriter implements ItemWriter<NvdCveItemTest> {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void write(Chunk<? extends NvdCveItemTest> chunk) throws Exception {
        List<? extends NvdCveItemTest> items = chunk.getItems();
        if (items.isEmpty()) {
            return;
        }

        String sql = "INSERT INTO nvd_cve_item (nvd_json) VALUES (?)";

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setString(1, items.get(i).getNvdJson());
            }

            @Override
            public int getBatchSize() {
                return items.size();
            }
        });
    }

}
