package com.cinvestav;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.apache.jena.rdf.model.Model;

import com.cinvestav.EEL.Babelfy;
import com.cinvestav.EEL.DBPediaSpotlight;
import com.cinvestav.EEL.EntityExtractor;
import com.cinvestav.EEL.ProcessDataset;
import com.cinvestav.EEL.Tagme;
import com.cinvestav.EEL.WAT;
import com.cinvestav.lod.LodFactory;

public class EELIntegration {

	private int FREQUENCY = 2; // SET for fr2
	private ArrayList<EntityExtractor> entityExtractors = new ArrayList<>();

	
	//DECLARE YOUR OWN TOKENS
	private String babelfyToken = "TOKENFORBABELFY";
	private String TagMeToken = "TOKENTAGME";
	private String WATToken = "TOKENWAT";

	ProcessDataset ds = null;

	public EELIntegration() {
		this.declareServices();

	}

	// for processing web requests
	public EELIntegration(InputStream is) {
		this.declareServices();
		ds = new ProcessDataset(is);

	}

	public EELIntegration(InputStream is, int system) {

		this.declareServices();

		// request from gerbil are received in NIF format
		ds = new ProcessDataset(is);

	}

	public String processing(String inputText) {

		Sentence s = new Sentence();
		s.setSentence(inputText);

		// extracting entities
		ArrayList<Entity> entities = this.executeServices(s.getSentence());

		if (entities != null) {

			// overlapping filters
			s.addEntities(this.overlappedFiltering(this.duplicatedFiltering(entities)));

		} else {

			System.out.println("response was null");
		}

		// change to write in turtle
		LodFactory lod = new LodFactory();

		Model model = lod.nifJenaModel();
		lod.prefixDeclaration(model);

		ArrayList<Sentence> sentences = new ArrayList<>();
		sentences.add(s);

		lod.populateModel(sentences, model);

		return lod.writeModel(model);

	}

	/**
	 * 
	 */
	public String processing() {

		ArrayList<Sentence> sentences = ds.querySentences();

		for (final ListIterator<Sentence> iter = sentences.listIterator(); iter.hasNext();) {
			Sentence s = iter.next();

			if (s.getEntities().size() == 0) {

				// clean special characterers
				s.setSentence(s.getSentence().replaceAll("\n", " "));
				s.setSentence(s.getSentence().replaceAll("\\\\", ""));
				s.setSentence(s.getSentence().replaceAll("’", "'"));

				ArrayList<Entity> entities = this.executeServices(s.getSentence());

				if (entities != null) {

					// first filter by frequency

					s.addEntities(this.overlappedFiltering(this.duplicatedFiltering(entities)));

				} else {

					System.out.println("it was null");
				}

			}

			iter.set(s);

		}

		// change to write in turtle
		// this.jsonWriter.writeObject(sentences, path);
		LodFactory lod = new LodFactory();
		// Endpoints.sendPost("","");
		// System.exit(0);

		Model model = lod.nifJenaModel();
		lod.prefixDeclaration(model);
		lod.populateModel(sentences, model);

		return lod.writeModel(model);

	}

	/**
	 * This method declares EEL services and parameters such as confidence and
	 * priority
	 */
	public void declareServices() {

		Babelfy babel = new Babelfy("http://babelfy.io/v1/disambiguate", babelfyToken, 0.1);// nhernandez.phd
		DBPediaSpotlight spotlight = new DBPediaSpotlight("http://model.dbpedia-spotlight.org/en/annotate", 0.65);
		Tagme tagme = new Tagme("https://tagme.d4science.org/tagme/tag", TagMeToken, 0.2);

		// Aida aida = new
		// Aida("https://gate.d5.mpi-inf.mpg.de/aida/service/disambiguate");

		WAT wat = new WAT("https://wat.d4science.org/wat/tag/tag", WATToken, 0.1);

		babel.setPriority(1);
		babel.setName("Babelfy");

		spotlight.setPriority(2);
		spotlight.setName("Spotlight");

		tagme.setPriority(3);
		tagme.setName("Tagme");

		// aida.setPriority(4);
		// aida.setName("Aida");

		wat.setPriority(5);
		wat.setName("WAT");

		this.addExtractor(babel);
		// this.addExtractor(aida);

		this.addExtractor(spotlight);
		this.addExtractor(tagme);

		this.addExtractor(wat);

	}

	/** This method run services and collect all posible entities and links */
	public ArrayList<Entity> executeServices(String text) {

		// ArrayList<Entity> entities = serialized.retrieveEntities(text, SYSTEM);

		ArrayList<Entity> entities = null;

		if (entities == null) {

			entities = new ArrayList<>();
			// run extractors
			for (EntityExtractor extractor : this.entityExtractors) {

				try {
					entities.addAll(extractor.getEntities(text));

				} catch (Exception e) { // catch any exception
					continue; // will just skip this iteration and jump to the next
				}
			}
			try {
				Thread.currentThread();
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		/// %%%%%%%
		// CHANGE PARAMETER FOR USING FREQUENCY, MUST BE SET TO 1 WHEN USING A SINGLE
		/// SYSTEM
		entities = this.filterEntitiesbyOffset(entities, FREQUENCY);

		return entities;
	}

	/** Keep entities with a frequency */
	public ArrayList<Entity> filterEntitiesbyOffset(ArrayList<Entity> entities, int f) {

		ArrayList<Entity> result = new ArrayList<>();

		Map<String, Entity> ne = new HashMap<String, Entity>();

		// get unique entities by mention
		for (Entity entity : entities) {
			ne.put(entity.getSurfaceText() + entity.getStart() + entity.getEnd(), entity);
		}

		// get those entities by frequency
		for (String key : ne.keySet()) {

			int count = 0;

			// iterate over entities
			for (Entity en : entities) {
				if (key.compareTo(en.getSurfaceText() + en.getStart() + en.getEnd()) == 0) {
					count++;
				}
			}

			if (count >= f) {

				for (Entity en : entities) {
					if (key.compareTo(en.getSurfaceText() + en.getStart() + en.getEnd()) == 0) {

						result.add(en);
					}

				}
			}
		}
		return result;
	}

	/** This method filters overlapped and/or duplicated entities */
	public ArrayList<Entity> overlappedFiltering(ArrayList<Entity> entities) {

		// overlapped entities
		ArrayList<Entity> newEntities = new ArrayList<>();
		List<Entity> ents2 = entities;

		for (int i = 0; i < ents2.size(); i++) {
			Entity actual = ents2.get(i);
			boolean contained = false;

			// si es parte de otra mas grande entonces no agregar
			for (int a = 0; a < ents2.size(); a++) {
				Entity en = ents2.get(a);

				// comparison by offset and similar strings
				if (actual.getStart() >= en.getStart() && actual.getEnd() <= en.getEnd()
						&& (actual.getSurfaceText().compareTo(en.getSurfaceText()) != 0)) {
					contained = true;
					break;
				}
			}

			// revisar si no fue agregado
			if (contained) {
				continue;
			} else {
				newEntities.add(actual); // se brinca eliminacion de repetidos por ahora
			}

		}

		return newEntities;

	}

	public ArrayList<Entity> duplicatedFiltering(ArrayList<Entity> entities) {

		ArrayList<Entity> newEntities = new ArrayList<>();

		Map<String, ArrayList<Entity>> dups = new HashMap<>();

		// get duplicated

		for (Entity actual : entities) {

			ArrayList<Entity> t = null;
			if (dups.containsKey(actual.getOffsetKey())) {

				t = dups.get(actual.getOffsetKey());

			} else {
				t = new ArrayList<>();
			}
			t.add(actual);
			dups.put(actual.getOffsetKey(), t);

		}

		Iterator<Map.Entry<String, ArrayList<Entity>>> it = dups.entrySet().iterator();

		while (it.hasNext()) {
			Map.Entry<String, ArrayList<Entity>> pair = it.next();

			ArrayList<Entity> tempe = pair.getValue();

			Entity ent = tempe.get(0); // same offset
			if (tempe.size() == 1) {
				newEntities.add(ent); // unique object
			} else {
				// apply votes >2 ents, (distinct uris?)

				Map<String, Integer> count = new HashMap<>();

				for (Entity ten : tempe) {

					if (count.containsKey(ten.getURI())) {
						count.put(ten.getURI(), count.get(ten.getURI()) + 1);
					} else {
						count.put(ten.getURI(), 1);
					}
				}

				String uri = "";
				int max = 0;

				// iterate map
				Iterator<Map.Entry<String, Integer>> itcounter = count.entrySet().iterator();

				while (itcounter.hasNext()) {
					Map.Entry<String, Integer> element = itcounter.next();

					if (element.getValue() > max) {
						uri = element.getKey();
						max = element.getValue();
					}

				}
				// pick higher, otherwise apply priority
				if (max > 1) {
					ent.setSource("integration"); // undefined yet
					ent.setURI(uri);

					newEntities.add(ent);
				} else {

					newEntities.add(this.prioritySelector(tempe));
				}

			}

		}

		return newEntities;
	}

	public Entity prioritySelector(ArrayList<Entity> ents) {

		/// review extractors
		// max priority
		Map<String, Integer> priority = new HashMap<>();

		for (EntityExtractor ex : entityExtractors) {
			priority.put(ex.getName(), ex.getPriority());

		}

		int higher = 0;
		Entity hig = null;

		for (Entity trav : ents) {
			int pri = priority.get(trav.getSource());
			if (pri > higher) {
				higher = pri;
				hig = trav;
			}
		}

		// traverse
		return hig;
	}

	/** Includes a new entity extractor to the list */
	public void addExtractor(EntityExtractor ee) {
		this.entityExtractors.add(ee);
	}

	public void printEntities(ArrayList<Entity> e) {
		System.out.println();

		for (Entity en : e) {

			System.out.println("mention " + en.getSurfaceText() + " Uri:" + en.getURI() + "	 start,end "
					+ en.getOffsetKey() + " source:" + en.getSource());
		}

	}

	public static void main(String arg[]) {

		// processing GERBIL request

		// String nif = "@prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .\n"
		// + "@prefix xsd: <http://www.w3.org/2001/XMLSchema#> .\n"
		// + "@prefix itsrdf: <http://www.w3.org/2005/11/its/rdf#> .\n"
		// + "@prefix nif:
		// <http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-core#> .\n"
		// + "@prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> .\n" + "\n"
		// + "<http://www.aksw.org/gerbil/NifWebService/request_56#char=0,434>\n"
		// + " a nif:RFC5147String , nif:String , nif:Context ;\n"
		// + " nif:beginIndex \"0\"^^xsd:nonNegativeInteger ;\n"
		// + " nif:endIndex \"434\"^^xsd:nonNegativeInteger ;\n"
		// + " nif:isString \" Reformist allies of Yugoslav President Vojislav Kostunica
		// have scored a decisive victory in today's Serbian parliamentary elections.
		// The Democratic opposition of Serbia, which supports the President, estimates
		// it has won 65% of the vote. The coalition took power after a popular uprising
		// ousted long time Yugoslav President Slobodan Milosevic in October. Mr.
		// Milosevic's Socialist Party and its allies won about 1/5th of the vote.
		// \\n\"^^xsd:string .";
		//
		// EELIntegration integration = new EELIntegration(new
		// ByteArrayInputStream(nif.getBytes()));
		// System.out.println(integration.processing().replaceAll("xsd:int",
		// "xsd:nonNegativeInteger"));

		// DECLARE TOKEN for Babelfy, Tagme, and WAT in method declareServices()
		EELIntegration integration = new EELIntegration();

		String sentence = "Bryan Lee Cranston is an American actor.  He is known for portraying \\\"Walter White\\\" in the drama series Breaking Bad.";

		System.out.println(integration.processing(sentence));

	}
}
