package io.github.christophknabe.multex.core.test;

import io.github.christophknabe.multex.core.MsgText;

import java.util.ListResourceBundle;

/**
 * ResourceBundle with message texts for testing class MsgText,
 * but without Locale and causeMarker.
 * 
 * @author Christoph Knabe 2003-09-09
 */
public class MsgTextResourceBundle extends ListResourceBundle {
	public static final String classCastExceptionText 
	= "Typumwandlung nicht moeglich von Objekt dieser Klasse";
	
	public static final String wertText = "Man kann mit Wert ";
	public static final String objektText = " nicht Objekt "; 
	public static final String initText = " initialisieren";
	public static final String initFailureText 
	= wertText + "{1}" + objektText + "{0}" + initText;
	
	public String getCauseMarker(){return null;}
    
	protected Object[][] getContents() {
		return _contents;
	}
	
	private final Object[][] _contents = new Object[][]{
		{ClassCastException.class.getName(), classCastExceptionText},
		{InitFailure.class.getName(), initFailureText},
		{
			getCauseMarker()==null ? "-dummy-" : MsgText.causeMarkerKey,
			getCauseMarker()==null ? "-dummy-" : getCauseMarker()
		}
	};

}
