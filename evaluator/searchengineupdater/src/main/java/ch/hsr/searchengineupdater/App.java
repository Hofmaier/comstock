package ch.hsr.searchengineupdater;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.similarity.LogLikelihoodSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.UncenteredCosineSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;
import org.apache.solr.common.SolrInputDocument;


/**
 * Hello world!
 *
 */
public class App {
	public static void main(String[] args) {
		if (args.length > 0) {
			if(args[0].equals("-full")){
				fullupdate();
			}
			if(args[0].equals("-update")){
				updateSimilarities();
			};
		}
		else{
			System.out.println("usage: searchengineupdater [-full -file preferencefile | -db sqlitefile] \n"
					+ "-update -file preferencefile");
		}
		fullupdate();
	}
	private static void updateSimilarities(){
		SolrInputFactory inputfactory = new SolrInputFactory();
		
	}

	private static void fullupdate() {
		try {
			String mlratings = "/home/lukas/Downloads/ml-latest-small/ratings.csv";
			String tagdataset = "/home/lukas/comstock/data/tagid-itemid.csv";
			String moviecsvpath = "/home/lukas/Downloads/ml-latest-small/movies.csv";
			String input = mlratings;
			
			DataModel dataModel;
			dataModel = new FileDataModel(new File(input));
			DataModel tagDataModel = new FileDataModel(new File(tagdataset));
			SolrInputFactory inputfactory = new SolrInputFactory();
			ItemSimilarity similarity = new UncenteredCosineSimilarity(
					dataModel);
			ItemSimilarity tagsimilarity = new LogLikelihoodSimilarity(
					tagDataModel);

			PreferenceConverter converter = new PreferenceConverter();
			DataModel likes = converter.pref2like(dataModel, 4.0f);
			ItemSimilarity llrsimilarity = new LogLikelihoodSimilarity(likes);
			DataReader datareader = new DataReader();
			Map<Long, Movie> movies = datareader.readMovies(moviecsvpath);
			List<SolrInputDocument> documents = inputfactory.createSolrDocs(
					dataModel, tagsimilarity, similarity, llrsimilarity,movies);
			SearchEngine searchEngine = new SearchEngine();
			searchEngine.update(documents);

		} catch (IOException e) {
			e.printStackTrace();
		} catch (TasteException e) {
			e.printStackTrace();
		}
	}
}
