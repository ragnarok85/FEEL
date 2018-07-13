package com.cinvestav.lod;

import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;

//http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-core#
public class NIFResources {
	private Resource word;
	private Resource phrase;
	private Resource context;
	private Resource sentence;
	private Resource string;
	private Resource RFC5147String;
	
	public NIFResources(){
		this.word = ResourceFactory.createResource(NS.NIF.ns()+"Word");
		this.phrase = ResourceFactory.createResource(NS.NIF.ns()+"Phrase");
		this.context = ResourceFactory.createResource(NS.NIF.ns()+"Context");
		this.sentence = ResourceFactory.createResource(NS.NIF.ns()+"Sentence");
		this.string = ResourceFactory.createResource(NS.NIF.ns()+"String");
		this.RFC5147String = ResourceFactory.createResource(NS.NIF.ns()+"RFC5147String");
	}

	public Resource getWord() {
		return word;
	}

	public Resource getPhrase() {
		return phrase;
	}

	public Resource getContext() {
		return context;
	}

	public Resource getSentence() {
		return sentence;
	}

	public Resource getString() {
		return string;
	}

	public void setString(Resource string) {
		this.string = string;
	}

	public Resource getRFC5147String() {
		return RFC5147String;
	}

	public void setRFC5147String(Resource rFC5147String) {
		RFC5147String = rFC5147String;
	}
}
