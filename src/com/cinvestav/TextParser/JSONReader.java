package com.cinvestav.TextParser;

import java.io.BufferedWriter;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

//import com.cinvestav.keTools.InvocationService;

public class JSONReader {

    JSONObject jobject = new JSONObject();

    private JSONArray jarray = new JSONArray();
    BufferedWriter bw = null;

    public JSONReader() {
    }

    //print array
    public void printJArray() {
        System.out.println(this.jarray.toJSONString());
    }

    /**
     * Remove unessential characteres provided by simple JSON
     */
    public String cleansingText(String sentence) {
        return sentence.replaceAll("\\\\/", "/");
    }

    /**
     * Get the array in their JSON form with unescaped characteres
     */
    public String getArrayAsJSON() {
        return this.cleansingText(this.jarray.toJSONString());
    }

    public String getStats() {

        int c = jarray.size();

        int entitiesBabel = 0, entitiesBabelwURI = 0,
                entitiesSpot = 0, entitiesSpotWURI = 0, entitiesAlchemy = 0, entitiesAlchemyWURI = 0;
        String NEBabel = "", NESpot = "", NEAlchemy = "";

        for (int i = 0; i < c; i++) {
            JSONObject entity = (JSONObject) jarray.get(i);

            String source = (String) entity.get("source");
            String surface = (String) entity.get("surface");
            String URI = (String) entity.get("URI");
            String types = (String) entity.get("types");

            if (source.compareTo("alchemy") == 0) {
                if (URI.compareTo("") == 0) {
                    entitiesAlchemyWURI++;
                } else {
                    entitiesAlchemy++;
                }
                NEAlchemy += "(" + surface + "," + URI + "," + types + "),";

            } else if (source.compareTo("babel") == 0) {
                if (URI.compareTo("") == 0) {
                    entitiesBabelwURI++;
                } else {
                    entitiesBabel++;
                }
                NEBabel += "(" + surface + "," + URI + "," + types + "),";

            } else if (source.compareTo("spotlight") == 0) {
                if (URI.compareTo("") == 0) {
                    entitiesSpotWURI++;
                } else {
                    entitiesSpot++;
                }
                NESpot += "(" + surface + "," + URI + "," + types + "),";
            }
        }
        String stats = "\"source: Babelfy\", " + "\"NE:" + entitiesBabel + "\",\"UE:" + entitiesBabelwURI + "\", \"Entities:" + NEBabel + "\"\n";
        stats += "\"source: Alchemy\", " + "\"NE:" + entitiesAlchemy + "\",\"UE:" + entitiesAlchemyWURI + "\", \"Entities:" + NEAlchemy + "\"\n";
        stats += "\"source: Spot\", " + "\"NE:" + entitiesSpot + "\",\"UE:" + entitiesSpotWURI + "\", \"Entities:" + NESpot + "\"";

        return stats;

    }

    public void alchemyObjects(String text) {

        JSONParser parser = new JSONParser();

        try {
            //parse puede recibir un String
//            Object obj = parser.parse(new FileReader("/Users/Jose/NetBeansProjects/KEToolsServices/data/example.json"));
            Object obj = parser.parse(text);

            JSONObject jsonObject = (JSONObject) obj;

            //String name = (String) jsonObject.get("status");
            JSONArray msg = (JSONArray) jsonObject.get("concepts");

            int count = msg.size(); // get totalCount of all jsonObjects
            for (int i = 0; i < count; i++) {   // iterate through jsonArray 
                JSONObject objc = (JSONObject) msg.get(i);  // get jsonObject @ i position 

                String entity = "";
                //in case of dbpedia url absence 
                if (objc.get("dbpedia") != null) {
                    entity = (String) objc.get("dbpedia");
                } else {
                    entity = (String) objc.get("freebase");

                }

                this.insertJarrayObject(objc.get("text"), entity,
                        objc.get("relevance"), "alchemy", "");

            }

            //entities that do not contain URI, only entities
            JSONArray msge = (JSONArray) jsonObject.get("entities");

            int countE = msge.size(); // get totalCount of all jsonObjects
            for (int i = 0; i < countE; i++) {   // iterate through jsonArray 
                JSONObject objc = (JSONObject) msge.get(i);  // get jsonObject @ i position 

                String entity = ""; //no Identifier
                this.insertJarrayObject(objc.get("text"), entity,
                        objc.get("relevance"), "alchemy", objc.get("type"));

            }

        } catch (ParseException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
//            Logger.getLogger(InvocationService.class.getName()).error("Null pointer alchemy " + text);

            System.out.println("Null pointer Alchemy: " + text);
        }

    }

    public void babelfyObjects(String text, String sentence) {
//        String[] tokens = sentence.split("\\W");

        JSONParser parser = new JSONParser();

        try {
            ///Object obj = parser.parse(new FileReader("/Users/Jose/NetBeansProjects/KEToolsServices/data/example3.json"));
            Object obj = parser.parse(text);

            JSONObject jsonObject = (JSONObject) obj;
            JSONArray msg = (JSONArray) jsonObject.get("concepts");
            // JSONArray msg = new JSONArray();

//            msg.add(parser.parse(text));
            int count = msg.size(); // get totalCount of all jsonObjects
            for (int i = 0; i < count; i++) {   // iterate through jsonArray 
                JSONObject objc = (JSONObject) msg.get(i);  // get jsonObject @ i position

                int tokenStart = Integer.parseInt(((JSONObject) (objc.get("charFragment"))).get("start").toString());
                int tokenEnd = Integer.parseInt(((JSONObject) (objc.get("charFragment"))).get("end").toString());

                String textEntity = sentence.subSequence(tokenStart-1, tokenEnd).toString();
//                for (int a = tokenStart; a <= tokenEnd; a++) {
//                    textEntity += tokens[a - 1] + " ";
//                }

                //crear el jobject y agregarle caracteristicas
                this.insertJarrayObject(textEntity.trim(), objc.get("DBpediaURL"),
                        objc.get("coherenceScore"), "babel", objc.get("babelSynsetID"));

            }

        } catch (ParseException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
//            Logger.getLogger(InvocationService.class.getName()).warn("Null pointer babelfy " + text);
            System.out.println("Null pointer Babelfy: "+text);
        }

    }

    public void spotLightObjects(String text) {

        JSONParser parser = new JSONParser();

        try {
            Object obj = parser.parse(text);

            JSONObject jsonObject = (JSONObject) obj;
            JSONArray msg = (JSONArray) jsonObject.get("Resources");

            int count = msg.size(); // get totalCount of all jsonObjects
            for (int i = 0; i < count; i++) {   // iterate through jsonArray 
                JSONObject objc = (JSONObject) msg.get(i);  // get jsonObject @ i position
                this.insertJarrayObject(objc.get("@surfaceForm"), objc.get("@URI"),
                        objc.get("@similarityScore"), "spotlight", objc.get("@types"));
            }

        } catch (ParseException e) {
            e.printStackTrace();
//            Logger.getLogger(InvocationService.class.getName()).warn("Spotlight parse Exception " + e.getMessage());

        } catch (NullPointerException e) {
//            Logger.getLogger(InvocationService.class.getName()).warn("Null pointer spotlight " + text + " " + e.getMessage());
            System.out.println("Null pointer Spotlight: " + text);
        }

    }

    /**
     * Create a JSONObject and append it to the JSONArray
     */
    public void insertJarrayObject(Object surface, Object URI, Object score, String source, Object types) {

        if (URI == null) {
            URI = "";
        }

        jobject = new JSONObject();
        jobject.put("surface", surface);
        jobject.put("URI", URI);
        jobject.put("score", score);
        jobject.put("source", source);
        jobject.put("types", types);

        jarray.add(jobject);

    }

    /**
     * Unescapes a string that contains standard Java escape sequences.
     * <ul>
     * <li><strong>&#92;b &#92;f &#92;n &#92;r &#92;t &#92;" &#92;'</strong> :
     * BS, FF, NL, CR, TAB, double and single quote.</li>
     * <li><strong>&#92;X &#92;XX &#92;XXX</strong> : Octal character
     * specification (0 - 377, 0x00 - 0xFF).</li>
     * <li><strong>&#92;uXXXX</strong> : Hexadecimal based Unicode
     * character.</li>
     * </ul>
     *
     * @param st A string optionally containing standard java escape sequences.
     * @return The translated string.
     */
    public String unescapeJavaString(String st) {

        StringBuilder sb = new StringBuilder(st.length());

        for (int i = 0; i < st.length(); i++) {
            char ch = st.charAt(i);
            if (ch == '\\') {
                char nextChar = (i == st.length() - 1) ? '\\' : st
                        .charAt(i + 1);
                // Octal escape?
                if (nextChar >= '0' && nextChar <= '7') {
                    String code = "" + nextChar;
                    i++;
                    if ((i < st.length() - 1) && st.charAt(i + 1) >= '0'
                            && st.charAt(i + 1) <= '7') {
                        code += st.charAt(i + 1);
                        i++;
                        if ((i < st.length() - 1) && st.charAt(i + 1) >= '0'
                                && st.charAt(i + 1) <= '7') {
                            code += st.charAt(i + 1);
                            i++;
                        }
                    }
                    sb.append((char) Integer.parseInt(code, 8));
                    continue;
                }
                switch (nextChar) {
                    case '\\':
                        ch = '\\';
                        break;
                    case 'b':
                        ch = '\b';
                        break;
                    case 'f':
                        ch = '\f';
                        break;
                    case 'n':
                        ch = '\n';
                        break;
                    case 'r':
                        ch = '\r';
                        break;
                    case 't':
                        ch = '\t';
                        break;
                    case '\"':
                        ch = '\"';
                        break;
                    case '\'':
                        ch = '\'';
                        break;
                    // Hex Unicode: u????
                    case 'u':
                        if (i >= st.length() - 5) {
                            ch = 'u';
                            break;
                        }
                        int code = Integer.parseInt(
                                "" + st.charAt(i + 2) + st.charAt(i + 3)
                                + st.charAt(i + 4) + st.charAt(i + 5), 16);
                        sb.append(Character.toChars(code));
                        i += 5;
                        continue;
                }
                i++;
            }
            sb.append(ch);
        }
        return sb.toString();
    }

//    public static void main(String[] args) {
//        JSONReader test = new JSONReader();
//
//        String text = "{\"concepts\":[{\"tokenFragment\":{\"start\":5,\"end\":5},\"charFragment\":{\"start\":15,\"end\":20},\"babelSynsetID\":\"bn:00027192n\",\"DBpediaURL\":\"http://dbpedia.org/resource/Rowing\",\"BabelNetURL\":\"http://babelnet.org/rdf/s00027192n\",\"score\":1.0,\"coherenceScore\":0.07142857142857142,\"globalScore\":4.5392646391284613E-4,\"source\":\"BABELFY\"},{\"tokenFragment\":{\"start\":5,\"end\":6},\"charFragment\":{\"start\":15,\"end\":23},\"babelSynsetID\":\"bn:02664190n\",\"DBpediaURL\":\"http://dbpedia.org/resource/Tu_Youyou\",\"BabelNetURL\":\"http://babelnet.org/rdf/s02664190n\",\"score\":1.0,\"coherenceScore\":0.35714285714285715,\"globalScore\":0.024965955515206535,\"source\":\"BABELFY\"},{\"tokenFragment\":{\"start\":6,\"end\":6},\"charFragment\":{\"start\":22,\"end\":23},\"babelSynsetID\":\"bn:00114626r\",\"DBpediaURL\":\"\",\"BabelNetURL\":\"http://babelnet.org/rdf/s00114626r\",\"score\":1.0,\"coherenceScore\":0.07142857142857142,\"globalScore\":4.5392646391284613E-4,\"source\":\"BABELFY\"},{\"tokenFragment\":{\"start\":8,\"end\":8},\"charFragment\":{\"start\":28,\"end\":32},\"babelSynsetID\":\"bn:00016756n\",\"DBpediaURL\":\"http://dbpedia.org/resource/Cathay\",\"BabelNetURL\":\"http://babelnet.org/rdf/s00016756n\",\"score\":0.8023774145616642,\"coherenceScore\":0.8571428571428571,\"globalScore\":0.2451202905129369,\"source\":\"BABELFY\"},{\"tokenFragment\":{\"start\":9,\"end\":9},\"charFragment\":{\"start\":34,\"end\":39},\"babelSynsetID\":\"bn:00095233v\",\"DBpediaURL\":\"\",\"BabelNetURL\":\"http://babelnet.org/rdf/s00095233v\",\"score\":0.0,\"coherenceScore\":0.0,\"globalScore\":0.0,\"source\":\"MCS\"},{\"tokenFragment\":{\"start\":11,\"end\":11},\"charFragment\":{\"start\":44,\"end\":54},\"babelSynsetID\":\"bn:00112044a\",\"DBpediaURL\":\"\",\"BabelNetURL\":\"http://babelnet.org/rdf/s00112044a\",\"score\":1.0,\"coherenceScore\":0.07142857142857142,\"globalScore\":4.5392646391284613E-4,\"source\":\"BABELFY\"},{\"tokenFragment\":{\"start\":12,\"end\":12},\"charFragment\":{\"start\":56,\"end\":61},\"babelSynsetID\":\"bn:00043783n\",\"DBpediaURL\":\"http://dbpedia.org/resource/Herbal_tea\",\"BabelNetURL\":\"http://babelnet.org/rdf/s00043783n\",\"score\":0.0,\"coherenceScore\":0.0,\"globalScore\":0.0,\"source\":\"MCS\"},{\"tokenFragment\":{\"start\":12,\"end\":13},\"charFragment\":{\"start\":56,\"end\":70},\"babelSynsetID\":\"bn:00043785n\",\"DBpediaURL\":\"\",\"BabelNetURL\":\"http://babelnet.org/rdf/s00043785n\",\"score\":0.0,\"coherenceScore\":0.0,\"globalScore\":0.0,\"source\":\"MCS\"},{\"tokenFragment\":{\"start\":13,\"end\":13},\"charFragment\":{\"start\":63,\"end\":70},\"babelSynsetID\":\"bn:00054126n\",\"DBpediaURL\":\"http://dbpedia.org/resource/History_of_medicine\",\"BabelNetURL\":\"http://babelnet.org/rdf/s00054126n\",\"score\":0.0,\"coherenceScore\":0.0,\"globalScore\":0.0,\"source\":\"MCS\"},{\"tokenFragment\":{\"start\":15,\"end\":15},\"charFragment\":{\"start\":75,\"end\":80},\"babelSynsetID\":\"bn:00094723v\",\"DBpediaURL\":\"\",\"BabelNetURL\":\"http://babelnet.org/rdf/s00094723v\",\"score\":0.0,\"coherenceScore\":0.0,\"globalScore\":0.0,\"source\":\"MCS\"},{\"tokenFragment\":{\"start\":17,\"end\":17},\"charFragment\":{\"start\":86,\"end\":94},\"babelSynsetID\":\"bn:00017557n\",\"DBpediaURL\":\"\",\"BabelNetURL\":\"http://babelnet.org/rdf/s00017557n\",\"score\":0.0,\"coherenceScore\":0.0,\"globalScore\":0.0,\"source\":\"MCS\"},{\"tokenFragment\":{\"start\":19,\"end\":19},\"charFragment\":{\"start\":99,\"end\":108},\"babelSynsetID\":\"bn:00086733v\",\"DBpediaURL\":\"\",\"BabelNetURL\":\"http://babelnet.org/rdf/s00086733v\",\"score\":0.0,\"coherenceScore\":0.0,\"globalScore\":0.0,\"source\":\"MCS\"},{\"tokenFragment\":{\"start\":20,\"end\":20},\"charFragment\":{\"start\":110,\"end\":114},\"babelSynsetID\":\"bn:00103328a\",\"DBpediaURL\":\"\",\"BabelNetURL\":\"http://babelnet.org/rdf/s00103328a\",\"score\":0.0,\"coherenceScore\":0.0,\"globalScore\":0.0,\"source\":\"MCS\"},{\"tokenFragment\":{\"start\":21,\"end\":21},\"charFragment\":{\"start\":116,\"end\":122},\"babelSynsetID\":\"bn:00052944n\",\"DBpediaURL\":\"http://dbpedia.org/resource/Malaria\",\"BabelNetURL\":\"http://babelnet.org/rdf/s00052944n\",\"score\":0.0,\"coherenceScore\":0.0,\"globalScore\":0.0,\"source\":\"MCS\"},{\"tokenFragment\":{\"start\":22,\"end\":22},\"charFragment\":{\"start\":124,\"end\":132},\"babelSynsetID\":\"bn:00076843n\",\"DBpediaURL\":\"http://dbpedia.org/resource/Therapy\",\"BabelNetURL\":\"http://babelnet.org/rdf/s00076843n\",\"score\":0.9565217391304348,\"coherenceScore\":0.42857142857142855,\"globalScore\":0.05991829323649569,\"source\":\"BABELFY\"}]}";
//
//        String array = "[{\"tokenFragment\":{\"start\":5,\"end\":5},\"charFragment\":{\"start\":15,\"end\":20},\"babelSynsetID\":\"bn:00027192n\",\"DBpediaURL\":\"http://dbpedia.org/resource/Rowing\",\"BabelNetURL\":\"http://babelnet.org/rdf/s00027192n\",\"score\":1.0,\"coherenceScore\":0.07142857142857142,\"globalScore\":4.5392646391284613E-4,\"source\":\"BABELFY\"},{\"tokenFragment\":{\"start\":5,\"end\":6},\"charFragment\":{\"start\":15,\"end\":23},\"babelSynsetID\":\"bn:02664190n\",\"DBpediaURL\":\"http://dbpedia.org/resource/Tu_Youyou\",\"BabelNetURL\":\"http://babelnet.org/rdf/s02664190n\",\"score\":1.0,\"coherenceScore\":0.35714285714285715,\"globalScore\":0.024965955515206535,\"source\":\"BABELFY\"},{\"tokenFragment\":{\"start\":6,\"end\":6},\"charFragment\":{\"start\":22,\"end\":23},\"babelSynsetID\":\"bn:00114626r\",\"DBpediaURL\":\"\",\"BabelNetURL\":\"http://babelnet.org/rdf/s00114626r\",\"score\":1.0,\"coherenceScore\":0.07142857142857142,\"globalScore\":4.5392646391284613E-4,\"source\":\"BABELFY\"},{\"tokenFragment\":{\"start\":8,\"end\":8},\"charFragment\":{\"start\":28,\"end\":32},\"babelSynsetID\":\"bn:00016756n\",\"DBpediaURL\":\"http://dbpedia.org/resource/Cathay\",\"BabelNetURL\":\"http://babelnet.org/rdf/s00016756n\",\"score\":0.8023774145616642,\"coherenceScore\":0.8571428571428571,\"globalScore\":0.2451202905129369,\"source\":\"BABELFY\"},{\"tokenFragment\":{\"start\":9,\"end\":9},\"charFragment\":{\"start\":34,\"end\":39},\"babelSynsetID\":\"bn:00095233v\",\"DBpediaURL\":\"\",\"BabelNetURL\":\"http://babelnet.org/rdf/s00095233v\",\"score\":0.0,\"coherenceScore\":0.0,\"globalScore\":0.0,\"source\":\"MCS\"},{\"tokenFragment\":{\"start\":11,\"end\":11},\"charFragment\":{\"start\":44,\"end\":54},\"babelSynsetID\":\"bn:00112044a\",\"DBpediaURL\":\"\",\"BabelNetURL\":\"http://babelnet.org/rdf/s00112044a\",\"score\":1.0,\"coherenceScore\":0.07142857142857142,\"globalScore\":4.5392646391284613E-4,\"source\":\"BABELFY\"},{\"tokenFragment\":{\"start\":12,\"end\":12},\"charFragment\":{\"start\":56,\"end\":61},\"babelSynsetID\":\"bn:00043783n\",\"DBpediaURL\":\"http://dbpedia.org/resource/Herbal_tea\",\"BabelNetURL\":\"http://babelnet.org/rdf/s00043783n\",\"score\":0.0,\"coherenceScore\":0.0,\"globalScore\":0.0,\"source\":\"MCS\"},{\"tokenFragment\":{\"start\":12,\"end\":13},\"charFragment\":{\"start\":56,\"end\":70},\"babelSynsetID\":\"bn:00043785n\",\"DBpediaURL\":\"\",\"BabelNetURL\":\"http://babelnet.org/rdf/s00043785n\",\"score\":0.0,\"coherenceScore\":0.0,\"globalScore\":0.0,\"source\":\"MCS\"},{\"tokenFragment\":{\"start\":13,\"end\":13},\"charFragment\":{\"start\":63,\"end\":70},\"babelSynsetID\":\"bn:00054126n\",\"DBpediaURL\":\"http://dbpedia.org/resource/History_of_medicine\",\"BabelNetURL\":\"http://babelnet.org/rdf/s00054126n\",\"score\":0.0,\"coherenceScore\":0.0,\"globalScore\":0.0,\"source\":\"MCS\"},{\"tokenFragment\":{\"start\":15,\"end\":15},\"charFragment\":{\"start\":75,\"end\":80},\"babelSynsetID\":\"bn:00094723v\",\"DBpediaURL\":\"\",\"BabelNetURL\":\"http://babelnet.org/rdf/s00094723v\",\"score\":0.0,\"coherenceScore\":0.0,\"globalScore\":0.0,\"source\":\"MCS\"},{\"tokenFragment\":{\"start\":17,\"end\":17},\"charFragment\":{\"start\":86,\"end\":94},\"babelSynsetID\":\"bn:00017557n\",\"DBpediaURL\":\"\",\"BabelNetURL\":\"http://babelnet.org/rdf/s00017557n\",\"score\":0.0,\"coherenceScore\":0.0,\"globalScore\":0.0,\"source\":\"MCS\"},{\"tokenFragment\":{\"start\":19,\"end\":19},\"charFragment\":{\"start\":99,\"end\":108},\"babelSynsetID\":\"bn:00086733v\",\"DBpediaURL\":\"\",\"BabelNetURL\":\"http://babelnet.org/rdf/s00086733v\",\"score\":0.0,\"coherenceScore\":0.0,\"globalScore\":0.0,\"source\":\"MCS\"},{\"tokenFragment\":{\"start\":20,\"end\":20},\"charFragment\":{\"start\":110,\"end\":114},\"babelSynsetID\":\"bn:00103328a\",\"DBpediaURL\":\"\",\"BabelNetURL\":\"http://babelnet.org/rdf/s00103328a\",\"score\":0.0,\"coherenceScore\":0.0,\"globalScore\":0.0,\"source\":\"MCS\"},{\"tokenFragment\":{\"start\":21,\"end\":21},\"charFragment\":{\"start\":116,\"end\":122},\"babelSynsetID\":\"bn:00052944n\",\"DBpediaURL\":\"http://dbpedia.org/resource/Malaria\",\"BabelNetURL\":\"http://babelnet.org/rdf/s00052944n\",\"score\":0.0,\"coherenceScore\":0.0,\"globalScore\":0.0,\"source\":\"MCS\"},{\"tokenFragment\":{\"start\":22,\"end\":22},\"charFragment\":{\"start\":124,\"end\":132},\"babelSynsetID\":\"bn:00076843n\",\"DBpediaURL\":\"http://dbpedia.org/resource/Therapy\",\"BabelNetURL\":\"http://babelnet.org/rdf/s00076843n\",\"score\":0.9565217391304348,\"coherenceScore\":0.42857142857142855,\"globalScore\":0.05991829323649569,\"source\":\"BABELFY\"}]";
//
//        String textToT = "At that time, Youyou Tu in China turned to traditional herbal medicine to tackle the challenge of developing novel Malaria therapies";
//
//        System.out.println("------");
//        // test.babelfyObjects(text, textToT);
//        // test.spotLightObjects(text);
//
//        test.babelfyObjects(text, textToT);
//        test.printJArray();
//
//    }

}
