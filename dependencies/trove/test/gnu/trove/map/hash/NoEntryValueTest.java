package gnu.trove.map.hash;

import gnu.trove.map.TObjectIntMap;
import junit.framework.TestCase;

import java.util.Arrays;


/**
 * Test for a number of bugs related to no_entry_value, including 3432402
 */
public class NoEntryValueTest extends TestCase {
	public void testAdjustToNoEntry() {
		TObjectIntMap<String> map = new TObjectIntHashMap<String>();
		
		assertEquals( 0, map.getNoEntryValue() );
		assertEquals( 0, map.get( "NotInThere" ) );
		
		map.put( "Value", 1 );
		assertEquals( 1, map.size() );
		assertEquals( 1, map.get( "Value" ) );
		assertTrue( map.containsKey( "Value" ) );
		assertTrue( map.containsValue( 1 ) );
		assertTrue( Arrays.equals( new int[] { 1 }, map.values() ) );
		
		map.adjustValue( "Value", -1 );
		assertEquals( 1, map.size() );
		assertEquals( 0, map.get( "Value" ) );
		assertTrue( map.containsKey( "Value" ) );
		assertTrue( map.containsValue( 0 ) );
		assertTrue( Arrays.equals( new int[] { 0 }, map.values() ) );
	}
}
