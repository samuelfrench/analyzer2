package basic;

import static org.junit.Assert.*;

import org.junit.Test;

import output.csv.util.DebugWriteSMA;
import domain.DataWrapper;
import parser.YahooParser;
import parser.support.DataWrapperSMAFunc;

public class Debug {

	//broken
	@Test
	public final void testWriteSMAMatrixToFile() {
		DataWrapper d = YahooParser.getDataWrapperFn("AMZN");
		DataWrapperSMAFunc.addSMARangeData(d, true);
		assertFalse(DebugWriteSMA.writeSMAMatrixToFile(d, "test3222last.csv"));
	}

}
