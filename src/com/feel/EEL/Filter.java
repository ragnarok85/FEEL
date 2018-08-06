package com.feel.EEL;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.feel.Entity;

public class Filter {
	private ArrayList<EntityExtractor> entityExtractors = new ArrayList<>();
	public Filter() {
		
	}
	
	public Filter(ArrayList<EntityExtractor> entityExtractors) {
		this.entityExtractors = entityExtractors;
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
			String source = trav.getSource();
			if(source.equals("integration")) {
				hig = trav;
				break;
			}
			int pri = priority.get(source);
			if (pri > higher) {
				higher = pri;
				hig = trav;
			}
		}

		// traverse
		return hig;
	}
	
	public ArrayList<Entity> duplicatedFiltering(ArrayList<Entity> entities) {

		ArrayList<Entity> newEntities = new ArrayList<>();

		Map<String, ArrayList<Entity>> dups = new HashMap<>();

		// create map of duplicated entities (same offset)
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

	
	public ArrayList<Entity> removeOnlyDuplicated(ArrayList<Entity> all) {
		ArrayList<Entity> fp = this.duplicatedFiltering(all);
		return fp;
	}
	
	//OVERLAPPING FILTERS
	
	public ArrayList<Entity> removeOverlapping(ArrayList<Entity> entities) {
		ArrayList<Entity> newEntities = new ArrayList<Entity>();
		for (int i = 0; i < entities.size(); i++) {
			for(int j = 0; j < entities.size(); j++) {
				if(j==i)
					continue;
				if(entities.get(i).getStart() >= entities.get(j).getStart() &&
						entities.get(i).getStart() < entities.get(j).getEnd() ||
							entities.get(j).getStart() >= entities.get(j).getStart() && 
								entities.get(j).getStart() < entities.get(i).getEnd()) {
					if(entities.get(i).getSurfaceText().length() > entities.get(j).getSurfaceText().length()) {
						newEntities.add(entities.get(i));
					}else if(entities.get(j).getSurfaceText().length() > entities.get(i).getSurfaceText().length()){
						newEntities.add(entities.get(j));
					}
					
				}
				
			}
		}
		
		return newEntities;
	}
	
}
