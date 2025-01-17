package com.osckorea.vuln_crawler.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table(name = "nvd_cve_item", schema = "test_schema")
@Getter
//추후 유지보수 고려
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
//추후 양방향 참조 고려할 것
@ToString
public class NvdCveItem {

    @Id
    private Long id;

    @Column("cve_name")
    private String cveName;

    private String description;

    @Column("base_score")
    private String baseScore;

    @Column("base_severity")
    private String baseSeverity;

    @Column("nvd_json")
    private String nvdJson;

    @Column("nvd_updated_json")
    private String nvdUpdatedJson;

    //OR AND 등의 조합으로 인해 조금 더 생각..
//    private String cpe

}
