package parser.support;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

import domain.csv.*;
public class DataWrapperSMAFunc {
	//going to try passing by reference here - may not work
	public static void addSMARangeData(DataWrapper dataWrapper, final boolean printDebug){
		if(dataWrapper.getRecords()==null){
			System.err.println("addSMARangeData: dataWrapper has null record parameter");
			return;
		}
		final Long recordCount = dataWrapper.getRecords().keySet().stream().count();
		if(recordCount < 1){
			System.err.println("no records");
			return;
		}
		
		if(printDebug && dataWrapper.getsMACrossover().isPresent()){
			System.out.println("addSMARangeData: overWriting existing values");
		}
		
		//getCloseOpenDiff(dataWrapper);
		
		ConcurrentMap<Long, ConcurrentMap<Long, Double>> SMAMatrix = 
				new ConcurrentHashMap<Long, ConcurrentMap<Long, Double>>();
		
		//TODO -https://docs.oracle.com/javase/8/docs/api/java/util/stream/Collectors.html
		dataWrapper.getRecords().keySet().parallelStream().forEach((k)-> {
			SMAMatrix.put(k, new ConcurrentHashMap<>());
		});
		
		Long startIndex = 0L;
		
		final Long maxIndex = recordCount;
		
		final List<Long> timeStamps = dataWrapper.getRecords().keySet().parallelStream().sorted().collect(Collectors.toList());//@TODO - TODO NEED TO FACTOR IN INCONSISTANT INTERVALS
		
		//map of timestamps to their index (0 based), once in order
		ConcurrentMap<Long, Long> ts = new ConcurrentHashMap<>();
		
		for(long l = 0L; l < timeStamps.stream().count(); l++){
			ts.put(l, timeStamps.get((int) l));
		}
		
		final ConcurrentMap<Long, Double> highLowDiff = getHighLowDiff(dataWrapper);
		
		while(startIndex < maxIndex){
			Long currentIndex = startIndex;
			List<Double> avg = new ArrayList<>();
			while(currentIndex++ < maxIndex){
				avg.add(highLowDiff.get(ts.get(currentIndex)));
				Double avgCurrent = null;
				try{
					avgCurrent = avg.parallelStream().mapToDouble(d->d).parallel().average().getAsDouble();
				} catch(NoSuchElementException e){
					avgCurrent = null;
					e.printStackTrace();
				}
						
					
				if(Optional.of(avgCurrent).isPresent()==false){
					System.err.println("Count not get average for timestamp: " + currentIndex);
					continue;
				}
				SMAMatrix
					.get(ts.get(currentIndex)) //get entry for timestamp
					.put(currentIndex-startIndex, avgCurrent); //add to the map for that timestamp
			}
		}
		dataWrapper.setsMACrossover(Optional.of(SMAMatrix));
		if(!dataWrapper.getsMACrossover().isPresent()){
			System.err.println("Unknown err: addSMARangeData");
		}
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

	private static ConcurrentMap<Long, Double> getHighLowDiff(DataWrapper dataWrapper) {
		//we will use this as one of our simple moving average points
		ConcurrentMap<Long, Double> highLowDiff = new ConcurrentHashMap<>();
		dataWrapper.getRecords().values().parallelStream().forEach((r)-> {
			if(highLowDiff.put(r.getTimestamp(),r.getHigh()-r.getLow())!=null){
				System.err.println("err");
			};
		});
		return highLowDiff;
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
