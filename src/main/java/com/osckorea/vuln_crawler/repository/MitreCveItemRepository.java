package com.osckorea.vuln_crawler.repository;

import com.osckorea.vuln_crawler.model.MitreCveItem;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MitreCveItemRepository extends CrudRepository<MitreCveItem, Long> {

    Optional<MitreCveItem> findOptionalByCveName(String cveName);

}