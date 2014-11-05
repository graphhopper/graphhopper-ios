package gnu.trove.benchmark;

/**
 *
 */
@SuppressWarnings( "UnnecessaryBoxing" )
class Constants {
	public static final Integer INT_OBJECTS[] = new Integer[ 1000000 ];
	public static final int INTS[] = new int[ 1000000 ];
	static {
		for( int i = 0; i < INT_OBJECTS.length; i++ ) {
			INT_OBJECTS[ i ] = Integer.valueOf( i );
			INTS[ i ] = i;
		}
	}

	public static final Byte BYTE_OBJECTS[] = new Byte[ 255 ];
	public static final byte BYTES[] = new byte[ 255 ];
	static {
		byte value = -128;
		for( int i = 0; i < BYTE_OBJECTS.length; i++ ) {
			BYTE_OBJECTS[ i ] = Byte.valueOf( value );
			BYTES[ i ] = value;
		}
	}

	public static final String STRING_OBJECTS[] = new String[ 300000 ];
	static {
		for( int i = 0; i < STRING_OBJECTS.length; i++ ) {
			STRING_OBJECTS[ i ] = String.valueOf( i );
		}
	}
}
