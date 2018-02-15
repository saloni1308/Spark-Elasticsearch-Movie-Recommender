package sparkes.recommender.movies;

import org.json.simple.JSONObject;

public class QueryBuilder {

    String getMovieRecommenderQuery(final String query, final String query_vector, final boolean cosine) {

        JSONObject generatedQuery = new JSONObject();
        JSONObject query_top = new JSONObject();
        JSONObject function_score = new JSONObject();
        JSONObject script_score = new JSONObject();
        JSONObject query_string = new JSONObject();
        JSONObject query_json = new JSONObject();
        JSONObject script = new JSONObject();
        JSONObject params = new JSONObject();
        params.put("field", "factor");
        params.put("vector", query_vector);
        params.put("cosine", cosine);
        script.put("params", params);
        script.put("lang", "native");
        script.put("inline", "payload_vector_score");
        script_score.put("script", script);
        query_json.put("query", query);
        query_string.put("query_string", query_json);
        function_score.put("query", query_string);
        function_score.put("script_score", script_score);
        function_score.put("boost_mode", "replace");
        query_top.put("function_score", function_score);
        generatedQuery.put("query", query_top);
        System.out.println(generatedQuery.toString());
        return "{\n    \"query\": {\n        \"function_score\": {\n            \"query\" : {\n                \"query_string\": {\n                    \"query\":\"" + query + "\"\n                }\n            },\n            \"script_score\": {\n                \"script\": {\n                        \"inline\": \"payload_vector_score\",\n                        \"lang\": \"native\",\n                        \"params\": {\n                            \"field\": \"factor\",\n                            \"vector\":\"" + query_vector + "\",\n                            \"cosine\" :" + cosine + "\n                        }\n                    }\n            },\n            \"boost_mode\": \"replace\"\n        }\n    }\n} ";
        //  return generatedQuery.toString();
    }
}
