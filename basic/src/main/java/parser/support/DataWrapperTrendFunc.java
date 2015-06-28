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
import domain.SMAMomentumBoolMatrix.SIGNAL;

public class DataWrapperTrendFunc {

	private static ConcurrentMap<Long, Integer> lookupByTimeStamp(List<Long> tsList) {
		ConcurrentMap<Long, Integer> lookupByTimeStamp;
		lookupByTimeStamp = new ConcurrentHashMap<>();
		tsList.parallelStream().forEach((t) -> lookupByTimeStamp.put(t, tsList.indexOf(t)));
		return lookupByTimeStamp;
	}

	private static ConcurrentMap<Integer, Long> lookupByIndex(List<Long> tsList) {
		ConcurrentMap<Integer, Long> lookupByIndex;
		lookupByIndex = new ConcurrentHashMap<>();
		tsList.parallelStream().forEach((t) -> lookupByIndex.put(tsList.indexOf(t), t));
		return lookupByIndex;
	}

	public static SMAMomentumBoolMatrix getMomentumMatrix(DataWrapper wrapper) {
		if (!wrapper.getsMAMatrix().isPresent()) {
			throw new IllegalArgumentException("Populate SMA Matrix before requesting getMomentumMatrix");
		}

		Optional<ConcurrentMap<Long, ConcurrentMap<Long, Optional<Double>>>> sma = wrapper.getsMAMatrix();
		ConcurrentMap<Long, ConcurrentMap<Long, SHIFT>> shiftMap = prepareEmptySMAChange(sma.get());

		/*
		 * first get the shift
		 */
		final List<Long> tsList = getListOfTimestamps(sma.get());

		// level 1: split off periods
		final List<Long> periodsToEvaluate = getPeriodList(sma.get());
		//periodsToEvaluate.parallelStream().forEach((p) -> {
		for(Long p : periodsToEvaluate){
			Optional<Double> ts_0 = Optional.of((double)-1);
			Optional<Double> ts_1 = Optional.of((double)-1);
			for (Long l : tsList) {
				
				// current time stamp
				
				//debug
				//p(sma.get().get(l).get(new Long(p.longValue())).get().toString());
				//end debug
				
				
				ts_0 = sma.get().get(l).get(p);
				if(ts_0==null){
					System.err.println("err");
					continue;
				}
				if (ts_1.isPresent()==false||ts_1.equals((double)-1)) { // this is the first pass, continue
					ts_1 = sma.get().get(l).get(new Long(p.longValue()));
					continue;
				}
				// assuming no error
				if (ts_0.isPresent()) {
					// are the two timestamps sma equal?
					if (ts_0.get().equals(ts_1.get())) {
						shiftMap.get(l).put(new Long(p.longValue()), SHIFT.FLAT);
					} else { // if not - determine greater of the pair
						double max = Math.max(ts_0.get().doubleValue(), ts_1.get().doubleValue());
						if (max == ts_0.get().doubleValue()) {
							shiftMap.get(l).put(new Long(p.longValue()), SHIFT.UP);
						}
						if (max == ts_1.get().doubleValue()) {
							shiftMap.get(l).put(new Long(p.longValue()), SHIFT.DOWN);
						}
					}
				} else {
					throw new IllegalArgumentException("both timestamp values do not have an associated sma for the period");
				}
				ts_1 = ts_0;
			}
		}

		// now we see if each shift is a continuation of previous trend
		ConcurrentMap<Long, ConcurrentMap<Long, SIGNAL>> signal = new ConcurrentHashMap<>();
		tsList.parallelStream().forEach((t) -> signal.put(t, new ConcurrentHashMap<>()));
		periodsToEvaluate.parallelStream().forEach((p) -> {
			SHIFT last = SHIFT.NA;
			SHIFT present = SHIFT.NA;
			for (Long l : tsList) {
				if (last == SHIFT.NA) {
					last = shiftMap.get(l).get(p);
					signal.get(l).put(p, SIGNAL.NA);
					continue;
				}
				present = shiftMap.get(l).get(p);
				if (last == present) {
					signal.get(l).put(p, SIGNAL.NO);
				} else {
					signal.get(l).put(p, SIGNAL.YES);
				}

				last = present;
			}
		});

		SMAMomentumBoolMatrix returnValue = new SMAMomentumBoolMatrix();
		returnValue.setShift(shiftMap);
		returnValue.setSignal(signal);

		return returnValue;
	}

	public static ConcurrentMap<Long, ConcurrentMap<Long, SHIFT>> prepareEmptySMAChange(
			ConcurrentMap<Long, ConcurrentMap<Long, Optional<Double>>> sma) {
		ConcurrentMap<Long, ConcurrentMap<Long,SHIFT>> emptyMap = new ConcurrentHashMap<>();
		sma.keySet().stream().forEach((k)->{
			emptyMap.put(k, new ConcurrentHashMap<Long, SHIFT>());
		});
		sma.keySet().stream().forEach((k)->
		{
			ConcurrentMap<Long, SHIFT> innerShift = new ConcurrentHashMap<>();
			sma.get(k).keySet().stream().forEach((p)->innerShift.put(p, SHIFT.INIT));
			emptyMap.put(k, innerShift);
		});
		return emptyMap;
	}

	public static List<Long> getListOfTimestamps(final ConcurrentMap<Long, ConcurrentMap<Long, Optional<Double>>> sma) {
		final List<Long> tsList = sma.keySet().stream().sorted().collect(Collectors.toList());
		return tsList;
	}

	@SuppressWarnings("unused")
	@Deprecated
	private static ConcurrentLinkedDeque<Long> getPeriodsToCheck(final ConcurrentMap<Long, ConcurrentMap<Long, Optional<Double>>> sma) {
		ConcurrentLinkedDeque<Long> periodsToCheck = new ConcurrentLinkedDeque<>();
		final long maxPeriod = sma.keySet().stream().count();
		long periodToAdd = 0;
		while (periodToAdd < maxPeriod) {
			periodsToCheck.addLast(periodToAdd);
			periodToAdd++;
		}
		return periodsToCheck;
	}

	private static List<Long> getPeriodList(final ConcurrentMap<Long, ConcurrentMap<Long, Optional<Double>>> sma) {
		List<Long> periodList = new ArrayList<>();
		final long maxPeriod = sma.keySet().stream().count();
		long periodToAdd = 0;
		while (periodToAdd < maxPeriod) {
			periodList.add(periodToAdd);
			periodToAdd++;
		}
		return periodList;
	}
	
	
	
	/*
	 * JUNK
	 */
	/*
	public static ConcurrentMap<Long, ConcurrentMap<Long, SHIFT>> prepareEmptySMAChange(final ConcurrentMap<Long, ConcurrentMap<Long, Optional<Double>>> sma) {

		if (sma == null) {
			throw new IllegalArgumentException("SMA Matrix is null, cannot get momentum");
		}
		List<Long> tsList;
		ConcurrentMap<Long, Integer> indexOfTs;
		tsList = getListOfTimestamps(sma);
		indexOfTs = lookupByTimeStamp(tsList);
		ConcurrentMap<Long, ConcurrentMap<Long, SHIFT>> shift = new ConcurrentHashMap<>();

		// tsIndex.forEach((t)->p("tIndex:" + index.get(t) + " t: " + t));

		//
		 //We are going to populate the maps ahead of time for easier
		 //calculation
		 //
		tsList.parallelStream().forEach((t) -> {
			ConcurrentMap<Long, SHIFT> emptyShift = new ConcurrentHashMap<>();
			tsList.parallelStream().filter((t2) -> indexOfTs.get(t2) <= indexOfTs.get(t).intValue()) // if
			// we
			// know
			// it
			// is empty
			.forEach((t2) -> emptyShift.put(t2, SHIFT.NA));

			// else if we can compute the value, go ahead and
			// place an empty notification
			tsList.parallelStream().filter((t2) -> indexOfTs.get(t2) > indexOfTs.get(t).intValue()).forEach((t2) -> emptyShift.put(t2, SHIFT.EMPTY));
			shift.put(t, emptyShift);
		});

		return shift;
	}
*/
}
