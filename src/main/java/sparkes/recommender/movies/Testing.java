package sparkes.recommender.movies;

import org.json.simple.JSONObject;

public class Testing {

    private String mergeJson(final JSONObject a, final JSONObject b) {

        return a.toString()+","+b.toString();
    }

    public static void main(String[] ars) {
        Testing driver = new Testing();
        JSONObject query_json = new JSONObject();
        JSONObject function_score_json = new JSONObject();
        JSONObject sub_query_json = new JSONObject();
        JSONObject sub_query_string_json = new JSONObject();
        JSONObject sub_sub_query_json = new JSONObject();
        JSONObject script_score_json = new JSONObject();
        JSONObject script_json = new JSONObject();
        JSONObject inline_json = new JSONObject();
        JSONObject script_params_json = new JSONObject();

       // JSONObject sub_sub_query_json = new JSONObject();

        sub_sub_query_json.put("query", "NOT A");
        sub_query_string_json.put("query_string",sub_sub_query_json );
        sub_query_json.put("query", sub_query_string_json);


        script_params_json.put("field", "@model.factor");
        script_params_json.put("vector", "query_vector");
        script_params_json.put("cosine", "cosine");
        inline_json.put("inline", "payload_vector_score");
        inline_json.put("lang", "native");
        inline_json.put("params", script_params_json);
        script_json.put("script",inline_json);
        script_score_json.put("script_score", script_json);



        String merged_json = driver.mergeJson(sub_query_json, script_json);

        function_score_json.put("function_score", merged_json);
        query_json.put("query", function_score_json);

        System.out.println(query_json.toJSONString());
/*        System.out.println(script_score_json.toJSONString());
        System.out.println(query_json.toJSONString());*/
    }

}
