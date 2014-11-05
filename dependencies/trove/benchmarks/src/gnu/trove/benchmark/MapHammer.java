package gnu.trove.benchmark;

import gnu.trove.map.TObjectLongMap;
import gnu.trove.map.hash.TObjectLongHashMap;
import gnu.trove.procedure.TObjectProcedure;

import java.text.NumberFormat;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;


/**
 *
 */
public class MapHammer {
	public static void main( String[] args ) {
		final TObjectLongMap<Integer> map = new TObjectLongHashMap<Integer>( 10000 );

//		// Disable auto-compaction
//		( ( TObjectLongHashMap ) map ).setAutoCompactionFactor( 0 );

		final Integer[] OBJECTS = new Integer[ 100000 ];
		for( int i = 0; i < OBJECTS.length; i++ ) {
			OBJECTS[ i ] = Integer.valueOf( i );
		}

		Random rand = new Random();

		final AtomicInteger adds = new AtomicInteger( 0 );
		final AtomicInteger edits = new AtomicInteger( 0 );
		final AtomicInteger removes = new AtomicInteger( 0 );

		final AtomicBoolean compact_flag = new AtomicBoolean( false );

		Timer stat_timer = new Timer();
		stat_timer.schedule( new TimerTask() {
			int run_count = 0;
			long start = System.currentTimeMillis();
			long total_ops;

			@Override
			public void run() {
				run_count++;
				if ( run_count == 60 ) {
					long duration = System.currentTimeMillis() - start;
					System.out.println( "Total ops: " + total_ops + "  Ops/sec: " +
						NumberFormat.getNumberInstance().format( total_ops /
							( duration / 1000.0 ) ) );
					System.exit( 0 );
				}

				int local_adds = adds.getAndSet( 0 );
				int local_edits = edits.getAndSet( 0 );
				int local_removes = removes.getAndSet( 0 );

//				long local_compactions = ( ( THash ) map ).compactions.getAndSet( 0 );

				long total_ops = ( long ) local_adds + ( long ) local_edits +
					( long ) local_removes;
				this.total_ops += total_ops;

//				compact_flag.set( true );

				System.out.println( "A: " + local_adds + "  E: " + local_edits +
					"  R: " + local_removes + "  Size: " + map.size() +
//					"  Compact: " + local_compactions +
					"  Ops/sec: " + total_ops );
			}
		}, 1000, 1000 );

//		stat_timer.schedule( new TimerTask() {
//			@Override
//			public void run() {
//				compact_flag.set( true );
//			}
//		}, 100, 100 );

		FirstValueSlot proc = new FirstValueSlot();

		//noinspection InfiniteLoopStatement
		while( true ) {
			if ( compact_flag.compareAndSet( true, false ) ) {
				( ( TObjectLongHashMap ) map ).compact();
			}

			int num = rand.nextInt( 3 );

			if ( num == 0 || map.isEmpty() ) {
				map.put( OBJECTS[ rand.nextInt( OBJECTS.length ) ], 0 );
				adds.incrementAndGet();
			}
			else {
				// Get a random value
				map.forEachKey( proc );
				Integer key = proc.key;

				if ( num == 1 ) {
					map.remove( key );
					removes.incrementAndGet();
				}
				else {
					map.increment( key );
					edits.incrementAndGet();
				}
			}
		}
	}

	private static class FirstValueSlot implements TObjectProcedure<Integer> {
		private volatile Integer key = null;

		@Override
		public boolean execute( Integer integer ) {
			key = integer;
			return false;
		}
	}
}
