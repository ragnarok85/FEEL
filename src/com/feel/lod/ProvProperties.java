package com.feel.lod;

import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.ResourceFactory;

/**
 * @author Jose
 *
 *
 */

public class ProvProperties {
	
	Property wasAttibuteTo;
	
	public ProvProperties(){
		this.wasAttibuteTo = ResourceFactory.createProperty(NS.PROV.ns()+"wasAttributeTo");
	}

	public Property getWasAttibuteTo() {
		return wasAttibuteTo;
	}

}
