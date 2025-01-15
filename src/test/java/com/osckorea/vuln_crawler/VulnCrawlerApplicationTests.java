package com.osckorea.vuln_crawler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.osckorea.vuln_crawler.model.NvdCveItem;
import com.osckorea.vuln_crawler.step.NvdFeedProcessor;
import com.osckorea.vuln_crawler.step.NvdFeedReader;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class VulnCrawlerApplicationTests {

	@Autowired
	private NvdFeedReader nvdFeedReader;

	@Autowired
	private NvdFeedProcessor processor;

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
			NvdCveItem item = processor.process(jsonNode);

			assertNotNull(item);
			System.out.println("Processed NvdCveItem #" + (count + 1) + ":");
			System.out.println(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(item));
			System.out.println("--------------------");

			// Verify essential fields
			assertFalse(item.getCveName().isEmpty());
			assertFalse(item.getDataType().isEmpty());
			assertFalse(item.getDataFormat().isEmpty());
			assertFalse(item.getDataVersion().isEmpty());
			assertFalse(item.getDescription().isEmpty());

			// Verify impact fields (these might be empty for some CVEs)
			assertNotNull(item.getImpactScore());
			assertNotNull(item.getImpactSeverity());

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

}
