package parser.support;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

import domain.SMAMomentumBoolMatrix;
import domain.SMAMomentumBoolMatrix.SHIFT;
public class DataWrapperTrendFunc {
	
	public static ConcurrentMap<Long, ConcurrentMap<Long, SHIFT>> prepareEmptySMAChange(
			final ConcurrentMap<Long, ConcurrentMap<Long, Optional<Double>>> sma){
		
		if(sma==null){
			throw new IllegalArgumentException("SMA Matrix is null, cannot get momentum");
		}
		final List<Long> tsList = getListOfTimestamps(sma);
		
		
		ConcurrentMap<Long, ConcurrentMap<Long, SHIFT>> shift = new ConcurrentHashMap<>();
		
		ConcurrentMap<Long,Integer> indexOfTs 
			= new ConcurrentHashMap<>();
		tsList.parallelStream().forEach((t)->indexOfTs.put(t, tsList.indexOf(t)+1)); //TODO MAY NOT NEED THIS +1
		
		
		//tsIndex.forEach((t)->p("tIndex:" + index.get(t) + " t: " + t));
		
		
		/*
		 * We are going to populate the maps ahead of time for easier calculation
		 */
		tsList.parallelStream().forEach((t)->
		{
			ConcurrentMap<Long, SHIFT> emptyShift = new ConcurrentHashMap<>();
			tsList.parallelStream().filter((t2)->indexOfTs.get(t2)<=indexOfTs.get(t).intValue()) //if we know it is empty
				.forEach((t2)->emptyShift.put(t2, SHIFT.NA)); //place an NA to mark that
			
			//else if we can compute the value, go ahead and place an empty notification
			tsList.parallelStream().filter((t2)->indexOfTs.get(t2)>indexOfTs.get(t).intValue())
				.forEach((t2)->emptyShift.put(t2, SHIFT.EMPTY));
			shift.put(t, emptyShift);
		});
		
		
		
		//SMAMomentumBoolMatrix calcVals = new SMAMomentumBoolMatrix();
		
		return shift;
	}


	public static List<Long> getListOfTimestamps(
			final ConcurrentMap<Long, ConcurrentMap<Long, Optional<Double>>> sma) {
		final List<Long> tsList
					= sma.keySet().stream().sorted().collect(Collectors.toList());
		return tsList;
	}
	
	/*
	public static SMAMomentumBoolMatrix getShiftDirections(ConcurrentMap<Long, ConcurrentMap<Long, SHIFT>> baseData, 
			final ConcurrentMap<Long, ConcurrentMap<Long, Optional<Double>>> sma){
		throw new Exception("Not implemented.");		
	} */
	
	//TODO
	@Deprecated//??????? not sure if needed
	public static 		ConcurrentLinkedDeque<Long> getPeriodsToCheck(final ConcurrentMap<Long, ConcurrentMap<Long, Optional<Double>>> sma){
		ConcurrentLinkedDeque<Long> periodsToCheck = new ConcurrentLinkedDeque<>();
		final long maxPeriod = sma.keySet().stream().count();
		long periodToAdd = 0;
		while(periodToAdd < maxPeriod){
			periodsToCheck.addLast(periodToAdd);
			periodToAdd++;
		}
		return periodsToCheck;
	}
	
	@SuppressWarnings("unused")
	private static void p(final String s){
		System.out.println(s);
	}
}
