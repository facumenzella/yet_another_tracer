package ar.edu.itba.it.cg.yart.parser;

import java.util.ArrayList;
import java.util.List;

public class Identifier {

	public enum IdentifierType {
		LOOKAT("LookAt"),
		CAMERA("Camera"),
		FILM("Film"),
		SHAPE("Shape");
		
		private String name;
		
		private IdentifierType(final String name) {
			this.name = name;
		}
		
		public String getName() {
			return name;
		}
	}

	private List<Property> properties;
	private Object parameter;
	private IdentifierType type;
	
	public static IdentifierType getByName(final String name) {
		for (IdentifierType type : IdentifierType.values()) {
			if (type.getName().equals(name)) {
				return type;
			}
		}
		
		return null;
	}

	public Identifier(final IdentifierType type, final String[] args) {
		this.type = type;
	}

	public void addProperty(final Property property) {
		if (properties == null) {
			properties = new ArrayList<Property>();
		}

		if (property != null) {
			properties.add(property);
		}
	}
	
	public IdentifierType getType() {
		return type;
	}
	
	public Object getParamter() {
		return parameter;
	}
	
	public List<Property> getProperties() {
		return properties;
	}
	
	@Override
	public String toString() {
		String children = "Empty";
		if (properties != null && !properties.isEmpty()) {
			children = properties.size() + " children";
		}
		
		return type.toString() + "(" + children + ")";
	}
}
