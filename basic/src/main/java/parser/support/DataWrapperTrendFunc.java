package parser.support;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

import domain.DataWrapper;
import domain.SMAMomentumBoolMatrix;
import domain.SMAMomentumBoolMatrix.SHIFT;





public class DataWrapperTrendFunc {
	
	public SMAMomentumBoolMatrix getMomentum(final DataWrapper.SMAMatrix sMAMatrix){
		
		if(!sMAMatrix.getsMAMatrix().isPresent()){
			throw new IllegalArgumentException("SMA Matrix is null, cannot get momentum");
		}
		ConcurrentMap<Long, ConcurrentMap<Long, Optional<Double>>> sma = sMAMatrix.getsMAMatrix().get();
		final List<Long> tsIndex
					= sma.keySet().stream().sorted().collect(Collectors.toList());
		
		
		ConcurrentMap<Long, ConcurrentMap<Long, SHIFT>> shift = new ConcurrentHashMap<>();
		
		
		/*
		 * We are going to populate the maps ahead of time for easier calculation
		 */
		tsIndex.parallelStream().forEach((t)->
		{
			ConcurrentMap<Long, SHIFT> emptyShift = new ConcurrentHashMap<>();
			tsIndex.parallelStream().filter((t2)->t2<=t) //if we know it is empty
				.forEach((t2)->emptyShift.put(t2, SHIFT.NA)); //place an NA to mark that
			
			//else if we can compute the value, go ahead and place an empty notification
			tsIndex.parallelStream().filter((t2)->t2>t).forEach((t2)->emptyShift.put(t2, SHIFT.EMPTY));
			shift.put(t, emptyShift);
		});
		
		
		
		//SMAMomentumBoolMatrix calcVals = new SMAMomentumBoolMatrix();
		
		return null;
	}
}
