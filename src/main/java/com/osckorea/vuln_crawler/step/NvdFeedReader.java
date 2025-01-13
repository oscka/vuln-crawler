package com.osckorea.vuln_crawler.step;


import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemReader;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class NvdFeedReader implements ItemReader<String> {
    private final RestTemplate restTemplate;

    private final String baseUrl = "https://nvd.nist.gov/feeds/json/cve/1.1/nvdcve-1.1-";
    private final List<String> years = Arrays.asList("2023", "2022", "2021", "2020", "2019", "2018", "2017", "2016", "2015", "2014", "2013", "2012", "2011", "2010", "2009", "2008", "2007", "2006", "2005", "2004", "2003", "2002");
    private int currentYearIndex = 0;

    @Override
    public String read() throws Exception {
        if (currentYearIndex >= years.size()) {
            return null;
        }

        String year = years.get(currentYearIndex);
        String url = baseUrl + year + ".json.zip";
        File zipFile = downloadFile(url);
        File jsonFile = unzipFile(zipFile);

        currentYearIndex++;
        return jsonFile;
    }

    private File downloadFile(String url) {
        // 파일 다운로드 로직 구현
    }

    private File unzipFile(File zipFile) {
        // ZIP 파일 압축 해제 로직 구현
    }
}
