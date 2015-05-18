package parser.support;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

import org.apache.commons.lang3.concurrent.ConcurrentUtils;

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
		
		
		/*
		 * CREATE OBJECT TO POPULATE
		 */
		ConcurrentMap<Long, ConcurrentMap<Long,Optional<Double>> >
			SMAMatrix = new ConcurrentHashMap<>();
		
		//TODO -https://docs.oracle.com/javase/8/docs/api/java/util/stream/Collectors.html
		dataWrapper.getRecords().keySet().parallelStream().forEach((k)-> {
			SMAMatrix.put(k, new ConcurrentHashMap<>());
		});
		
		long startIndex = 0;
		
		long maxIndex = recordCount;
		
		List<Long> timeStamps = dataWrapper.getRecords().keySet().stream().sorted().collect(Collectors.toList());
		ConcurrentMap<Long, Double> highLowDiff = getHighLowDiff(dataWrapper);
		List<Double> avg = new ArrayList<>();

		while(startIndex < maxIndex){
			long currentIndex = startIndex;
			avg.clear();
			while(currentIndex < maxIndex){
				avg.add(highLowDiff.get(timeStamps.get((int)currentIndex)));
				double avgCurrent = -1;
				avgCurrent = (avg.stream().mapToDouble(d->d).sum()/(double)avg.size());
				SMAMatrix
					.get(timeStamps.get((int) currentIndex)) //get entry for timestamp
					.put(currentIndex-startIndex, Optional.of(avgCurrent)); //add to the map for that timestamp
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
			//debug
				System.out.println("High: " + r.getHigh() + " low: " + r.getLow() + " result " + (r.getHigh()-r.getLow()));
			if(highLowDiff.put(r.getTimestamp(),r.getHigh()-r.getLow())!=null){
				System.err.println("err");
			};
		});
		return highLowDiff;
	}

	@SuppressWarnings("unused")
	public static void getCloseOpenDiff(DataWrapper dataWrapper) {
		ConcurrentMap<Long,Double> closeOpenDiff = new ConcurrentHashMap<>();
		dataWrapper.getRecords().values().parallelStream().forEach((r)-> {
			if(closeOpenDiff.put(r.getTimestamp(), r.getClose() - r.getOpen())!=null){
				System.err.println("err");
			}
		});
	}

	@SuppressWarnings("unused")
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
