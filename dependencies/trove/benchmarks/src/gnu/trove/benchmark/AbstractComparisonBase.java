package gnu.trove.benchmark;

import cern.colt.function.IntProcedure;
import cern.colt.map.OpenIntIntHashMap;
import com.logicartisan.thrifty.benchmark.Benchmark;
import gnu.trove.map.TByteByteMap;
import gnu.trove.map.TIntIntMap;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TByteByteHashMap;
import gnu.trove.map.hash.THashMap;
import gnu.trove.map.hash.TIntIntHashMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import gnu.trove.procedure.TByteProcedure;
import gnu.trove.procedure.TIntProcedure;
import gnu.trove.procedure.TObjectProcedure;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;


/**
 *
 */
@SuppressWarnings( "UnusedDeclaration" )
abstract class AbstractComparisonBase extends Benchmark {
	protected Map<Byte,Byte> byte_java_map;                     // Java HashMap
	protected TByteByteMap byte_t_primitive_map;                // Trove primitive
	protected THashMap<Byte,Byte> byte_t_map;                   // Trove object
	protected gnu.trove.TByteByteHashMap byte_t2_primitive_map; // Trove2 (old) primitive
	protected gnu.trove.THashMap<Byte,Byte> byte_t2_map;        // Trove2 (old) object

	protected Map<Integer,Integer> int_java_map;                // Java HashMap
	protected TIntIntMap int_t_primitive_map;                   // Trove primitive
	protected THashMap<Integer,Integer> int_t_map;              // Trove object
	protected gnu.trove.TIntIntHashMap int_t2_primitive_map;    // Trove2 (old) primitive
	protected gnu.trove.THashMap<Integer,Integer> int_t2_map;   // Trove2 (old) object
	protected OpenIntIntHashMap int_colt_primitive_map;         // Colt primitive

	protected Map<String,String> string_java_map;               // Java HashMap
	protected TObjectIntMap<String> string_t_primitive_map;     // Trove primitive
	protected THashMap<String,String> string_t_map;             // Trove object
	protected gnu.trove.TObjectIntHashMap<String> string_t2_primitive_map;// Trove2 (old) primitive
	protected gnu.trove.THashMap<String,String> string_t2_map;  // Trove2 (old) object


	protected final AtomicInteger int_slot = new AtomicInteger();

	protected final BytePrimitiveTotaler byte_primitive_totaler =
		new BytePrimitiveTotaler();
	protected final ByteTotaler byte_totaler = new ByteTotaler();

	protected final IntPrimitiveTotaler int_primitive_totaler =
		new IntPrimitiveTotaler();
	protected final IntTotaler int_totaler = new IntTotaler();

	protected final StringTotaler string_totaler = new StringTotaler();


	protected final Trove2BytePrimitiveTotaler trove2_byte_primitive_totaler =
		new Trove2BytePrimitiveTotaler();
	protected final Trove2ByteTotaler trove2_byte_totaler = new Trove2ByteTotaler();

	protected final Trove2IntPrimitiveTotaler trove2_int_primitive_totaler =
		new Trove2IntPrimitiveTotaler();
	protected final Trove2IntTotaler trove2_int_totaler = new Trove2IntTotaler();

	protected final Trove2StringTotaler trove2_string_totaler = new Trove2StringTotaler();


	protected final ColtIntTotaler colt_int_totaler = new ColtIntTotaler();


	private final boolean fill_maps;

	AbstractComparisonBase( boolean fill_maps ) {
		this.fill_maps = fill_maps;
	}

	@Override
	public void setUp( Method upcoming_method ) {
		String method_name = upcoming_method.getName();

		switch( method_name ) {
			// Bytes...
			case "testByte_JavaHashMap":
				byte_java_map = new HashMap<>();
				if ( fill_maps ) {
					for( Byte i : Constants.BYTE_OBJECTS ) {
						byte_java_map.put( i, i );
					}
				}
				break;
			case "testByte_TPrimitiveHashMap":
				byte_t_primitive_map = new TByteByteHashMap( Constants.BYTES.length );
				if ( fill_maps ) {
					for( byte i : Constants.BYTES ) {
						byte_t_primitive_map.put( i, i );
					}
				}
				break;
			case "testByte_THashMap":
				byte_t_map = new THashMap<>( Constants.BYTE_OBJECTS.length );
				if ( fill_maps ) {
					for( Byte i : Constants.BYTE_OBJECTS ) {
						byte_t_map.put( i, i );
					}
				}
				break;
			case "testByte_Trove2PrimitiveHashMap":
				byte_t2_primitive_map =
					new gnu.trove.TByteByteHashMap( Constants.BYTES.length );
				if ( fill_maps ) {
					for( byte i : Constants.BYTES ) {
						byte_t2_primitive_map.put( i, i );
					}
				}
				break;
			case "testByte_Trove2HashMap":
				byte_t2_map = new gnu.trove.THashMap<>( Constants.BYTE_OBJECTS.length );
				if ( fill_maps ) {
					for( Byte i : Constants.BYTE_OBJECTS ) {
						byte_t2_map.put( i, i );
					}
				}
				break;

			// Ints...
			case "testInt_JavaHashMap":
				int_java_map = new HashMap<>();
				if ( fill_maps ) {
					for( Integer i : Constants.INT_OBJECTS ) {
						int_java_map.put( i, i );
					}
				}
				break;
			case "testInt_TPrimitiveHashMap":
				int_t_primitive_map = new TIntIntHashMap( Constants.INTS.length );
				if ( fill_maps ) {
					for( int i : Constants.INTS ) {
						int_t_primitive_map.put( i, i );
					}
				}
				break;
			case "testInt_THashMap":
				int_t_map = new THashMap<>( Constants.INT_OBJECTS.length );
				if ( fill_maps ) {
					for( Integer i : Constants.INT_OBJECTS ) {
						int_t_map.put( i, i );
					}
				}
				break;
			case "testInt_Trove2PrimitiveHashMap":
				int_t2_primitive_map =
					new gnu.trove.TIntIntHashMap( Constants.INTS.length );
				if ( fill_maps ) {
					for( int i : Constants.INTS ) {
						int_t2_primitive_map.put( i, i );
					}
				}
				break;
			case "testInt_Trove2HashMap":
				int_t2_map = new gnu.trove.THashMap<>( Constants.INT_OBJECTS.length );
				if ( fill_maps ) {
					for( Integer i : Constants.INT_OBJECTS ) {
						int_t2_map.put( i, i );
					}
				}
				break;
			case "testInt_ColtPrimitiveHashMap":
				int_colt_primitive_map =
					new OpenIntIntHashMap( Constants.INTS.length );
				if ( fill_maps ) {
					for( int i : Constants.INTS ) {
						int_colt_primitive_map.put( i, i );
					}
				}
				break;

			// Objects...
			case "testString_JavaHashMap":
				string_java_map = new HashMap<>();
				if ( fill_maps ) {
					for( String i : Constants.STRING_OBJECTS ) {
						string_java_map.put( i, i );
					}
				}
				break;
			case "testString_TPrimitiveHashMap":
				string_t_primitive_map =
					new TObjectIntHashMap<>( Constants.STRING_OBJECTS.length );
				if ( fill_maps ) {
					for( String i : Constants.STRING_OBJECTS ) {
						string_t_primitive_map.put( i, i.length() );
					}
				}
				break;
			case "testString_THashMap":
				string_t_map = new THashMap<>( Constants.STRING_OBJECTS.length );
				if ( fill_maps ) {
					for( String i : Constants.STRING_OBJECTS ) {
						string_t_map.put( i, i );
					}
				}
				break;
			case "testString_Trove2PrimitiveHashMap":
				string_t2_primitive_map =
					new gnu.trove.TObjectIntHashMap<>( Constants.STRING_OBJECTS.length );
				if ( fill_maps ) {
					for( String i : Constants.STRING_OBJECTS ) {
						string_t2_primitive_map.put( i, i.length() );
					}
				}
				break;
			case "testString_Trove2HashMap":
				string_t2_map =
					new gnu.trove.THashMap<>( Constants.STRING_OBJECTS.length );
				if ( fill_maps ) {
					for( String i : Constants.STRING_OBJECTS ) {
						string_t2_map.put( i, i );
					}
				}
				break;
		}
	}

	@Override
	public void tearDown( Method completed_method ) {
		byte_java_map = null;
		byte_t_primitive_map = null;
		byte_t_map = null;
		byte_t2_primitive_map = null;
		byte_t2_map = null;

		int_java_map = null;
		int_t_primitive_map = null;
		int_t_map = null;
		int_t2_primitive_map = null;
		int_t2_map = null;
		int_colt_primitive_map = null;

		string_java_map = null;
		string_t_primitive_map = null;
		string_t_map = null;
		string_t2_primitive_map = null;
		string_t2_map = null;
	}



	// Bytes...
	public abstract void testByte_JavaHashMap();
	public abstract void testByte_TPrimitiveHashMap();
	public abstract void testByte_THashMap();
	public abstract void testByte_Trove2PrimitiveHashMap();
	public abstract void testByte_Trove2HashMap();

	// Ints...
	public abstract void testInt_JavaHashMap();
	public abstract void testInt_TPrimitiveHashMap();
	public abstract void testInt_THashMap();
	public abstract void testInt_Trove2PrimitiveHashMap();
	public abstract void testInt_Trove2HashMap();
	public abstract void testInt_ColtPrimitiveHashMap();

	// Objects...
	public abstract void testString_JavaHashMap();
	public abstract void testString_TPrimitiveHashMap();
	public abstract void testString_THashMap();
	public abstract void testString_Trove2PrimitiveHashMap();
	public abstract void testString_Trove2HashMap();


	// Trove 3 support classes

	protected static class BytePrimitiveTotaler implements TByteProcedure {
		int total = 0;

		void reset() {
			total = 0;
		}

		public boolean execute( byte a ) {
			total += a;
			return true;
		}
	}

	protected static class ByteTotaler implements TObjectProcedure<Byte> {
		int total = 0;

		void reset() {
			total = 0;
		}

		public boolean execute( Byte value ) {
			total += value.intValue();
			return true;
		}
	}

	protected static class IntPrimitiveTotaler implements TIntProcedure {
		int total = 0;

		void reset() {
			total = 0;
		}

		public boolean execute( int a ) {
			total += a;
			return true;
		}
	}

	@SuppressWarnings( "UnnecessaryUnboxing" )
	protected static class IntTotaler implements TObjectProcedure<Integer> {
		int total = 0;

		void reset() {
			total = 0;
		}

		public boolean execute( Integer value ) {
			total += value.intValue();
			return true;
		}
	}

	protected static class StringTotaler implements TObjectProcedure<String> {
		int total = 0;

		void reset() {
			total = 0;
		}

		public boolean execute( String value ) {
			total += value.length();
			return true;
		}
	}

	// Trove 2 support classes

	protected static class Trove2BytePrimitiveTotaler implements gnu.trove.TByteProcedure {
		int total = 0;

		void reset() {
			total = 0;
		}

		public boolean execute( byte a ) {
			total += a;
			return true;
		}
	}

	protected static class Trove2ByteTotaler implements gnu.trove.TObjectProcedure<Byte> {
		int total = 0;

		void reset() {
			total = 0;
		}

		public boolean execute( Byte value ) {
			total += value.intValue();
			return true;
		}
	}

	protected static class Trove2IntPrimitiveTotaler implements gnu.trove.TIntProcedure {
		int total = 0;

		void reset() {
			total = 0;
		}

		public boolean execute( int a ) {
			total += a;
			return true;
		}
	}

	@SuppressWarnings( "UnnecessaryUnboxing" )
	protected static class Trove2IntTotaler implements gnu.trove.TObjectProcedure<Integer> {
		int total = 0;

		void reset() {
			total = 0;
		}

		public boolean execute( Integer value ) {
			total += value.intValue();
			return true;
		}
	}

	protected static class Trove2StringTotaler implements gnu.trove.TObjectProcedure<String> {
		int total = 0;

		void reset() {
			total = 0;
		}

		public boolean execute( String value ) {
			total += value.length();
			return true;
		}
	}


	// Colt support classes

	protected static class ColtIntTotaler implements IntProcedure {
		int total = 0;

		void reset() {
			total = 0;
		}

		@Override
		public boolean apply( int i ) {
			total += i;
			return true;
		}
	}
}
