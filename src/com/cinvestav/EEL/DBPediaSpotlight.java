/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cinvestav.EEL;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Consts;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
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
public class DBPediaSpotlight extends EntityExtractor {

	public DBPediaSpotlight(String serviceURL, Double confidence) {
		super(serviceURL, "");
		super.setMinConfidence(confidence);
		super.setName("Spotlight");

	}


	@Override
	public ArrayList<Entity> getEntities(String sentence) {
		super.initializeEntities();
		String response = this.sendPost(sentence);
		

		this.readOutput(response); // feed entities array
		

		super.removeMentions();
		return super.getEntities();
	}

	// HTTP POST request
	public String sendPost(String sentence) {

		HttpClient client = HttpClientBuilder.create().build();
		HttpPost post = new HttpPost(super.getServiceURL());
		post.addHeader("Accept", "application/json");
		post.addHeader("content-type","application/x-www-form-urlencoded");

		List<NameValuePair> formparams = new ArrayList<NameValuePair>();

		formparams.add(new BasicNameValuePair("text", sentence));
		formparams.add(new BasicNameValuePair("confidence", super.getMinConfidence().toString()));
//		formparams.add(new BasicNameValuePair("support", 80 + ""));

		UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams, Consts.UTF_8);
		post.setEntity(entity);

		HttpResponse respons = null;
		org.apache.http.HttpEntity entityResp = null;
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

	public void readOutput(String text) {

		JSONParser parser = new JSONParser();

		try {
			Object obj = parser.parse(text);

			JSONObject jsonObject = (JSONObject) obj;
			JSONArray msg = (JSONArray) jsonObject.get("Resources");

			int count = msg.size(); // get totalCount of all jsonObjects
			for (int i = 0; i < count; i++) { // iterate through jsonArray
				JSONObject objc = (JSONObject) msg.get(i); // get jsonObject @ i position

				Entity tent = new Entity();
				tent.setSource(super.getName());

				String surface = objc.get("@surfaceForm").toString();
				tent.setSurfaceText(surface);
				tent.setURI(objc.get("@URI").toString());
				tent.setConfidenceScore(objc.get("@similarityScore").toString());
				tent.setTypes(objc.get("@types").toString());

				int offs = Integer.parseInt(objc.get("@offset").toString());
				tent.setOffset(offs, offs + surface.length());

				super.addEntity(tent);

			}

		} catch (ParseException e) {
			e.printStackTrace();


		} catch (NullPointerException e) {
			System.out.println("Null pointer Spotlight: " + text);
		}

	}

}
