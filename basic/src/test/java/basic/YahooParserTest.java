package basic;

import static org.junit.Assert.*;

import org.junit.Test;

import domain.csv.DataWrapper;
import parser.YahooParser;

public class YahooParserTest {

	@Test
	public final void simpleTest() {
		assertTrue(YahooParser.getDataWrapper("INTC").getTicker().equalsIgnoreCase("INTC"));
	}
	
	@Test
	public final void nonNullTest(){
		final DataWrapper testWrap = YahooParser.getDataWrapper("AMZN");
		assertTrue(testWrap!=null);
		assertTrue(testWrap.getRecords().keySet().size()>0);
		testWrap.getRecords().keySet().stream().forEachOrdered(((t)-> {
			assertTrue(testWrap.getRecords().get(t)!=null);
		}));
	}

}
