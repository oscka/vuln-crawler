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

    @Column("data_type")
    private String dataType;

    @Column("data_format")
    private String dataFormat;

    @Column("data_version")
    private String dataVersion;

    private String description;

    @Column("impact_score")
    private String impactScore;

    @Column("impact_severity")
    private String impactSeverity;

    @Column("nvd_json")
    private String nvdJson;

    //OR AND 등의 조합으로 인해 조금 더 생각..
//    private String cpe

}
