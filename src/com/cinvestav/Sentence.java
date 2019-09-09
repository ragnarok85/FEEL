
package com.cinvestav;

import java.util.ArrayList;

/**
 *
 * @author jose Usado para guardar las sentencias de un documento y su POS
 */
public class Sentence {

	private int idDoc = 0; // documento al que pertenece la sentencia
	private int idSentence = 0; // identiicador de la sentencia
	private String sentence = ""; // el texto de la sentencia
	private String POS = ""; // etiquetas gramaticales de cada termino en formato CONLL
	private String tree = ""; // se coloca el arbol sintactico para mejor procesamiento
	private int proc = 0; // para filtrar posibles relaciones en las sentencias, 0 por defecto
	private String NE = "";
	private String URI = "";
	private int beginIndex = 0;
	private int endIndex = 0;


	private ArrayList<Entity> entities = new ArrayList<>();

	public Sentence() {
	}

	public Sentence(int idDoc, String sentence, int idSentence, String POS, String tree, int proc, String NEs) {
		this.idDoc = idDoc;
		this.sentence = sentence;
		this.idSentence = idSentence;
		this.POS = POS;
		this.tree = tree;
		this.proc = proc;
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
	 * @return the idDoc
	 */
	public int getIdDoc() {
		return idDoc;
	}

	/**
	 * @param idDoc
	 *            the idDoc to set
	 */
	public void setIdDoc(int idDoc) {
		this.idDoc = idDoc;
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
	 * @return the idSentence
	 */
	public int getIdSentence() {
		return idSentence;
	}

	/**
	 * @param idSentence
	 *            the idSentence to set
	 */
	public void setIdSentence(int idSentence) {
		this.idSentence = idSentence;
	}

	/**
	 * @return the POS
	 */
	public String getPOS() {
		return POS;
	}

	/**
	 * @param POS
	 *            the POS to set
	 */
	public void setPOS(String POS) {
		this.POS = POS;
	}

	/**
	 * @return the tree
	 */
	public String getTree() {
		return tree;
	}

	/**
	 * @param tree
	 *            the tree to set
	 */
	public void setTree(String tree) {
		this.tree = tree;
	}

	/**
	 * @return the proc
	 */
	public int getProc() {
		return proc;
	}

	/**
	 * @param proc
	 *            the proc to set
	 */
	public void setProc(int proc) {
		this.proc = proc;
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
