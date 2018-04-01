package sparkes.recommender.movies;

import org.apache.spark.ml.recommendation.ALSModel;

import com.esotericsoftware.kryo.Kryo;

import org.apache.spark.serializer.KryoRegistrator;

public class I_KryoRegistrator implements KryoRegistrator{

  @Override
  public void registerClasses(final Kryo kryo) {
    kryo.register(ALSModel.class);
    kryo.register(Recommender.class);
    kryo.register(FeatureVector.class);
    kryo.register(Movie.class);
    kryo.register(QueryBuilder.class);
    kryo.register(Main.class);
  }
}
