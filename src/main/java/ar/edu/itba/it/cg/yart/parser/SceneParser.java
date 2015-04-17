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
		Path path = Paths.get(filePath);
		Scanner scanner =  new Scanner(path, StandardCharsets.UTF_8.name());
		while (scanner.hasNextLine() && status != ParserStatus.END) {
			String rawLine = scanner.nextLine().trim();
			String uncommentedLine = StringUtils.substringBefore(rawLine, "#");
			
			if (uncommentedLine != null && !uncommentedLine.isEmpty()) {
				processLine(uncommentedLine);
			}
		}
		applyProperties();
		scanner.close();
	}
	
	public void processLine(final String line) throws ParseException {
		String first = StringUtils.substringBefore(line, " ");
		System.out.println(first);
		
		if (first.charAt(0) == '"') { // Is a property
			processProperties(line);
		}
		else if (Character.isUpperCase(first.charAt(0))) { // Is an attribute
			applyProperties();
			processAttribute(first, StringUtils.substringAfter(line, " ").split("\\s"));
		}
	}
	
	public void processProperties(final String line) throws ParseException {
		String[] properties = StringUtils.substringsBetween(line, "\"", "\"");
		String[] values = StringUtils.substringsBetween(line, "[", "]");
		
		if (properties.length != values.length) {
			throw new ParseException("Property and value amount does not match.", 0);
		}
		
		for (int i = 0; i < properties.length; i++) {
			String[] p = properties[i].split("\\s+");
			accProperties.add(new Property(p[1], p[0], values[i]));
		}
	}
	
	public void processAttribute(final String attribute, final String[] args) throws ParseException {
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
	}
	
	public void closeAttribute() {
		applyProperties();
		applyIdentifiers();
		currentAttribute = null;
	}
	
	public void applyIdentifiers() {
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
		}
		
		accIdentifiers.clear();
	}
	
	public void applyProperties() {
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
