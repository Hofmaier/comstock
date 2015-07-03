package ch.hsr.evaluator;

import junit.framework.TestCase;

import org.junit.Test;

public class EvaluatorTest extends TestCase{

	@Test
	public void testRelevantItems() {
	}

	@Test
	public void testItembased() {
		Evaluator test = new Evaluator();
		assertNotNull(test.itembased()); 
	}

	@Test
	public void testRandom() {
		Evaluator test = new Evaluator();
		assertNotNull(test.random());
	}

}
