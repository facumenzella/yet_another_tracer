package ar.edu.itba.it.cg.yart.parser;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Identifier {

	public enum IdentifierType {
		LOOKAT("LookAt"),
		CAMERA("Camera"),
		FILM("Film"),
		SHAPE("Shape"),
		MATERIAL("Material");
		
		private String name;
		
		private IdentifierType(final String name) {
			this.name = name;
		}
		
		public String getName() {
			return name;
		}
	}

	private Map<String, Property> properties;
	private String[] parameter;
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
		
		for (int i = 0; i < args.length; i++) {
			args[i] = args[i].replaceAll("\"", "");
		}
		
		this.parameter = args;
	}

	public void addProperty(final Property property) {
		if (properties == null) {
			properties = new HashMap<String, Property>();
		}

		if (property != null) {
			properties.put(property.getName(), property);
		}
	}
	
	public IdentifierType getType() {
		return type;
	}
	
	public String[] getParamters() {
		return parameter;
	}
	
	public Collection<Property> getProperties() {
		return properties.values();
	}
	
	public Property getProperty(final String propertyName) {
		Property ret = null;
		
		if (properties != null) {
			ret = properties.get(propertyName);
		}
		
		return ret;
	}
	
	public boolean hasProperty(final String propertyName) {
		return properties != null && properties.containsKey(propertyName);
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
