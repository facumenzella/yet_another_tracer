package ar.edu.itba.it.cg.yart.parser;

import java.util.ArrayList;
import java.util.List;

public class Attribute {
	
	public enum AttributeType {
		WORLD,
		ATTRIBUTE,
		OBJECT,
		TRANSFORM
	}

	private final AttributeType type;
	private List<Identifier> identifiers;
	
	public Attribute(final AttributeType type) {
		this.type = type;
	}
	
	public void addIdentifier(final Identifier identifier) {
		if (identifiers == null) {
			identifiers = new ArrayList<Identifier>();
		}
		
		if (identifier != null) {
			identifiers.add(identifier);
		}		
	}
	
	public AttributeType getType() {
		return type;
	}
}
