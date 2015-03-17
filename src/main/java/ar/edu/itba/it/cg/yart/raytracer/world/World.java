package ar.edu.itba.it.cg.yart.raytracer.world;

import java.util.ArrayList;
import java.util.List;

import ar.edu.itba.it.cg.yart.color.Color;
import ar.edu.itba.it.cg.yart.geometry.Point3;
import ar.edu.itba.it.cg.yart.geometry.Vector3d;
import ar.edu.itba.it.cg.yart.geometry.primitives.GeometricObject;
import ar.edu.itba.it.cg.yart.geometry.primitives.Sphere;
import ar.edu.itba.it.cg.yart.light.AmbientLight;
import ar.edu.itba.it.cg.yart.light.Directional;
import ar.edu.itba.it.cg.yart.light.Light;
import ar.edu.itba.it.cg.yart.light.Matte;
import ar.edu.itba.it.cg.yart.matrix.ArrayIntegerMatrix;
import ar.edu.itba.it.cg.yart.raytracer.Tracer;
import ar.edu.itba.it.cg.yart.raytracer.ViewPlane;
import ar.edu.itba.it.cg.yart.raytracer.camera.Camera;
import ar.edu.itba.it.cg.yart.raytracer.camera.PinholeCamera;
import ar.edu.itba.it.cg.yart.ui.RenderWindow;

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
		final Point3 eye = new Point3(0,0,100);
		final Point3 lookat = new Point3(0,0,-30); // point where we look at
		final Vector3d up = new Vector3d(0,1,0); // up vector, rotates around the camera z-axis

		final double distance = 40;
		final double zoom = 2;
		final PinholeCamera camera = new PinholeCamera(tracer, eye, lookat, up, distance, zoom);

		final World world = new World(vp, camera, tracer);
		
		world.setBackgroundColor(Color.blackColor());
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

		
		
		
		RenderWindow window = new RenderWindow(vp.hRes, vp.vRes, 32);
		camera.setCallbacks(window);

		
		
		/*RenderWindow window = new RenderWindow(vp.hRes, vp.vRes, 32);
		camera.setCallbacks(window);*/
		
		
//		final Sphere s3 = new Sphere(new Point3(0,-50,-30), 40.0f);
//		s3.color = Color.greenColor();
//		final Sphere s4 = new Sphere(new Point3(-20,0,-100), 5.0f);
//		s4.color = Color.yellowColor();
		
		world.addObject(s1);
		world.addObject(s2);
		world.addLight(new Directional());
		world.addObject(s3);
//		world.addObject(s4);
		
		return world;
	}

	public enum Scenario {
		// TODO : add scenarios
		SPHERE_WORLD_1;
	}
}
