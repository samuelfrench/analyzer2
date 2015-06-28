package basic;

import static org.junit.Assert.*;

import org.junit.Test;

import output.csv.util.DebugWriteSMA;
import db.Connection;
import domain.DataWrapper;
import domain.SMAMomentumBoolMatrix;
import parser.YahooParser;
import parser.support.DataWrapperSMAFunc;
import parser.support.DataWrapperTrendFunc;

public class Debug {

	//broken
	//@Test
	public final void testWriteSMAMatrixToFile() {
		DataWrapper d = YahooParser.getDataWrapperFn("AMZN");
		DataWrapperSMAFunc.addSMARangeData(d, true);
		assertFalse(DebugWriteSMA.writeSMAMatrixToFile(d, "test3222last.csv"));
	}
	
		@Test
		public final void testSMAChangeBasic() {
			DataWrapper d = YahooParser.getDataWrapperFn("AMZN");
			DataWrapperSMAFunc.addSMARangeData(d, true);
		//	assertFalse(DebugWriteSMA.writeSMAMatrixToFile(d, "test3223last.csv"));
			//DataWrapperTrendFunc.getMomentum(d.getsMAMatrix().get());
		}
		
		@Test
		public final void testDB(){
			DataWrapper d = YahooParser.getDataWrapperFn("AMZN");
			DataWrapperSMAFunc.addSMARangeData(d, true);
			SMAMomentumBoolMatrix data = DataWrapperTrendFunc.getMomentumMatrix(d);
			if(data==null){
				fail("could not get momentum matrix");
			}
			Connection.insertTrendReversals(data);
			Connection.getTrendReversals();
			//Connection.
			//Connection.dbTestOp();
		}

}
