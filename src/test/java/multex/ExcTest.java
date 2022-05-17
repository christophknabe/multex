package multex;

import org.junit.Assert;
import org.junit.Test;

public class ExcTest extends Assert {
	
	static class MyStaticExc extends Exc {}
	class MyDynamicExc extends Exc {}
	static class MyStaticNonexc extends Exc {}

	@Test
	public void newExc_mustBeStatic() {
		{
			final Exc result = new MyStaticExc();
			assertEquals("multex.ExcTest$MyStaticExc", result.toString());
		}
		{
			try{
				new MyDynamicExc();
				fail("IllegalArgumentException expected");
			}catch(IllegalArgumentException expected) {
				final Throwable cause = expected.getCause();
				assertNotNull(cause);
				assertEquals(MyDynamicExc.class.getName(), cause.getClass().getName());
			}
		}
	}
	
	@Test
	public void newExc_mustEndWithExc() {
		try{
			new MyStaticNonexc();
			fail("IllegalArgumentException expected");
		}catch(IllegalArgumentException expected) {}
	}
	

}
