package parser.support;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

import domain.DataWrapper;
import domain.SMAMomentumBoolMatrix;
import domain.SMAMomentumBoolMatrix.SHIFT;
public class DataWrapperTrendFunc {
	
	public static ConcurrentMap<Long, ConcurrentMap<Long, SHIFT>> prepareEmptySMAChange(
			final ConcurrentMap<Long, ConcurrentMap<Long, Optional<Double>>> sma){
		
		if(sma==null){
			throw new IllegalArgumentException("SMA Matrix is null, cannot get momentum");
		}
		List<Long> tsList;
		ConcurrentMap<Long,Integer> indexOfTs;
		tsList = getListOfTimestamps(sma);
		indexOfTs = createTsMap(tsList);
		ConcurrentMap<Long, ConcurrentMap<Long, SHIFT>> shift = new ConcurrentHashMap<>();

		
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
				
		return shift;
	}

	@Deprecated
	private static ConcurrentMap<Long, Integer> createTsIndexMap(List<Long> tsList) {
		ConcurrentMap<Long, Integer> indexOfTs;
		indexOfTs
			= new ConcurrentHashMap<>();
		tsList.parallelStream().forEach((t)->indexOfTs.put(t, tsList.indexOf(t)+1)); //TODO MAY NOT NEED THIS +1
		return indexOfTs;
	}
	
	@SuppressWarnings("unused")
	private static ConcurrentMap<Integer,Long> createIndexTsMap(List<Long> tsList){
		ConcurrentMap<Integer, Long> indexToTs;
		indexToTs
			= new ConcurrentHashMap<>();
		tsList.parallelStream().forEach((t)->indexToTs.put(tsList.indexOf(t),t)); //TODO MAY NOT NEED THIS +1
		return indexToTs;
	}
	

	//SMAMomentumBoolMatrix calcVals = new SMAMomentumBoolMatrix();

	public static SMAMomentumBoolMatrix getMomentumMatrix(DataWrapper wrapper){
		if(!wrapper.getsMAMatrix().isPresent()){
			throw new IllegalArgumentException("Populate SMA Matrix before requesting getMomentumMatrix");
		}
		
		ConcurrentMap<Long, ConcurrentMap<Long, Optional<Double>>> sma = wrapper.getsMAMatrix().get();
		ConcurrentMap<Long, ConcurrentMap<Long, SHIFT>> shiftMap = prepareEmptySMAChange(sma);
		return null;
		/*
		 * use create ts index map to iterate through every timestamp, in parallel for every period
		 */
		
		
		
		
		/*
		periodList.parallelStream().forEach((p)->{
			
		});
		*/
		
		
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
	@Deprecated
	public static ConcurrentLinkedDeque<Long> getPeriodsToCheck(final ConcurrentMap<Long, ConcurrentMap<Long, Optional<Double>>> sma){
		ConcurrentLinkedDeque<Long> periodsToCheck = new ConcurrentLinkedDeque<>();
		final long maxPeriod = sma.keySet().stream().count();
		long periodToAdd = 0;
		while(periodToAdd < maxPeriod){
			periodsToCheck.addLast(periodToAdd);
			periodToAdd++;
		}
		return periodsToCheck;
	}
	
	public static List<Long> getPeriodList(final ConcurrentMap<Long, ConcurrentMap<Long, Optional<Double>>> sma){
		List<Long> periodList = new ArrayList<>();
		final long maxPeriod = sma.keySet().stream().count();
		long periodToAdd = 0;
		while(periodToAdd < maxPeriod){
			periodList.add(periodToAdd);
			periodToAdd++;
		}
		
		return periodList;
		
	}
	
	@SuppressWarnings("unused")
	private static void p(final String s){
		System.out.println(s);
	}
}
