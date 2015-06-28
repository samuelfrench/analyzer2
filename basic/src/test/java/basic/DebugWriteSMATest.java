package basic;

import static org.junit.Assert.*;

import org.junit.Test;

import output.csv.util.DebugWriteSMA;
import parser.YahooParser;
import parser.support.DataWrapperSMAFunc;
import parser.support.DataWrapperTrendFunc;
import domain.DataWrapper;
import domain.SMAMomentumBoolMatrix;

public class DebugWriteSMATest {

	@Test
	public final void testWriteSMAAndShiftMatrixToFile() {
		DataWrapper d = YahooParser.getDataWrapperFn("AMZN");
		DataWrapperSMAFunc.addSMARangeData(d, true);
		SMAMomentumBoolMatrix data = DataWrapperTrendFunc.getMomentumMatrix(d);
		DebugWriteSMA.writeSMAAndShiftMatrixToFile(d, data, "debug1.csv");
	}

	//@Test
	public final void testWriteSMAMatrixToFile() {
		fail("Not yet implemented");
	}

}
