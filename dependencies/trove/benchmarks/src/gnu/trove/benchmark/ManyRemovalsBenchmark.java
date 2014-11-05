package gnu.trove.benchmark;

import com.logicartisan.thrifty.benchmark.Benchmark;
import gnu.trove.map.TMap;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.THashMap;
import gnu.trove.map.hash.TObjectIntHashMap;

import java.util.HashMap;
import java.util.Map;


/**
 * This benchmark adds five strings and then delete the first four added. It then repeats
 * for a new set. So, the map grows, but has a high removal rate. This exercises
 * compaction in Trove maps.
 */
@SuppressWarnings( "UnusedDeclaration" )
public class ManyRemovalsBenchmark extends Benchmark {
	public void testTHashMap() {
		TMap<String,String> map = new THashMap<String, String>();

		// Add 5, remove the first four, repeat
		String[] to_remove = new String[ 4 ];
		int batch_index = 0;
		for( String s : Constants.STRING_OBJECTS ) {
			if ( batch_index < 4 ) {
				to_remove[ batch_index ] = s;
			}

			map.put( s, s );
			batch_index++;

			if ( batch_index == 5 ) {
				for( String s_remove : to_remove ) {
					map.remove( s_remove );
				}
				batch_index = 0;
			}
		}
	}

	public void testTPrimitiveHashMap() {
		TObjectIntMap<String> map = new TObjectIntHashMap<String>();

		// Add 5, remove the first four, repeat
		String[] to_remove = new String[ 4 ];
		int batch_index = 0;
		for( String s : Constants.STRING_OBJECTS ) {
			if ( batch_index < 4 ) {
				to_remove[ batch_index ] = s;
			}

			map.put( s, s.length() );
			batch_index++;

			if ( batch_index == 5 ) {
				for( String s_remove : to_remove ) {
					map.remove( s_remove );
				}
				batch_index = 0;
			}
		}
	}

	public void testJavaHashMap() {
		Map<String,String> map = new HashMap<String, String>();

		// Add 5, remove the first four, repeat
		String[] to_remove = new String[ 4 ];
		int batch_index = 0;
		for( String s : Constants.STRING_OBJECTS ) {
			if ( batch_index < 4 ) {
				to_remove[ batch_index ] = s;
			}

			map.put( s, s );
			batch_index++;

			if ( batch_index == 5 ) {
				for( String s_remove : to_remove ) {
					map.remove( s_remove );
				}
				batch_index = 0;
			}
		}
	}
	public void testTrove2HashMap() {
		gnu.trove.THashMap<String,String> map = new gnu.trove.THashMap<String, String>();

		// Add 5, remove the first four, repeat
		String[] to_remove = new String[ 4 ];
		int batch_index = 0;
		for( String s : Constants.STRING_OBJECTS ) {
			if ( batch_index < 4 ) {
				to_remove[ batch_index ] = s;
			}

			map.put( s, s );
			batch_index++;

			if ( batch_index == 5 ) {
				for( String s_remove : to_remove ) {
					map.remove( s_remove );
				}
				batch_index = 0;
			}
		}
	}

	public void testTrove2PrimitiveHashMap() {
		gnu.trove.TObjectIntHashMap<String> map = new gnu.trove.TObjectIntHashMap<String>();

		// Add 5, remove the first four, repeat
		String[] to_remove = new String[ 4 ];
		int batch_index = 0;
		for( String s : Constants.STRING_OBJECTS ) {
			if ( batch_index < 4 ) {
				to_remove[ batch_index ] = s;
			}

			map.put( s, s.length() );
			batch_index++;

			if ( batch_index == 5 ) {
				for( String s_remove : to_remove ) {
					map.remove( s_remove );
				}
				batch_index = 0;
			}
		}
	}
}
