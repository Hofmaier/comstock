package ch.hsr.evaluator;

import junit.framework.TestCase;

import org.junit.Test;

public class EvaluatorTest extends TestCase{

	@Test
	public void testRelevantItems() {
	}

	@Test
	public void testItembased() {
		RecommenderFactory test = new RecommenderFactory();
		assertNotNull(test.itembased()); 
	}

	@Test
	public void testRandom() {
		RecommenderFactory test = new RecommenderFactory();
		assertNotNull(test.random());
	}

}
