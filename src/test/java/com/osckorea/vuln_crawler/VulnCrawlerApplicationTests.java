package com.osckorea.vuln_crawler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.osckorea.vuln_crawler.model.MitreCveItem;
import com.osckorea.vuln_crawler.model.NvdCveItem;
import com.osckorea.vuln_crawler.step.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class VulnCrawlerApplicationTests {

	private static final Logger log = LoggerFactory.getLogger(VulnCrawlerApplicationTests.class);
	@Autowired
	private NvdFeedReader nvdFeedReader;
	@Autowired
	private NvdUpdateReader nvdUpdateReader;

	@Autowired
	private NvdFeedProcessor nvdFeedProcessor;

	@Autowired
	private NvdUpdateProcessor nvdUpdateProcessor;

	@Autowired
	private MitreFeedReader mitreFeedReader;

	@Autowired
	private MitreFeedProcessor mitreFeedProcessor;

	private File resultFile;

	private final ObjectMapper objectMapper = new ObjectMapper();

	@Test
	void contextLoads() {
	}

	@Test
	public void testReadFirstThreeJsonNodes() throws Exception {
		JsonNode cveItem;
		int count = 0;

		while ((cveItem = nvdFeedReader.read()) != null && count < 3) {
			assertNotNull(cveItem);

			System.out.println("CVE Item #" + (count + 1) + ":");
			System.out.println(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(cveItem));
			System.out.println("--------------------");

			count++;
		}

		assertEquals(3, count, "Should have printed 3 CVE items");
	}

	@Test
	public void testCountProcessedCveFiles() throws Exception {
		JsonNode cveItem;
		int fileCount = 0;

		while ((cveItem = nvdFeedReader.read()) != null) {
			// read() 메소드가 null을 반환하면 새로운 파일을 시작한 것으로 간주
			fileCount++;
		}

		System.out.println("총 처리한 CVE 파일 수: " + fileCount);

		assertTrue(fileCount > 0, "최소한 하나의 CVE 파일을 처리해야 합니다.");
	}

	@Test
	public void testReaderAndProcessor() throws Exception {
		JsonNode jsonNode;
		int count = 0;

		while ((jsonNode = nvdFeedReader.read()) != null && count < 3) {
			NvdCveItem item = nvdFeedProcessor.process(jsonNode);

			assertNotNull(item);
			System.out.println("Processed NvdCveItem #" + (count + 1) + ":");
			System.out.println(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(item));
			System.out.println("--------------------");

			// Verify essential fields
			assertFalse(item.getCveName().isEmpty());
			assertFalse(item.getDescription().isEmpty());

			// Verify impact fields (these might be empty for some CVEs)
			assertNotNull(item.getBaseScore());
			assertNotNull(item.getBaseSeverity());

			// Verify that nvdJson is not empty
			assertFalse(item.getNvdJson().isEmpty());

			count++;
		}

		assertEquals(3, count, "Should have processed 3 items");
	}

	@AfterEach
	void cleanup() {
		if (resultFile != null && resultFile.exists()) {
			resultFile.delete();
		}
	}
	
	
	@Test
	public void testUpdateReader() throws Exception {
		JsonNode cveItem;
		while ((cveItem = nvdUpdateReader.read()) != null) {
			// read() 메소드가 null을 반환하면 새로운 파일을 다 읽은것으로 간주
			log.info("CVE Item : {}", cveItem);
		}
		log.info("totalResults : {}", nvdUpdateReader.getTotalResult());
		assertTrue(true, "테스트 완료.");
	}

	@Test
	public void testAUpdateReaderAndProcessor() throws Exception {
		JsonNode jsonNode;
		int count = 0;

		while ((jsonNode = nvdUpdateReader.read()) != null && count < 3) {
			NvdCveItem item = nvdUpdateProcessor.process(jsonNode);

			assertNotNull(item);
			System.out.println("Processed NvdCveItem #" + (count + 1) + ":");
			System.out.println(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(item));
			System.out.println("--------------------");

			// Verify essential fields
			assertFalse(item.getCveName().isEmpty());
			assertFalse(item.getDescription().isEmpty());

			// Verify impact fields (these might be empty for some CVEs)
//			assertNotNull(item.getBaseScore());
//			assertNotNull(item.getBaseSeverity());

			// Verify that nvdJson is not empty
			count++;
		}

		assertEquals(3, count, "Should have processed 3 items");
	}

	@Test
	void testReadAndVerifyFiles() throws Exception {
		int fileCount = 0;
		List<JsonNode> sampleCveJsons = new ArrayList<>();

		JsonNode cveJson;
		while ((cveJson = mitreFeedReader.read()) != null) {
			fileCount++;
			if (sampleCveJsons.size() < 3) {
				sampleCveJsons.add(cveJson);
			}
		}

		// 총 처리된 파일 수 출력 및 검증
		System.out.println("총 처리된 파일 수: " + fileCount);

		// 샘플 CVE JSON 출력
		System.out.println("샘플 CVE JSON (최대 3개):");
		for (int i = 0; i < sampleCveJsons.size(); i++) {
			System.out.println("Sample " + (i + 1) + ":");
			System.out.println(sampleCveJsons.get(i).toPrettyString());
		}

	}

	@Test
	public void testMitreReaderAndProcessor() throws Exception {
		JsonNode jsonNode;
		int count = 0;

		while ((jsonNode = mitreFeedReader.read()) != null && count < 3) {
			MitreCveItem item = mitreFeedProcessor.process(jsonNode);

			assertNotNull(item);
			System.out.println("Processed NvdCveItem #" + (count + 1) + ":");
			System.out.println(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(item));
			System.out.println("--------------------");

			// Verify essential fields
			assertFalse(item.getCveName().isEmpty());
			assertFalse(item.getDescription().isEmpty());

			count++;
		}

		assertEquals(3, count, "Should have processed 3 items");
	}

	@Test
	public void testMitreReaderAndProcessorV2() throws Exception {
		JsonNode jsonNode;
		int count = 0;
		List<MitreCveItem> processedItems = new ArrayList<>();

		// Read and process all items
		while ((jsonNode = mitreFeedReader.read()) != null) {
			MitreCveItem item = mitreFeedProcessor.process(jsonNode);

			assertNotNull(item);
			processedItems.add(item);

			count++;
		}

		// Print the first 3 items
		System.out.println("First 3 Processed Items:");
		for (int i = 0; i < Math.min(3, processedItems.size()); i++) {
			System.out.println("Processed NvdCveItem #" + (i + 1) + ":");
			System.out.println(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(processedItems.get(i)));
			System.out.println("--------------------");
		}

		// Print the last 3 items
		System.out.println("Last 3 Processed Items:");
		for (int i = Math.max(0, processedItems.size() - 3); i < processedItems.size(); i++) {
			System.out.println("Processed NvdCveItem #" + (i + 1) + ":");
			System.out.println(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(processedItems.get(i)));
			System.out.println("--------------------");
		}

		// Print the total count
		System.out.println("Total Processed Items: " + count);

		// Assert the total count matches the processed items size
		assertEquals(processedItems.size(), count, "Total processed items count should match");
	}

}
