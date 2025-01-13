package com.osckorea.vuln_crawler.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;


@Table(name = "nvditemtest", schema = "test_schema")
@Getter
//추후 유지보수 고려
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
//추후 양방향 참조 고려할 것
@ToString
public class NvdCveItemTest {

    @Id
    private Long id;

    @Column("nvd_json")
    private String nvdJson;

}
