package com.cinvestav.lod;

import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringEscapeUtils;
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
import com.cinvestav.TextParser.JSONutilities;
import com.cinvestav.sparql.Endpoints;

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

	public static void main(String[] args) throws IOException {
		LodFactory lod = new LodFactory();
		// Endpoints.sendPost("","");
		// System.exit(0);
		JSONutilities jUtils = new JSONutilities();
		List<Sentence> listSentence = jUtils.readSentences("output/sentences.json");
		Model model = lod.nifJenaModel();
		lod.prefixDeclaration(model);

		lod.populateModel(listSentence, model);
		// model.write(System.out, "TURTLE");
		lod.writeModel(model, "nifOutput.ttl");
	}

	public Model nifJenaModel() {
		return ModelFactory.createDefaultModel();
	}

	public void prefixDeclaration(Model model) {
		model.setNsPrefix("nif", NS.NIF.ns());
		model.setNsPrefix("rdf", NS.RDF.ns());
		model.setNsPrefix("rdfs", NS.RDFS.ns());
		model.setNsPrefix("itsrdf", NS.ITSRDF.ns());
		model.setNsPrefix("prov", NS.PROV.ns());
		model.setNsPrefix("xsd", NS.XSD.ns());
//		model.setNsPrefix("dbr", NS.DBPEDIA.ns());
		model.setNsPrefix("dbr", "http://dbpedia.org/resource/");
	}

	public void populateModel(List<Sentence> listSentence, Model model) {
		List<Statement> listStatement = new ArrayList<Statement>();
		for (Sentence snt : listSentence) {
			String isString = snt.getSentence();
			// adding nif context
			// Resource referenceContext = createContextStatements(model, isString,
			// snt.getIdSentence(), listStatement);

			Resource referenceContext = createContextStatements(model, snt, listStatement);

			// adding each entity as nif data
			for (Entity ent : snt.getEntities()) {
				String anchorOf = ent.getSurfaceText();
				int beginIndex = ent.getStart();
				int endIndex = ent.getEnd();
				String taConfidence = ent.getConfidenceScore();
				String wasAttributeTo = ent.getSource();
				String taIdentRef = extractList(ent.getURI());
				String taClassRef = extractList(ent.getTypes());

				// Resource subject = createSubject(snt.getIdSentence(), beginIndex, endIndex);

				Resource subject = createSubject(snt.getURI().replaceAll("#.*", ""), beginIndex, endIndex);

				listStatement.addAll(createLinkStatement(model, subject, anchorOf, taConfidence, wasAttributeTo,
						taIdentRef, taClassRef, beginIndex, endIndex, referenceContext));
			}
		}
		model.add(listStatement);
	}

	public Resource createContextStatements(Model model, String isString, int idSentence,
			List<Statement> listStatements) {
		String beginIndex = Integer.toString(0);
		String endIndex = Integer.toString(isString.length());

		Resource subject = ResourceFactory.createResource(defaultUri + "Sentence_" + idSentence);

		Literal integer = ResourceFactory.createTypedLiteral(22);

		listStatements.add(model.createStatement(subject, RDF.type, nifResources.getContext()));
		listStatements.add(model.createStatement(subject, nifProperties.getIsString(),
				ResourceFactory.createStringLiteral(isString)));
		listStatements.add(model.createStatement(subject, nifProperties.getBeginIndex(),
				ResourceFactory.createTypedLiteral(beginIndex, integer.getDatatype())));
		listStatements.add(model.createStatement(subject, nifProperties.getEndIndex(),
				ResourceFactory.createTypedLiteral(endIndex, integer.getDatatype())));
		return subject;
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

	/*
	 * "surfaceText":"ecclesiastical", "source":"Tagme",
	 * "confidenceScore":"0.07041211426258087", "types":"", "start":91, "end":105,
	 * "offsetKey":"91-105", "uri":"http://dbpedia.org/page/Church_architecture"
	 * 
	 */

	public List<Statement> createLinkStatement(Model model, Resource subject, String anchorOf, String taConfidence,
			String wasAttributeTo, String taIdentRef, String taClassRef, int beginIndex, int endIndex,
			Resource referenceContext) {
		List<Statement> listStatement = new ArrayList<Statement>();
		Literal integer = ResourceFactory.createTypedLiteral(beginIndex);
		Literal d = ResourceFactory.createTypedLiteral(0.123);

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
//		listStatement.add(model.createStatement(subject, itsrdfProperties.getTaConfidence(),
//				ResourceFactory.createTypedLiteral(taConfidence, d.getDatatype())));
		if (taIdentRef.length() > 0)
			listStatement.add(model.createStatement(subject, itsrdfProperties.getTaIdentRef(),
					ResourceFactory.createResource(taIdentRef)));
//		if (taClassRef.length() > 0)
//			listStatement.add(model.createStatement(subject, itsrdfProperties.getTaClassRef(),
//					ResourceFactory.createResource(taClassRef)));

		return listStatement;
	}

	public Resource createSubject(int sentenceId, int beginIndex, int endIndex) {
		Resource subject = ResourceFactory
				.createResource(defaultUri + "Sentence_" + sentenceId + "?" + "char=" + beginIndex + "," + endIndex);

		return subject;
	}

	public Resource createSubject(String baseURI, int beginIndex, int endIndex) {
		Resource subject = ResourceFactory.createResource(baseURI + "#" + "char=" + beginIndex + "," + endIndex);

		return subject;
	}

	public String extractList(String list) {
		if (list.length() == 0)
			return "";
		String ref = "";
		Set<String> names = new HashSet<String>();
		for (String r : list.split(",\\s")) {
			if (r.length() > 0) {
				if (r.contains("YAGO:")) {
					r = r.replace("YAGO:", "http://yago-knowledge.org/resource/");
					// System.out.println(r);
					r = Endpoints.queryYagoIdentifier(r);
				} else if (r.contains("bn:")) {
					r = r.replace("bn:", "http://babelnet.org/rdf/");
					// r = Endpoints.queryBnIdentifier(r);
				}
				names.add(StringEscapeUtils.unescapeJava(r));
			}
		}
		for (String r : names) {
			ref = r + ",";
		}
		return ref.substring(0, ref.length() - 1);
	}

	public void writeModel(Model model, String output) throws IOException {
		OutputStream os = Files.newOutputStream(Paths.get(output));
		model.write(os, "TURTLE");
	}

	public String writeModel(Model model) {

		String syntax = "TTL"; // also try "N-TRIPLE" and "TURTLE"
		StringWriter out = new StringWriter();

		model.write(out, syntax);

		return out.toString();
	}

}
