package ar.edu.itba.it.cg.yart.parser;

import java.io.File;
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
	
	public String getPath() {
		return filePath;
	}
	
	public void parseFile() throws SceneParseException {
		parseFile(this.filePath);
	}
	
	private void parseFile(final String filePath) throws SceneParseException {
		Path path = Paths.get(filePath);
		String folder = path.getParent().toString();
		Scanner scanner;
		try {
			scanner = new Scanner(path, StandardCharsets.UTF_8.name());
		}
		catch (IOException e) {
			throw new SceneParseException("Couldn't load file " + filePath + ".");
		}
		while (scanner.hasNextLine() && status != ParserStatus.END) {
			String rawLine = scanner.nextLine().trim().replaceAll("\\s", " ");
			String uncommentedLine = StringUtils.substringBefore(rawLine, "#");
			
			if (uncommentedLine != null && !uncommentedLine.isEmpty()) {
				processLine(uncommentedLine, folder);
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
	
	private void processLine(final String line, final String folder) throws SceneParseException {
		String first = StringUtils.substringBefore(line, " ");
		
		if (first.charAt(0) == '"') { // Is a property
			processProperties(line);
		}
		else if (Character.isUpperCase(first.charAt(0))) { // Is an attribute
			applyProperties();
			processAttribute(first, StringUtils.substringAfter(line, " ").split("\\s"), folder);
		}
	}
	
	private void processProperties(final String line) throws SceneParseException {
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
	
	private void processAttribute(final String attribute, final String[] args, final String folder) throws SceneParseException {
		// First, check if we're dealing with an Identifier
		IdentifierType identifierType = Identifier.getByName(attribute);
		if (identifierType != null) {
			currentIdentifier = new Identifier(identifierType, args);
			accIdentifiers.add(currentIdentifier);
		}
		// Maybe it's an Attribute
		else if (attribute.equals("AttributeBegin")) {
			if (currentAttribute != null) {
				throw new SceneParseException("Syntax error: Cannot create an attribute inside another");
			}
			
			closeAttribute();
			currentAttribute = new Attribute(AttributeType.ATTRIBUTE, args);
			attributes.add(currentAttribute);
			status = ParserStatus.ATTRIBUTE;
		}
		else if (attribute.equals("AttributeEnd")) {
			if (currentAttribute == null || currentAttribute.getType() != AttributeType.ATTRIBUTE) {
				throw new SceneParseException("Syntax error: AttributeEnd found without matching AttributeBegin");
			}
			
			closeAttribute();
			status = ParserStatus.WORLD;
		}
		else if (attribute.equals("ObjectBegin")) {
			if (currentAttribute != null) {
				throw new SceneParseException("Syntax error: Cannot create an attribute inside another");
			}
			
			closeAttribute();
			currentAttribute = new Attribute(AttributeType.OBJECT, args);
			attributes.add(currentAttribute);
			status = ParserStatus.ATTRIBUTE;
		}
		else if (attribute.equals("ObjectEnd")) {
			if (currentAttribute == null || currentAttribute.getType() != AttributeType.OBJECT) {
				throw new SceneParseException("Syntax error: ObjectEnd found without matching ObjectBegin");
			}
			
			closeAttribute();
			status = ParserStatus.WORLD;
		}
		else if (attribute.equals("TransformBegin")) {
			if (currentAttribute != null) {
				throw new SceneParseException("Syntax error: Cannot create an attribute inside another");
			}
			
			closeAttribute();
			currentAttribute = new Attribute(AttributeType.TRANSFORM, args);
			attributes.add(currentAttribute);
			status = ParserStatus.ATTRIBUTE;
		}
		else if (attribute.equals("TransformEnd")) {
			if (currentAttribute == null || currentAttribute.getType() != AttributeType.TRANSFORM) {
				throw new SceneParseException("Syntax error: TransformEnd found without matching TransformBegin");
			}
			
			closeAttribute();
			status = ParserStatus.WORLD;
		}
		else if (attribute.equals("WorldBegin")) {
			if (status != ParserStatus.GLOBAL) {
				throw new SceneParseException("Syntax error: World must be defined with global scope");
			}
			
			// Commit all global identifiers
			closeAttribute();
			status = ParserStatus.WORLD;
		}
		else if (attribute.equals("WorldEnd")) {
			if (status == ParserStatus.ATTRIBUTE) {
				throw new SceneParseException("Syntax error: WorldEnd found while an Attribute is still open");
			}
			else if (status != ParserStatus.WORLD) {
				throw new SceneParseException("Syntax error: WorldEnd found without matching WorldBegin");
			}
			status = ParserStatus.END;
		}
		else if (attribute.equals("Include")) {
			String path = null;
			String file = StringUtils.substringBetween(args[0], "\"");
			if (StringUtils.isEmpty(folder)) {
				path = file;
			}
			else {
				path = folder + File.separator + file;
			}
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
			} catch (SceneParseException e) {
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
