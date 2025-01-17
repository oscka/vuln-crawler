package com.osckorea.vuln_crawler.step;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Slf4j
@Component
@RequiredArgsConstructor
@Qualifier("mitreUpdateReader")
public class MitreUpdateReader  implements ItemReader<JsonNode> {
    private final RestTemplate restTemplate;
    private final String baseUrl = "https://github.com/CVEProject/cvelistV5/archive/refs/heads/main.zip";
    private ObjectMapper objectMapper = new ObjectMapper();
    private Iterator<File> cveFileIterator;

    // 처리할 연도를 직접 전역 변수로 설정
    private final List<String> targetYears = Arrays.asList("2025");
//    private final List<String> targetYears = Arrays.asList("1999", "2000", "2001", "2002", "2003", "2004", "2005", "2006"
//            , "2007", "2008", "2009", "2010", "2011", "2012", "2013", "2014", "2015", "2016", "2017", "2018", "2019", "2020"
//            , "2021", "2022", "2023", "2024", "2025");

    @Override
    public JsonNode read() throws Exception {
        if (cveFileIterator == null) {
            initializeReader();
        }

        while (cveFileIterator.hasNext()) {
            File cveFile = cveFileIterator.next();
            if (cveFile.getName().endsWith(".json")) {
                return objectMapper.readTree(cveFile);
            }
        }

        return null;
    }

    private void initializeReader() throws Exception {
        log.info("====================initializeReader 시작====================");
        byte[] zipBytes = restTemplate.getForObject(baseUrl, byte[].class);
        File tempZipFile = File.createTempFile("cve_data", ".zip");
        Files.write(tempZipFile.toPath(), zipBytes);

        File extractDir = Files.createTempDirectory("cve_extract").toFile();
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(tempZipFile))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                if (isTargetYearEntry(entry.getName())) {
                    File entryFile = new File(extractDir, entry.getName());
                    if (entry.isDirectory()) {
                        entryFile.mkdirs();
                    } else {
                        try (FileOutputStream fos = new FileOutputStream(entryFile)) {
                            byte[] buffer = new byte[1024];
                            int len;
                            while ((len = zis.read(buffer)) > 0) {
                                fos.write(buffer, 0, len);
                            }
                        }
                    }
                }
            }
        }

        List<File> targetYearDirs = targetYears.stream()
                .map(year -> new File(extractDir, "cvelistV5-main/cves/" + year))
                .collect(Collectors.toList());

        cveFileIterator = targetYearDirs.stream()
                .flatMap(dir -> {
                    Iterator<File> fileIterator = FileUtils.iterateFiles(dir, new String[]{"json"}, true);
                    return StreamSupport.stream(
                            Spliterators.spliteratorUnknownSize(fileIterator, Spliterator.ORDERED),
                            false
                    );
                })
                .iterator();
    }

    private boolean isTargetYearEntry(String entryName) {
        return targetYears.stream().anyMatch(year -> entryName.contains("/cves/" + year + "/"));
    }
}
