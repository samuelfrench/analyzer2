package calculate;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

import domain.BollingerBandRecord;
import domain.BollingerBandSet;
import domain.DataWrapper;

public class BollingerBandFactory {
	public static BollingerBandSet getBands(DataWrapper data, Integer period, BollingerBandSet.TIME_UNIT timeUnit, Double stdDev){
		BollingerBandSet bandSet = 
				new BollingerBandSet(data.getTicker(),timeUnit, stdDev, period);
		ConcurrentMap<Long, BollingerBandRecord> records = new ConcurrentHashMap<>(data.getRecords().size());
		data.getRecords().keySet().parallelStream().forEach((k)->records.put(k, new BollingerBandRecord(data.getRecords().get(k).getClose())));
		/*
		 * Next we need to calculate standard deviations from the MA to get the upper
		 * and lower band
		 */
		
		//TODO
		return null;
	}
	
	
	public static ConcurrentMap<Long, Double> calculateMovingAverage(final DataWrapper data, final Integer periods){
		ConcurrentMap<Long, Double> maMap = new ConcurrentHashMap<>();
		data.getRecords().keySet().parallelStream().forEach((k)->{
			maMap.put(k,getIndividualMovingAverage(data,k,periods));
		});
		return maMap;
	}
	
	private static Double getIndividualMovingAverage(final DataWrapper data, final Long timestamp, final Integer periods){
		List<Long> sortedTimestamps = data.getRecords().keySet().stream().sequential().sorted().collect(Collectors.toList());
		Integer index = sortedTimestamps.indexOf(timestamp);
		if(index<periods){
			return null;
		}
		List<Long> toEvaluate = sortedTimestamps.subList(index-periods, index);
		double average = 0;
		for(Long ts : toEvaluate){
			average += data.getRecords().get(ts).getClose();
		}
		return average / periods;
	}
}
