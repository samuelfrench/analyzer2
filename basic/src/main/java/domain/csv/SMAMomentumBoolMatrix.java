package domain.csv;

import java.util.Map;
import java.util.concurrent.ConcurrentMap;

public class SMAMomentumBoolMatrix {
	
	/*this tells us if our SMA of the rate of change between 
	 *the difference between the high and low values of a period(SMA_r)
	 *is moving upward or down within the period p (H-L)/2
	 */
	public static enum SHIFT {UP, DOWN, FLAT};
	
	
	public static enum SIGNAL {YES,NO}; //yes = shift is in opposite direction from previous interval
	//no - trend has not reversed
	
	ConcurrentMap<Long, ConcurrentMap<Integer, SIGNAL>> signal;
	ConcurrentMap<Long, ConcurrentMap<Integer, SHIFT>> shift; //within current period
}
