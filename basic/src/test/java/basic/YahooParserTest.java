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
		for(int x = 0; x < 10; x++){
			nonNull("AMZN");
			nonNull("V");
			nonNull("SLV");
		}
	}

	private void nonNull(String tickerSymbol) {
		final DataWrapper testWrap = YahooParser.getDataWrapper(tickerSymbol);
		assertTrue(testWrap!=null);
		assertTrue(testWrap.getRecords().keySet().size()>0);
		testWrap.getRecords().keySet().parallelStream().forEach(((t)-> {
			assertTrue(testWrap.getRecords().get(t)!=null);
		}));
	}

}
