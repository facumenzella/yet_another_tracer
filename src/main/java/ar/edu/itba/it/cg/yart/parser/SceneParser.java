package ar.edu.itba.it.cg.yart.parser;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.apache.commons.lang3.StringUtils;

import ar.edu.itba.it.cg.yart.geometry.Point3;
import ar.edu.itba.it.cg.yart.geometry.Vector3d;
import ar.edu.itba.it.cg.yart.light.materials.Material;
import ar.edu.itba.it.cg.yart.parser.Attribute.AttributeType;

public class SceneParser {
	
	private enum ParserStatus {
		GLOBAL,
		WORLD,
		ATTRIBUTE,
		END
	}
	
	private enum Identifier {
		LOOKAT,
		CAMERA,
		FILM,
	}

	private final String filePath;
	private Map<String, Material> materials;
	
	private ParserStatus status;
	private Identifier identifier;
	private Attribute currentAttribute;
	private List<Property> accProperties;
	
	public SceneParser(final String filePath) {
		this.filePath = filePath;
		materials = new HashMap<String, Material>();
		status = ParserStatus.GLOBAL;
		accProperties = new ArrayList<Property>();
	}
	
	public void parseFile() throws IOException, ParseException {
		Path path = Paths.get(filePath);
		Scanner scanner =  new Scanner(path, StandardCharsets.UTF_8.name());
		while (scanner.hasNextLine() && status != ParserStatus.END) {
			processLine(scanner.nextLine());
		}
		applyProperties();
		scanner.close();
	}
	
	public void processLine(final String line) throws ParseException {
		Scanner scanner = new Scanner(line);
		
		if (!scanner.hasNext()) {
			scanner.close();
			return;
		}
		
		String first = scanner.next();
		if (first.startsWith("#")) { // Is a comment
			
		}
		else if (first.startsWith("\"")) { // Is a property
			processProperties(line);
		}
		else if (first.matches("^[A-Z].*")) { // Is an attribute
			applyProperties();
			processAttribute(first, line);
		}
		
		scanner.close();
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
	
	public void processAttribute(final String attribute, final String line) throws ParseException {
		String[] args = line.split("\\s");
		
		switch (status) {
		case ATTRIBUTE:
			break;
		case GLOBAL:
			if (attribute.equals("WorldBegin")) {
				status = ParserStatus.WORLD;
			}
			else if (attribute.equals("LookAt")) {
				if (args.length != 10) {
					throw new ParseException("Syntax error: LookAt expects 9 doubles, found " + (args.length - 1), 0);
				}
				
				// TODO Use these?
				Point3 eye = new Point3(Double.valueOf(args[1]), Double.valueOf(args[2]), Double.valueOf(args[3]));
				Point3 lookat = new Point3(Double.valueOf(args[4]), Double.valueOf(args[5]), Double.valueOf(args[6]));
				Vector3d up = new Vector3d(Double.valueOf(args[7]), Double.valueOf(args[8]), Double.valueOf(args[9]));
				identifier = Identifier.LOOKAT;
			}
			else if (attribute.equals("Camera")) {
				String type = StringUtils.substringBetween(line, "\"", "\"");
				if (type.equals("perspective")) {
					// TODO Instance camera
					System.out.println("Perspective camera alright");
				}
				identifier = Identifier.CAMERA;
			}
			else if (attribute.equals("Film")) {
				identifier = Identifier.FILM;
			}
			break;
		case WORLD:
			if (attribute.equals("AttributeBegin")) {
				currentAttribute = new Attribute(AttributeType.ATTRIBUTE);
				status = ParserStatus.ATTRIBUTE;
			}
			else if (attribute.equals("AttributeEnd")) {
				status = ParserStatus.WORLD;
			}
			else if (attribute.equals("ObjectBegin")) {
				currentAttribute = new Attribute(AttributeType.OBJECT);
				status = ParserStatus.ATTRIBUTE;
			}
			else if (attribute.equals("WorldEnd")) {
				status = ParserStatus.END;
				applyProperties();
			}
			break;
		}
		
		//System.out.println("Attribute: " + attribute + "  Param: " + args[1]);
	}
	
	public void applyProperties() {
		for (Property p : accProperties) {
			System.out.println(p.getName() + "-->" + p.getValue());
		}
		accProperties.clear();
	}
}
