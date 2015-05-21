package basic;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentMap;

import org.junit.Test;

import domain.DataRecord;
import domain.DataWrapper;
import parser.YahooParser;

public class YahooParserTest {

	@Test
	public final void simpleTest() {
		assertTrue(YahooParser.getDataWrapperFn("INTC").getTicker().equalsIgnoreCase("INTC"));
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
	
	@Test
	public final void nonDuplicateTest(){
		List<String> testList = new ArrayList<>();
		testList.addAll(Arrays.asList(new String[]{"INTC", "AMD", "AMZN", "ARMH", "SLV", "V", "BABA"}));
		ConcurrentMap<String, DataWrapper> maps = YahooParser.getDataWrapperMapFn(testList);
		if(maps.values().parallelStream().distinct().count()!=testList.size()){
			fail("wrong result size");
		}
		maps.values().parallelStream().forEach((d)->{
			assertEquals(
					
					d.getRecords().values().parallelStream().mapToDouble(DataRecord::getTimestamp).distinct().count(),
					
					d.getRecords().keySet().parallelStream().count());
			
			assertTrue(d.getRecords().keySet().parallelStream().count()>0);
			
		});
	}

	private void nonNull(String tickerSymbol) {
		final DataWrapper testWrap = YahooParser.getDataWrapperFn(tickerSymbol);
		assertTrue(testWrap!=null);
		assertTrue(testWrap.getRecords().keySet().size()>0);
		testWrap.getRecords().keySet().parallelStream().forEach(((t)-> {
			assertTrue(testWrap.getRecords().get(t)!=null);
		}));
	}
	
	private void nonNullSet(List<String> tickerSymbol) {
		ConcurrentMap<String, DataWrapper> map = YahooParser.getDataWrapperMapFn(tickerSymbol);
		assertTrue(map.keySet().size() == tickerSymbol.size());
		map.keySet().parallelStream().forEach((k) -> assertTrue(map.get(k).getRecords().keySet().size()>0));
		map.keySet().parallelStream().forEach((k)-> {
			map.get(k).getRecords().keySet().parallelStream().forEach(((t)-> {
				assertTrue(map.get(k).getRecords().get(t)!=null);
			}));
		});
	}

}
