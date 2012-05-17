jasperserver-simple-java-rest-client
====================================

# Overview

A Simple Java Client for Jasperserver 4.5 (and Above) that uses the [jasper](http://jasperforge.org/plugins/mwiki/index.php/Jasperserver/REST_Web_Services_API "Web Service API based on the REST framework")  

Executes a report in a remote server and retrieves the generated file (ie: pdf) to a local folder.

This library is created by [gkudos](http://gkudos.com/ "Kudos Ltda.")

# Sample Usage

View (src/test/java/com/gkudos/jasperserver/client/JasperserverRestClientTest.java)

	Report report = new Report();
	report.setUrl("/reports/samples/Employees");
	report.setOutputFolder(outPutDir.getAbsolutePath());
	JasperserverRestClient client = JasperserverRestClient.getInstance(serverUrl, serverUser, serverPassword);
	File reportFile  = client.getReportAsFile(report);


# License 

You are free to use it for any of your projects without restriction. 
License: MIT http://www.opensource.org/licenses/mit-license.php
