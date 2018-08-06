
package com.feel;

import java.util.ArrayList;

/**
 *
 * @author jose 
 */
public class Sentence {

	private String sentence = ""; 
	private String NE = "";
	private String URI = "";
	private int beginIndex = 0;
	private int endIndex = 0;

	private ArrayList<Entity> entities = new ArrayList<>();

	public Sentence() {
	}

	public Sentence(String sentence, String NEs) {
		this.sentence = sentence;
		this.NE = NEs;

	}

	/**
	 * Add an entity to the list
	 */
	public void addEntity(Entity e) {
		this.entities.add(e);

	}

	/** add entities */
	public void addEntities(ArrayList<Entity> ent) {
		this.entities.addAll(ent);
	}

	/**
	 * @return the entities
	 */
	public ArrayList<Entity> getEntities() {
		return entities;
	}


	/**
	 * @return the sentence
	 */
	public String getSentence() {
		return sentence;
	}

	/**
	 * @param sentence
	 *            the sentence to set
	 */
	public void setSentence(String sentence) {
		this.sentence = sentence;
	}

	/**
	 * @return the NE
	 */
	public String getNE() {
		return NE;
	}

	/**
	 * @param NE
	 *            the NE to set
	 */
	public void setNE(String NE) {
		this.NE = NE;
	}

	public String getURI() {
		return URI;
	}

	public void setURI(String uRI) {
		URI = uRI;
	}

	public int getBeginIndex() {
		return beginIndex;
	}

	public void setBeginIndex(int beginIndex) {
		this.beginIndex = beginIndex;
	}

	public int getEndIndex() {
		return endIndex;
	}

	public void setEndIndex(int endIndex) {
		this.endIndex = endIndex;
	}

}
