package multex.test;

import junit.framework.Assert;
import junit.framework.AssertionFailedError;

/** Extension of the standard JUnit Assert class with some special assertions,
 * which give better diagnostics in case of long Strings or collections.
 * @author Knabe
 */
public class MultexAssert extends Assert {

	/**Checks, that i_contained is contained in i_total*/
	public static void assertIsContained(final String i_contained, final String i_total)
	throws AssertionFailedError
	{
		if(i_total.indexOf(i_contained)<0){
			throw new AssertionFailedError(
				"Expected string <" + i_contained 
				+ "> was not contained in <" + i_total + ">."
			);
		}
	}

	/**Checks, that one String is the first part of another. 
	 * @param i_expected The String, which shall be the first part of the other String
	 * @param i_actual The String, whose first part shall be the other
	 */
	public static void assertIsStart(final String i_expected, final String i_actual)
	throws AssertionFailedError
	{
		if(i_expected==null && i_actual==null){return;}
		if(i_expected==null){fail("expected String is null");}
		if(i_actual==null){fail("actual String is null");}
		final int lengthOfExpected = i_expected.length();
		if(lengthOfExpected>i_actual.length()){
			fail("Expected start String <" + i_expected
				+ "> longer than actual total String <" + i_actual + ">."
			);
		}
		for(int i=0; i<lengthOfExpected; i++){
			final char expectedChar = i_expected.charAt(i);
			final char actualChar   = i_actual.charAt(i);
			if(expectedChar!=actualChar){
				fail("Expected start String is not completely contained in actual total String."
					+ "\nCorrect common start part is <" + i_expected.substring(0,i) + ">"
					+ "\nFirst difference occurs at index " + i 
					+ "\n where <" + i_expected.substring(i) + "> is not the start of <" + i_actual.substring(i) + ">."
				);
			}
		}
//		if(!i_actual.startsWith(i_expected)){
//			throw new AssertionFailedError(
//				"Expected string <" + i_expected 
//				+ "> is not the start of <" + i_actual + ">."
//			);
//		}
	}

	/**Checks, that the arrays expected and got are both null, or have the same length 
	 * and each element equals to the corresponding.
	 * */
	public static void assertEquals(final Object[] i_expected, final Object[] i_got)
	throws AssertionFailedError
	{
		if(i_expected==i_got){return;}
		if(i_expected==null){fail("Expected array is null, but the array got not");}
		if(i_expected.length!=i_got.length){
			fail("Expected array has length " + i_expected.length 
				+ ", but array got has length " + i_got.length
			);
		}
		for(int i=0; i<i_expected.length; i++){
			assertEquals("Array element " + i, i_expected[i], i_got[i]);
		}
	}
	
	public static void assertLongStringEquals(
		final String i_expected, final String i_got
	){
		if(i_expected==i_got){return;}
		if(i_expected==null){fail("Expected String is null, but the String got not. It is <"+i_got+">");}
		if(i_expected.length()!=i_got.length()){
			fail("Expected String has length " + i_expected.length() 
				+ ", but String got has length " + i_got.length()
				+ "\nExpected: <" + i_expected + ">, got: <" + i_got + ">"
			);
		}
		for(int i=0; i<i_expected.length(); i++){
			assertEquals(
				"String character at index " + i + ": ", 
				i_expected.charAt(i), 
				i_got.charAt(i)
			);
		}
		
	}
}
