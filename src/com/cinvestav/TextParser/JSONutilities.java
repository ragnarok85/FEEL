package com.cinvestav.TextParser;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;

import com.cinvestav.Entity;
//import com.cinvestav.SemanticRelation;
import com.cinvestav.Sentence;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JSONutilities {
	private ObjectMapper mapper = new ObjectMapper();

	public JSONutilities() {
		// TODO Auto-generated constructor stub
	}

	/** used to write a java object into JSON */
	public void writeObject(Object towrite, String filePath) {

		// "/Users/Jose/Desktop/Doctorado/cuatrimestres/cuatrimestre
		// 10/representation/documents/d2.json"
		ObjectMapper mapper = new ObjectMapper();

		String injson = "";
		try {
			injson = mapper.writeValueAsString(towrite);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Writer out = null;
		try {
			out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath), "UTF-8"));
		} catch (UnsupportedEncodingException | FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			out.write(injson);
			out.flush();
			out.close();
		} catch (IOException e) {
			System.out.println("Error writing json");
			e.printStackTrace();
		}
	}

	public ArrayList<Sentence> readSentences(String pathread) {
		ArrayList<Sentence> sents = null;
		try {
			sents = mapper.readValue(new File(pathread), new TypeReference<ArrayList<Sentence>>() {
			});

		} catch (IOException e) {
			e.printStackTrace();
		}

		return sents;
	}

}
