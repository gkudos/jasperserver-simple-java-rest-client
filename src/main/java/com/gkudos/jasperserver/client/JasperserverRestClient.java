package com.gkudos.jasperserver.client;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.MediaType;

import org.apache.commons.io.FileUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import com.sun.jersey.client.apache.ApacheHttpClient;
import com.sun.jersey.client.apache.config.ApacheHttpClientConfig;
import com.sun.jersey.client.apache.config.DefaultApacheHttpClientConfig;

/**
 * 
 * @author juan.mendez
 * 
 */
public final class JasperserverRestClient {

	private static final Logger LOGGER = LoggerFactory.getLogger(JasperserverRestClient.class);

	private static JasperserverRestClient instance;
	private ClientConfig clientConfig;
	private String user;
	private String pwd;

	private Map<String, String> resourceCache;
	private String restEndpointUrl;

	/**
	 * 
	 * @param serverUrl
	 * @param user
	 * @param pwd
	 * @return
	 */
	public static JasperserverRestClient getInstance(String serverUrl, String user, String pwd) {
		if (instance == null) {
			instance = new JasperserverRestClient(serverUrl, user, pwd);
		}
		return instance;
	}

	/**
	 * 
	 */
	private JasperserverRestClient(String serverUrl, String user, String pwd) {
		this.user = user;
		this.pwd = pwd;
		// Server paths
		restEndpointUrl = serverUrl + (serverUrl.endsWith("/") ? "rest" : "/rest");
		clientConfig = new DefaultApacheHttpClientConfig();
		clientConfig.getProperties().put(ClientConfig.PROPERTY_FOLLOW_REDIRECTS, true);
		clientConfig.getProperties().put(ApacheHttpClientConfig.PROPERTY_HANDLE_COOKIES, true);

		resourceCache = new HashMap<String, String>();
	}

	/**
	 * 
	 * Run report in jasperserver with rest web services
	 * http://jasperforge.org/plugins/espforum/view.php?group_id=83&forumid=101&topicid=94262
	 * 
	 * @param report
	 * @return
	 * @throws Exception
	 */
	public File getReportAsFile(Report reporte) throws Exception {
		LOGGER.debug("getReportAsFile");

		/*
		 * PrintStream out = null; try { out = new PrintStream( new
		 * FileOutputStream("E:\\snc\\temp\\mylog.txt")); } catch
		 * (FileNotFoundException e) { LOGGER.error( e.getMessage(), e); }
		 */

		// "automagically" manages cookies
		ApacheHttpClient client = ApacheHttpClient.create(clientConfig);
		// Client client = Client.create(clientConfig);
		// debug http traffic
		// client.addFilter(new LoggingFilter(out));
		client.addFilter(new HTTPBasicAuthFilter(user, pwd));
		/*
		 * client.addFilter(new ClientFilter() { private List<Object> cookies;
		 * 
		 * @Override public ClientResponse handle(ClientRequest request) throws
		 * ClientHandlerException {
		 * LOGGER.debug("****************************************"); if (cookies
		 * != null) { request.getHeaders().put("Cookie", cookies);
		 * LOGGER.debug("Adicionando cookies al siguiente request:"+cookies); }
		 * 
		 * ClientResponse response = getNext().handle(request);
		 * LOGGER.debug("Headers:"+response.getHeaders());
		 * 
		 * List<NewCookie> responseCookies = response.getCookies() ; if
		 * (responseCookies!= null && responseCookies.size()>0) { if (cookies ==
		 * null) { LOGGER.debug("Inicializa lista de cookies..."); cookies = new
		 * ArrayList<Object>(); } // simple addAll just for illustration (should
		 * probably // check for duplicates and expired cookies)
		 * cookies.addAll(response.getCookies());
		 * LOGGER.debug("Cookies originales:"+response.getCookies()); }
		 * LOGGER.debug("****************************************"); return
		 * response; } });
		 */

		String describeResourcePath = "/resource" + reporte.getUrl();
		String generateReportPath = "/report" + reporte.getUrl() + "?RUN_OUTPUT_FORMAT=" + reporte.getFormat();
		// LOGGER.debug("describeResourcePath:" + describeResourcePath);
		// LOGGER.debug("putUrlPath:" + generateReportPath);

		// ////////////////////////////////////////////////////////////////////////////////
		// Obtener recurso
		// ////////////////////////////////////////////////////////////////////////////////
		LOGGER.debug("Obtener Recurso...");
		WebResource resource = null;
		String resourceResponse = null;

		if (resourceCache.containsKey(describeResourcePath)) {
			// LOGGER.debug("caché..." );
			resourceResponse = resourceCache.get(describeResourcePath);
		} else {
			resource = client.resource(restEndpointUrl);
			resource.accept(MediaType.APPLICATION_XML);
			resourceResponse = resource.path(describeResourcePath).get(String.class);
			resourceCache.put(describeResourcePath, resourceResponse);
		}
		Document resourceXML = parseResource(resourceResponse);
		// ////////////////////////////////////////////////////////////////////////////////
		// Generar Reporte
		// ////////////////////////////////////////////////////////////////////////////////
		LOGGER.debug("Generar Reporte...");
		resourceXML = addParametersToResource(resourceXML, reporte);
		resource = client.resource(restEndpointUrl + generateReportPath);
		resource.accept(MediaType.TEXT_XML);
		String reportResponse = resource.put(String.class, serializetoXML(resourceXML));
		// ////////////////////////////////////////////////////////////////////////////////
		// Descargar Reporte
		// ////////////////////////////////////////////////////////////////////////////////
		LOGGER.debug("Obtener archivo...");
		String urlReport = parseReport(reportResponse);
		resource = client.resource(urlReport);

		File destFile = null;
		try {
			LOGGER.debug("Inicia escritura de archivo...");
			File remoteFile = resource.get(File.class);
			File parentDir = new File(reporte.getOutputFolder());
			destFile = File.createTempFile("report_", "." + getExtension(reporte.getFormat()), parentDir);
			// LOGGER.debug("remoteFile:" + remoteFile.getAbsolutePath());
			LOGGER.debug("destFile:" + destFile.getAbsolutePath());
			FileUtils.copyFile(remoteFile, destFile);
			LOGGER.debug("Finaliza escritura de archivo...");
		} catch (IOException e) {
			throw e;
		}
		return destFile;
	}

	/**
	 * 
	 * @return
	 * @throws DocumentException
	 */
	private Document parseResource(String resourceAsText) throws Exception {
		// LOGGER.debug("parseResource:\n" + resourceAsText);
		Document document;
		try {
			document = DocumentHelper.parseText(resourceAsText);
		} catch (DocumentException e) {
			throw e;
		}
		return document;
	}

	/**
	 * 
	 */
	private String parseReport(String reportResponse) throws Exception {
		LOGGER.debug("reportResponse:\n" + reportResponse);
		String urlReport = null;
		try {
			Document document = DocumentHelper.parseText(reportResponse);
			Node node = document.selectSingleNode("/report/uuid");
			String uuid = node.getText();
			node = document.selectSingleNode("/report/totalPages");
			Integer totalPages = Integer.parseInt(node.getText());
			if (totalPages == 0) {
				throw new Exception("Error generando reporte");
			}
			urlReport = this.restEndpointUrl + "/report/" + uuid + "?file=report";
		} catch (DocumentException e) {
			throw e;
		}
		return urlReport;
	}

	/**
	 * 
	 * @param resource
	 * @param reporte
	 * @return
	 */
	private Document addParametersToResource(Document resource, Report reporte) {
		// LOGGER.debug("addParametersToResource");

		Element root = resource.getRootElement();
		Map<String, String> params = reporte.getParams();
		for (Map.Entry<String, String> entry : params.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			if (key != null && value != null) {
				root.addElement("parameter").addAttribute("name", key).addText(value);
			}
		}
		// LOGGER.debug("resource:" + resource.asXML());
		return resource;
	}

	/**
	 * 
	 * @param aEncodingScheme
	 * @throws IOException
	 * @throws Exception
	 */
	private String serializetoXML(Document resource) throws Exception {
		OutputFormat outformat = OutputFormat.createCompactFormat();
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		outformat.setEncoding("ISO-8859-1");

		try {
			XMLWriter writer = new XMLWriter(out, outformat);
			writer.write(resource);
			writer.flush();
		} catch (IOException e) {
			throw e;
		}
		return out.toString();
	}
	
	/**
	 * 
	 * @param format
	 * @return
	 */
	private String getExtension(String format) {
		String ext = null;
		if (format.equals(Report.FORMAT_PDF)) {
			ext = "pdf";
		} else if (format.equals(Report.FORMAT_EXCEL)) {
			ext = "xls";
		}
		return ext;
	}

}
