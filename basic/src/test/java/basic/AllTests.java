package basic;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ DataWrapperSMAFuncTest.class, DataWrapperTrendFuncTest.class,
		Debug.class, DebugWriteSMATest.class, YahooParserTest.class })
public class AllTests {

}
