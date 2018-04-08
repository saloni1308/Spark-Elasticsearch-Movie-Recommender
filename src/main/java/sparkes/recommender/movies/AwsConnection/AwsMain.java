package sparkes.recommender.movies.AwsConnection;

import com.amazonaws.auth.*;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NStringEntity;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

public class AwsMain {

  private static String serviceName = "es";
  private static String region = "ap-south-1";
  private static String aesEndpoint = "https://search-testing-es-7rm3haacj5orszjxrrb3svhcau.ap-south-1.es.amazonaws.com";

  private static String payload = "{ \"type\": \"s3\", \"settings\": { \"bucket\": \"your-bucket\", \"region\": \"ap-south-1\", \"role_arn\": \"arn:aws:es:ap-south-1:191155221734:domain/testing-es\" } }";
  private static String snapshotPath = "/snapshot/my-snapshot-repo";

  private static String sampleDocument = "{" + "\"title\":\"Walk the Line\"," + "\"director\":\"James Mangold\"," + "\"year\":\"2005\"}";
  private static String indexingPath = "/i_movie/movies";

  static final BasicAWSCredentials credentials = new BasicAWSCredentials("AKIAIKRSZLQQA3OMB7TA", "Cnni6qZ2H3BmmWfpNjRpxvJZQ5xloOHU34RL8gcL");
  static final AWSCredentialsProvider credentialsProvider = new AWSStaticCredentialsProvider(credentials);

  public static void main(String[] args) throws IOException {
    RestClient esClient = esClient(serviceName, region);

    // Register a snapshot repository
    HttpEntity entity = new NStringEntity(payload, ContentType.APPLICATION_JSON);
    Map<String, String> params = Collections.emptyMap();

    Response response = esClient.performRequest("POST", snapshotPath, params, entity);
    System.out.println(response.toString());

    // Index a document
    entity = new NStringEntity(sampleDocument, ContentType.APPLICATION_JSON);
    String id = "1";
    //response = esClient.performRequest("PUT", indexingPath + "/" + id, params, entity);
    response = esClient.performRequest("DELETE", indexingPath + "/" + id, params, entity);
    System.out.println(response.toString());
  }

  // Adds the interceptor to the ES REST client
  public static RestClient esClient(String serviceName, String region) {
    AWS4Signer signer = new AWS4Signer();
    signer.setServiceName(serviceName);
    signer.setRegionName(region);
    HttpRequestInterceptor interceptor = new AWSRequestSigningApacheInterceptor(serviceName, signer, credentialsProvider);
    return RestClient.builder(HttpHost.create(aesEndpoint)).setHttpClientConfigCallback(hacb -> hacb.addInterceptorLast(interceptor)).build();
  }
}