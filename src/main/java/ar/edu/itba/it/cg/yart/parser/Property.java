package ar.edu.itba.it.cg.yart.parser;

import ar.edu.itba.it.cg.yart.color.Color;
import ar.edu.itba.it.cg.yart.geometry.Point3;
import ar.edu.itba.it.cg.yart.geometry.Vector3d;


public class Property {
	
	public enum PropertyType {
		INTEGER("integer"),
		FLOAT("float"),
		POINT("point"),
		VECTOR("vector"),
		NORMAL("normal"),
		COLOR("color"),
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
	
	public Property(final String name, final PropertyType type, final String value) {
		this.name = name;
		this.type = type;
		
		String[] values = value.split("\\s+");
		
		if (type == null) {
			throw new IllegalArgumentException("Property type cannot be null");
		}
		
		switch (type) {
		case BOOL:
			this.value = new boolean[values.length];
			for (int i = 0; i < values.length; i++) {
				((boolean[]) this.value)[i] = Boolean.valueOf(values[i]);
			}
			break;
		case COLOR:
			if (values.length % 3 != 0) {
				throw new IllegalArgumentException("Color properties need multiple of 3 values, found " + values.length);
			}
			this.value = new Color[values.length];
			for (int i = 0; i < values.length; i += 3) {
				((Color[]) this.value)[i] = new Color(Double.valueOf(values[i]), Double.valueOf(values[i+1]), Double.valueOf(values[i+2]));
			}
			break;
		case FLOAT:
			this.value = new double[values.length];
			for (int i = 0; i < values.length; i++) {
				((double[]) this.value)[i] = Double.valueOf(values[i]);
			}
			break;
		case INTEGER:
			this.value = new int[values.length];
			for (int i = 0; i < values.length; i++) {
				((int[]) this.value)[i] = Integer.valueOf(values[i]);
			}
			break;
		case VECTOR:
		case NORMAL:
			if (values.length % 3 != 0) {
				throw new IllegalArgumentException("Vector properties need multiple of 3 values, found " + values.length);
			}
			this.value = new Vector3d[values.length];
			for (int i = 0; i < values.length; i += 3) {
				((Vector3d[]) this.value)[i] = new Vector3d(Double.valueOf(values[i]), Double.valueOf(values[i+2]), Double.valueOf(values[i+1]));
			}
			break;
		case POINT:
			if (values.length % 3 != 0) {
				throw new IllegalArgumentException("Point properties need multiple of 3 values, found " + values.length);
			}
			this.value = new Point3[values.length];
			for (int i = 0; i < values.length; i += 3) {
				((Point3[]) this.value)[i] = new Point3(Double.valueOf(values[i]), Double.valueOf(values[i+2]), Double.valueOf(values[i+1]));
			}
			break;
		case STRING:
			this.value = new String[values.length];
			for (int i = 0; i < values.length; i++) {
				((String[]) this.value)[i] = values[i];
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
		return ((boolean[]) value);
	}
	
	public double getDouble() {
		return getDoubles()[0];
	}
	
	public double[] getDoubles() {
		return ((double[]) value);
	}
	
	public int getInteger() {
		return getIntegers()[0];
	}
	
	public int[] getIntegers() {
		return ((int[]) value);
	}
	
	public Vector3d getVector() {
		return getVectors()[0];
	}
	
	public Vector3d[] getVectors() {
		return ((Vector3d[]) value);
	}
	
	public Vector3d getNormal() {
		return getNormals()[0];
	}
	
	public Vector3d[] getNormals() {
		return getVectors();
	}
	
	public Color getColor() {
		return getColors()[0];
	}
	
	public Color[] getColors() {
		return ((Color[]) value);
	}
	
	public String getString() {
		return getStrings()[0];
	}
	
	public String[] getStrings() {
		return ((String[]) value);
	}
	
	public Point3 getPoint() {
		return getPoints()[0];
	}
	
	public Point3[] getPoints() {
		return ((Point3[]) value);
	}
	
	public PropertyType getType() {
		return type;
	}
	
	@Override
	public String toString() {
		return "(" + getType().getName() + ") " + getName() + " = " + getValue();
	}
}
