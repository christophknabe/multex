package io.github.christophknabe.multex.core.annotation;

import io.github.christophknabe.multex.core.Exc;
import io.github.christophknabe.multex.tool.TextPattern;
import org.junit.Test;

import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

public final class TextPatternAnnotationTest {
	//TODO Test for parameter insertion:
	static class MyStaticExc extends Exc {}

	class MyDynamicExc extends Exc {}
	static class MyStaticNonexc extends Exc {}

	@TextPattern("Person with username {0} not found in database")
	public static class PersonNotFoundExc extends Exc {
		public PersonNotFoundExc(final String username){
			super(null, username);
		}
	}

	@Test
	public void newExc_mustBeStatic() {
		{
			//WHEN
			final PersonNotFoundExc result = new PersonNotFoundExc("username");
			//THEN
			assertEquals("io.github.christophknabe.multex.core.annotation.TextPatternAnnotationTest$PersonNotFoundExc: Person with username {0} not found in database\n" +
					"    {0} = 'username'", result.toString());
			final Class<? extends PersonNotFoundExc> resultClass = result.getClass();
			final int modifiers = resultClass.getModifiers();
            if(!Modifier.isStatic(modifiers)){
				fail("Throwable class " + resultClass.getName() + " must be static");
			}
			final TextPattern resultAnnotation = resultClass.getAnnotation(TextPattern.class);

			final String textPattern = resultAnnotation.value();
			assertEquals("Person with username {0} not found in database", textPattern);
		}
	}

}
