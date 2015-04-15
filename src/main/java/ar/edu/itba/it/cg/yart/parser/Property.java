package ar.edu.itba.it.cg.yart.parser;

public class Property {

	private final String name;
	private final Object value;
	private final String type;
	
	public Property(final String name, final String type, final String value) {
		this.name = name;
		this.type = type;
		
		String[] values = value.split("\\s+");
		
		if (type.equals("integer")) {
			if (values.length == 1) {
				this.value = Integer.valueOf(value);
			}
			else {
				this.value = new Integer[values.length];
				for (int i = 0; i < values.length; i++) {
					((Integer[]) this.value)[i] = Integer.valueOf(value);
				}
			}
		}
		else if (type.equals("float")) {
			if (values.length == 1) {
				this.value = Double.valueOf(value);
			}
			else {
				this.value = new Double[values.length];
				for (int i = 0; i < values.length; i++) {
					((Double[]) this.value)[i] = Double.valueOf(value);
				}
			}
		}
		else {
			this.value = null;
		}
	}
	
	public String getName() {
		return name;
	}
	
	public Object getValue() {
		return value;
	}
	
	public String getType() {
		return type;
	}
}
