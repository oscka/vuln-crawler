package com.osckorea.vuln_crawler.step;


import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemReader;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Component
@RequiredArgsConstructor
public class NvdFeedReader implements ItemReader<File> {
    private final RestTemplate restTemplate;

    private final String baseUrl = "https://nvd.nist.gov/feeds/json/cve/1.1/nvdcve-1.1-";
//    private final List<String> years = Arrays.asList("2025", "2024", "2023", "2022", "2021", "2020", "2019", "2018", "2017", "2016", "2015", "2014", "2013", "2012", "2011", "2010", "2009", "2008", "2007", "2006", "2005", "2004", "2003", "2002");
    private final List<String> years = Arrays.asList("2025", "2024");

    private int currentYearIndex = 0;

    public int getYearsSize() {
        return years.size();
    }

    @Override
    public File read() throws Exception {
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

    // 파일 삭제 고려할 것
    private File downloadFile(String url) throws IOException {
        ResponseEntity<byte[]> response = restTemplate.getForEntity(url, byte[].class);
        File tempFile = File.createTempFile("nvd-", ".zip");
        Files.write(tempFile.toPath(), response.getBody());
        return tempFile;
    }

    private File unzipFile(File zipFile) throws IOException {
        System.out.println("압축 해제 시작");
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

        System.out.println("압축 해제 전부 끝");
        return outputDir.listFiles()[0]; // 압축 해제된 JSON 파일 반환
    }
}
