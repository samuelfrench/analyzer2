package basic;

import static org.junit.Assert.*;

import org.junit.Test;

import domain.csv.DataWrapper;
import parser.YahooParser;

public class YahooParserTest {

	@Test
	public final void test() {
		for(int x = 0; x < 100; x++){
			//System.out.println(x + ": " + YahooParser.splitViaLine(YahooParser.getIntraDaily("AMZN"))[x]);
		}
		//System.out.println(YahooParser.splitViaLine(YahooParser.getIntraDaily("INTC"))[9]);
		//System.out.println(YahooParser.getFormattedDate(1431720000L));
		String fullText = YahooParser.getIntraDaily("AMZN");
		DataWrapper dw = YahooParser.getData(YahooParser.splitViaLineList(fullText));
		dw.getRecords().keySet().forEach((p)->System.out.println(p));
	}

}
