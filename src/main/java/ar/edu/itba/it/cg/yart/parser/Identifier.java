package ar.edu.itba.it.cg.yart.parser;

import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import ar.edu.itba.it.cg.yart.color.Color;
import ar.edu.itba.it.cg.yart.geometry.Point3d;
import ar.edu.itba.it.cg.yart.geometry.Vector3d;
import ar.edu.itba.it.cg.yart.parser.Property.PropertyType;

public class Identifier {

	public enum IdentifierType {
		LOOKAT("LookAt", 9),
		CAMERA("Camera", 1),
		FILM("Film", 1),
		SHAPE("Shape", 1),
		MATERIAL("Material", 1),
		NAMED_MATERIAL("NamedMaterial", 1),
		MAKE_NAMED_MATERIAL("MakeNamedMaterial", 1),
		TEXTURE("Texture", 3),
		LIGHT_SOURCE("LightSource", 1),
		IDENTITY("Identity", 0),
		TRANSFORM("Transform", 16),
		TRANSLATE("Translate", 3),
		ROTATE("Rotate", 4),
		SCALE("Scale", 3);
		
		final private String name;
		final private int expectedParameters;
		
		private IdentifierType(final String name, final int expectedParameters) {
			this.name = name;
			this.expectedParameters = expectedParameters;
		}
		
		public String getName() {
			return name;
		}
		
		public int getExpectedParameters() {
			return expectedParameters;
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

	public Identifier(final IdentifierType type, final String[] args) throws SceneParseException  {
		this.type = type;
		
		if (args.length < type.getExpectedParameters() || args[0] == null || args[0].isEmpty()) {
			throw new SceneParseException("Failed to read Identifier \"" + type.getName() + "\". Expected at least " + type.getExpectedParameters() + " parameters.");
		}
		
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
			properties.put(property.getName().toUpperCase(Locale.US), property);
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
			ret = properties.get(propertyName.toUpperCase(Locale.US));
		}
		
		return ret;
	}
	
	public boolean hasProperty(final String propertyName) {
		return properties != null && properties.containsKey(propertyName.toUpperCase(Locale.US));
	}
	
	public PropertyType getPropertyType(final String propertyName) {
		if (hasProperty(propertyName)) {
			return getProperty(propertyName).getType();
		}
		
		return null;
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
