package com.osckorea.vuln_crawler.repository;

import com.osckorea.vuln_crawler.model.NvdCveParseItem;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NvdCveParseItemRepository extends CrudRepository<NvdCveParseItem, Long> {



}
