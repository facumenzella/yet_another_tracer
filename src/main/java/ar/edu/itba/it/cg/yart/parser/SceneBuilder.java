package ar.edu.itba.it.cg.yart.parser;

import java.util.ArrayList;
import java.util.List;

import ar.edu.itba.it.cg.yart.color.Color;
import ar.edu.itba.it.cg.yart.geometry.Point3;
import ar.edu.itba.it.cg.yart.geometry.Vector3d;
import ar.edu.itba.it.cg.yart.geometry.primitives.GeometricObject;
import ar.edu.itba.it.cg.yart.geometry.primitives.Plane;
import ar.edu.itba.it.cg.yart.geometry.primitives.Sphere;
import ar.edu.itba.it.cg.yart.light.AmbientLight;
import ar.edu.itba.it.cg.yart.light.Directional;
import ar.edu.itba.it.cg.yart.light.Light;
import ar.edu.itba.it.cg.yart.light.PointLight;
import ar.edu.itba.it.cg.yart.light.materials.Material;
import ar.edu.itba.it.cg.yart.light.materials.Matte;
import ar.edu.itba.it.cg.yart.light.materials.Reflective;
import ar.edu.itba.it.cg.yart.light.materials.Transparent;
import ar.edu.itba.it.cg.yart.raytracer.camera.Camera;
import ar.edu.itba.it.cg.yart.raytracer.camera.PinholeCamera;
import ar.edu.itba.it.cg.yart.raytracer.interfaces.RayTracer;
import ar.edu.itba.it.cg.yart.raytracer.world.World;

public class SceneBuilder {
	
	private List<GeometricObject> objects = new ArrayList<GeometricObject>();
	private RayTracer raytracer;
	private SceneParser parser;
	
	private Material currentMaterial;
	
	public void buildRayTracer(final RayTracer raytracer, final SceneParser parser) {
		reset();
		this.raytracer = raytracer;
		this.parser = parser;
		
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
					String[] params = i.getParamters();
					raytracer.setViewParameters(
							new Point3(Double.valueOf(params[0]), Double.valueOf(params[2]), Double.valueOf(params[1])),
							new Point3(Double.valueOf(params[3]), Double.valueOf(params[5]), Double.valueOf(params[4])),
							new Vector3d(Double.valueOf(params[6]), Double.valueOf(params[8]), Double.valueOf(params[7])));
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
					case LIGHT:
						world.addLight(buildLight(i));
						break;
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
		
		String type = identifier.getParamters()[0];
		
		if (type.equals("matte")) {
			Matte mat = new Matte();
			mat.setCd(identifier.getProperty("Kd").getColor());
			mat.setKd(0.50);
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
		
		return ret;
	}
	
	private GeometricObject buildShape(Identifier identifier) {
		GeometricObject object = null;
		
		String strType = identifier.getParamters()[0];
		if (strType.equals("shpere")) {
			object = new Sphere(new Point3(0,0,0), identifier.getDouble("radius", 1.0));
		}
		else if (strType.equals("plane")) {
			object = new Plane(new Point3(0,0,0), identifier.getNormal("n", null));
		}
		
		object.setMaterial(currentMaterial);
		
		return object;
	}
	
	private Light buildLight(Identifier identifier) {
		Light ret = null;
		
		String type = identifier.getParamters()[0];
		
		if (type.equals("point")) {
			Vector3d from = identifier.getVector("from", new Vector3d(0,0,0));
			Color l = identifier.getColor("l", Color.whiteColor());
			double power = identifier.getDouble("power", 100);
			PointLight light = new PointLight(power, l, from);
			ret = light;
		}
		else if (type.equals("distant")) {
			Color l = identifier.getColor("l", Color.whiteColor());
			Point3[] def = {new Point3(0,0,0), new Point3(1,1,1)};
			Point3[] fromTo = identifier.getPoints("from/to", def);
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
	
	private Camera buildCamera(Identifier identifier) {
		Camera ret = null;
		
		if (identifier.getParamters()[0].equals("perspective")) {
			PinholeCamera cam = new PinholeCamera(new Point3(0,0,200), new Point3(0,0,0), new Vector3d(0,1,0), 500, 1);
			cam.setFov(identifier.getDouble("fov", 90));
			ret = cam;
		}
		
		return ret;
	}
	
	private void reset() {
		raytracer = null;
		objects.clear();
	}

}
