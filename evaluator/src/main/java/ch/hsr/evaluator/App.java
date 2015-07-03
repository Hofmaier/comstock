package ch.hsr.evaluator;

import java.io.File;
import java.io.IOException;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.eval.IRStatistics;
import org.apache.mahout.cf.taste.eval.RecommenderIRStatsEvaluator;
import org.apache.mahout.cf.taste.impl.eval.GenericRecommenderIRStatsEvaluator;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.model.DataModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class App 
{
    public static void main( String[] args )
    {
    	String smallinput = "/home/lukas/comstock/data/d";
    	String mlratings = "/home/lukas/Downloads/ml-latest-small/ratings.csv";
    	String input = mlratings;
    	int precisionat = 30;
    	double evaluationPercentage = 1.0;
    	double threshhold = 5.0;
    	try {
			DataModel dataModel = new FileDataModel(new File(input));
			RecommenderIRStatsEvaluator evaluator = new GenericRecommenderIRStatsEvaluator();
			Logger log = LoggerFactory.getLogger(App.class);
			Evaluator recommenderfactory = new Evaluator(); 
			//recommenderfactory.recall(dataModel);
			IRStatistics itemirstats = null;

					
			
			itemirstats = evaluator.evaluate(recommenderfactory.itembased(), 
					null, 
					dataModel, 
					null,
					precisionat,
					threshhold, 
					evaluationPercentage);
					
			log.info(itemirstats.toString());
		} catch (IOException e) {
			e.printStackTrace();
		} catch (TasteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

}
