package com.feel.EEL;

import java.util.ArrayList;
import java.util.ListIterator;

import com.feel.Entity;

/**
*
* @author jose
*/

public abstract class EntityExtractor {
	private String USER_AGENT = "Mozilla/5.0";
	private String serviceURL = "http://babelfy.io/v1/disambiguate";
	private String tokenKey = "10564323-95d6-4d00-8043-2a19a76b0a0e";
	private ArrayList<Entity> entities = null;
	private boolean keepMentions = false;
	private int priority = 0;
	private double confidence = 0d;
	private String name="";

	public boolean isKeepMentions() {
		return keepMentions;
	}

	public void setKeepMentions(boolean keepMentions) {
		this.keepMentions = keepMentions;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public EntityExtractor(String serviceUrl, String token) {
		this.serviceURL = serviceUrl;
		this.tokenKey = token;
	}
	
	public EntityExtractor(String serviceUrl){
		this.serviceURL = serviceUrl;
	}

	public String getUSER_AGENT() {
		return USER_AGENT;
	}

	public void setUSER_AGENT(String uSER_AGENT) {
		USER_AGENT = uSER_AGENT;
	}

	public String getServiceURL() {
		return serviceURL;
	}

	public void setServiceURL(String serviceURL) {
		this.serviceURL = serviceURL;
	}

	public String getTokenKey() {
		return tokenKey;
	}

	public void setTokenKey(String tokenKey) {
		this.tokenKey = tokenKey;
	}

	public ArrayList<Entity> getEntities() {

		return entities;
	}

	public void setEntities(ArrayList<Entity> entities) {
		this.entities = entities;
	}

	public void initializeEntities() {
		this.entities = new ArrayList<>();
	}

	public void addEntity(Entity en) {
		this.entities.add(en);

	}

	public EntityExtractor() {
	}

	public abstract ArrayList<Entity> getEntities(String sentence); // probably from a web service

	public String getUserAgent() {
		return USER_AGENT;
	}

	public void setUserAgent(String userAgent) {
		this.USER_AGENT = userAgent;
	}

	public void setMinConfidence(Double confidence) {
		this.confidence = confidence;
	}

	public Double getMinConfidence() {
		return this.confidence;
	}

	/** Entities without URI */
	public void removeMentions() {

		if (!this.keepMentions) {
			ListIterator<Entity> enit = this.entities.listIterator();

			while (enit.hasNext()) {
				Entity ent = enit.next();

				if (ent.getURI().compareTo("") == 0 || Double.parseDouble(ent.getConfidenceScore()) < this.confidence) {
					enit.remove();
				}
			}
		}
	}

	public int getPriority() {
		return priority;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
