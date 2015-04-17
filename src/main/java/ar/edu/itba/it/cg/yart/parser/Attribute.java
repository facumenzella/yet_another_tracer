package ar.edu.itba.it.cg.yart.parser;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

public class Attribute {
	
	public enum AttributeType {
		WORLD,
		ATTRIBUTE,
		OBJECT,
		TRANSFORM
	}

	private final AttributeType type;
	private Object parameter;
	private List<Identifier> identifiers;
	
	public Attribute(final AttributeType type, final String[] args) throws ParseException {
		this.type = type;
		
		if (type == AttributeType.OBJECT) {
			if (args == null || args.length == 0 || args[0].isEmpty()) {
				throw new ParseException("Syntax error: ObjectBegin expects at least one parameter", 0);
			}
			
			String strParam = StringUtils.substringBetween(args[0], "\"");
			
			if (strParam == null || strParam.isEmpty()) {
				throw new ParseException("Syntax error: ObjectBegin expects at least one parameter", 0);
			}
			
			parameter = strParam;
		}
	}
	
	public void addIdentifier(final Identifier identifier) {
		if (identifiers == null) {
			identifiers = new ArrayList<Identifier>();
		}
		
		if (identifier != null) {
			identifiers.add(identifier);
		}		
	}
	
	public List<Identifier> getIdentifiers() {
		return identifiers;
	}
	
	public AttributeType getType() {
		return type;
	}
	
	public Object getParamter() {
		return parameter;
	}
	
	@Override
	public String toString() {
		return type.toString();
	}
}
