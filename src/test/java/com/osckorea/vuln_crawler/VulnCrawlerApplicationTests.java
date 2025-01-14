package com.osckorea.vuln_crawler;

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

	private File resultFile;

	@Test
	void contextLoads() {
	}

	@Test
	public void testReadIntegration() throws Exception {
		File resultFile;
		int fileCount = 0;
		while ((resultFile = nvdFeedReader.read()) != null) {
			assertNotNull(resultFile);
			assertTrue(resultFile.exists());
			assertTrue(resultFile.isFile());
			assertTrue(resultFile.getName().endsWith(".json"));
			fileCount++;
		}
		assertEquals(nvdFeedReader.getYearsSize(), fileCount);
	}

	@AfterEach
	void cleanup() {
		if (resultFile != null && resultFile.exists()) {
			resultFile.delete();
		}
	}

}
