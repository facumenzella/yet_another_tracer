package ar.edu.itba.it.cg.yart.parser;

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
	private List<Identifier> identifiers = new ArrayList<Identifier>();
	
	public Attribute(final AttributeType type, final String[] args) throws SceneParseException {
		this.type = type;
		
		if (type == AttributeType.OBJECT) {
			if (args == null || args.length == 0 || args[0].isEmpty()) {
				throw new SceneParseException("Syntax error: ObjectBegin expects at least one parameter");
			}
			
			String strParam = StringUtils.substringBetween(args[0], "\"");
			
			if (strParam == null || strParam.isEmpty()) {
				throw new SceneParseException("Syntax error: ObjectBegin expects at least one parameter");
			}
			
			parameter = strParam;
		}
	}
	
	public void addIdentifier(final Identifier identifier) {
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
