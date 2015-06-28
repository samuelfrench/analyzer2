package analyzer;

import static org.junit.Assert.*;

import org.junit.Test;

import domain.DataWrapper;
import query.YahooQuery;

public class TestYahooQuery {

	@Test
	public void testGetDataWrapperFn() {
		DataWrapper luvWrapper = YahooQuery.getDataWrapperFn("LUV");
		if(luvWrapper==null){
			fail("returned null value");
		}
		if(luvWrapper.getRecords()==null){
			fail("records are null");
		}
		luvWrapper.getRecords().forEach((k,v)->{
			if(v.getClose()==null){
				fail("close is null");
			}
			if(v.getHigh()==null){
				fail("high is null");
			}
			if(v.getLow()==null){
				fail("low is null");
			}
			if(v.getOpen()==null){
				fail("open is null");
			}
			if(v.getTimestamp()==null){
				fail("timestamp is null");
			}
			if(v.getVolume()==null){
				fail("volume is null");
			}
		});
	}

}
