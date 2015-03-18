package ar.edu.itba.it.cg.yart.raytracer.world;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ar.edu.itba.it.cg.yart.color.Color;
import ar.edu.itba.it.cg.yart.geometry.Point3;
import ar.edu.itba.it.cg.yart.geometry.Vector3d;
import ar.edu.itba.it.cg.yart.geometry.primitives.GeometricObject;
import ar.edu.itba.it.cg.yart.geometry.primitives.Sphere;
import ar.edu.itba.it.cg.yart.light.AmbientLight;
import ar.edu.itba.it.cg.yart.light.Directional;
import ar.edu.itba.it.cg.yart.light.Light;
import ar.edu.itba.it.cg.yart.light.Matte;
import ar.edu.itba.it.cg.yart.raytracer.Tracer;
import ar.edu.itba.it.cg.yart.raytracer.camera.Camera;
import ar.edu.itba.it.cg.yart.raytracer.camera.PinholeCamera;

public class World {

	private Color backgroundColor;
	private List<GeometricObject> objects = new ArrayList<GeometricObject>();
	private List<Light> lights = new ArrayList<Light>();
	private Set<Camera> cameras;
	private AmbientLight ambientLight;
	private Camera activeCamera;
	
	/**
	 * Creates a sad, empty World.
	 */
	public World() {
		this.ambientLight = new AmbientLight();
	}
	
	
	public World(final String filename) {
		this();
		// TODO Build scene file parser
		buildTestWorld();
	}
	
	private void buildTestWorld() {
		final Point3 eye = new Point3(0,0,100);
		final Point3 lookat = new Point3(0,0,-30); // point where we look at
		final Vector3d up = new Vector3d(0,1,0); // up vector, rotates around the camera z-axis

		final double distance = 40;
		final double zoom = 2;
		final PinholeCamera camera = new PinholeCamera(new Tracer(), eye, lookat, up, distance, zoom);
		
		setBackgroundColor(Color.blackColor());
		
		final Sphere s1 = new Sphere(new Point3(-30,0.0,-30), 40.0f);
		Matte s1m = new Matte();
		s1m.setCd(Color.whiteColor());
		s1m.setKd(0.75);
		s1m.setKa(0.30);		
		s1.setMaterial(s1m);
		
		final Sphere s2 = new Sphere(new Point3(-30,15,5), 10.0f);
		Matte s2m = new Matte();
		s2m.setCd(Color.yellowColor());
		s2m.setKd(0.65);
		s2m.setKa(0.25);		
		s2.setMaterial(s2m);
		
		final Sphere s3 = new Sphere(new Point3(-10,-15,0), 7.0f);
		Matte s3m = new Matte();
		s3m.setCd(Color.blueColor());
		s3m.setKd(0.85);
		s3m.setKa(0.25);		
		s3.setMaterial(s3m);
		
		addCamera(camera);
		addObject(s1);
		addObject(s2);
		addLight(new Directional());
		addObject(s3);
	}
	
	public AmbientLight getAmbientLight() {
		return ambientLight;
	}
	
	public void setAmbientLight(final AmbientLight ambientLight) {
		this.ambientLight = ambientLight;
	}
	
	public Camera getActiveCamera() {
		return activeCamera;
	}
	
	public void setActiveCamera(final Camera camera) {
		activeCamera = camera;
	}
	
	public void setBackgroundColor(final Color color) {
		backgroundColor = new Color(color.r, color.g, color.b, color.a);
	}
	
	public Color getBackgroundColor() {
		return backgroundColor;
	}

	public void addObject(final GeometricObject object) {
		if (object != null) {
			objects.add(object);
		}
	}
	
	public List<GeometricObject> getObjects() {
		return objects;
	}
	
	public void addCamera(final Camera camera) {
		if (cameras == null) {
			cameras = new HashSet<Camera>();
		}
		
		if (cameras.isEmpty()) {
			setActiveCamera(camera);
		}
		
		cameras.add(camera);
	}
	
	public void addLight(final Light light) {
		lights.add(light);
	}
	
	public List<Light> getLights() {
		return lights;
	}
}
