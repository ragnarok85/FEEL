package com.cinvestav;

import java.io.Serializable;

/**
 *
 * @author Jose Clase que representa una entidad nombrada con identificador de
 *         una base de conocimiento como dbpedia, freebase o yago, entre otras
 */
public class Entity implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String surfaceText = ""; // surface text
	private String source = ""; // extraction tool e.g. spotlight, tagme
	private String URI; // identifier
	private String confidenceScore = ""; // degree of confidence
	private String types = ""; //
	private int start = 0;
	private int end = 0;
//	private String offsetKey="";

	public Entity() {
	}

	public Entity(String surface, String URI) {
		this.surfaceText = surface;
		this.URI = URI;
	}

	public Entity(String literal, String URL, String conf) {
		this.surfaceText = literal;
		this.URI = URL;
		this.confidenceScore = conf;
	}

	public int numberOfLiteralChars() {

		return this.getSurfaceText().length();
	}

	public int numberOfLiteralWords() {

		int wordCount = 0;

		boolean word = false;
		int endOfLine = getSurfaceText().length() - 1;

		for (int i = 0; i < getSurfaceText().length(); i++) {
			// if the char is a letter, word = true.
			if (Character.isLetter(getSurfaceText().charAt(i)) && i != endOfLine) {
				word = true;
				// if char isn't a letter and there have been letters before,
				// counter goes up.
			} else if (!Character.isLetter(surfaceText.charAt(i)) && word) {
				wordCount++;
				word = false;
				// last word of String; if it doesn't end with a non letter, it
				// wouldn't count without this.
			} else if (Character.isLetter(getSurfaceText().charAt(i)) && i == endOfLine) {
				wordCount++;
			}
		}
		return wordCount;

	}

	/**
	 * @return the surfaceText
	 */
	public String getSurfaceText() {
		return surfaceText;
	}

	/**
	 * @param surfaceText
	 *            the surfaceText to set
	 */
	public void setSurfaceText(String surfaceText) {
		this.surfaceText = surfaceText;
	}

	/**
	 * @return the source
	 */
	public String getSource() {
		return source;
	}

	/**
	 * @param source
	 *            the source to set
	 */
	public void setSource(String source) {
		this.source = source;
	}

	/**
	 * @return the URI
	 */
	public String getURI() {
		return URI;
	}

	/**
	 * @param URI
	 *            the URI to set
	 */
	public void setURI(String URI) {
		this.URI = URI;
	}

	/**
	 * @return the confidenceScore
	 */
	public String getConfidenceScore() {
		return confidenceScore;
	}

	/**
	 * @param confidenceScore
	 *            the confidenceScore to set
	 */
	public void setConfidenceScore(String confidenceScore) {
		this.confidenceScore = confidenceScore;
	}

	/**
	 * @return the types
	 */
	public String getTypes() {
		return types;
	}

	/**
	 * @param types
	 *            the types to set
	 */
	public void setTypes(String types) {
		this.types = types;
	}

	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public int getEnd() {
		return end;
	}

	public void setEnd(int end) {
		this.end = end;
	}

	public void setOffset(int start, int end) {
		this.start=start;
		this.end=end;
	}
	
	public String getOffsetKey() {
		return this.start+"-"+this.end;
		
	}
	

}
