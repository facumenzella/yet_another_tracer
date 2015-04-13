package ar.edu.itba.it.cg.yart.parser;

public class Property {

	private final String name;
	private final Object value;
	
	public Property(final String name, final Object value) {
		this.name = name;
		this.value = value;
	}
	
	public String getName() {
		return name;
	}
	
	public Object getValue() {
		return value;
	}
}
