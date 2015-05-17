package parser.support;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import domain.csv.*;
public class DataWrapperSMAFunc {
	//going to try passing by reference here - may not work
	public static void addSMARangeData(DataWrapper dataWrapper, final boolean printDebug){
		if(dataWrapper.getRecords()==null){
			System.err.println("addSMARangeData: dataWrapper has null record parameter");
			return;
		}
		long recordCount = dataWrapper.getRecords().keySet().stream().count();
		if(recordCount < 1){
			System.err.println("");
			return;
		}
		
		if(printDebug && dataWrapper.getsMACrossover().isPresent()){
			System.out.println("addSMARangeData: overWriting existing values");
		}
		ConcurrentMap<Long, ConcurrentMap<Long, Double>> SMAMatrix = 
				new ConcurrentHashMap<Long, ConcurrentMap<Long, Double>>();
		
		
		
	}
	
	//write function to return optional
}
