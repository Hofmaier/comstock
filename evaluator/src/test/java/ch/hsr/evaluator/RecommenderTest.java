package ch.hsr.evaluator;

import java.io.File;
import java.io.IOException;
import java.util.List;

import junit.framework.TestCase;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.solr.client.solrj.SolrQuery;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class RecommenderTest extends TestCase{
	
	private DataModel datamodel;

	@Before
	public void setUp() throws IOException{
		datamodel = new FileDataModel(new File("../data/ml-latest-small/ratings.csv"));
	}
	
	@Test
	public void testTransformToList() {
		try {
		MostPopular recommender = new MostPopular(this.datamodel);
		int topnsize = 10;
		long userid1 = 1;
		long userid2 = 2;
		
		List<RecommendedItem> recommendationsUser1 = recommender.recommend(userid1, topnsize);
		List<RecommendedItem> recommendationsUser2 = recommender.recommend(userid2, topnsize);
		RecommendedItem firstitemUser1 = recommendationsUser1.get(1);
		RecommendedItem firstitemUser2 = recommendationsUser2.get(1);
		assertEquals(318l, firstitemUser1.getItemID());
		assertEquals(firstitemUser1, firstitemUser2);
		} catch (TasteException e) {
			Assert.fail(e.getMessage());
	}
	}
	
	@Test
	public void testCreateSolrQuery() {
		LLRRecommender recommender = new LLRRecommender(datamodel, datamodel, "");
		long userid1 = 1;
		try {
			SolrQuery solrQuery = recommender.createSolrQuery(userid1, 10);
			assertNotNull(solrQuery);
		} catch (TasteException e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testRecommenderFactory() {
		RecommenderFactory test = new RecommenderFactory("", datamodel, datamodel);
		assertNotNull(test.random());
		assertNotNull(test.llrRecommender());
		assertNotNull(test.mostpopular());
	}


}
