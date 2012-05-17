jasperserver-simple-java-rest-client
====================================

# Overview

A Simple Java Client for Jasperserver 4.5 (and Above) that uses the [Web Service API based on the REST framework](http://jasperforge.org/plugins/mwiki/index.php/Jasperserver/REST_Web_Services_API "Web Service API based on the REST framework")  

In simple words, you can run a report published in a remote server and retrieve the generated file (pdf or excel) to a local folder.

You will need maven 3 (http://maven.apache.org/) to build this library.

This library is created by [Kudos Ltda.](http://gkudos.com/ "Kudos Ltda.")

# Sample Usage

View (src/test/java/com/gkudos/jasperserver/client/JasperserverRestClientTest.java)

The following unit tests are available:
* testGetReportAsFile
* testGetBigReportAsFile
* testGetBigReportAsExcelFile
* testGetReportWithParamsAsFile

Execute a report without parameters:

	Report report = new Report();
	report.setUrl("/reports/samples/Employees");
	report.setOutputFolder(outPutDir.getAbsolutePath());
	JasperserverRestClient client = JasperserverRestClient.getInstance(serverUrl, serverUser, serverPassword);
	File reportFile  = client.getReportAsFile(report);

Execute a report with parameters:

	Report report = new Report();
	report.setUrl("/reports/samples/Department");
	report.setOutputFolder(outPutDir.getAbsolutePath());
	report.addParameter("department", "11");
	JasperserverRestClient client = JasperserverRestClient.getInstance(serverUrl, serverUser, serverPassword);
	File reportFile = client.getReportAsFile(report);


# License 

You are free to use it for any of your projects without restriction. 
License: MIT http://www.opensource.org/licenses/mit-license.php
