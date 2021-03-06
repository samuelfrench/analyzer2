package basic;

import static org.junit.Assert.*;

import java.util.List;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import domain.DataWrapper;
import parser.YahooParser;
import parser.support.DataWrapperSMAFunc;

public class DataWrapperSMAFuncTest {
	
	//@Test
	public final void testAddSMARangeData() {
		fail("Not yet implemented"); // TODO
	}

	/*
	 * This test is used by manually checking against the file in excel TODO
	 */
	@Test
	public final void testGetHighLowDiff() {
		DataWrapper dataWrapper = YahooParser.getDataWrapperFn("AMZN");
		List<Long> ts = dataWrapper.getRecords().keySet().stream().sorted().collect(Collectors.toList());
		final ConcurrentMap<Long, Double> resultMap = DataWrapperSMAFunc.getHighLowDiff(dataWrapper);
		if(ts.size() != resultMap.keySet().size()){
			fail("Incorrect result size");
		}
		resultMap.keySet().stream().sorted().distinct().forEach((k)->{
			System.out.println("k: " + k + ", v: " + resultMap.get(k));
		});
	}

	
	/*
	 * TODO - verify correct output
	 */
	@Test
	public final void testGetCloseOpenDiff() {
		DataWrapper dataWrapper = YahooParser.getDataWrapperFn("AMZN");
		List<Long> ts = dataWrapper.getRecords().keySet().stream().sorted().collect(Collectors.toList());
		final ConcurrentMap<Long, Double> resultMap = DataWrapperSMAFunc.getHighLowDiff(dataWrapper);
		if(ts.size() != resultMap.keySet().size()){
			fail("Incorrect result size");
		}
		resultMap.keySet().stream().sorted().distinct().forEach((k)->{
			System.out.println("k: " + k + ", v: " + resultMap.get(k));
		});
		fail("Not yet implemented"); // TODO
	}

	//@Test
	public final void testGetUpwardMovement() {
		fail("Not yet implemented"); // TODO
	}

}
