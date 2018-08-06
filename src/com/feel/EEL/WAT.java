package com.feel.EEL;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509ExtendedTrustManager;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.feel.Entity;

/**
 * @author Jose
 *
 *
 */

public class WAT extends EntityExtractor {

	private final String USER_AGENT = "Mozilla/5.0";


	public WAT(String serviceURL, String token, Double minConfidence) {

		super.setServiceURL(serviceURL);
		super.setTokenKey(token);
		super.setMinConfidence(minConfidence);
		super.setName("WAT");
	}

	public static void main(String[] args) throws Exception {

		WAT service = new WAT("https://wat.d4science.org/wat/tag/tag",
				"23e8a7b9-57f0-4167-8f33-3adab8a485d5-843339462", .01);

		String sentence = "Bryan Lee Cranston is an American actor.  He is known for portraying \"Walter White\" in the drama series Breaking Bad.";
		
		sentence ="Bryan Lee Cranston is an American actor";
		ArrayList<Entity> me = service.getEntities(sentence);

		for (Entity en : me) {
			System.out.println(" startEnd:"+en.getStart()+","+en.getEnd()+" Mention " + en.getSurfaceText());
			System.out.println("URI " + en.getURI());
			System.out.println("confidence " + en.getConfidenceScore());

		}

	}

	@Override
	public ArrayList<Entity> getEntities(String sentence) {
		super.initializeEntities();
		String response = null;
		try {
			response = this.sendPost(sentence);
		} catch (KeyManagementException | NoSuchAlgorithmException | IOException e) {
			e.printStackTrace();
		}

		this.readOutput(response); // feed entities array
		super.removeMentions();
		return super.getEntities();
	}

	@SuppressWarnings("deprecation")
	public String sendPost(String sentence)
			throws UnsupportedEncodingException, IOException, NoSuchAlgorithmException, KeyManagementException {
		
		TrustManager[] trustAllCerts = new TrustManager[] { new X509ExtendedTrustManager() {
			@Override
			public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				return null;
			}

			@Override
			public void checkClientTrusted(X509Certificate[] certs, String authType) {
			}

			@Override
			public void checkServerTrusted(X509Certificate[] certs, String authType) {
			}

			@Override
			public void checkClientTrusted(X509Certificate[] xcs, String string, Socket socket)
					throws CertificateException {

			}

			@Override
			public void checkServerTrusted(X509Certificate[] xcs, String string, Socket socket)
					throws CertificateException {

			}

			@Override
			public void checkClientTrusted(X509Certificate[] xcs, String string, SSLEngine ssle)
					throws CertificateException {

			}

			@Override
			public void checkServerTrusted(X509Certificate[] xcs, String string, SSLEngine ssle)
					throws CertificateException {

			}

		} };

		SSLContext sc = SSLContext.getInstance("SSL");
		sc.init(null, trustAllCerts, new java.security.SecureRandom());
		HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

		// Create all-trusting host name verifier
		HostnameVerifier allHostsValid = new HostnameVerifier() {
			@Override
			public boolean verify(String hostname, SSLSession session) {
				return true;
			}
		};
		// Install the all-trusting host verifier
		HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
		/*
		 * end of the fix
		 */
		String urlParameters = "";

		urlParameters += "gcube-token=" + super.getTokenKey();
		urlParameters += "&text=" + URLEncoder.encode(sentence);
		urlParameters += "&lang=en";
		
		
		
		URL url = new URL(super.getServiceURL()+"?"+urlParameters);

		HttpsURLConnection con = (HttpsURLConnection) url.openConnection();

		// add reuqest header
		con.setRequestMethod("GET");
		con.setRequestProperty("User-Agent", USER_AGENT);

		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();

		return response.toString();
	}

	/** Routine to read json output */
	public void readOutput(String text) {

		JSONParser parser = new JSONParser();

		try {

			Object obj = parser.parse(text);

			JSONObject jsonObject = (JSONObject) obj;
			JSONArray msg = (JSONArray) jsonObject.get("annotations");

			int count = msg.size(); // get totalCount of all jsonObjects
			for (int i = 0; i < count; i++) { // iterate through jsonArray
				JSONObject objc = (JSONObject) msg.get(i); // get jsonObject @ i position

				Entity tent = new Entity();
				tent.setSource(super.getName());
				tent.setSurfaceText(objc.get("spot").toString());
				tent.setURI("http://dbpedia.org/resource/" + objc.get("title").toString().replaceAll(" ", "_"));
				tent.setConfidenceScore(objc.get("rho").toString());
				tent.setOffset(Integer.parseInt(objc.get("start").toString()),
						Integer.parseInt(objc.get("end").toString()));

				super.addEntity(tent);

			}

		} catch (ParseException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
		}

	}

}