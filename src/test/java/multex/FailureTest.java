package multex;

import org.junit.Assert;
import org.junit.Test;

public class FailureTest extends Assert {
	
	static class MyStaticFailure extends Failure {}
	class MyDynamicFailure extends Failure {}
	static class MyStaticNonfailure extends Failure {}

	@Test
	public void newFailure_mustBeStatic() {
		{
			final Failure result = new MyStaticFailure();
			assertEquals("multex.FailureTest$MyStaticFailure", result.toString());
		}
		{
			try{
				new MyDynamicFailure();
				fail("IllegalArgumentException expected");
			}catch(IllegalArgumentException expected) {
				final Throwable cause = expected.getCause();
				assertNotNull(cause);
				assertEquals(MyDynamicFailure.class.getName(), cause.getClass().getName());
			}
		}
	}
	
	@Test
	public void newFailure_mustEndWithFailure() {
		try{
			new MyStaticNonfailure();
			fail("IllegalArgumentException expected");
		}catch(IllegalArgumentException expected) {}
	}
	

}
