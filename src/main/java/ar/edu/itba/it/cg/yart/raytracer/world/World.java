package ar.edu.itba.it.cg.yart.raytracer.world;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ar.edu.itba.it.cg.yart.acceleration_estructures.BSPAxisAligned;
import ar.edu.itba.it.cg.yart.color.Color;
import ar.edu.itba.it.cg.yart.geometry.Point3;
import ar.edu.itba.it.cg.yart.geometry.Vector3d;
import ar.edu.itba.it.cg.yart.geometry.primitives.Disc;
import ar.edu.itba.it.cg.yart.geometry.primitives.GeometricObject;
import ar.edu.itba.it.cg.yart.geometry.primitives.Plane;
import ar.edu.itba.it.cg.yart.geometry.primitives.Sphere;
import ar.edu.itba.it.cg.yart.geometry.primitives.mesh.Mesh;
import ar.edu.itba.it.cg.yart.light.AmbientLight;
import ar.edu.itba.it.cg.yart.light.Directional;
import ar.edu.itba.it.cg.yart.light.Light;
import ar.edu.itba.it.cg.yart.light.PointLight;
import ar.edu.itba.it.cg.yart.light.materials.Matte;
import ar.edu.itba.it.cg.yart.light.materials.Reflective;
import ar.edu.itba.it.cg.yart.raytracer.camera.Camera;

public class World {

	private Color backgroundColor;
	private List<GeometricObject> objects = new ArrayList<GeometricObject>();
	private List<Light> lights = new ArrayList<Light>();
	private Set<Camera> cameras;
	private AmbientLight ambientLight;
	private Camera activeCamera;
	BSPAxisAligned bspTree;
	
	/**
	 * Creates a sad, empty World.
	 */
	public World() {
		this.ambientLight = new AmbientLight();
		this.cameras = new HashSet<Camera>();
	}
	
	
	public World(final String filename) {
		this();
		// TODO Build scene file parser
		buildMeshWorld();
	}
	
	private void buildMeshWorld() {
		setBackgroundColor(Color.blackColor());
		
		Mesh mesh = new Mesh();
		
		Reflective s1m = new Reflective();
		s1m.setCd(Color.redColor());
		s1m.setKd(0.75);
		s1m.setKs(0.3);
		s1m.setKa(0.3);
		s1m.setExp(20);
		s1m.setCr(Color.whiteColor());
		s1m.setKr(0.75);
		mesh.setMaterial(s1m);
		
		Matte right = new Matte();
		right.setCd(Color.yellowColor());
		right.setKd(0.50);
		right.setKa(0.15);
		final Plane floor = new Plane(new Point3(0,-30,0), new Vector3d(0,1,0));
		Matte floorM = new Matte();
		floorM.setCd(new Color(0.4, 0.4, 0.4));
		floorM.setKd(0.50);
		floorM.setKa(0.15);
		floor.setMaterial(right);
		
		final PointLight light2 = new PointLight(2,Color.whiteColor(),new Vector3d(60,40,30));
		addLight(light2);
		
		List<GeometricObject> objects = new ArrayList<GeometricObject>();
		objects.addAll(mesh.triangles);
		objects.add(floor);

		this.addObjects(objects);
	}
	
	private void buildTestWorld() {
		setBackgroundColor(Color.blackColor());
		final Sphere s1 = new Sphere(new Point3(20,0,-10), 30.0f);
		Reflective s1m = new Reflective();
		s1m.setCd(Color.redColor());
		s1m.setKd(0.75);
		s1m.setKs(0.3);
		s1m.setKa(0.3);
		s1m.setExp(20);
		s1m.setCr(Color.whiteColor());
		s1m.setKr(0.75);
		s1.setMaterial(s1m);
		final Sphere s2 = new Sphere(new Point3(-35,-10,5), 20.0f);
		Reflective s2m = new Reflective();
		s2m.setCd(Color.greenColor());
		s2m.setKd(0.50);
		s2m.setKa(0.3);
		s2m.setKs(0.2);
		s2m.setExp(10);
		s2m.setCr(Color.whiteColor());
		s2m.setKr(0.95);
		s2.setMaterial(s2m);
		final Sphere s3 = new Sphere(new Point3(5,-20,40), 10.0f);
		Reflective s3m = new Reflective();	
		s3m.setCd(Color.blueColor());
		s3m.setKd(0.85);
		s3m.setKa(0.25);
		s3m.setKs(0.3);
		s3m.setExp(10);
		s3m.setCr(Color.whiteColor());
		s3m.setKr(0.5);
		s3.setMaterial(s3m);
		final Disc d1 = new Disc(new Point3(0, 0, 0), new Vector3d(5,2,2), 30);
		Matte d1m = new Matte();
		d1m.setCd(Color.redColor());
		d1m.setKd(0.75);
		d1m.setKa(0.30);		
		d1.setMaterial(d1m);
		final Plane background = new Plane(new Point3(0,0,-150), new Vector3d(0,0,1));
		Matte planeMaterial = new Matte();
		planeMaterial.setCd(Color.yellowColor());
		planeMaterial.setKd(0.50);
		planeMaterial.setKa(0.15);
		background.setMaterial(planeMaterial);
		final Plane backgroundLeft = new Plane(new Point3(-150,0,0), new Vector3d(1,0,0));
		Matte left = new Matte();
		left.setCd(Color.yellowColor());
		left.setKd(0.50);
		left.setKa(0.15);
		backgroundLeft.setMaterial(left);
		final Plane backgroundBottom = new Plane(new Point3(0,-150,0), new Vector3d(0,1,0));
		Matte bottom = new Matte();
		bottom.setCd(Color.yellowColor());
		bottom.setKd(0.50);
		bottom.setKa(0.15);
		backgroundBottom.setMaterial(bottom);
		final Plane backgroundTop = new Plane(new Point3(0,150,0), new Vector3d(0,-1,0));
		Matte top = new Matte();
		top.setCd(Color.yellowColor());
		top.setKd(0.50);
		top.setKa(0.15);
		backgroundTop.setMaterial(top);
		final Plane backgroundRight = new Plane(new Point3(150,0,0), new Vector3d(-1,0,0));
		Matte right = new Matte();
		right.setCd(Color.yellowColor());
		right.setKd(0.50);
		right.setKa(0.15);
		backgroundRight.setMaterial(right);
		final Plane floor = new Plane(new Point3(0,-30,0), new Vector3d(0,1,0));
		Matte floorM = new Matte();
		floorM.setCd(new Color(0.4, 0.4, 0.4));
		floorM.setKd(0.50);
		floorM.setKa(0.15);
		floor.setMaterial(right);
		
		final Directional light1 = new Directional(2.0,Color.whiteColor(),new Vector3d(-2,7,3));
		final PointLight light2 = new PointLight(2,Color.whiteColor(),new Vector3d(60,40,30));
		
//		addLight(light1);
		addLight(light2);
//		light1.shadowsOff();
//		light2.shadowsOff();
				
		final List<GeometricObject> objects = new ArrayList<GeometricObject>();
		
		objects.add(s1);
		objects.add(s2);
		objects.add(s3);
//		addObject(d1);
		objects.add(floor);
		this.addObjects(objects);
//		addObject(background);
//		addObject(backgroundLeft);
//		addObject(backgroundBottom);
//		addObject(backgroundRight);
//		addObject(backgroundTop);
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
	
	public void addObjects(final List<GeometricObject> objects) {
		this.objects = objects;
		this.bspTree = new BSPAxisAligned(200, 1000, 0, 1000);
		this.bspTree.buildTree(objects);
	}
	
	public void addObject(final Mesh mesh) {
		if (mesh != null) {
				addObjects(mesh.triangles);
		}
	}
	
	public List<GeometricObject> getObjects() {
		return objects;
	}
	
	public BSPAxisAligned getTree() {
		return bspTree;
	}
	
	public void addCamera(final Camera camera) {
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
	
	public void setCamera(final Camera camera) {
		this.addCamera(camera);
	}

}
