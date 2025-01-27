package com.osckorea.vuln_crawler.repository;

import com.osckorea.vuln_crawler.model.NvdCveItem;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NvdCveItemRepository extends CrudRepository<NvdCveItem, Long> {

    NvdCveItem findByCveName(String cveName);

    Optional<NvdCveItem> findOptionalByCveName(String cveName);

    @Query("SELECT nvd_json FROM nvd_cve_item")
    Iterable<String> findAllNvdJsons();

}