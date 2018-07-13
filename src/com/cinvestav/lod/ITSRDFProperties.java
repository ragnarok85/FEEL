package com.cinvestav.lod;

import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.ResourceFactory;
//http://www.w3.org/2005/11/its/rdf#
public class ITSRDFProperties {
	private Property taIdentRef;
	private Property taClassRef;
	private Property taConfidence;
	
	public ITSRDFProperties(){
		this.taIdentRef = ResourceFactory.createProperty(NS.ITSRDF.ns()+"taIdentRef");
		this.taClassRef = ResourceFactory.createProperty(NS.ITSRDF.ns()+"taClassRef");
		this.taConfidence = ResourceFactory.createProperty(NS.ITSRDF.ns()+"taConfidence");
	}

	public Property getTaIdentRef() {
		return taIdentRef;
	}

	public Property getTaClassRef() {
		return taClassRef;
	}

	public Property getTaConfidence() {
		return taConfidence;
	}

}
