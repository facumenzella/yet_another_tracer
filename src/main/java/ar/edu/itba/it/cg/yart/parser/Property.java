package ar.edu.itba.it.cg.yart.parser;

import ar.edu.itba.it.cg.yart.color.Color;
import ar.edu.itba.it.cg.yart.geometry.Point3d;
import ar.edu.itba.it.cg.yart.geometry.Vector3d;


public class Property {
	
	public enum PropertyType {
		INTEGER("integer"),
		FLOAT("float"),
		POINT("point"),
		VECTOR("vector"),
		NORMAL("normal"),
		COLOR("color"),
		TEXTURE("texture"),
		BOOL("bool"),
		STRING("string");
		
		private String name;
		
		private PropertyType(final String name) {
			this.name = name;
		}
		
		public String getName() {
			return name;
		}
	}

	private final String name;
	private final Object value;
	private final PropertyType type;
	
	public static PropertyType getType(final String name) {
		for (PropertyType type : PropertyType.values()) {
			if (type.getName().equals(name)) {
				return type;
			}
		}
		
		return null;
	}
	
	public Property(final String name, final PropertyType type, final String value) throws IllegalArgumentException {
		this.name = name;
		this.type = type;
		
		String[] values = value.split("\\s+");
		
		if (type == null) {
			throw new IllegalArgumentException("Property type cannot be null");
		}
		else if (name == null || name.isEmpty()) {
			throw new IllegalArgumentException("Missing property name");
		}
		
		double[] doubleValues;
		
		switch (type) {
		case BOOL:
			this.value = ParserUtils.parseBooleanArray(values);
			break;
		case COLOR:
			if (values.length % 3 != 0) {
				throw new IllegalArgumentException("Color properties need multiple of 3 values, found " + values.length);
			}
			this.value = new Color[values.length / 3];
			doubleValues = ParserUtils.parseDoubleArray(values);
			for (int i = 0; i < values.length; i += 3) {
				((Color[]) this.value)[i / 3] = new Color(doubleValues[i], doubleValues[i+1], doubleValues[i+2]);
			}
			break;
		case FLOAT:
			this.value = ParserUtils.parseDoubleArray(values);
			break;
		case INTEGER:
			this.value = ParserUtils.parseIntegerArray(values);
			break;
		case VECTOR:
		case NORMAL:
			if (values.length % 3 != 0) {
				throw new IllegalArgumentException("Vector properties need multiple of 3 values, found " + values.length);
			}
			this.value = new Vector3d[values.length / 3];
			doubleValues = ParserUtils.parseDoubleArray(values);
			for (int i = 0; i < values.length; i += 3) {
				((Vector3d[]) this.value)[i / 3] = new Vector3d(doubleValues[i], doubleValues[i+1], doubleValues[i+2]);
			}
			break;
		case POINT:
			if (values.length % 3 != 0) {
				throw new IllegalArgumentException("Point properties need multiple of 3 values, found " + values.length);
			}
			this.value = new Point3d[values.length / 3];
			doubleValues = ParserUtils.parseDoubleArray(values);
			for (int i = 0; i < values.length; i += 3) {
				((Point3d[]) this.value)[i / 3] = new Point3d(doubleValues[i], doubleValues[i+1], doubleValues[i+2]);
			}
			break;
		case TEXTURE:
		case STRING:
			this.value = new String[values.length];
			boolean empty = true;
			for (int i = 0; i < values.length; i++) {
				((String[]) this.value)[i] = values[i].replaceAll("\"", "");
				empty &= ((String[]) this.value)[i].isEmpty();
			}
			if (empty) {
				throw new IllegalArgumentException("String property must be non-empty");
			}
			break;
		default:
			this.value = null;
			break;
		}
	}
	
	public String getName() {
		return name;
	}
	
	public Object getValue() {
		return value;
	}
	
	public boolean getBoolean() {
		return getBooleans()[0];
	}
	
	public boolean[] getBooleans() {
		try {
			return ((boolean[]) value);
		}
		catch (ClassCastException e) {
			throw new ClassCastException(getErrorMessage("boolean"));
		}
	}
	
	public double getDouble() {
		return getDoubles()[0];
	}
	
	public double[] getDoubles() {
		try {
			return ((double[]) value);
		}
		catch (ClassCastException e) {
			throw new ClassCastException(getErrorMessage("float"));
		}
	}
	
	public int getInteger() {
		return getIntegers()[0];
	}
	
	public int[] getIntegers() {
		try {
			return ((int[]) value);
		}
		catch (ClassCastException e) {
			throw new ClassCastException(getErrorMessage("integer"));
		}
	}
	
	public Vector3d getVector() {
		return getVectors()[0];
	}
	
	public Vector3d[] getVectors() {
		try {
			return ((Vector3d[]) value);
		}
		catch (ClassCastException e) {
			throw new ClassCastException(getErrorMessage("vector"));
		}
	}
	
	public Vector3d getNormal() {
		return getNormals()[0];
	}
	
	public Vector3d[] getNormals() {
		try {
			return getVectors();
		}
		catch (ClassCastException e) {
			throw new ClassCastException(getErrorMessage("normal"));
		}
	}
	
	public Color getColor() {
		return getColors()[0];
	}
	
	public Color[] getColors() {
		try {
			return ((Color[]) value);
		}
		catch (ClassCastException e) {
			throw new ClassCastException(getErrorMessage("color"));
		}
	}
	
	public String getString() {
		StringBuilder sb = new StringBuilder();
		for (String s : getStrings()) {
			sb.append(s);
		}
		return sb.toString();
	}
	
	public String[] getStrings() {
		try {
			return ((String[]) value);
		}
		catch (ClassCastException e) {
			throw new ClassCastException(getErrorMessage("string"));
		}
	}
	
	public Point3d getPoint() {
		return getPoints()[0];
	}
	
	public Point3d[] getPoints() {
		try {
			return ((Point3d[]) value);
		}
		catch (ClassCastException e) {
			throw new ClassCastException(getErrorMessage("point"));
		}
	}
	
	public PropertyType getType() {
		return type;
	}
	
	@Override
	public String toString() {
		return "(" + getType().getName() + ") " + getName() + " = " + getValue();
	}
	
	private String getErrorMessage(String expectedType) {
		return "Couldn't retreive " + expectedType + " property \"" + name + "\". Found " + type + ".";
	}
}
