# vuln-crawler


## 개요
> 이 프로젝트는 NVD(National Vulnerability Database)와 Mitre의 CVE(Common Vulnerabilities and Exposures) 취약점 정보를 로컬 데이터베이스에 저장하고 업데이트하기 위한 Spring Batch 기반 애플리케이션입니다. 이는 추후 SBOM(Software Bill of Materials)과 연계하여 취약점 정보를 효율적으로 관리하고 조회하기 위한 기반을 제공합니다.


## 주요 특징
- Spring Batch를 사용한 대용량 데이터 처리
- NVD와 Mitre CVE 데이터의 초기 로딩 및 주기적 업데이트
- RESTful API를 통한 배치 작업 제어
- PostgreSQL 데이터베이스를 사용한 데이터 저장


## Spring Batch란?

#### 이 프로젝트는 Spring Batch의 Chunk 처리 방식을 사용합니다. Chunk 방식은 대용량 데이터를 처리할 때 메모리 사용을 최적화하고 성능을 향상시킵니다.
Chunk vs Tasklet:
> Chunk 처리: 데이터를 일정 단위(chunk)로 나누어 처리합니다. Reader, Processor, Writer의 단계를 거칩니다.
> - 한 번에 지정된 수의 데이터(예: 500개)를 처리합니다.
> - 대용량 데이터 처리에 적합합니다.
> <br>
> 
> Tasklet 처리: 단일 태스크를 실행하는 간단한 방식입니다.
> - 단일 작업을 하나의 스텝에서 수행합니다.
> - 파일 삭제와 같은 단순 작업에 적합합니다.
> <br>
> 자세한 내용은 Spring Batch 공식 문서를 참조하세요.
> <br>
> https://docs.spring.io/spring-batch/reference/


## 작업 흐름
- Controller: REST API를 통해 각 Job을 시작합니다.
- Reader: 데이터 소스에서 정보를 읽어옵니다.
- Processor: 읽어온 데이터를 가공합니다.
- Writer: 처리된 데이터를 데이터베이스에 저장합니다.


## 데이터 처리

#### 초기 데이터 설정
1. NVD와 Mitre에서 제공하는 데이터 피드를 다운로드합니다.
2. 압축을 해제하고 JSON 파싱을 수행합니다.
3. 500개 단위의 chunk로 데이터를 처리하여 데이터베이스에 저장합니다.

#### 취약점 정보 업데이트
1. NVD: 특정 기간의 CVE 변경 이력과 상세 내용을 조회하는 API를 사용하여 데이터를 업데이트합니다.
2. Mitre: 초기 데이터 설정과 동일한 방식으로 전체 데이터를 다시 로드하여 업데이트합니다.

> 참고: Mitre의 경우 공식 API 사용에 제한이 있어, 데이터 피드를 직접 다운로드하는 방식을 채택했습니다. 이를 위해 commons-io:commons-io:2.11.0 라이브러리를 사용하여 재귀적 파일 탐색을 수행합니다.

## 데이터베이스 구조

> PostgreSQL을 사용하며, Spring Data JDBC로 데이터베이스를 관리합니다.
> <br>
> 기본 PostgreSQL DB 초기화 및 세팅 방법은 하기 링크를 참고 바랍니다.
> <br>
> https://github.com/oscka/SBOM-Manager/blob/develop/README.md

#### 테이블 스키마
```
CREATE TABLE test_schema.nvd_cve_item (
    id SERIAL PRIMARY KEY,
    cve_name VARCHAR(255),
    description TEXT,
    base_score VARCHAR(50),
    base_severity VARCHAR(50),
    nvd_json JSONB,
    nvd_updated_json JSONB
);

CREATE TABLE test_schema.mitre_cve_item (
    id SERIAL PRIMARY KEY,
    cve_name VARCHAR(255),
    description TEXT,
    problem_types JSONB,
    base_score VARCHAR(50),
    base_severity VARCHAR(50),
    cve_json JSONB,
    cve_adp_json JSONB
);
```








