package com.feel.EEL;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Consts;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.feel.Entity;

/**
 *
 * @author jose
 */
public class DBPediaSpotlight extends EntityExtractor {

	public DBPediaSpotlight(String serviceURL, Double confidence) {
		super(serviceURL, "");
		super.setMinConfidence(confidence);
		super.setName("Spotlight");

	}

	public static void main(String[] args) throws Exception {

		DBPediaSpotlight http = new DBPediaSpotlight("http://model.dbpedia-spotlight.org/en/annotate", -1.0);
		
		String text = "Bryan Lee Cranston is an American actor.  He is known for portraying \"Walter White\" in the drama series Breaking Bad.";

		
		text="Dirk Kuyt admits Holland could not have asked for a better start to their qualifying campaign as they prepare for Norway.\\n\\nOranje have won both of their opening matches to sit at the top of Group Nine with a two-point advantage - and a game in hand - over nearest challengers Scotland.\\n\\nBert van Marwijk's men have already claimed the scalps of FYR Macedonia and Iceland, but they face a much sterner test against the Norwegians in Oslo.\\n\\nYet Kuyt is hopeful they can maintain their 100% qualifying record at the expense of Aage Hareide's men.\\n\\n\\\"We have got the start we wanted in World Cup qualifying. Six points from two matches,\\\" the Liverpool forward told Dagbladet.\\n\\n\\\"It would a be a dream it we could make that nine points on Wednesday evening. Obviously that's what we're aiming for.\\\"\\n\\nNorway's 0-0 draw in Scotland last Saturday has left the Scandinavians four points behind Holland in the table, although Kuyt is not expecting his team to have things their own way in Oslo.\\n\\n\\\"We know what we'll be up against in Norway,\\\" he continued. \\\"It's difficult playing at Ullevaal, very difficult.\\n\\n\\\"The Norwegian side has a lot of quality and I heard they played well in Scotland. A goalless draw at Hampden Park is very respectable.\\\"\\n\\nThe match will also give Kuyt the chance to play against former Liverpool team-mate John Arne Riise - a prospect the ex-Feyenoord man is relishing.\\n\\n\\\"He's a good friend and sometimes we used to play golf together. He's not a bad golfer either and often beat me. That gives me something to avenge in Oslo,\\\" Kuyt joked.\\n\\nFor Ajax striker Klaas-Jan Huntelaar, victory on Wednesday will give Holland the chance to start to thinking about South Africa in two years time.\\n\\n\\\"The prospects are rosy, that much is clear,\\\" he told Sportweek.\\n\\n\\\"It's a bit premature to be packing our suitcases, but if we get a good result against Norway we will have come along way in our journey.\\n\\n\\\"I have a good feeling about it, but we must first achieve it of course.\\\"\\n";
		
		Long intime = System.currentTimeMillis();
		ArrayList<Entity> me = http.getEntities(text);
		Long endtime = System.currentTimeMillis();

		Long total = (endtime - intime);// 1000;

		System.out.println("Response time " + total);
		for (Entity en : me) {
			System.out.println("Mention " + en.getSurfaceText() + " startEnd:" + en.getStart() + "," + en.getEnd()
					+ " URI " + en.getURI());

		}
	}

	@Override
	public ArrayList<Entity> getEntities(String sentence) {
		super.initializeEntities();
		String response = this.sendPost(sentence);
		this.readOutput(response); // feed entities array

		super.removeMentions();
		return super.getEntities();
	}

	// HTTP POST request
	public String sendPost(String sentence) {

		HttpClient client = HttpClientBuilder.create().build();
		HttpPost post = new HttpPost(super.getServiceURL());
		post.addHeader("Accept", "application/json");
		post.addHeader("content-type","application/x-www-form-urlencoded");

		List<NameValuePair> formparams = new ArrayList<NameValuePair>();

		formparams.add(new BasicNameValuePair("text", sentence));
		formparams.add(new BasicNameValuePair("confidence", super.getMinConfidence().toString()));

		UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams, Consts.UTF_8);
		post.setEntity(entity);

		HttpResponse respons = null;
		org.apache.http.HttpEntity entityResp = null;
		String textoResp = "";
		try {
			respons = client.execute(post);
			entityResp = respons.getEntity();
			textoResp = EntityUtils.toString(entityResp, "UTF-8");
			EntityUtils.consume(entityResp);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return textoResp;
	}

	public void readOutput(String text) {

		JSONParser parser = new JSONParser();

		try {
			Object obj = parser.parse(text);

			JSONObject jsonObject = (JSONObject) obj;
			JSONArray msg = (JSONArray) jsonObject.get("Resources");

			int count = msg.size(); // get totalCount of all jsonObjects
			for (int i = 0; i < count; i++) { // iterate through jsonArray
				JSONObject objc = (JSONObject) msg.get(i); // get jsonObject @ i position

				Entity tent = new Entity();
				tent.setSource(super.getName());

				String surface = objc.get("@surfaceForm").toString();
				tent.setSurfaceText(surface);
				tent.setURI(objc.get("@URI").toString());
				tent.setConfidenceScore(objc.get("@similarityScore").toString());

				int offs = Integer.parseInt(objc.get("@offset").toString());
				tent.setOffset(offs, offs + surface.length());

				super.addEntity(tent);
			}

		} catch (ParseException e) {
			e.printStackTrace();

		} catch (NullPointerException e) {
			System.out.println("Null pointer Spotlight: " + text);
		}

	}

}
