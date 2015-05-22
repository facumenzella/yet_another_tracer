package ar.edu.itba.it.cg.yart.parser;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ar.edu.itba.it.cg.yart.YartConstants;
import ar.edu.itba.it.cg.yart.color.Color;
import ar.edu.itba.it.cg.yart.geometry.Instance;
import ar.edu.itba.it.cg.yart.geometry.Point3d;
import ar.edu.itba.it.cg.yart.geometry.Vector3d;
import ar.edu.itba.it.cg.yart.geometry.primitives.GeometricObject;
import ar.edu.itba.it.cg.yart.geometry.primitives.Plane;
import ar.edu.itba.it.cg.yart.geometry.primitives.Sphere;
import ar.edu.itba.it.cg.yart.geometry.primitives.mesh.Mesh;
import ar.edu.itba.it.cg.yart.geometry.primitives.mesh.MeshData;
import ar.edu.itba.it.cg.yart.light.AmbientLight;
import ar.edu.itba.it.cg.yart.light.Directional;
import ar.edu.itba.it.cg.yart.light.Light;
import ar.edu.itba.it.cg.yart.light.PointLight;
import ar.edu.itba.it.cg.yart.light.materials.Material;
import ar.edu.itba.it.cg.yart.light.materials.Matte;
import ar.edu.itba.it.cg.yart.light.materials.Phong;
import ar.edu.itba.it.cg.yart.light.materials.Reflective;
import ar.edu.itba.it.cg.yart.light.materials.Transparent;
import ar.edu.itba.it.cg.yart.parser.Identifier.IdentifierType;
import ar.edu.itba.it.cg.yart.parser.Property.PropertyType;
import ar.edu.itba.it.cg.yart.raytracer.camera.Camera;
import ar.edu.itba.it.cg.yart.raytracer.camera.PinholeCamera;
import ar.edu.itba.it.cg.yart.raytracer.interfaces.RayTracer;
import ar.edu.itba.it.cg.yart.raytracer.world.World;
import ar.edu.itba.it.cg.yart.textures.ConstantColor;
import ar.edu.itba.it.cg.yart.textures.ImageTexture;
import ar.edu.itba.it.cg.yart.textures.Texture;
import ar.edu.itba.it.cg.yart.textures.mapping.Mapping;
import ar.edu.itba.it.cg.yart.textures.mapping.RectangularMapping;
import ar.edu.itba.it.cg.yart.textures.mapping.SphericalMapping;
import ar.edu.itba.it.cg.yart.textures.wrappers.ClampWrap;
import ar.edu.itba.it.cg.yart.textures.wrappers.ColorWrap;
import ar.edu.itba.it.cg.yart.textures.wrappers.RepeatWrap;
import ar.edu.itba.it.cg.yart.textures.wrappers.Wrapper;
import ar.edu.itba.it.cg.yart.transforms.Matrix4d;

public class SceneBuilder {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(YartConstants.LOG_FILE);
	
	private List<GeometricObject> objects = new ArrayList<GeometricObject>();
	private Map<MeshData, Mesh> meshes = new HashMap<MeshData, Mesh>();
	private RayTracer raytracer;
	private SceneParser parser;
	
	private Map<String, Texture> textures = new HashMap<String, Texture>();
	private Map<String, Material> namedMaterials = new HashMap<String, Material>();
	private Material currentMaterial;
	
	private Deque<Matrix4d> transformMatrices = new ArrayDeque<Matrix4d>();
	
	private final Sphere referenceSphere = new Sphere();
	private final Plane referencePlane = new Plane();
	private final Material defaultMaterial;
	
	public SceneBuilder() {
		defaultMaterial = new Matte().setCd(new Color(0.75, 0.75, 0.75)).setKd(0.5).setKa(0.15);
	}
	
	public void buildRayTracer(final RayTracer raytracer, final SceneParser parser) throws SceneParseException {
		reset();
		this.raytracer = raytracer;
		this.parser = parser;
		
		transformMatrices.push(new Matrix4d());
		
		build();
	}
	
	private void build() throws SceneParseException {
		parser.parseFile();
		World world = new World();

		for (Identifier i : parser.getGlobalIdentifiers()) {
			switch (i.getType()) {
			case CAMERA:
				raytracer.setCamera(buildCamera(i));
				break;
			case FILM:
				String args[] = i.getParameters();
				if (args.length < 1) {
					// TODO Missing Film type throw exception
					String error = "Film expects ";
					LOGGER.error("");
					throw new SceneParseException("");
				}
				if (!args[0].equals("fleximage")) {
					// TODO Fire warning, only fleximage is supported
				}
				raytracer.setResolution(i.getInteger("xresolution", 800),
						i.getInteger("yresolution", 600));
				break;
			case LOOKAT:
				String[] params = i.getParameters();
				raytracer
						.setViewParameters(
								new Point3d(Double.valueOf(params[0]), Double
										.valueOf(params[1]), Double
										.valueOf(params[2])),
								new Point3d(Double.valueOf(params[3]), Double
										.valueOf(params[4]), Double
										.valueOf(params[5])),
								new Vector3d(Double.valueOf(params[6]), Double
										.valueOf(params[7]), Double
										.valueOf(params[8])));
				break;
			}
		}

		for (Attribute a : parser.getAttributes()) {
			for (Identifier i : a.getIdentifiers()) {
				switch (i.getType()) {
				case SHAPE:
					final GeometricObject shape = buildShape(i);
					if (shape != null) {
						objects.add(shape);
					}
					break;
				case MATERIAL:
					currentMaterial = buildMaterial(i);
					break;
				case LIGHT_SOURCE:
					world.addLight(buildLight(i));
					break;
				case NAMED_MATERIAL:
					String args[] = i.getParameters();
					Material ret;
					if (args.length < 1) {
						// TODO Missing material name
					} else if ((ret = namedMaterials.get(args[0])) == null) {
						// TODO Error building material
					} else {
						currentMaterial = ret;
					}
					break;
				case MAKE_NAMED_MATERIAL:
					addNamedMaterial(i);
					break;
				case TEXTURE:
					buildTexture(i);
					break;
				case IDENTITY:
					transformMatrices.pop();
					transformMatrices.push(new Matrix4d());
					break;
				case TRANSFORM:
					transformMatrices.pop();
					transformMatrices.push(transform(i));
					break;
				case ROTATE:
					Matrix4d next = rotate(i);
					transformMatrices.pop();
					transformMatrices.push(next);
					break;
				case TRANSLATE:
					next = translate(i);
					transformMatrices.pop();
					transformMatrices.push(next);
					break;
				case SCALE:
					next = scale(i);
					transformMatrices.pop();
					transformMatrices.push(next);
				}
			}
		}

		world.addObjects(objects);

		world.setBackgroundColor(Color.blackColor());
		raytracer.setWorld(world);
	}
	
	private Material buildMaterial(Identifier identifier) {
		Material ret = null;
		
		String type = identifier.getParameters()[0];
		
		try {
			if (type.equals("matte")) {
				Matte mat = new Matte();
				mat.setCd(getColorOrTexture(identifier, "Kd", Color.whiteColor()));
				mat.setKd(1);
				mat.setKa(0.15);
				ret = mat;
			}
			else if (type.equals("mirror")) {
				Reflective mat = new Reflective();
				mat.setCd(getColorOrTexture(identifier, "Kd", Color.blackColor()));
				mat.setCr(getColorOrTexture(identifier, "Kr", Color.whiteColor()));
				mat.setKd(0.75);
				mat.setKs(0.0);
				mat.setKa(0.3);
				mat.setExp(0);
				mat.setKr(1);
				ret = mat;
			}
			else if (type.equals("glass")) {
				double ior = identifier.getDouble("index", 1.5);
				if (ior < 1) {
					ior = 1;
				}
				
				Transparent mat = new Transparent();
				mat.setCd(Color.blackColor());
				mat.setKs(0.5);
				mat.setExp(1000);
				mat.setCr(getColorOrTexture(identifier, "Kr", Color.whiteColor()));
				mat.setKr(0.5);
				mat.setIor(ior);
				mat.setKt(1);
				ret = mat;
			}
			else if (type.equals("metal2")) {
				double uroughness = identifier.getDouble("uroughness", 0.001);
				double vroughness = identifier.getDouble("vroughness", 0.001);
				double finalRoughness = Math.max(uroughness, vroughness);
				
				if (finalRoughness <= 0) {
					// TODO Roughness cannot be a negative number
					finalRoughness = 0.001;
				}
				else if (finalRoughness > 1) {
					// TODO Roughness cannot be greater than 1
					finalRoughness = 1;
				}
				
				double exponent = 1 / finalRoughness;
				
				Phong mat = new Phong();
				mat.setCd(getColorOrTexture(identifier, "Kr", Color.whiteColor()));
				mat.setKd(1);
				mat.setKa(0.15);
				mat.setExp(exponent);
				mat.setKs(1 - finalRoughness);
				ret = mat;
			}
		}
		catch (ClassCastException e) {
			// TODO Print warning
		}
		
		return ret;
	}
	
	private Texture getColorOrTexture(Identifier identifier, String property, Color defaultColor) {
		Texture ret;
		PropertyType type = identifier.getPropertyType(property);
		
		if (type == PropertyType.COLOR) {
			Color color = identifier.getColor(property, defaultColor);
			ret = new ConstantColor(color);
		}
		else if (type == PropertyType.TEXTURE) {
			String textureName = identifier.getString(property, null);
			if (!textures.containsKey(textureName)) {
				// TODO Texture not found, fire some warning
				ret = new ConstantColor(defaultColor);
			}
			else {
				ret = textures.get(textureName);
			}
		}
		else {
			throw new ClassCastException("Property " + property + " must be either a COLOR or TEXTURE. Found " + type + ".");
		}
		
		return ret;
	}
	
	private Texture buildTexture(final Identifier identifier) {
		Texture ret = null;
		final String[] args = identifier.getParameters();
		
		if (args.length >= 3) {
			final String name = args[0];
			final String color = args[1];
			final String type = args[2];
			
			if (!color.equalsIgnoreCase("color")) {
				// TODO We only support color right now, fire some warning, but carry on
			}
			
			if (type.equalsIgnoreCase("imagemap")) {
				try {
					if (!identifier.hasProperty("filename")) {
						// TODO Missing filename
						return null;
					}
					else if (!identifier.hasProperty("wrap")) {
						// TODO Missing wrap type
						return null;
					}
					BufferedImage image = ImageIO.read(new File(identifier.getString("filename", null)));
					Mapping mapping = null;
					Wrapper wrapper = null;
					String mappingStr = identifier.getString("mapping", "uv");
					String wrapperStr = identifier.getString("wrap", null);
					
					// Load mapping
					if (mappingStr.equalsIgnoreCase("uv")) {
						// TODO Add UV mapping
						mapping = null;
					}
					else if (mappingStr.equalsIgnoreCase("spherical")) {
						mapping = new SphericalMapping();
					}
					else if (mappingStr.equalsIgnoreCase("planar")) {
						mapping = new RectangularMapping();
					}
					else {
						// TODO Mapping not recognized, defaulting to UV
						mapping = null;
					}
					
					// Load wrap type
					if (wrapperStr.equalsIgnoreCase("repeat")) {
						wrapper = new RepeatWrap();
					}
					else if (wrapperStr.equalsIgnoreCase("black")) {
						wrapper = new ColorWrap();
					}
					else if (wrapperStr.equalsIgnoreCase("clamp")) {
						wrapper = new ClampWrap();
					}
					
					ret = new ImageTexture(image, mapping, wrapper);
				}
				catch (IOException e) {
					// TODO Couldn't load texture
				}
			}
			else {
				// TODO We only support imagemaps, fire some warning
			}
			
			if (ret != null) {
				textures.put(name, ret);
			}
		}
		
		return ret;
	}
	
	private GeometricObject buildShape(Identifier identifier) {
		Instance instance = null;
		Matrix4d localMatrix = new Matrix4d();
		
		String strType = identifier.getParameters()[0];
		if (strType.equals("sphere")) {
			double radius = identifier.getDouble("radius", 1.0);
			instance = new Instance(referenceSphere);
			localMatrix = localMatrix.scale(radius, radius, radius);
		}
		else if (strType.equals("plane")) {
			//Vector3d normal = identifier.getNormal("n", new Vector3d(0, 0, 1)).normalizedVector();
			instance = new Instance(new Plane());
		}
		else if (strType.equals("mesh")) {
			final int[] triIndicesArray = identifier.getIntegers("triindices", null);
			final Point3d[] verticesArray = identifier.getPoints("P", null);
			final Vector3d[] normalsArray = identifier.getNormals("N", null);
			final double[] uvList = identifier.getDoubles("uv", null);
			final double[] uList;
			final double[] vList;
			
			final MeshData meshData = new MeshData(triIndicesArray, verticesArray, normalsArray);
			
			Mesh mesh = null;
			
			if (meshes.containsKey(meshData)) {
			    mesh = meshes.get(meshData);
			}
			else {
			    List<Point3d> vertices = new ArrayList<Point3d>(Arrays.asList(verticesArray));
	            List<Vector3d> normals = new ArrayList<Vector3d>(Arrays.asList(normalsArray));
	            List<Integer> indices = new ArrayList<Integer>(triIndicesArray.length);
	            
	            // Load UV map
	            if (uvList != null && uvList.length > 1) {
	            	int items = (int) Math.ceil(uvList.length / 2);
	            	uList = new double[items];
	            	vList = new double[items];
	            	for (int i = 0; i < uList.length; i++) {
	            		uList[i] = uvList[i * 2 + 1];
	            		vList[i] = uvList[i * 2];
	            	}
	            }
	            else {
	            	uList = null;
	            	vList = null;
	            }
	            
	            for (int i : triIndicesArray) {
	                indices.add(i);
	            }
	            
	            mesh = new Mesh(vertices, normals, indices, uList, vList, true);
	            meshes.put(meshData, mesh);
	            
	            if (currentMaterial == null) {
	    			// TODO Material not set, loading default
	    			currentMaterial = defaultMaterial;
	    			System.out.println("Failed");
	    		}
			}
			instance = new Instance(mesh);
		}
		
		if (currentMaterial == null) {
			// TODO Material not set, loading default
			currentMaterial = defaultMaterial;
			System.out.println("Failed");
		}
		
		instance.setMaterial(currentMaterial);
		Matrix4d matrix = transformMatrices.peek();
		instance.applyTransformation(localMatrix.leftMultiply(matrix));
		return instance;
	}
	
	private Light buildLight(Identifier identifier) {
		Light ret = null;
		
		String type = identifier.getParameters()[0];
		
		double gain = identifier.getDouble("gain", 1.0f);
		
		if (type.equals("point")) {
			Point3d from = identifier.getPoint("from", new Point3d(0,0,0));
			Color l = identifier.getColor("l", Color.whiteColor());
			PointLight light = new PointLight(gain, l, new Vector3d(from.x, from.y, from.z));
			light.applyTransformation(new Matrix4d().leftMultiply(transformMatrices.peek()));
			ret = light;
		}
		else if (type.equals("distant")) {
			Color l = identifier.getColor("l", Color.whiteColor());
			Point3d[] def = {new Point3d(0,0,0), new Point3d(1,1,1)};
			Point3d[] fromTo = identifier.getPoints("from/to", def);
			Directional light = new Directional(2, l, fromTo[1].sub(fromTo[0]));
			ret = light;
		}
		else if (type.equals("infinite")) {
			Color l = identifier.getColor("l", Color.whiteColor());
			AmbientLight light = new AmbientLight(l);
			ret = light;
		}
		
		return ret;
	}
	
	private void addNamedMaterial(Identifier identifier) {
		String[] idArgs = identifier.getParameters();
		try {
			// Wrap the given identifier in a convenient Material one
			String[] args = {identifier.getString("type", "matte")};
			Identifier i = new Identifier(IdentifierType.MATERIAL, args);
			i.addProperties(identifier.getProperties());
			Material ret = buildMaterial(i);
			if (ret != null) {
				namedMaterials.put(idArgs[0], ret);
			}
			else {
				// TODO COuldn't laod material
			}
		}
		catch (SceneParseException e) {
			// TODO Missing material name
		}
	}
	
	private Camera buildCamera(Identifier identifier) {
		Camera ret = null;
		
		if (identifier.getParameters()[0].equals("perspective")) {
			PinholeCamera cam = new PinholeCamera(new Point3d(0,0,200), new Point3d(0,0,0), new Vector3d(0,1,0), 500, 1);
			cam.setFov(identifier.getDouble("fov", 90));
			ret = cam;
		}
		
		return ret;
	}
	
	 private Matrix4d transform(Identifier identifier) {
         final double m00 = Double.valueOf(identifier.getParameters()[0]);
         final double m10 = Double.valueOf(identifier.getParameters()[1]);
         final double m20 = Double.valueOf(identifier.getParameters()[2]);
         final double m30 = Double.valueOf(identifier.getParameters()[3]);
        
         final double m01 = Double.valueOf(identifier.getParameters()[4]);
         final double m11 = Double.valueOf(identifier.getParameters()[5]);
         final double m21 = Double.valueOf(identifier.getParameters()[6]);
         final double m31 = Double.valueOf(identifier.getParameters()[7]);
        
         final double m02 = Double.valueOf(identifier.getParameters()[8]);
         final double m12 = Double.valueOf(identifier.getParameters()[9]);
         final double m22 = Double.valueOf(identifier.getParameters()[10]);
         final double m32 = Double.valueOf(identifier.getParameters()[11]);
         
         final double m03 = Double.valueOf(identifier.getParameters()[12]);
         final double m13 = Double.valueOf(identifier.getParameters()[13]);
         final double m23 = Double.valueOf(identifier.getParameters()[14]);
         final double m33 = Double.valueOf(identifier.getParameters()[15]);
        
         Matrix4d transformMatrix = new Matrix4d(m00, m01, m02, m03, m10, m11, m12, m13, m20, m21, m22, m23, m30, m31, m32, m33);
        
//         transformMatrix = converseMatrix.leftMultiply(transformMatrix).leftMultiply(converseMatrix);
//         transformMatrix.m01 = transformMatrix.m02;
//         transformMatrix.m02 = transformMatrix.m01;
//         transformMatrix.m10 = transformMatrix.m20;
//         transformMatrix.m20 = transformMatrix.m10;
//         transformMatrix.m11 = transformMatrix.m22;
//         transformMatrix.m22 = transformMatrix.m11;
//         transformMatrix.m12 = transformMatrix.m21;
//         transformMatrix.m21 = transformMatrix.m12;
         
         return transformMatrix;
 }
	
	private Matrix4d rotate(Identifier identifier) {
		Matrix4d current = transformMatrices.peek();
		
		double degrees = Double.valueOf(identifier.getParameters()[0]);
		int rotateX = Integer.valueOf(identifier.getParameters()[1]);
		int rotateY = Integer.valueOf(identifier.getParameters()[2]);
		int rotateZ = Integer.valueOf(identifier.getParameters()[3]);
		
		if (rotateX != 0) {
			current = current.rotateX(degrees);
		}
		
		if (rotateY != 0) {
			current = current.rotateY(degrees);
		}
		
		if (rotateZ != 0) {
			current = current.rotateZ(degrees);
		}
		
		return current;
	}
	
	private Matrix4d translate(Identifier identifier) {
		Matrix4d current = transformMatrices.peek();
		
		double translateX = Double.valueOf(identifier.getParameters()[1]);
		double translateY = Double.valueOf(identifier.getParameters()[2]);
		double translateZ = Double.valueOf(identifier.getParameters()[3]);
		
		current.transform(translateX, translateY, translateZ);
		
		return current;
	}
	
	private Matrix4d scale(Identifier identifier) {
		Matrix4d current = transformMatrices.peek();
		
		double scaleX = Double.valueOf(identifier.getParameters()[1]);
		double scaleY = Double.valueOf(identifier.getParameters()[2]);
		double scaleZ = Double.valueOf(identifier.getParameters()[3]);
		
		current.scale(scaleX, scaleY, scaleZ);
		
		return current;
	}
	
	private void reset() {
		raytracer = null;
		objects.clear();
	}

}
