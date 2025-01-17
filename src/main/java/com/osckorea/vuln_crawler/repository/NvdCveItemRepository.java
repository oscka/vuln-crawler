package com.osckorea.vuln_crawler.repository;

import com.osckorea.vuln_crawler.model.NvdCveItem;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NvdCveItemRepository extends CrudRepository<NvdCveItem, Long> {

    NvdCveItem findByCveName(String cveName);

    Optional<NvdCveItem> findOptionalByCveName(String cveName);

}
