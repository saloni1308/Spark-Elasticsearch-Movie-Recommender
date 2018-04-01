package sparkes.recommender.movies;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NStringEntity;
import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.mllib.recommendation.Rating;
import org.apache.spark.sql.*;
import org.apache.spark.sql.Row;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.spark.rdd.api.java.JavaEsSpark;
import scala.Tuple2;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Map;

public class Testing implements Serializable {
  static SparkConf conf;
  static JavaSparkContext jsc;
  static SparkSession sparkSession;
  static SparkContext sc;
  static RestClient client;


  Dataset<Row> dataTest1;
  Dataset<Row> dataTest2;

  public void initializeSession() {
    //spark
    conf = new SparkConf().setAppName("Movie Recommender").setMaster("local[*]");
    sc = new SparkContext(conf);
    jsc = JavaSparkContext.fromSparkContext(sc);
    sparkSession = new SparkSession(sc);

    //elasticsearch
    client = RestClient.builder(new HttpHost("localhost", 9200, "http")).build();
  }


  private void closeESClientConnection(RestClient client) {
    try {
      client.close();
    } catch (IOException e) {
      e.printStackTrace();
    }

  }


  public static void main(String arg[]) throws IOException {
    // initialize spark session
    Testing driver = new Testing();
    driver.initializeSession();
    sparkSession.close();

  }


}
