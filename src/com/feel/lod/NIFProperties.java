package com.feel.lod;

import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.ResourceFactory;

/**
 * @author Jose
 *
 *
 */
public class NIFProperties {
	private Property anchorOf;
	private Property beginIndex;
	private Property endIndex;
	private Property referenceContext;
	private Property superString;
	private Property isString;
	
	
	public NIFProperties(){
		this.anchorOf = ResourceFactory.createProperty(NS.NIF.ns()+"anchorOf");
		this.beginIndex = ResourceFactory.createProperty(NS.NIF.ns()+"beginIndex");
		this.endIndex = ResourceFactory.createProperty(NS.NIF.ns()+"endIndex");
		this.referenceContext = ResourceFactory.createProperty(NS.NIF.ns()+"referenceContext");
		this.superString = ResourceFactory.createProperty(NS.NIF.ns()+"superString");
		this.isString = ResourceFactory.createProperty(NS.NIF.ns() + "isString");
		
	}

	public Property getAnchorOf() {
		return anchorOf;
	}

	public Property getBeginIndex() {
		return beginIndex;
	}

	public Property getEndIndex() {
		return endIndex;
	}

	public Property getReferenceContext() {
		return referenceContext;
	}

	public Property getSuperString() {
		return superString;
	}

	public Property getIsString() {
		return isString;
	}


	
}
