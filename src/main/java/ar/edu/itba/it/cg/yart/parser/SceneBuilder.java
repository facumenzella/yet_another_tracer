package ar.edu.itba.it.cg.yart.parser;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ar.edu.itba.it.cg.yart.color.Color;
import ar.edu.itba.it.cg.yart.geometry.Instance;
import ar.edu.itba.it.cg.yart.geometry.Point3d;
import ar.edu.itba.it.cg.yart.geometry.Vector3d;
import ar.edu.itba.it.cg.yart.geometry.primitives.GeometricObject;
import ar.edu.itba.it.cg.yart.geometry.primitives.Plane;
import ar.edu.itba.it.cg.yart.geometry.primitives.Sphere;
import ar.edu.itba.it.cg.yart.geometry.primitives.mesh.Mesh;
import ar.edu.itba.it.cg.yart.light.AmbientLight;
import ar.edu.itba.it.cg.yart.light.Directional;
import ar.edu.itba.it.cg.yart.light.Light;
import ar.edu.itba.it.cg.yart.light.PointLight;
import ar.edu.itba.it.cg.yart.light.materials.Material;
import ar.edu.itba.it.cg.yart.light.materials.Matte;
import ar.edu.itba.it.cg.yart.light.materials.Reflective;
import ar.edu.itba.it.cg.yart.light.materials.Transparent;
import ar.edu.itba.it.cg.yart.parser.Identifier.IdentifierType;
import ar.edu.itba.it.cg.yart.raytracer.camera.Camera;
import ar.edu.itba.it.cg.yart.raytracer.camera.PinholeCamera;
import ar.edu.itba.it.cg.yart.raytracer.interfaces.RayTracer;
import ar.edu.itba.it.cg.yart.raytracer.world.World;
import ar.edu.itba.it.cg.yart.transforms.Matrix4d;

public class SceneBuilder {
	
	private List<GeometricObject> objects = new ArrayList<GeometricObject>();
	private RayTracer raytracer;
	private SceneParser parser;
	
	private Map<String, Material> namedMaterials = new HashMap<String, Material>();
	private Material currentMaterial;
	
	private Deque<Matrix4d> transformMatrices = new ArrayDeque<Matrix4d>();
	
	private final Sphere referenceSphere = new Sphere();
	private final Plane referencePlane = new Plane();
	private final Material defaultMaterial;
	
	public SceneBuilder() {
		defaultMaterial = new Matte().setCd(new Color(0.75, 0.75, 0.75)).setKd(0.5).setKa(0.15);
	}
	
	public void buildRayTracer(final RayTracer raytracer, final SceneParser parser) {
		reset();
		this.raytracer = raytracer;
		this.parser = parser;
		
		transformMatrices.push(new Matrix4d());
		
		build();
	}
	
	private void build() {
		try {
			parser.parseFile();
			World world = new World();
			
			for (Identifier i : parser.getGlobalIdentifiers()) {
				switch (i.getType()) {
				case CAMERA:
					raytracer.setCamera(buildCamera(i));
					break;
				case FILM:
					raytracer.setResolution(i.getProperty("xresolution").getInteger(), i.getProperty("yresolution").getInteger());
					break;
				case LOOKAT:
					String[] params = i.getParameters();
					raytracer.setViewParameters(
							new Point3d(Double.valueOf(params[0]), Double.valueOf(params[2]), -Double.valueOf(params[1])),
							new Point3d(Double.valueOf(params[3]), Double.valueOf(params[5]), -Double.valueOf(params[4])),
							new Vector3d(Double.valueOf(params[6]), Double.valueOf(params[8]), -Double.valueOf(params[7])));
					break;
				}
			}
			
			for (Attribute a : parser.getAttributes()) {
				for (Identifier i : a.getIdentifiers()) {
					switch (i.getType()) {
					case SHAPE:
						objects.add(buildShape(i));
						break;
					case MATERIAL:
						currentMaterial = buildMaterial(i);
						break;
					case LIGHT_SOURCE:
						world.addLight(buildLight(i));
						break;
					case NAMED_MATERIAL:
						currentMaterial = namedMaterials.get(i.getParameters()[0]);
						break;
					case MAKE_NAMED_MATERIAL:
						addNamedMaterial(i);
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
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private Material buildMaterial(Identifier identifier) {
		Material ret = null;
		
		String type = identifier.getParameters()[0];
		
		if (type.equals("matte")) {
			Matte mat = new Matte();
			mat.setCd(identifier.getProperty("Kd").getColor());
			mat.setKd(1);
			mat.setKa(0.15);
			ret = mat;
		}
		else if (type.equals("mirror")) {
			Reflective mat = new Reflective();
			mat.setCd(identifier.getProperty("Kr").getColor());
			mat.setKd(0.75);
			mat.setKs(0.3);
			mat.setKa(0.3);
			mat.setExp(20);
			mat.setCr(identifier.getProperty("Kr").getColor());
			mat.setKr(1);
			ret = mat;
		}
		else if (type.equals("glass")) {
			Transparent mat = new Transparent();
			mat.setCd(Color.blueColor());
			mat.setKs(0.5);
			mat.setExp(1000);
			mat.setCr(Color.whiteColor());
			mat.setKr(0.5);
			mat.setIor(2);
			mat.setKt(0.2);
			ret = mat;
		}
		else {
			// TODO Material not recognized, load default
			ret = defaultMaterial;
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
			Vector3d normal = identifier.getNormal("n", new Vector3d(0, 1, 0));
			instance = new Instance(referencePlane);
		}
		else if (strType.equals("mesh")) {
			int[] ind = identifier.getIntegers("triindices", null);
			List<Point3d> vertices = new ArrayList<Point3d>(Arrays.asList(identifier.getPoints("P", null)));
			List<Vector3d> normals = new ArrayList<Vector3d>(Arrays.asList(identifier.getNormals("N", null)));
			List<Integer> indices = new ArrayList<Integer>(ind.length);
			
			for (int i : ind) {
				indices.add(i);
			}
			Mesh mesh = new Mesh(vertices, normals, indices, true);
			instance = new Instance(mesh);
			if (currentMaterial == null) {
				// TODO Material not set, loading default
				currentMaterial = defaultMaterial;
			}
			mesh.setMaterial(currentMaterial);
		}
		
		if (currentMaterial == null) {
			// TODO Material not set, loading default
			currentMaterial = defaultMaterial;
		}
		
		instance.setMaterial(currentMaterial);
		Matrix4d matrix = transformMatrices.peek();
		instance.applyTransformation(localMatrix.rightMultiply(matrix));
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
			light.applyTransformation(transformMatrices.peek());
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
		// Wrap the given identifier in a convenient Material one
		String[] args = {identifier.getString("type", "matte")};
		Identifier i = new Identifier(IdentifierType.MATERIAL, args);
		i.addProperties(identifier.getProperties());
		namedMaterials.put(identifier.getParameters()[0], buildMaterial(i));
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
		
		final double m02 = Double.valueOf(identifier.getParameters()[8]);
		final double m12 = Double.valueOf(identifier.getParameters()[9]);
		final double m22 = Double.valueOf(identifier.getParameters()[10]);
		final double m32 = Double.valueOf(identifier.getParameters()[11]);
		
		final double m01 = Double.valueOf(identifier.getParameters()[4]);
		final double m11 = Double.valueOf(identifier.getParameters()[5]);
		final double m21 = Double.valueOf(identifier.getParameters()[6]);
		final double m31 = Double.valueOf(identifier.getParameters()[7]);
		
		final double m03 = Double.valueOf(identifier.getParameters()[12]);
		final double m13 = Double.valueOf(identifier.getParameters()[13]);
		final double m23 = Double.valueOf(identifier.getParameters()[14]);
		final double m33 = Double.valueOf(identifier.getParameters()[15]);
		
		return new Matrix4d(m00, m01, m02, m03, m10, m11, m12, m13, m20, m21, m22, m23, m30, m31, m32, m33);
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
