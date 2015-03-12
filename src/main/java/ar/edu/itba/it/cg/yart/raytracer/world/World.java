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
import ar.edu.itba.it.cg.yart.raytracer.Ray;
import ar.edu.itba.it.cg.yart.raytracer.Tracer;
import ar.edu.itba.it.cg.yart.raytracer.ViewPlane;

public class World {

	private Color backgroundColor;
	private ViewPlane view;
	public Tracer tracer = new Tracer();
	public List<GeometricObject> objects = new ArrayList<GeometricObject>();
	public Light ambientLight;
	public List<Light> lights = new ArrayList<Light>();

	private World(final ViewPlane view) {
		this.view = view;
		ambientLight = new AmbientLight();
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
	
	public ArrayIntegerMatrix render(final ViewPlane vp) {

		ArrayIntegerMatrix ret = new ArrayIntegerMatrix(vp.hRes, vp.vRes);
		Ray ray = new Ray(new Point3(0, 0, 30));

		for (int row = 0; row < vp.vRes; row++) { // up
			for (int col = 0; col < vp.hRes; col++) { // across
				final Vector3d vector = new Vector3d(vp.pixelSize
						* (col - 0.5 * (vp.hRes - 1.0)), vp.pixelSize
						* (row - 0.5 * (vp.vRes - 1.0)), -60);
				ray.direction = Vector3d.normalize(vector);

				Color pixelColor = tracer.traceRay(ray, objects);
				if (pixelColor == null) {
					pixelColor = backgroundColor;
				}

				ret.put(col, vp.hRes - row - 1, pixelColor.toInt());
			}
		}

		return ret;
	}
	
	public static World spheresWorld(final ViewPlane vp) {
		final World world = new World(vp);
		final Sphere s1 = new Sphere(new Point3(0,0.0f,-30), 30.0f);
		s1.color = Color.redColor();
		final Sphere s2 = new Sphere(new Point3(10,50,-50), 10.0f);
		s2.color = Color.blueColor();
		final Sphere s3 = new Sphere(new Point3(200,50,-50), 35.0f);
		s3.color = Color.greenColor();
		final Sphere s4 = new Sphere(new Point3(-20,0,-100), 5.0f);
		s4.color = Color.yellowColor();
		
		world.addObject(s1);
		world.addObject(s2);
		world.addObject(s3);
		world.addObject(s4);
		
		return world;
	}

}
