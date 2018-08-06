<%@page import="java.nio.charset.StandardCharsets"%>
<%@page import="java.io.ByteArrayInputStream"%>
<%@page import="com.feel.EEL.EELIntegration"%>
<%@page import="java.io.InputStreamReader"%>
<%@page import="java.io.BufferedReader"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.Enumeration"%>
<%
	BufferedReader in = new BufferedReader(new InputStreamReader(request.getInputStream()));

	String nifString = "";

	String line = null;
	while ((line = in.readLine()) != null) {
		nifString += line + "\n";
	}

	EELIntegration integrator = new EELIntegration(new ByteArrayInputStream(nifString.getBytes()));

	response.setContentType("application/x-turtle");

	String responseService= new String(integrator.processing().replaceAll("xsd:int", "xsd:nonNegativeInteger").replaceAll("@en", "^^xsd:string").trim().getBytes(),StandardCharsets.UTF_8);
	
	System.out.println("Output "+responseService);
	System.out.println("End output");


	response.setHeader("content", "application/x-turtle");

	response.setCharacterEncoding("UTF-8");

	out.print(responseService);

%>