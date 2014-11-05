package gnu.trove.benchmark;

/**
 * Tests remove performance.
 */
@SuppressWarnings( "UnusedDeclaration" )
public class RemoveBenchmark extends AbstractComparisonBase {
	public RemoveBenchmark() {
		super( false );      // we want filled maps
	}


	// Bytes...

	public void testByte_TPrimitiveHashMap() {
		for( byte i : Constants.BYTES ) {
			byte_t_primitive_map.remove( i );
		}
	}

	public void testByte_THashMap() {
		for( Byte i : Constants.BYTE_OBJECTS ) {
			byte_t_map.remove( i );
		}
	}

	public void testByte_JavaHashMap() {
		for( Byte i : Constants.BYTE_OBJECTS ) {
			byte_java_map.remove( i );
		}
	}

	@Override
	public void testByte_Trove2PrimitiveHashMap() {
		for( byte i : Constants.BYTES ) {
			byte_t2_primitive_map.remove( i );
		}
	}

	@Override
	public void testByte_Trove2HashMap() {
		for( Byte i : Constants.BYTE_OBJECTS ) {
			byte_t2_map.remove( i );
		}
	}


	// Ints...

	public void testInt_TPrimitiveHashMap() {
		for( int i : Constants.INTS ) {
			int_t_primitive_map.remove( i );
		}
	}

	public void testInt_THashMap() {
		for( Integer i : Constants.INT_OBJECTS ) {
			int_t_map.remove( i );
		}
	}

	public void testInt_JavaHashMap() {
		for( Integer i : Constants.INT_OBJECTS ) {
			int_java_map.remove( i );
		}
	}

	@Override
	public void testInt_Trove2PrimitiveHashMap() {
		for( int i : Constants.INTS ) {
			int_t2_primitive_map.remove( i );
		}
	}

	@Override
	public void testInt_Trove2HashMap() {
		for( Integer i : Constants.INT_OBJECTS ) {
			int_t2_map.remove( i );
		}
	}

	@Override
	public void testInt_ColtPrimitiveHashMap() {
		for( int i : Constants.INTS ) {
			int_colt_primitive_map.removeKey( i );
		}
	}


	// Objects...

	public void testString_TPrimitiveHashMap() {
		for( String i : Constants.STRING_OBJECTS ) {
			string_t_primitive_map.remove( i );
		}
	}

	public void testString_THashMap() {
		for( String i : Constants.STRING_OBJECTS ) {
			string_t_map.remove( i );
		}
	}

	public void testString_JavaHashMap() {
		for( String i : Constants.STRING_OBJECTS ) {
			string_java_map.remove( i );
		}
	}

	@Override
	public void testString_Trove2PrimitiveHashMap() {
		for( String i : Constants.STRING_OBJECTS ) {
			string_t2_primitive_map.remove( i );
		}
	}

	@Override
	public void testString_Trove2HashMap() {
		for( String i : Constants.STRING_OBJECTS ) {
			string_t2_map.remove( i );
		}
	}
}
