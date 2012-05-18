package com.gkudos.jasperserver.client;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.fail;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

/**
 * 
 * @author juanmendez@gkudos.com
 * 
 */

public class JasperserverRestClientTest {
	public static final Logger LOGGER = LoggerFactory.getLogger(JasperserverRestClientTest.class);
	
	private final static String serverUrl = "http://myserver/jasperserver/";
	private final static String serverUser = "joeuser";
	private final static String serverPassword = "joeuser";
	
	private File outPutDir;
	
	@BeforeTest
	public void beforeTest() {
		LOGGER.debug("beforeTest...");
		outPutDir = new File(System.getProperty("java.io.tmpdir"));
		assertNotNull(outPutDir);
	}

	@AfterTest
	public void afterTest() {
		LOGGER.debug("afterTest...");
	}
	

	/**
	 * Ejecuta el reporte y retorna un archivo
	 */
	@Test
	public void testGetReportAsFile() {
		LOGGER.debug("testGetReportAsFile");
		try {
			Report report = new Report();
			report.setUrl("/reports/samples/Employees");
			report.setOutputFolder(outPutDir.getAbsolutePath());
			LOGGER.info(report.toString());
			JasperserverRestClient client = JasperserverRestClient.getInstance(serverUrl, serverUser, serverPassword);
			File reportFile  = client.getReportAsFile(report);
			assertNotNull(reportFile);
			LOGGER.debug("reportFile:"+reportFile.getAbsolutePath());
		} catch (Exception e) {
			fail(e.getMessage(), e);
		}
	}
	
	@Test
	public void testGetBigReportAsFile() {
		LOGGER.debug("testGetBigReportAsFile");
		try {
			Report report = new Report();
			report.setUrl("/reports/samples/SalesByMonth");
			report.setOutputFolder(outPutDir.getAbsolutePath());
			LOGGER.info(report.toString());
			JasperserverRestClient client = JasperserverRestClient.getInstance(serverUrl, serverUser, serverPassword);
			File reportFile = client.getReportAsFile(report);
			assertNotNull(reportFile);
			LOGGER.debug("reportFile:"+reportFile.getAbsolutePath());
		} catch (Exception e) {
			fail(e.getMessage(), e);
		}
	}
	
	@Test
	public void testGetBigReportAsExcelFile() {
		LOGGER.debug("testGetBigReportAsExcelFile");
		try {
			Report report = new Report();
			report.setFormat(Report.FORMAT_EXCEL);
			report.setUrl("/reports/samples/SalesByMonth");
			report.setOutputFolder(outPutDir.getAbsolutePath());
			LOGGER.info(report.toString());
			JasperserverRestClient client = JasperserverRestClient.getInstance(serverUrl, serverUser, serverPassword);
			File reportFile = client.getReportAsFile(report);
			assertNotNull(reportFile);
			LOGGER.debug("reportFile:"+reportFile.getAbsolutePath());
		} catch (Exception e) {
			fail(e.getMessage(), e);
		}
	}
	
	@Test
	public void testGetReportWithParamsAsFile() {
		LOGGER.debug("testGetReportWithParamsAsFile");
		try {
			Report report = new Report();
			report.setUrl("/reports/samples/Department");
			report.setOutputFolder(outPutDir.getAbsolutePath());
			report.addParameter("department", "11");
			LOGGER.info(report.toString());
			JasperserverRestClient client = JasperserverRestClient.getInstance(serverUrl, serverUser, serverPassword);
			File reportFile = client.getReportAsFile(report);
			assertNotNull(reportFile);
			LOGGER.debug("reportFile:"+reportFile.getAbsolutePath());
		} catch (Exception e) {
			fail(e.getMessage(), e);
		}
	}

}
