package parser.support;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.LongStream;

import domain.*;
public class DataWrapperSMAFunc {
	//going to try passing by reference here - may not work
	public static void addSMARangeData(DataWrapper dataWrapper, final boolean printDebug){
		if(dataWrapper.getRecords()==null){
			System.err.println("addSMARangeData: dataWrapper has null record parameter");
			return;
		}
		int recordCount = dataWrapper.getRecords().keySet().size();
		if(recordCount < 1){
			System.err.println("no records");
			return;
		}
		
		
		/*
		 * CREATE OBJECT TO POPULATE
		 */
		ConcurrentMap<Long, ConcurrentMap<Long,Optional<Double>> >
			SMAMatrix = new ConcurrentHashMap<>();
		
		//TODO -https://docs.oracle.com/javase/8/docs/api/java/util/stream/Collectors.html
		LongStream sortedTimestamps = dataWrapper.getRecords().values().parallelStream().mapToLong((v)->v.getTimestamp()).sorted();
		Queue<Long> timeStamps = new LinkedBlockingQueue<>();
		sortedTimestamps.forEachOrdered((k)->{
			SMAMatrix.put(k, new ConcurrentHashMap<>());
			timeStamps.add(k);
		});
		
		int startIndex = 0;
		
		int maxIndex = recordCount;
		
		Map<Integer,Long> timeStampMap = new ConcurrentHashMap<>();
		for(int i = 0; !timeStamps.isEmpty(); i++){
			timeStampMap.put(i, timeStamps.remove());
		}
		
		
		
		ConcurrentMap<Long, Double> highLowDiff = getHighLowDiff(dataWrapper);
		List<Double> avg = new ArrayList<>();

		while(startIndex < maxIndex){
			int currentIndex = startIndex;
			avg.clear();
			while(currentIndex < maxIndex){
				avg.add(highLowDiff.get(timeStampMap.get(currentIndex)));
				double avgCurrent = -1;
				avgCurrent = (avg.stream().mapToDouble(d->d).sum()/(double)avg.size());
				SMAMatrix
					.get(timeStampMap.get(currentIndex)) //get entry for timestamp
					.put((long) (currentIndex-startIndex), Optional.of(avgCurrent)); //add to the map for that timestamp
				currentIndex++;
			}
			startIndex++;
		}
		dataWrapper.setsMAMatrix(Optional.of(SMAMatrix));
		if(!dataWrapper.getsMAMatrix().isPresent()){
			System.err.println("Unknown err: addSMARangeData");
		}
	}

	public static ConcurrentMap<Long, Double> getHighLowDiff(DataWrapper dataWrapper) {
		//we will use this as one of our simple moving average points
		ConcurrentMap<Long, Double> highLowDiff = new ConcurrentHashMap<>();
		dataWrapper.getRecords().values().parallelStream().forEach((r)-> {
			if(highLowDiff.put(r.getTimestamp(), (r.getHigh()-r.getLow())) 
					!= null){
				System.err.println("err");
			};
		});
		return highLowDiff;
	}

	public static void getCloseOpenDiff(DataWrapper dataWrapper) {
		ConcurrentMap<Long,Double> closeOpenDiff = new ConcurrentHashMap<>();
		dataWrapper.getRecords().values().parallelStream().forEach((r)-> {
			if(closeOpenDiff.put(r.getTimestamp(), r.getClose() - r.getOpen())!=null){
				System.err.println("err");
			}
		});
	}

	public static ConcurrentMap<Long, Boolean> getUpwardMovement(
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
}
