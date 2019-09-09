package com.cinvestav.EEL;

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
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.cinvestav.Entity;

/**
 *
 * @author jose
 */
public class Babelfy extends EntityExtractor {

	public Babelfy() {
	}

	public Babelfy(String url, String token, Double confidence) {
		super(url, token);

		super.setMinConfidence(confidence);
		super.setName("Babelfy");
	}

	@Override
	public ArrayList<Entity> getEntities(String sentence) {
		// entities = new ArrayList<>(); // initialize array
		super.initializeEntities();
		String response = this.sendPost(sentence);


		this.readOutput("{\"concepts\":" + response + "}", sentence); // feed entities array
		super.removeMentions();

		return super.getEntities();
	}

	// HTTP POST request
	public String sendPost(String sentence) {

		HttpClient client = HttpClientBuilder.create().build();
		HttpPost post = new HttpPost(super.getServiceURL());

		HttpGet get = new HttpGet(super.getServiceURL());

		// get.setHeader("Content-Type", "application/x-zip");
		get.setHeader("Accept-Encoding", "gzip");

		List<NameValuePair> formparams = new ArrayList<NameValuePair>();

		formparams.add(new BasicNameValuePair("key", super.getTokenKey()));
		formparams.add(new BasicNameValuePair("text", "{" + sentence + "}"));
		formparams.add(new BasicNameValuePair("lang", "EN"));
		formparams.add(new BasicNameValuePair("MCS", "ON_WITH_STOPWORDS"));
		formparams.add(new BasicNameValuePair("annRes", "BN"));
		formparams.add(new BasicNameValuePair("cands", "TOP"));
		//formparams.add(new BasicNameValuePair("annType", "NAMED_ENTITIES"));
		// For more parameters
		// http://babelfy.org/guide
		// formparams.add(new BasicNameValuePair("th", "0.9"));
		//formparams.add(new BasicNameValuePair("annType", "NAMED_ENTITIES"));
		// formparams.add(new BasicNameValuePair("annType", "ALL"));
		// formparams.add(new BasicNameValuePair("match", "EXACT_MATCHING"));

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

		return textoResp;
	}

	/** Routine to read json output */
	public void readOutput(String text, String sentence) {

		JSONParser parser = new JSONParser();

		try {

			Object obj = parser.parse(text);

			JSONObject jsonObject = (JSONObject) obj;
			JSONArray msg = (JSONArray) jsonObject.get("concepts");

			int count = msg.size(); // get totalCount of all jsonObjects
			for (int i = 0; i < count; i++) { // iterate through jsonArray
				JSONObject objc = (JSONObject) msg.get(i); // get jsonObject @ i position

				int tokenStart = Integer.parseInt(((JSONObject) (objc.get("charFragment"))).get("start").toString());
				int tokenEnd = Integer.parseInt(((JSONObject) (objc.get("charFragment"))).get("end").toString());

				String textEntity = sentence.subSequence(tokenStart - 1, tokenEnd).toString().trim();

				Entity tent = new Entity();
				tent.setSource(super.getName());
				tent.setSurfaceText(textEntity);
				tent.setURI(objc.get("DBpediaURL").toString());
				tent.setConfidenceScore(objc.get("coherenceScore").toString());
				tent.setTypes(objc.get("babelSynsetID").toString());

				tent.setOffset(tokenStart - 1, tokenEnd);

				super.addEntity(tent);
			}

		} catch (ParseException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
		}

	}

}
