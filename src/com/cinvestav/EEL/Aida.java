package com.cinvestav.EEL;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.http.Consts;
import org.apache.http.HttpEntity;
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

import com.cinvestav.Entity;

public class Aida extends EntityExtractor {

	public Aida() {

	}

	/*
	 * TODO crate a method to query DBpedia and retrieve the sameAs resource to the
	 * Yago entity EXAMPLE: Select distinct ?s where {?s owl:sameAs
	 * yago-res:United_Kingdom}
	 */
	public Aida(String url) {
		super(url);
		super.setName("Aida");
	}

	@Override
	public ArrayList<Entity> getEntities(String sentence) {
		super.initializeEntities();
		String response = this.sendPost(sentence);

		// System.out.println(response);

		this.readOutput("{\"concepts\":" + response + "}", sentence); // feed entities array
		super.removeMentions();

		return super.getEntities();
	}

	public static void main(String[] args) {
		EntityExtractor aida = new Aida();
		aida.setServiceURL("https://gate.d5.mpi-inf.mpg.de/aida/service/disambiguate");

		// String text = "Dylan was born in Duluth.";
		String text = "John Joseph Earley was the son of James Earley, a fourth generation Irish stone carver and ecclesiastical artist.";

		text = "Saban leaves Dolphins for Alabama job  Coach ends speculation, takes offer of 8 years, $32 million — all guaranteed    DAVIE, Fla. - Nick Saban is 'Bama bound.    Ending five weeks of denials and two days of deliberation, Saban accepted the Alabama coaching job and abandoned his bid to rebuild the Miami Dolphins after only two seasons.    ESPN said the deal is for eight years and $32 million, all of it guaranteed.    Miami owner Wayne Huizenga said he was informed of the decision in a meeting Wednesday at Saban's house. Huizenga announced the departure at a news conference that Saban didn't attend.    “It is what it is,” Huizenga said, borrowing Saban's pet phrase. “I'm not upset because it's more involved than what you think.”    Since late November, Saban had issued frequent, angry public denials of interest in moving to Tuscaloosa. Huizenga said the change of heart wasn't driven by money, and Saban never sought a raise or contract extension.    Instead, Huizenga hinted that family issues for Saban and his wife, Terry, were a factor.    “I've been through this with Nick for quite some time now, and I feel the pain and so forth and so on of Nick and Terry, and it's not a very simple thing,” Huizenga said. “I think Nick's great. I'll be Nick's biggest fan. I'll be cheering for him to win that bowl game.”    A preference for the college game and the campus lifestyle may have swayed Saban. He won a national championship at LSU and is 15-17 with the Dolphins. This was his first losing season in 13 years as a head coach.    The Crimson Tide first approached Saban shortly after firing Mike Shula. Huizenga has said he received repeated assurances from Saban that he would return in 2007, and two weeks ago Saban said: “I'm not going to be the Alabama coach.”    But when the Dolphins' 6-10 season ended Sunday, Alabama sweetened an offer that reportedly would make him the highest-paid coach in college football. He had three years remaining on his Miami contract at $4.5 million a year.    In the past, Huizenga has been persuasive when dealing with coaches. He talked Don Shula into retirement in 1996, talked Jimmy Johnson out of retiring three years later — Johnson lasted one more season — and was able to lure Saban to the pros in 2004 after other NFL teams had failed.    But this time, Huizenga failed to change Saban's mind.    After Saban turned down the Tide in early December, Alabama offered the job to Rich Rodriguez, but he decided to stay at West Virginia. Alabama lost last week to Oklahoma State in the Independence Bowl to finish 6-7.    Possible candidates to replace Saban include Chicago Bears defensive coordinator Ron Rivera, former Green Bay head coach Mike Sherman, San Diego Chargers offensive coordinator Cam Cameron, Indianapolis assistant Jim Caldwell, Tennessee Titans offensive coordinator Norm Chow and Pittsburgh Steelers assistants Russ Grimm and Ken Whisenhut.    Huizenga didn't rule out hiring a college coach, as he did when Saban came to the Dolphins from LSU two years ago.    “There's only one thing I want to do, and it's win,” Huizenga said. “I don't care what it takes, what it costs, what's involved, we're going to make this a winning franchise. It's no fun owning a team if you're not winning, I can tell you that. And we are absolutely, positively going to get back to being a winning team. And sooner rather than later.”    Leading the search for a coach will be Joe Bailey, chief executive officer of Dolphins Enterprises, and Brian Wiedmeier, the Dolphins' president and chief operating officer. The Arizona Cardinals and Atlanta Falcons are also seeking a new coach.    The Dolphins' next coach will be their fourth in nine seasons.    “I wish you hadn't brought that up,” Huizenga said with a wry smile.    It has been a frustrating a stretch of instability for a franchise that had the same coach — Shula — for 26 years. Miami has failed to make the playoffs the past five years, a team record.    The Dolphins are coming off their third losing season since 1969 and likely face a roster overhaul. With Daunte Culpepper struggling to recover from reconstructive knee surgery in 2005, Miami remains unsettled at quarterback, a troublesome position since Dan Marino retired seven years ago. The team needs upgrades in almost every other area for a feeble offense and aging defense.    Saban leaves behind the NFL's largest staff of assistants and general manager Randy Mueller, who might be given more responsibility under a new coaching regime.    The Dolphins haven't reached the AFC championship game since Huizenga became majority owner in 1994.    “All I want to figure out is how the heck we're going to win,” he said. “And that's what everyone with the Dolphins wants, to win. So win, win, win. That's all I can say. We're going to go out there and kick some butt and make something happen, I guarantee you.”  NCAA    ";

		
		text=text.replaceAll("“|”", "\"");
		
		
		ArrayList<Entity> me = aida.getEntities(text);

		for (Entity en : me) {
			System.out.println("Mention " + en.getSurfaceText());
			System.out.println("start: " + en.getStart() + ", End:" + en.getEnd());
			System.out.println("URI " + en.getURI());
		}

	}

	public String sendPost(String sentence) {

		HttpClient client = HttpClientBuilder.create().build();
		HttpPost post = new HttpPost(super.getServiceURL());

		List<NameValuePair> formparams = new ArrayList<NameValuePair>();

		formparams.add(new BasicNameValuePair("text", sentence));

		UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams, Consts.UTF_8);

		post.setEntity(entity);

		HttpResponse respons = null;
		HttpEntity entityResp = null;
		String textoResp = "";
		try {
			respons = client.execute(post);
			entityResp = respons.getEntity();
			textoResp = EntityUtils.toString(entityResp, "UTF-8");
			
			System.out.println("Response: "+textoResp);
			EntityUtils.consume(entityResp);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return textoResp;
	}

	public void readOutput(String text, String sentence) {
		/*
		 * 
		 * concepts -- JsonObject mentions -- JsonArray JsonObject "offset" -- int
		 * "name" -- String "length" -- int "bestEntity" -- JsonObject "kbIdentifier" --
		 * String "disambiguationScore" -- String entityMetadata -- JsonObject
		 * kbIdentifier : -- JSONOBject "type" --> JSONArray "url" --> String
		 */
		JSONParser parser = new JSONParser();

		try {
			// System.out.println(text);
			Object obj = parser.parse(text);

			JSONObject jsonObject = (JSONObject) obj;

			JSONObject concepts = (JSONObject) jsonObject.get("concepts");

			JSONArray msg = (JSONArray) concepts.get("mentions");

			int count = msg.size(); // get totalCount of all jsonObjects
			for (int i = 0; i < count; i++) {
				JSONObject objm = (JSONObject) msg.get(i);
				JSONObject bestEntity = (JSONObject) objm.get("bestEntity");
				if (bestEntity == null) {
					continue;
				}

				int tokenStart = Integer.parseInt(objm.get("offset").toString());
				int tokenEnd = tokenStart + Integer.parseInt(objm.get("length").toString());

				String textEntity = objm.get("name").toString();
				String uri = bestEntity.get("kbIdentifier").toString();
				String types = "";

				Entity tent = new Entity();
				tent.setSource(super.getName());
				tent.setSurfaceText(textEntity);
				tent.setURI(uri);
				tent.setConfidenceScore(bestEntity.get("disambiguationScore").toString());

				JSONObject metadata = (JSONObject) concepts.get("entityMetadata");
				JSONObject entity = (JSONObject) metadata.get(uri);
				JSONArray arrayTypes = (JSONArray) entity.get("type");
				int countTypes = arrayTypes.size();
				Set<String> setTypes = new HashSet<String>(); // para eliminar repetidos.. no sé porque regresa tanto
																// tipos repetidos
//				for (int j = 0; j < countTypes; j++) {
//					setTypes.add(arrayTypes.get(i).toString());
//				}
//
//				for (String t : setTypes) {
//					types += t + ",";
//				}

				tent.setTypes(types);

				tent.setOffset(tokenStart, tokenEnd);

				super.addEntity(tent);

			}
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
		}

	}

}
