package basic;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentMap;

import org.junit.Test;

import domain.csv.DataWrapper;
import edu.emory.mathcs.backport.java.util.Arrays;
import parser.YahooParser;

public class YahooParserTest {

	@Test
	public final void simpleTest() {
		assertTrue(YahooParser.getDataWrapper("INTC").getTicker().equalsIgnoreCase("INTC"));
	}
	
	@Test
	public final void nonNullTest(){
			nonNull("AMZN");
			nonNull("V");
			nonNull("SLV");
		
	}
	
	@Test
	public final void nonNullSetTest(){
		List<String> testList = new ArrayList<>();
		testList.addAll(Arrays.asList(new String[]{"INTC", "AMD", "AMZN", "ARMH", "SLV", "V", "BABA"}));
		nonNullSet(testList);
	}

	private void nonNull(String tickerSymbol) {
		final DataWrapper testWrap = YahooParser.getDataWrapper(tickerSymbol);
		assertTrue(testWrap!=null);
		assertTrue(testWrap.getRecords().keySet().size()>0);
		testWrap.getRecords().keySet().parallelStream().forEach(((t)-> {
			assertTrue(testWrap.getRecords().get(t)!=null);
		}));
	}
	
	private void nonNullSet(List<String> tickerSymbol) {
		ConcurrentMap<String, DataWrapper> map = YahooParser.getDataWrapperMap(tickerSymbol);
		assertTrue(map.keySet().size() == tickerSymbol.size());
		map.keySet().parallelStream().forEach((k) -> assertTrue(map.get(k).getRecords().keySet().size()>0));
		map.keySet().parallelStream().forEach((k)-> {
			map.get(k).getRecords().keySet().parallelStream().forEach(((t)-> {
				assertTrue(map.get(k).getRecords().get(t)!=null);
				//System.out.println(map.get(k).getRecords().get(t).getHigh());
			}));
		});
	}

}
