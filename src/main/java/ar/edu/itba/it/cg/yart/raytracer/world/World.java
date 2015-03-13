package ar.edu.itba.it.cg.yart.raytracer.world;

import java.util.ArrayList;
import java.util.List;

import ar.edu.itba.it.cg.yart.color.Color;
import ar.edu.itba.it.cg.yart.geometry.Point3;
import ar.edu.itba.it.cg.yart.geometry.Vector3d;
import ar.edu.itba.it.cg.yart.geometry.primitives.GeometricObject;
import ar.edu.itba.it.cg.yart.geometry.primitives.Sphere;
import ar.edu.itba.it.cg.yart.light.AmbientLight;
import ar.edu.itba.it.cg.yart.light.Light;
import ar.edu.itba.it.cg.yart.matrix.ArrayIntegerMatrix;
import ar.edu.itba.it.cg.yart.raytracer.Tracer;
import ar.edu.itba.it.cg.yart.raytracer.ViewPlane;
import ar.edu.itba.it.cg.yart.raytracer.camera.Camera;
import ar.edu.itba.it.cg.yart.raytracer.camera.PinholeCamera;

public class World {

	public Color backgroundColor;
	public ViewPlane vp;
	public Tracer tracer;
	public List<GeometricObject> objects = new ArrayList<GeometricObject>();
	public Light ambientLight;
	public List<Light> lights = new ArrayList<Light>();
	public final ArrayIntegerMatrix ret;
	public final Camera camera;
	
	private World(final ViewPlane view, final Camera camera, final Tracer tracer) {
		this.ambientLight = new AmbientLight();
		this.vp = view;
		this.ret = new ArrayIntegerMatrix(vp.hRes, vp.vRes);
		this.camera = camera;
	}

	public void setBackgroundColor(final Color color) {
		backgroundColor = new Color(color.r, color.g, color.b, color.a);
	}

	public void addObject(final GeometricObject object) {
		if (object != null) {
			objects.add(object);
		}
	}
	
	public void addLight(final Light light) {
		lights.add(light);
	}

	public ArrayIntegerMatrix render() {
		this.camera.renderScene(this);
		return this.ret;
	}
	
	public static World spheresWorld(final ViewPlane vp) {
		final Tracer tracer = new Tracer();
		final Point3 eye = new Point3(0,0,30);
		final Point3 lookat = new Point3(-30,0,-30); // point where we look at
		final Vector3d up = new Vector3d(0,1,0); // up vector, rotates around the camera z-axis
		final double distance = 40;
		final World world = new World(vp, new PinholeCamera(tracer, eye, lookat, up, distance), tracer);
		world.setBackgroundColor(Color.whiteColor());
		final Sphere s1 = new Sphere(new Point3(-30,0.0f,-30), 30.0f);
		s1.color = Color.redColor();
		final Sphere s2 = new Sphere(new Point3(30,0,-30), 30.0f);
		s2.color = Color.blueColor();
//		final Sphere s3 = new Sphere(new Point3(0,-50,-30), 40.0f);
//		s3.color = Color.greenColor();
//		final Sphere s4 = new Sphere(new Point3(-20,0,-100), 5.0f);
//		s4.color = Color.yellowColor();
		
		world.addObject(s1);
		world.addObject(s2);
//		world.addObject(s3);
//		world.addObject(s4);
		
		return world;
	}

}
