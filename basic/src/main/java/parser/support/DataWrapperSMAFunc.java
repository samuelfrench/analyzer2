package parser.support;

import java.util.Arrays;
import java.util.HashMap;
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
			System.err.println("no records");
			return;
		}
		
		if(printDebug && dataWrapper.getsMACrossover().isPresent()){
			System.out.println("addSMARangeData: overWriting existing values");
		}
		
		getHighLowDiff(dataWrapper);
		
		//getCloseOpenDiff(dataWrapper);
		
		
		ConcurrentMap<Long, ConcurrentMap<Long, Double>> SMAMatrix = 
				new ConcurrentHashMap<Long, ConcurrentMap<Long, Double>>();
		dataWrapper.getRecords().keySet().parallelStream().forEach((k)-> {
			SMAMatrix.put(k, new ConcurrentHashMap<>());
		});
		
		
		
	}

	/*
	 * Probably not going to need to do this - see whiteboard_capture_001.png for better idea
	 */
	@Deprecated
	private static ConcurrentMap<Long, Long> getPreviousPeriodCountMap(
			final ConcurrentMap<Long, ConcurrentMap<Long, Double>> SMAMatrix) {
		ConcurrentMap<Long, Long> previousPeriodCount = new ConcurrentHashMap<>();
		SMAMatrix.keySet().parallelStream().forEach(
				(k) -> 
					previousPeriodCount.put(k, 
							previousPeriodCount.keySet().parallelStream().filter((p)->(p<k)).count()));
		return previousPeriodCount;
	}

	private static void getHighLowDiff(DataWrapper dataWrapper) {
		//we will use this as one of our simple moving average points
		ConcurrentMap<Long, Double> highLowDiff = new ConcurrentHashMap<>();
		dataWrapper.getRecords().values().parallelStream().forEach((r)-> {
			if(highLowDiff.put(r.getTimestamp(),r.getHigh()-r.getLow())!=null){
				System.err.println("err");
			};
		});
	}

	@SuppressWarnings("unused")
	private static void getCloseOpenDiff(DataWrapper dataWrapper) {
		ConcurrentMap<Long,Double> closeOpenDiff = new ConcurrentHashMap<>();
		dataWrapper.getRecords().values().parallelStream().forEach((r)-> {
			if(closeOpenDiff.put(r.getTimestamp(), r.getClose() - r.getOpen())!=null){
				System.err.println("err");
			}
		});
	}

	@SuppressWarnings("unused")
	private static ConcurrentMap<Long, Boolean> getUpwardMovement(
			ConcurrentMap<Long, Double> closeOpenDiff) {
		
		ConcurrentMap<Long, Boolean> upwardMovement = new ConcurrentHashMap<>();
		closeOpenDiff.keySet().parallelStream().forEach((k)->{
			if(closeOpenDiff.get(k)>0){
				upwardMovement.put(k, true);
				return;
			}
			upwardMovement.put(k, false);
		});
		return upwardMovement;
	}
	
	/*
	 * OptionalDouble 	average()
Returns an OptionalDouble describing the arithmetic mean of elements of this stream, or an empty optional if this stream is empty.
	 */
	
	//write function to return optional
}
