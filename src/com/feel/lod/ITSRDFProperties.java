package com.feel.lod;

import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.ResourceFactory;

/**
 * @author Jose
 *
 *
 */

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
