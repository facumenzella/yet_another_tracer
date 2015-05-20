package ar.edu.itba.it.cg.yart.raytracer.world;

import java.util.ArrayList;
import java.util.List;

import ar.edu.itba.it.cg.yart.acceleration_estructures.fkdtree.nlog2n.YAFKDTree2;
import ar.edu.itba.it.cg.yart.color.Color;
import ar.edu.itba.it.cg.yart.geometry.Instance;
import ar.edu.itba.it.cg.yart.geometry.Point3d;
import ar.edu.itba.it.cg.yart.geometry.Vector3d;
import ar.edu.itba.it.cg.yart.geometry.primitives.GeometricObject;
import ar.edu.itba.it.cg.yart.geometry.primitives.Plane;
import ar.edu.itba.it.cg.yart.geometry.primitives.Sphere;
import ar.edu.itba.it.cg.yart.light.AmbientLight;
import ar.edu.itba.it.cg.yart.light.Directional;
import ar.edu.itba.it.cg.yart.light.Light;
import ar.edu.itba.it.cg.yart.light.PointLight;
import ar.edu.itba.it.cg.yart.light.materials.Matte;
import ar.edu.itba.it.cg.yart.light.materials.Reflective;
import ar.edu.itba.it.cg.yart.light.materials.Transparent;
import ar.edu.itba.it.cg.yart.transforms.Matrix4d;

public class World {

	private Color backgroundColor;
	private List<GeometricObject> objects = new ArrayList<GeometricObject>();
	private List<Light> lights = new ArrayList<Light>();
	private List<Light> castShadowLights = new ArrayList<Light>();
	private List<Light> doNotCastShadowLights = new ArrayList<Light>();
	private AmbientLight ambientLight;
	private YAFKDTree2 kdTree;
	private boolean preprocessed = false;
	
	/**
	 * Creates a sad, empty World.
	 */
	public World() {
		this.ambientLight = new AmbientLight(Color.blackColor());
	}
	
	public void preprocess() {
		if (!preprocessed) {
//			this.bspTree = new BSPAxisAligned(200, 1000, 0, 1000);
//			this.bspTree.buildTree(objects);
			preprocessed = true;
		}
	}
	
	public void buildTestWorld() {
		setBackgroundColor(Color.blackColor());
		final Instance s1 = new Instance(new Sphere());
		s1.applyTransformation(Matrix4d.scaleMatrix(2, 2, 2));
		Reflective s1m = new Reflective();
		s1m.setCd(Color.redColor());
		s1m.setKd(0.75);
		s1m.setKs(0.3);
		s1m.setKa(0.3);
		s1m.setExp(20);
		s1m.setCr(Color.whiteColor());
		s1m.setKr(0.4);
		s1.setMaterial(s1m);
		final Instance s2 = new Instance(new Sphere());
		s2.applyTransformation(Matrix4d.scaleMatrix(2,2,2).rightMultiply(Matrix4d.transformMatrix(-2,4,0)));
		Reflective s2m = new Reflective();
		s2m.setCd(Color.greenColor());
		s2m.setKd(0.50);
		s2m.setKa(0.3);
		s2m.setKs(0.2);
		s2m.setExp(10);
		s2m.setCr(Color.whiteColor());
		s2m.setKr(0.5);
		s2.setMaterial(s2m);
		final Instance s3 = new Instance(new Sphere());
		Transparent s3m = new Transparent();	
		s3m.setCd(Color.blueColor());
		s2m.setKd(0.50);
		s2m.setKa(0.3);
		s3m.setKs(0.5);
		s3m.setExp(1000);
		s3m.setCr(Color.whiteColor());
		s3m.setKr(0.5);
		s3m.setIor(1.5);
		s3m.setKt(0.9);
		s3.setMaterial(s3m);
		Matte d1m = new Matte();
		d1m.setCd(Color.redColor());
		d1m.setKd(0.75);
		d1m.setKa(0.30);		
		final Plane background = new Plane(new Point3d(0,0,-150), new Vector3d(0,0,1));
		Matte planeMaterial = new Matte();
		planeMaterial.setCd(Color.yellowColor());
		planeMaterial.setKd(0.50);
		planeMaterial.setKa(0.15);
		background.setMaterial(planeMaterial);
		final Plane backgroundLeft = new Plane(new Point3d(-150,0,0), new Vector3d(1,0,0));
		Matte left = new Matte();
		left.setCd(Color.yellowColor());
		left.setKd(0.50);
		left.setKa(0.15);
		backgroundLeft.setMaterial(left);
		final Plane backgroundBottom = new Plane(new Point3d(0,-150,0), new Vector3d(0,1,0));
		Matte bottom = new Matte();
		bottom.setCd(Color.yellowColor());
		bottom.setKd(0.50);
		bottom.setKa(0.15);
		backgroundBottom.setMaterial(bottom);
		final Plane backgroundTop = new Plane(new Point3d(0,150,0), new Vector3d(0,-1,0));
		Matte top = new Matte();
		top.setCd(Color.yellowColor());
		top.setKd(0.50);
		top.setKa(0.15);
		backgroundTop.setMaterial(top);
		final Plane backgroundRight = new Plane(new Point3d(150,0,0), new Vector3d(-1,0,0));
		Matte right = new Matte();
		right.setCd(Color.yellowColor());
		right.setKd(0.50);
		right.setKa(0.15);
		backgroundRight.setMaterial(right);
		final Instance floor = new Instance(new Plane());
//		floor.applyTransformation(Matrix4d.transformMatrix(0, -30, 0));
		Matte floorM = new Matte();
		floorM.setCd(new Color(0.4, 0.4, 0.4));
		floorM.setKd(0.50);
		floorM.setKa(0.15);
		floor.setMaterial(right);
		
		final Directional light1 = new Directional(2.0,Color.whiteColor(),new Vector3d(-2,7,3));
		final PointLight light2 = new PointLight(2,Color.whiteColor(),new Vector3d(60,30,40));
		
//		addLight(light1);
		addLight(light2);
//		light1.shadowsOff();
//		light2.shadowsOff();
				
		
		objects.add(s1);
//		objects.add(s2);
//		objects.add(s3);
		objects.add(floor);
//		this.addObjects(objects);
//		addObject(background);
//		addObject(backgroundLeft);
//		addObject(backgroundBottom);
//		addObject(backgroundRight);
//		addObject(backgroundTop);
		this.kdTree = YAFKDTree2.build(this.objects, 30);
	}
	
	
	public AmbientLight getAmbientLight() {
		return ambientLight;
	}
	
	public void setAmbientLight(final AmbientLight ambientLight) {
		this.ambientLight = ambientLight;
	}
	
	public void setBackgroundColor(final Color color) {
		backgroundColor = new Color(color.r, color.g, color.b, color.a);
	}
	
	public Color getBackgroundColor() {
		return backgroundColor;
	}
	
	public void addObjects(final List<GeometricObject> objects) {
		this.objects.clear();
		this.objects.addAll(objects);
		preprocessed = false;
		this.kdTree = YAFKDTree2.build(this.objects, 30);
	}
	
	public void addObject(final GeometricObject object) {
		objects.add(object);
		preprocessed = false;
	}
	
	public List<GeometricObject> getObjects() {
		return objects;
	}
	
	public YAFKDTree2 getTree() {
		return kdTree;
	}
	
	public void addLight(final Light light) {
		if (light instanceof AmbientLight) {
			setAmbientLight((AmbientLight) light);
		}
		else {
			lights.add(light);
			if (light.castShadows()) {
				castShadowLights.add(light);
			} else {
				doNotCastShadowLights.add(light);
			}
		}
	}
	
	public List<Light> getLights() {
		return lights;
	}
	
	public List<Light> getCastShadowLights() {
		return castShadowLights;
	}
	
	public List<Light> getDoNotCastShadowLights() {
		return doNotCastShadowLights;
	}

}
