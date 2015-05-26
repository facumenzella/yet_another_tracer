package ar.edu.itba.it.cg.yart.parser;

import ar.edu.itba.it.cg.yart.parser.Identifier.IdentifierType;
import ar.edu.itba.it.cg.yart.parser.Property.PropertyType;

public class PropertyNotFoundException extends Exception {

	private static final long serialVersionUID = 1L;
	
	private final String propertyName;
	private final PropertyType propertyType;
	private final IdentifierType identifierType;
	
	public PropertyNotFoundException(final String propertyName, final IdentifierType identifierType, final PropertyType propertyType) {
		this.propertyName = propertyName;
		this.propertyType = propertyType;
		this.identifierType = identifierType;
	}
	
	@Override
	public String getMessage() {
		return "Required " + propertyType.getName() + " \"" + propertyName + "\" in Identifier \"" + identifierType.getName() + "\" not found";
	}

}
