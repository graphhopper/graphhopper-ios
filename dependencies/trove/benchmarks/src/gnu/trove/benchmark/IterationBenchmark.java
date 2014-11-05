package gnu.trove.benchmark;

import gnu.trove.iterator.TByteByteIterator;
import gnu.trove.iterator.TIntIntIterator;
import gnu.trove.iterator.TObjectIntIterator;

import java.util.Iterator;


/**
 * Tests get performance.
 */
@SuppressWarnings( { "UnusedDeclaration", "UnnecessaryUnboxing" } )
public class IterationBenchmark extends AbstractComparisonBase {
	private static final int ITERATIONS_BYTE = 100;
	private static final int ITERATIONS_INT = 50;
	private static final int ITERATIONS_STRING = 50;

	public IterationBenchmark() {
		super( true );      // we want filled maps
	}


	// Bytes...

	@Override
	public void testByte_TPrimitiveHashMap() {
		int total = 0;

		for( int i = 0; i < ITERATIONS_BYTE; i++ ) {
			TByteByteIterator iterator = byte_t_primitive_map.iterator();
			while( iterator.hasNext() ) {
				iterator.advance();
				total += iterator.key();
			}
		}

		int_slot.set( total );
	}

	@Override
	public void testByte_THashMap() {
		int total = 0;

		for( int i = 0; i < ITERATIONS_BYTE; i++ ) {
			Iterator<Byte> iterator = byte_t_map.keySet().iterator();

			//noinspection WhileLoopReplaceableByForEach
			while( iterator.hasNext() ) {
				total += iterator.next().intValue();
			}
		}

		int_slot.set( total );
	}

	@Override
	public void testByte_JavaHashMap() {
		int total = 0;

		for( int i = 0; i < ITERATIONS_BYTE; i++ ) {
			Iterator<Byte> iterator = byte_java_map.keySet().iterator();

			//noinspection WhileLoopReplaceableByForEach
			while( iterator.hasNext() ) {
				total += iterator.next().intValue();
			}
		}

		int_slot.set( total );
	}

	@Override
	public void testByte_Trove2PrimitiveHashMap() {
		int total = 0;

		for( int i = 0; i < ITERATIONS_BYTE; i++ ) {
			gnu.trove.TByteByteIterator iterator = byte_t2_primitive_map.iterator();
			while( iterator.hasNext() ) {
				iterator.advance();
				total += iterator.key();
			}
		}

		int_slot.set( total );
	}

	@Override
	public void testByte_Trove2HashMap() {
		int total = 0;

		for( int i = 0; i < ITERATIONS_BYTE; i++ ) {
			Iterator<Byte> iterator = byte_t2_map.keySet().iterator();

			//noinspection WhileLoopReplaceableByForEach
			while( iterator.hasNext() ) {
				total += iterator.next().intValue();
			}
		}

		int_slot.set( total );
	}


	// Ints...

	@Override
	public void testInt_TPrimitiveHashMap() {
		int total = 0;

		for( int i = 0; i < ITERATIONS_INT; i++ ) {
			TIntIntIterator iterator = int_t_primitive_map.iterator();
			while( iterator.hasNext() ) {
				iterator.advance();
				total += iterator.key();
			}
		}

		int_slot.set( total );
	}

	@Override
	public void testInt_THashMap() {
		int total = 0;

		for( int i = 0; i < ITERATIONS_INT; i++ ) {
			Iterator<Integer> iterator = int_t_map.keySet().iterator();

			//noinspection WhileLoopReplaceableByForEach
			while( iterator.hasNext() ) {
				total += iterator.next().intValue();
			}
		}

		int_slot.set( total );
	}

	@Override
	public void testInt_JavaHashMap() {
		int total = 0;

		for( int i = 0; i < ITERATIONS_INT; i++ ) {
			Iterator<Integer> iterator = int_java_map.keySet().iterator();

			//noinspection WhileLoopReplaceableByForEach
			while( iterator.hasNext() ) {
				total += iterator.next().intValue();
			}
		}

		int_slot.set( total );
	}

	@Override
	public void testInt_Trove2PrimitiveHashMap() {
		int total = 0;

		for( int i = 0; i < ITERATIONS_INT; i++ ) {
			gnu.trove.TIntIntIterator iterator = int_t2_primitive_map.iterator();
			while( iterator.hasNext() ) {
				iterator.advance();
				total += iterator.key();
			}
		}

		int_slot.set( total );
	}

	@Override
	public void testInt_Trove2HashMap() {
		int total = 0;

		for( int i = 0; i < ITERATIONS_INT; i++ ) {
			Iterator<Integer> iterator = int_t2_map.keySet().iterator();

			//noinspection WhileLoopReplaceableByForEach
			while( iterator.hasNext() ) {
				total += iterator.next().intValue();
			}
		}

		int_slot.set( total );
	}

	@Override
	public void testInt_ColtPrimitiveHashMap() {
		throw new UnsupportedOperationException();
	}


	// Objects...

	@Override
	public void testString_TPrimitiveHashMap() {
		int total = 0;

		for( int i = 0; i < ITERATIONS_STRING; i++ ) {
			TObjectIntIterator<String> iterator = string_t_primitive_map.iterator();
			while( iterator.hasNext() ) {
				iterator.advance();
				total += iterator.key().length();
			}
		}

		int_slot.set( total );
	}

	@Override
	public void testString_THashMap() {
		int total = 0;

		for( int i = 0; i < ITERATIONS_STRING; i++ ) {
			Iterator<String> iterator = string_t_map.keySet().iterator();

			//noinspection WhileLoopReplaceableByForEach
			while( iterator.hasNext() ) {
				total += iterator.next().length();
			}
		}

		int_slot.set( total );
	}

	@Override
	public void testString_JavaHashMap() {
		int total = 0;

		for( int i = 0; i < ITERATIONS_STRING; i++ ) {
			Iterator<String> iterator = string_java_map.keySet().iterator();

			//noinspection WhileLoopReplaceableByForEach
			while( iterator.hasNext() ) {
				total += iterator.next().length();
			}
		}

		int_slot.set( total );
	}

	@Override
	public void testString_Trove2PrimitiveHashMap() {
		int total = 0;

		for( int i = 0; i < ITERATIONS_STRING; i++ ) {
			gnu.trove.TObjectIntIterator<String> iterator =
				string_t2_primitive_map.iterator();
			while( iterator.hasNext() ) {
				iterator.advance();
				total += iterator.key().length();
			}
		}

		int_slot.set( total );
	}

	@Override
	public void testString_Trove2HashMap() {
		int total = 0;

		for( int i = 0; i < ITERATIONS_STRING; i++ ) {
			Iterator<String> iterator = string_t2_map.keySet().iterator();

			//noinspection WhileLoopReplaceableByForEach
			while( iterator.hasNext() ) {
				total += iterator.next().length();
			}
		}

		int_slot.set( total );
	}
}
