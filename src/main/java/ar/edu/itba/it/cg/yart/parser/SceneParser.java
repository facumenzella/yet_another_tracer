package ar.edu.itba.it.cg.yart.parser;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ar.edu.itba.it.cg.yart.YartConstants;
import ar.edu.itba.it.cg.yart.parser.Attribute.AttributeType;
import ar.edu.itba.it.cg.yart.parser.Identifier.IdentifierType;
import ar.edu.itba.it.cg.yart.parser.Property.PropertyType;
import ar.edu.itba.it.cg.yart.raytracer.interfaces.RayTracer;
import ar.edu.itba.it.cg.yart.raytracer.world.World;

public class SceneParser {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(YartConstants.LOG_FILE);

	private final RayTracer raytracer;
	private final SceneBuilder sceneBuilder;
	
	private Identifier currentIdentifier;
	
	private List<Property> accProperties = new ArrayList<Property>();
	
	private final Path basePath;
	private final Path filePath;
	
	public SceneParser(final String filePath, final RayTracer raytracer) {
		this.raytracer = raytracer;
		this.sceneBuilder = new SceneBuilder(raytracer);
		this.filePath = Paths.get(filePath).normalize();
		this.basePath = this.filePath.getParent();
	}
	
	public void parse() throws SceneParseException {
		try {
			sceneBuilder.setBasePath(basePath);
			World world = new World();
			raytracer.setWorld(world);
			parseFile(filePath);
		}
		catch (Exception e) {
			final String err = e.getMessage();
			LOGGER.error(err);
			throw new SceneParseException(err);
		}
	}
	
	private void parseFile(final Path path) throws SceneParseException {
		String folder = path.getParent().toString();
		Scanner scanner;
		try {
			scanner = new Scanner(path, StandardCharsets.UTF_8.name());
		}
		catch (IOException e) {
			throw new SceneParseException("Couldn't load file " + path + ".");
		}
		while (scanner.hasNextLine()) {
			String rawLine = scanner.nextLine().trim().replaceAll("\\s", " ");
			String uncommentedLine = StringUtils.substringBefore(rawLine, "#");
			
			if (uncommentedLine != null && !uncommentedLine.isEmpty()) {
				processLine(uncommentedLine, folder);
			}
		}
		applyProperties();
		scanner.close();
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
		
		int pLength = 0;
		int vLength = 0;
		
		if (properties != null)
			pLength = properties.length;
		if (values != null)
			vLength = values.length;
		
		int total = Math.min(pLength, vLength);
		
		try {
			for (int i = 0; i < total; i++) {
				String[] p = properties[i].split("\\s+");
				if (p.length < 2) {
					LOGGER.warn("Properties need a type and a name");
				}
				else {
					PropertyType type = Property.getType(p[0]);
					if (type == null) {
						LOGGER.warn("Unkown property type \"{}\"", p[0]);
					}
					else if (p.length < 2) {
						LOGGER.warn("Unkown property type \"{}\"", p[0]);
					}
					else {
						accProperties.add(new Property(p[1], type, values[i]));
					}
				}
			}
		}
		catch (IllegalArgumentException e) {
			LOGGER.warn(e.getMessage());
		}
	}
	
	private void processAttribute(final String attribute, final String[] args, final String folder) throws SceneParseException {
		// First, check if we're dealing with an Identifier
		IdentifierType identifierType = Identifier.getByName(attribute);
		if (identifierType != null) {
			currentIdentifier = new Identifier(identifierType, args);
		}
		// Maybe it's an Attribute
		else if (attribute.equals("AttributeBegin")) {
			sceneBuilder.attributeBegin(new Attribute(AttributeType.ATTRIBUTE, args));
		}
		else if (attribute.equals("ObjectBegin")) {
			sceneBuilder.attributeBegin(new Attribute(AttributeType.OBJECT, args));
		}
		else if (attribute.equals("TransformBegin")) {
			sceneBuilder.attributeBegin(new Attribute(AttributeType.TRANSFORM, args));
		}
		else if (attribute.equals("WorldBegin")) {
			sceneBuilder.attributeBegin(new Attribute(AttributeType.WORLD, args));
		}
		else if (attribute.equals("AttributeEnd") || attribute.equals("ObjectEnd") || attribute.equals("TransformEnd") || attribute.equals("WorldEnd")) {
			sceneBuilder.attributeEnd();
		}
		else if (attribute.equals("Include")) {
			parseFile(basePath.resolve(Paths.get(StringUtils.substringBetween(args[0], "\""))));
		}
		else {
			LOGGER.info("Identifier \"" + attribute + "\" not recognized");
		}
	}
	
	private void applyProperties() throws SceneParseException {
		if (currentIdentifier != null) {
			for (Property p : accProperties) {
				currentIdentifier.addProperty(p);
			}
			sceneBuilder.addIdentifier(currentIdentifier);
			currentIdentifier = null;
		}
		else {
			if (!accProperties.isEmpty())
				LOGGER.info("Discarded {} orphan properties", accProperties.size());
		}
		
		accProperties.clear();
	}
}
