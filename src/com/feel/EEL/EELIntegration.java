package com.feel.EEL;

/**
*
* @author jose
*/

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.ListIterator;

import org.apache.jena.rdf.model.Model;

import com.feel.Entity;
import com.feel.Sentence;
import com.feel.lod.LodFactory;

public class EELIntegration {

	private ArrayList<EntityExtractor> entityExtractors = new ArrayList<>();

	String extractor = "";

	ProcessDataset ds = null;

	public EELIntegration() {
		ds = new ProcessDataset("Resources/dataset_task_1.ttl");
		this.declareServices();
	}

	public EELIntegration(InputStream is) {
		this.declareServices();
		ds = new ProcessDataset(is);
	}
	
	public String processing() throws IOException {

		ArrayList<Sentence> sentences = ds.querySentences();
		Filter f = new Filter(entityExtractors);
		
		for (final ListIterator<Sentence> iter = sentences.listIterator(); iter.hasNext();) {
			Sentence s = iter.next();
			
			if (s.getEntities().size() == 0) {

				ArrayList<Entity> all = this.executeServices(s.getSentence().replaceAll("\n", " "));
				
				ArrayList<Entity> dup = f.removeOnlyDuplicated(all);
				ArrayList<Entity> over = f.removeOverlapping(dup);
				s.addEntities(over);
			} else {
				for (Entity e : s.getEntities()) {
					System.out.println("Ent " + e.getSurfaceText() + " uri " + e.getURI() + " s:" + e.getSource());

				}

			}
			iter.set(s);

		}
		LodFactory lod = new LodFactory();

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

		Babelfy babel = new Babelfy("http://babelfy.io/v1/disambiguate", "41711a94-f7a3-46a1-a637-eb4f3400444e", 0.1);// nhernandez

		DBPediaSpotlight spotlight = new DBPediaSpotlight("http://model.dbpedia-spotlight.org/en/annotate", 0.7);

		Tagme tagme = new Tagme("https://tagme.d4science.org/tagme/tag",
				"d9bafe9e-da35-44eb-94af-e0c1bbd16461-843339462", 0.2);


		WAT wat = new WAT("https://wat.d4science.org/wat/tag/tag","d9bafe9e-da35-44eb-94af-e0c1bbd16461-843339462", 0.1);

		babel.setPriority(2);
		babel.setName("Babelfy");
		spotlight.setPriority(4);
		spotlight.setName("Spotlight");
		tagme.setPriority(3);
		tagme.setName("Tagme");
		wat.setPriority(5);
		wat.setName("WAT");

		this.addExtractor(babel);
		this.addExtractor(spotlight);
		this.addExtractor(tagme);
		this.addExtractor(wat);

	}
	

	/** This method run services and collect all posible entities and links */
	public ArrayList<Entity> executeServices(String text) {
		ArrayList<Entity> entities = null;

		if (entities == null) {
			entities = new ArrayList<Entity>();
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
				e.printStackTrace();
			}
		}
		return entities;
	}

	

	/** Includes a new entity extractor to the list */
	public void addExtractor(EntityExtractor ee) {
		this.entityExtractors.add(ee);
	}

}
