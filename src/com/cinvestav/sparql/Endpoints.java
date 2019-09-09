package com.cinvestav.sparql;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;

public class Endpoints {

	private static String babelnet = "http://babelnet.org/rdf/";
	private static String babelnetGet = "http://babelnet.org/sparql/?query=";
	private static String dbpedia = "http://dbpedia.org/sparql";
	private static String yago = "https://linkeddata1.calcul.u-psud.fr/sparql";

	public static String queryBnIdentifier(String bnIdentifier) {
		String dbpediaUri = "";
		String queryString = "SELECT ?o WHERE { " + " <http://babelnet.org/rdf/" + bnIdentifier
				+ "> <http://www.w3.org/2004/02/skos/core#exactMatch> ?o. " + " }";
		Query query = QueryFactory.create(queryString);
		try (QueryExecution qexec = QueryExecutionFactory.sparqlService(babelnet, query)) {
			ResultSet rs = qexec.execSelect();
			while (rs.hasNext()) {
				QuerySolution qs = rs.next();
				String uri = qs.getResource("?o").getURI();
				if (uri.contains("dbpedia.org")) {
					dbpediaUri = uri;
					break;
				}
			}
		}

		return dbpediaUri;
	}

	public static String queryYagoIdentifier(String yagoIdent) {
		System.out.println("Yago " + yagoIdent);


		String dbpediaUri = "";
		String queryString = "SELECT ?v WHERE {" + " GRAPH ?g { " + " <http://yago-knowledge.org/resource/" + yagoIdent
				+ "> <http://www.w3.org/2002/07/owl#sameAs> ?v. " + " } " + " } ";

		String toDbpedia = "select ?v where {?v <http://www.w3.org/2002/07/owl#sameAs> <" + yagoIdent + ">} LIMIT 1";
		Query query = QueryFactory.create(queryString);
		try (QueryExecution qexec = QueryExecutionFactory.sparqlService(dbpedia, toDbpedia)) {
			// System.out.println(qexec.toString());
			ResultSet rs = qexec.execSelect();
			while (rs.hasNext()) {
				QuerySolution qs = rs.next();
				String sol = qs.getResource("?v").getURI();
				if (sol.contains("dbpedia.org")) {
					dbpediaUri = sol;
					break;
				}

			}
		}
		return dbpediaUri;

	}

	// dc41cd14-12b7-45d9-9b5b-2b51e9394864
	public static String sendPost(String sentence, String key) {

		HttpClient client = HttpClientBuilder.create().build();
		HttpPost post = new HttpPost(babelnetGet);

		HttpGet get = new HttpGet(
				"http://babelnet.org/sparql/?query=SELECT%20DISTINCT%20%3Fsense%20%3Fsynset%20WHERE%20%7B%20%20%20%20%3Fentries%20a%20lemon%3ALexicalEntry%20.%20%20%20%20%3Fentries%20lemon%3Asense%20%3Fsense%20.%20%20%20%20%3Fsense%20lemon%3Areference%20%3Fsynset%20.%20%20%20%20%3Fentries%20rdfs%3Alabel%20%22home%22%40en%20.%7D%20LIMIT%2010&format=text%2Fhtml&key=%3Cyour_key%3E");
		List<NameValuePair> formparams = new ArrayList<NameValuePair>();
		String queryString = "SELECT ?o WHERE { "
				+ " <http://babelnet.org/rdf/s00044576n> <http://www.w3.org/2004/02/skos/core#exactMatch> ?o. " + " }";
		formparams.add(new BasicNameValuePair("key", "dc41cd14-12b7-45d9-9b5b-2b51e9394864"));
		formparams.add(new BasicNameValuePair("query", queryString));

		UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams, Consts.UTF_8);

		post.setEntity(entity);

		HttpResponse respons = null;
		HttpEntity entityResp = null;
		String textoResp = "";
		try {
			respons = client.execute(post);
			entityResp = respons.getEntity();
			textoResp = EntityUtils.toString(entityResp, "UTF-8");
			EntityUtils.consume(entityResp);
		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println(textoResp);
		return textoResp;
	}

}
