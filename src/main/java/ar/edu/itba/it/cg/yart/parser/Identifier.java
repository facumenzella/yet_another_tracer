package ar.edu.itba.it.cg.yart.parser;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import ar.edu.itba.it.cg.yart.color.Color;
import ar.edu.itba.it.cg.yart.geometry.Point3d;
import ar.edu.itba.it.cg.yart.geometry.Vector3d;

public class Identifier {

	public enum IdentifierType {
		LOOKAT("LookAt"),
		CAMERA("Camera"),
		FILM("Film"),
		SHAPE("Shape"),
		MATERIAL("Material"),
		NAMED_MATERIAL("NamedMaterial"),
		MAKE_NAMED_MATERIAL("MakeNamedMaterial"),
		TEXTURE("Texture"),
		LIGHT_SOURCE("LightSource"),
		IDENTITY("Identity"),
		TRANSFORM("Transform"),
		TRANSLATE("Translate"),
		ROTATE("Rotate"),
		SCALE("Scale");
		
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
			args[i] = args[i].replaceAll("[\"\\[\\]]", "");
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
	
	public void addProperties(final Collection<Property> properties) {
		for (Property p : properties) {
			addProperty(p);
		}
	}
	
	public IdentifierType getType() {
		return type;
	}
	
	public String[] getParameters() {
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
	
	public boolean getBoolean(final String propertyName, boolean defaultValue) {
		if (hasProperty(propertyName)) {
			return getProperty(propertyName).getBoolean();
		}
		else {
			return defaultValue;
		}
	}
	
	public boolean[] getBooleans(final String propertyName, boolean[] defaultValue) {
		if (hasProperty(propertyName)) {
			return getProperty(propertyName).getBooleans();
		}
		else {
			return defaultValue;
		}
	}
	
	public double getDouble(final String propertyName, double defaultValue) {
		if (hasProperty(propertyName)) {
			return getProperty(propertyName).getDouble();
		}
		else {
			return defaultValue;
		}
	}
	
	public double[] getDoubles(final String propertyName, double[] defaultValue) {
		if (hasProperty(propertyName)) {
			return getProperty(propertyName).getDoubles();
		}
		else {
			return defaultValue;
		}
	}
	
	public int getInteger(final String propertyName, int defaultValue) {
		if (hasProperty(propertyName)) {
			return getProperty(propertyName).getInteger();
		}
		else {
			return defaultValue;
		}
	}
	
	public int[] getIntegers(final String propertyName, int[] defaultValue) {
		if (hasProperty(propertyName)) {
			return getProperty(propertyName).getIntegers();
		}
		else {
			return defaultValue;
		}
	}
	
	public Vector3d getVector(final String propertyName, Vector3d defaultValue) {
		if (hasProperty(propertyName)) {
			return getProperty(propertyName).getVector();
		}
		else {
			return defaultValue;
		}
	}
	
	public Vector3d[] getVectors(final String propertyName, Vector3d[] defaultValue) {
		if (hasProperty(propertyName)) {
			return getProperty(propertyName).getVectors();
		}
		else {
			return defaultValue;
		}
	}
	
	public Vector3d getNormal(final String propertyName, Vector3d defaultValue) {
		return getVector(propertyName, defaultValue);
	}
	
	public Vector3d[] getNormals(final String propertyName, Vector3d[] defaultValue) {
		return getVectors(propertyName, defaultValue);
	}
	
	public Color getColor(final String propertyName, Color defaultValue) {
		if (hasProperty(propertyName)) {
			return getProperty(propertyName).getColor();
		}
		else {
			return defaultValue;
		}
	}
	
	public Color[] getColors(final String propertyName, Color[] defaultValue) {
		if (hasProperty(propertyName)) {
			return getProperty(propertyName).getColors();
		}
		else {
			return defaultValue;
		}
	}
	
	public String getString(final String propertyName, String defaultValue) {
		if (hasProperty(propertyName)) {
			return getProperty(propertyName).getString();
		}
		else {
			return defaultValue;
		}
	}
	
	public String[] getStrings(final String propertyName, String[] defaultValue) {
		if (hasProperty(propertyName)) {
			return getProperty(propertyName).getStrings();
		}
		else {
			return defaultValue;
		}
	}
	
	public Point3d getPoint(final String propertyName, Point3d defaultValue) {
		if (hasProperty(propertyName)) {
			return getProperty(propertyName).getPoint();
		}
		else {
			return defaultValue;
		}
	}
	
	public Point3d[] getPoints(final String propertyName, Point3d[] defaultValue) {
		if (hasProperty(propertyName)) {
			return getProperty(propertyName).getPoints();
		}
		else {
			return defaultValue;
		}
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
