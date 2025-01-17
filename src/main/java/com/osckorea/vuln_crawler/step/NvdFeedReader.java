package com.osckorea.vuln_crawler.step;


import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

@Slf4j
@Component
@RequiredArgsConstructor
@Qualifier("nvdFeedReader")
public class NvdFeedReader implements ItemReader<JsonNode> {
    private final RestTemplate restTemplate;
    private final String baseUrl = "https://nvd.nist.gov/feeds/json/cve/1.1/nvdcve-1.1-";
    private int currentYear = Calendar.getInstance().get(Calendar.YEAR);
    private JsonParser jsonParser;
    private Iterator<JsonNode> cveItemsIterator;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public JsonNode read() throws Exception {
        log.info("==================Read Start==================");
        // json 파일이 들어가있는 cveItemsIterator가 비어있을때
        if (cveItemsIterator == null || !cveItemsIterator.hasNext()) {
            // 가져올 수 있는 연간 nvd json 파일이 없을때
            if (!initializeNextFile()) {
                log.info("==================Maybe This???? ==================");

                // test code for restart job
                this.currentYear = Calendar.getInstance().get(Calendar.YEAR);
                this.jsonParser = null;
                this.cveItemsIterator = null;
                this.objectMapper = new ObjectMapper();

                return null; // 모든 파일 처리 완료
            }
        }

        if (cveItemsIterator.hasNext()) {
            return cveItemsIterator.next();
        }

        return null;
    }

    private boolean initializeNextFile() throws Exception {
//        while (currentYear >= 2002) {
        while (currentYear >= 2025) {
            String url = baseUrl + currentYear + ".json.zip";
            try {
                File zipFile = downloadFile(url);
                if (verifyDownloadWithRetry(zipFile)) {
                    File jsonFile = unzipFile(zipFile);
                    jsonParser = objectMapper.getFactory().createParser(jsonFile);
                    JsonNode rootNode = objectMapper.readTree(jsonParser);
                    cveItemsIterator = rootNode.get("CVE_Items").elements();
                    currentYear--;
                    return true;
                }
            } catch (HttpClientErrorException.NotFound e) {
                currentYear--;
            } catch (Exception e) {
                throw new RuntimeException("파일 다운로드 또는 검증 중 오류 발생: " + e.getMessage());
            } finally {
                // 임시 파일 삭제 로직
            }
        }
        return false;
    }

    private boolean verifyDownloadWithRetry(File file) throws InterruptedException {
        int maxAttempts = 3;
        int attempt = 0;
        while (attempt < maxAttempts) {
            if (verifyDownload(file)) {
                return true;
            }
            attempt++;
            if (attempt < maxAttempts) {
                Thread.sleep(10000); // 10초 대기
            }
        }
        throw new RuntimeException("파일 검증 실패: 3번의 시도 후에도 실패");
    }

    private boolean verifyDownload(File file) {
        if (file.length() > 0) {
            try (ZipFile zipFile = new ZipFile(file)) {
                return true;
            } catch (IOException e) {
                System.out.println("파일 검증 n회 실패");
                return false;
            }
        }
        return false;
    }

    private File downloadFile(String url) throws IOException {
        ResponseEntity<byte[]> response = restTemplate.getForEntity(url, byte[].class);
        File tempFile = File.createTempFile("nvd-", ".zip");
        Files.write(tempFile.toPath(), response.getBody());
        return tempFile;
    }

    private File unzipFile(File zipFile) throws IOException {
        File outputDir = Files.createTempDirectory("nvd-json").toFile();
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                File outputFile = new File(outputDir, entry.getName());
                try (FileOutputStream fos = new FileOutputStream(outputFile)) {
                    byte[] buffer = new byte[1024];
                    int len;
                    while ((len = zis.read(buffer)) > 0) {
                        fos.write(buffer, 0, len);
                    }
                }
            }
        }
        return outputDir.listFiles()[0];
    }
}





//        // 압축 해제된 JSON 파일의 첫 10줄 읽기
//        File[] files = outputDir.listFiles();
//        if (files != null) {
//            for (File file : files) {
//                System.out.println("파일명: " + file.getName());
//                System.out.println("첫 10줄 내용:");
//                try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
//                    for (int i = 0; i < 10; i++) {
//                        String line = reader.readLine();
//                        if (line == null) break;
//                        System.out.println(line);
//                    }
//                }
//                System.out.println("--------------------");
//            }
//        }