package com.cinvestav.lod;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.vocabulary.RDF;

import com.cinvestav.Entity;
import com.cinvestav.Sentence;

public class LodFactory {
	String defaultUri = "http:tamps.cinvesetav.mx/";
	NIFProperties nifProperties;
	NIFResources nifResources;
	ITSRDFProperties itsrdfProperties;
	ProvProperties provProperties;

	public LodFactory() {
		this.nifProperties = new NIFProperties();
		this.nifResources = new NIFResources();
		this.itsrdfProperties = new ITSRDFProperties();
		this.provProperties = new ProvProperties();
	}

	public Model nifJenaModel() {
		return ModelFactory.createDefaultModel();
	}

	public void prefixDeclaration(Model model) {
		model.setNsPrefix("owl", "http://www.w3.org/2002/07/owl#");
		model.setNsPrefix("nif", NS.NIF.ns());
		model.setNsPrefix("rdf", NS.RDF.ns());
		model.setNsPrefix("rdfs", NS.RDFS.ns());
		model.setNsPrefix("itsrdf", NS.ITSRDF.ns());
		model.setNsPrefix("xsd", NS.XSD.ns());
	}

	public void populateModel(List<Sentence> listSentence, Model model) {
		List<Statement> listStatement = new ArrayList<Statement>();
		for (Sentence snt : listSentence) {

			Resource referenceContext = createContextStatements(model, snt, listStatement);

			// adding each entity as nif data
			for (Entity ent : snt.getEntities()) {
				String anchorOf = ent.getSurfaceText();
				int beginIndex = ent.getStart();
				int endIndex = ent.getEnd();
				String taConfidence = ent.getConfidenceScore();
				String wasAttributeTo = ent.getSource();
				String taIdentRef = ent.getURI();

				Resource subject = createSubject(snt.getURI().replaceAll("#.*", ""), beginIndex, endIndex);

				listStatement.addAll(createLinkStatement(model, subject, anchorOf, taConfidence, wasAttributeTo,
						taIdentRef, beginIndex, endIndex, referenceContext));
			}
		}
		model.add(listStatement);
	}


	public Resource createContextStatements(Model model, Sentence sentence, List<Statement> listStatements) {
		String beginIndex = Integer.toString(0);
		String endIndex = Integer.toString(sentence.getSentence().length());

		Resource subject = ResourceFactory.createResource(sentence.getURI());

		Literal integer = ResourceFactory.createTypedLiteral("22", XSDDatatype.XSDnonNegativeInteger);

		listStatements.add(model.createStatement(subject, RDF.type, nifResources.getContext()));
		listStatements.add(model.createStatement(subject, nifProperties.getIsString(),
				ResourceFactory.createStringLiteral(sentence.getSentence())));
		listStatements.add(model.createStatement(subject, nifProperties.getBeginIndex(),
				ResourceFactory.createTypedLiteral(beginIndex, integer.getDatatype())));
		listStatements.add(model.createStatement(subject, nifProperties.getEndIndex(),
				ResourceFactory.createTypedLiteral(endIndex, integer.getDatatype())));
		return subject;
	}

	public List<Statement> createLinkStatement(Model model, Resource subject, String anchorOf, String taConfidence,
			String wasAttributeTo, String taIdentRef, int beginIndex, int endIndex,
			Resource referenceContext) {
		List<Statement> listStatement = new ArrayList<Statement>();
		Literal integer = ResourceFactory.createTypedLiteral(beginIndex);

		String[] splitAnchor = anchorOf.split(" ");

		if (splitAnchor.length > 1) {
			listStatement.add(model.createStatement(subject, RDF.type, nifResources.getPhrase()));
		} else {
			listStatement.add(model.createStatement(subject, RDF.type, nifResources.getWord()));
		}

		listStatement.add(model.createStatement(subject, RDF.type, nifResources.getString()));
		listStatement.add(model.createStatement(subject, RDF.type, nifResources.getRFC5147String()));

		listStatement.add(model.createStatement(subject, nifProperties.getAnchorOf(),
				ResourceFactory.createLangLiteral(anchorOf, "en")));
		listStatement.add(model.createStatement(subject, provProperties.getWasAttibuteTo(),
				ResourceFactory.createStringLiteral(wasAttributeTo)));
		listStatement.add(model.createStatement(subject, nifProperties.getBeginIndex(),
				ResourceFactory.createTypedLiteral(Integer.toString(beginIndex), integer.getDatatype())));
		listStatement.add(model.createStatement(subject, nifProperties.getEndIndex(),
				ResourceFactory.createTypedLiteral(Integer.toString(endIndex), integer.getDatatype())));
		listStatement.add(model.createStatement(subject, nifProperties.getReferenceContext(), referenceContext));
		if (taIdentRef.length() > 0)
			listStatement.add(model.createStatement(subject, itsrdfProperties.getTaIdentRef(),
					ResourceFactory.createResource(taIdentRef)));

		return listStatement;
	}


	public Resource createSubject(String baseURI, int beginIndex, int endIndex) {
		Resource subject = ResourceFactory.createResource(baseURI + "#" + "char=" + beginIndex + "," + endIndex);

		return subject;
	}

	public String writeModel(Model model) {

		String syntax = "TTL"; // also try "N-TRIPLE" and "TURTLE"
		StringWriter out = new StringWriter();

		model.write(out, syntax);

		return out.toString();
	}
	
}
