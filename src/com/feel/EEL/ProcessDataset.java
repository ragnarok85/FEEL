package com.feel.EEL;

import java.io.InputStream;
import java.util.ArrayList;

import org.apache.jena.query.Dataset;
import org.apache.jena.query.DatasetFactory;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.Syntax;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;

import com.feel.Sentence;


/**
 * @author Jose
 *
 *
 */
public class ProcessDataset {

	private Dataset graphquad = null;
	
	public ProcessDataset(InputStream dataset) {
		graphquad = DatasetFactory.create();
		RDFDataMgr.read(graphquad, dataset, Lang.TURTLE);
	}

	public ProcessDataset(String dataset) {
		graphquad = RDFDataMgr.loadDataset(dataset, Lang.TURTLE);

	}

	public ArrayList<Sentence> querySentences() {
		ArrayList<Sentence> sentences = new ArrayList<>();

		Query query = QueryFactory.create();

		String preparedQuery = "" + "PREFIX owl:   <http://www.w3.org/2002/07/owl#>\n"
				+ "PREFIX rdf:   <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"
				+ "PREFIX xsd:   <http://www.w3.org/2001/XMLSchema#>\n"
				+ "PREFIX oke:   <http://www.ontologydesignpatterns.org/data/oke-challenge/task-1/>\n"
				+ "PREFIX itsrdf: <http://www.w3.org/2005/11/its/rdf#>\n"
				+ "PREFIX rdfs:  <http://www.w3.org/2000/01/rdf-schema#>\n"
				+ "PREFIX nif:   <http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-core#>\n"
				+ "PREFIX dul:   <http://www.ontologydesignpatterns.org/ont/dul/DUL.owl#>\n"
				+ "PREFIX dbpedia: <http://dbpedia.org/resource/>\n"
				+ "PREFIX d0:    <http://ontologydesignpatterns.org/ont/wikipedia/d0.owl#>\n"
				+ "PREFIX dc:    <http://purl.org/dc/elements/1.1/> \n" 
				+ "SELECT ?URI ?begin ?end ?sentence WHERE { "
				+ "?URI a nif:Context ." + "?URI nif:isString ?sentence ." + "?URI nif:beginIndex ?begin ."
				+ "?URI nif:endIndex ?end ." + " } ";

		QueryFactory.parse(query, preparedQuery, "", Syntax.syntaxSPARQL_11);

		QueryExecution qexec = QueryExecutionFactory.create(query, graphquad);
		String sentence = "";
		try {
			ResultSet results = qexec.execSelect();
			while (results.hasNext()) {
				QuerySolution soln = results.nextSolution();

				sentence = soln.getLiteral("sentence").toString();//
				String ne = soln.getResource("URI").getURI();
				int begin = soln.getLiteral("begin").getInt();
				int end = soln.getLiteral("end").getInt();
				
				sentence = sentence.replace("\\\"","\"");


				Sentence res = new Sentence();

				res.setNE(ne); // code of sentence
				res.setURI(ne);
				res.setBeginIndex(begin);
				res.setEndIndex(end);
				res.setSentence(sentence);

				sentences.add(res);

			}
		} finally {
			qexec.close();
		}
		return sentences;
	}

}
