package ar.edu.itba.it.cg.yart.parser;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.apache.commons.lang3.StringUtils;

import ar.edu.itba.it.cg.yart.parser.Attribute.AttributeType;
import ar.edu.itba.it.cg.yart.parser.Identifier.IdentifierType;
import ar.edu.itba.it.cg.yart.parser.Property.PropertyType;

public class SceneParser {
	
	private enum ParserStatus {
		GLOBAL,
		WORLD,
		ATTRIBUTE,
		END
	}

	private final String filePath;
	
	private ParserStatus status;
	
	private Attribute currentAttribute;
	private Identifier currentIdentifier;
	
	private List<Attribute> attributes = new ArrayList<Attribute>();
	private List<Identifier> globalIdentifiers = new ArrayList<Identifier>();
	private List<Identifier> accIdentifiers = new ArrayList<Identifier>();
	private List<Property> accProperties = new ArrayList<Property>();
	
	public SceneParser(final String filePath) {
		this.filePath = filePath;
		status = ParserStatus.GLOBAL;
	}
	
	public void parseFile() throws IOException, ParseException {
		parseFile(this.filePath);
	}
	
	private void parseFile(final String filePath) throws IOException, ParseException {
		Path path = Paths.get(filePath);
		Scanner scanner =  new Scanner(path, StandardCharsets.UTF_8.name());
		while (scanner.hasNextLine() && status != ParserStatus.END) {
			String rawLine = scanner.nextLine().trim().replaceAll("\\s", " ");
			String uncommentedLine = StringUtils.substringBefore(rawLine, "#");
			
			if (uncommentedLine != null && !uncommentedLine.isEmpty()) {
				processLine(uncommentedLine);
			}
		}
		applyProperties();
		scanner.close();
	}
	
	public List<Attribute> getAttributes() {
		return attributes;
	}
	
	public List<Identifier> getGlobalIdentifiers() {
		return globalIdentifiers;
	}
	
	private void processLine(final String line) throws IOException, ParseException {
		String first = StringUtils.substringBefore(line, " ");
		
		if (first.charAt(0) == '"') { // Is a property
			processProperties(line);
		}
		else if (Character.isUpperCase(first.charAt(0))) { // Is an attribute
			applyProperties();
			processAttribute(first, StringUtils.substringAfter(line, " ").split("\\s"));
		}
	}
	
	private void processProperties(final String line) throws ParseException {
		String[] properties = StringUtils.substringsBetween(line, "\"", "\"");
		String[] values = StringUtils.substringsBetween(line, "[", "]");
		
		int total = Math.min(properties.length, values.length);
		
		for (int i = 0; i < total; i++) {
			String[] p = properties[i].split("\\s+");
			PropertyType type = Property.getType(p[0]);
			if (type == null) {
				// TODO Unkown type, say something here
			}
			else if (p.length < 2) {
				// TODO Missing property name
			}
			else {
				accProperties.add(new Property(p[1], type, values[i]));
			}
		}
	}
	
	private void processAttribute(final String attribute, final String[] args) throws IOException, ParseException {
		// First, check if we're dealing with an Identifier
		IdentifierType identifierType = Identifier.getByName(attribute);
		if (identifierType != null) {
			currentIdentifier = new Identifier(identifierType, args);
			accIdentifiers.add(currentIdentifier);
		}
		// Maybe it's an Attribute
		else if (attribute.equals("AttributeBegin")) {
			if (currentAttribute != null) {
				throw new ParseException("Syntax error: Cannot create an attribute inside another", 0);
			}
			
			closeAttribute();
			currentAttribute = new Attribute(AttributeType.ATTRIBUTE, args);
			attributes.add(currentAttribute);
			status = ParserStatus.ATTRIBUTE;
		}
		else if (attribute.equals("AttributeEnd")) {
			if (currentAttribute == null || currentAttribute.getType() != AttributeType.ATTRIBUTE) {
				throw new ParseException("Syntax error: AttributeEnd found without matching AttributeBegin", 0);
			}
			
			closeAttribute();
			status = ParserStatus.WORLD;
		}
		else if (attribute.equals("ObjectBegin")) {
			if (currentAttribute != null) {
				throw new ParseException("Syntax error: Cannot create an attribute inside another", 0);
			}
			
			closeAttribute();
			currentAttribute = new Attribute(AttributeType.OBJECT, args);
			attributes.add(currentAttribute);
			status = ParserStatus.ATTRIBUTE;
		}
		else if (attribute.equals("ObjectEnd")) {
			if (currentAttribute == null || currentAttribute.getType() != AttributeType.OBJECT) {
				throw new ParseException("Syntax error: ObjectEnd found without matching ObjectBegin", 0);
			}
			
			closeAttribute();
			status = ParserStatus.WORLD;
		}
		else if (attribute.equals("TransformBegin")) {
			if (currentAttribute != null) {
				throw new ParseException("Syntax error: Cannot create an attribute inside another", 0);
			}
			
			closeAttribute();
			currentAttribute = new Attribute(AttributeType.TRANSFORM, args);
			attributes.add(currentAttribute);
			status = ParserStatus.ATTRIBUTE;
		}
		else if (attribute.equals("TransformEnd")) {
			if (currentAttribute == null || currentAttribute.getType() != AttributeType.TRANSFORM) {
				throw new ParseException("Syntax error: TransformEnd found without matching TransformBegin", 0);
			}
			
			closeAttribute();
			status = ParserStatus.WORLD;
		}
		else if (attribute.equals("WorldBegin")) {
			if (status != ParserStatus.GLOBAL) {
				throw new ParseException("Syntax error: World must be defined with global scope", 0);
			}
			
			// Commit all global identifiers
			closeAttribute();
			status = ParserStatus.WORLD;
		}
		else if (attribute.equals("WorldEnd")) {
			if (status == ParserStatus.ATTRIBUTE) {
				throw new ParseException("Syntax error: WorldEnd found while an Attribute is still open", 0);
			}
			else if (status != ParserStatus.WORLD) {
				throw new ParseException("Syntax error: WorldEnd found without matching WorldBegin", 0);
			}
			status = ParserStatus.END;
		}
		else if (attribute.equals("Include")) {
			String path = StringUtils.substringBetween(args[0], "\"");
			parseFile(path);
		}
	}
	
	private void closeAttribute() {
		applyProperties();
		applyIdentifiers();
		currentAttribute = null;
	}
	
	private void applyIdentifiers() {
		if (status == ParserStatus.GLOBAL) {
			for (Identifier i : accIdentifiers) {
				globalIdentifiers.add(i);
			}
		}
		else if (currentAttribute != null) {
			for (Identifier i : accIdentifiers) {
				currentAttribute.addIdentifier(i);
			}
		}
		else {
			// TODO Warning here, we're trying to add identifiers without any Attribute
			Attribute attribute = null;
			try {
				attribute = new Attribute(AttributeType.ATTRIBUTE, null);
			} catch (ParseException e) {
			}
			attributes.add(attribute);
			for (Identifier i : accIdentifiers) {
				attribute.addIdentifier(i);
			}
		}
		
		accIdentifiers.clear();
	}
	
	private void applyProperties() {
		if (currentIdentifier != null) {
			for (Property p : accProperties) {
				currentIdentifier.addProperty(p);
			}
		}
		else {
			// TODO Warning here, we're trying to add properties without any Identifier
		}
		
		accProperties.clear();
	}
}
