package basic;

import static org.junit.Assert.*;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;

import parser.YahooParser;
import parser.support.DataWrapperSMAFunc;
import parser.support.DataWrapperTrendFunc;
import domain.DataWrapper;
import domain.SMAMomentumBoolMatrix.SHIFT;

public class DataWrapperTrendFuncTest {

	private ConcurrentMap<Long, ConcurrentMap<Long, Optional<Double>>> testSMA;
	
	@Before
	public void setUp() throws Exception {
		DataWrapper d = YahooParser.getDataWrapperFn("AMZN");
		DataWrapperSMAFunc.addSMARangeData(d, true);
		testSMA = d.getsMAMatrix().get();
	}

	@Test
	public final void testEmptyInit() {
		final ConcurrentMap<Long, ConcurrentMap<Long, Optional<Double>>> testSMACopy = testSMA;
		final ConcurrentMap<Long, ConcurrentMap<Long, SHIFT>> emptyShiftMap = DataWrapperTrendFunc.prepareEmptySMAChange(testSMACopy);
		List<Long> timeStamps = testSMACopy.keySet().stream().sorted().collect(Collectors.toList());
		long lastVal = -1;
		for(Long l: timeStamps){
			getNACount(emptyShiftMap, l);
			if(getNACount(emptyShiftMap, l) <= 0){
				fail("intraTS doesn't have any NA placeholders");
			}
			assertFalse("At least one NA expected.",getNACount(emptyShiftMap,l)<=0);
			if(lastVal>0){
				if(getNACount(emptyShiftMap, lastVal)>=getNACount(emptyShiftMap,l)){
					fail("The number of NAs in a previous timestamp's record is greater than that for current, with a constant period set");
				}
				assertFalse("NA count for the last timestamp should not be equal to the first",getNACount(emptyShiftMap, lastVal)==getNACount(emptyShiftMap,l));
			} else {
				if(lastVal != -1){
					fail("Last val invalid, test needs to be fixed/revised");
				}
			}
			lastVal = l.longValue();
		}
	}

	private long getNACount(
			final ConcurrentMap<Long, ConcurrentMap<Long, SHIFT>> emptyShiftMap,
			Long l) {
		return emptyShiftMap.get(l).values().parallelStream().filter((v)->v==SHIFT.NA).count();
	}

}
