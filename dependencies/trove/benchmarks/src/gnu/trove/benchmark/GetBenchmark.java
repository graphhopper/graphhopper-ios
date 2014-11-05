package gnu.trove.benchmark;

/**
 * Tests get performance.
 */
@SuppressWarnings( "UnusedDeclaration" )
public class GetBenchmark extends AbstractComparisonBase {
	private static final int ITERATIONS_BYTE = 100;
	private static final int ITERATIONS_INT = 15;
	private static final int ITERATIONS_STRING = 10;

	public GetBenchmark() {
		super( true );      // we want filled maps
	}


	// Bytes...

	@Override
	public void testByte_TPrimitiveHashMap() {
		for( int j = 0; j < ITERATIONS_BYTE; j++ ) {
			for( byte i : Constants.BYTES ) {
				byte_t_primitive_map.get( i );
			}
		}
	}

	@Override
	public void testByte_THashMap() {
		for( int j = 0; j < ITERATIONS_BYTE; j++ ) {
			for( Byte i : Constants.BYTE_OBJECTS ) {
				byte_t_map.get( i );
			}
		}
	}

	@Override
	public void testByte_JavaHashMap() {
		for( int j = 0; j < ITERATIONS_BYTE; j++ ) {
			for( Byte i : Constants.BYTE_OBJECTS ) {
				byte_java_map.get( i );
			}
		}
	}

	@Override
	public void testByte_Trove2PrimitiveHashMap() {
		for( int j = 0; j < ITERATIONS_BYTE; j++ ) {
			for( byte i : Constants.BYTES ) {
				byte_t2_primitive_map.get( i );
			}
		}
	}

	@Override
	public void testByte_Trove2HashMap() {
		for( int j = 0; j < ITERATIONS_BYTE; j++ ) {
			for( Byte i : Constants.BYTE_OBJECTS ) {
				byte_t2_map.get( i );
			}
		}
	}


	// Ints...

	@Override
	public void testInt_TPrimitiveHashMap() {
		for( int j = 0; j < ITERATIONS_INT; j++ ) {
			for( int i : Constants.INTS ) {
				int_t_primitive_map.get( i );
			}
		}
	}

	@Override
	public void testInt_THashMap() {
		for( int j = 0; j < ITERATIONS_INT; j++ ) {
			for( Integer i : Constants.INT_OBJECTS ) {
				int_t_map.get( i );
			}
		}
	}

	@Override
	public void testInt_JavaHashMap() {
		for( int j = 0; j < ITERATIONS_INT; j++ ) {
			for( Integer i : Constants.INT_OBJECTS ) {
				int_java_map.get( i );
			}
		}
	}

	@Override
	public void testInt_Trove2PrimitiveHashMap() {
		for( int j = 0; j < ITERATIONS_INT; j++ ) {
			for( int i : Constants.INTS ) {
				int_t2_primitive_map.get( i );
			}
		}
	}

	@Override
	public void testInt_Trove2HashMap() {
		for( int j = 0; j < ITERATIONS_INT; j++ ) {
			for( Integer i : Constants.INT_OBJECTS ) {
				int_t2_map.get( i );
			}
		}
	}

	@Override
	public void testInt_ColtPrimitiveHashMap() {
		for( int j = 0; j < ITERATIONS_INT; j++ ) {
			for( int i : Constants.INTS ) {
				int_colt_primitive_map.get( i );
			}
		}
	}


	// Objects...

	@Override
	public void testString_TPrimitiveHashMap() {
		for( int j = 0; j < ITERATIONS_STRING; j++ ) {
			for( String i : Constants.STRING_OBJECTS ) {
				string_t_primitive_map.get( i );
			}
		}
	}

	@Override
	public void testString_THashMap() {
		for( int j = 0; j < ITERATIONS_STRING; j++ ) {
			for( String i : Constants.STRING_OBJECTS ) {
				string_t_map.get( i );
			}
		}
	}

	@Override
	public void testString_JavaHashMap() {
		for( int j = 0; j < ITERATIONS_STRING; j++ ) {
			for( String i : Constants.STRING_OBJECTS ) {
				string_java_map.get( i );
			}
		}
	}

	@Override
	public void testString_Trove2PrimitiveHashMap() {
		for( int j = 0; j < ITERATIONS_STRING; j++ ) {
			for( String i : Constants.STRING_OBJECTS ) {
				string_t2_primitive_map.get( i );
			}
		}
	}

	@Override
	public void testString_Trove2HashMap() {
		for( int j = 0; j < ITERATIONS_STRING; j++ ) {
			for( String i : Constants.STRING_OBJECTS ) {
				string_t2_map.get( i );
			}
		}
	}
}
