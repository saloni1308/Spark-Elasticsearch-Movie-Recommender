package sparkes.recommender.movies;

import org.json.simple.JSONObject;

public class Query_json {
  String q = "q";
  String query_vector = "query_vector";
  String cosine = "cosine";
  JSONObject query = new JSONObject();

  void generate_query() {
    JSONObject function_score = new JSONObject();
    JSONObject script_score = new JSONObject();
    JSONObject query_string = new JSONObject();
    JSONObject query_json = new JSONObject();
    JSONObject script = new JSONObject();
    JSONObject params = new JSONObject();
    params.put("field", "@model.factor");
    params.put("vector", query_vector);
    params.put("cosine", cosine);
    script.put("params", params);
    script.put("lang", "native");
    script.put("inline", "payload_vector_score");
    script_score.put("script", script);
    query_json.put("query", q);
    query_string.put("query_string", query_json);
    function_score.put("query", query_string);
    function_score.put("script_score", script_score);
    function_score.put("boost_mode", "replace");
    query.put("function_score", function_score);
  }

  public static void main(String arg[]) {
    Query_json query_json =new Query_json();
    query_json.generate_query();
    System.out.println(query_json.query.toJSONString());
  }
}
