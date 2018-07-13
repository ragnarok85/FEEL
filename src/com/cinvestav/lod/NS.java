package com.cinvestav.lod;

public enum NS {
	
	RDF("http://www.w3.org/1999/02/22-rdf-syntax-ns#"),
	RDFS("http://www.w3.org/2000/01/rdf-schema#"),
	NIF("http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-core#"),
	ITSRDF("http://www.w3.org/2005/11/its/rdf#"),
	XSD("http://www.w3.org/2001/XMLSchema#"),
	PROV("http://www.w3.org/ns/prov#"),
	DBPEDIA("http://dbpedia.org/resource/");
	
	private String ns;
	
	NS(String ns){
		this.ns = ns;
	}
	
	public String ns(){
		return this.ns;
	}

}
