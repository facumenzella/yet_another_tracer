package ar.edu.itba.it.cg.yart.raytracer;

import java.util.ArrayList;
import java.util.List;

import ar.edu.itba.it.cg.yart.color.Color;
import ar.edu.itba.it.cg.yart.geometry.Point3;
import ar.edu.itba.it.cg.yart.geometry.Vector3d;
import ar.edu.itba.it.cg.yart.geometry.primitives.GeometricObject;
import ar.edu.itba.it.cg.yart.light.AmbientLight;
import ar.edu.itba.it.cg.yart.light.Light;
import ar.edu.itba.it.cg.yart.matrix.ArrayIntegerMatrix;

public class World {

	private Color backgroundColor;
	private ViewPlane view;
	public Tracer tracer = new Tracer();
	public List<GeometricObject> objects = new ArrayList<GeometricObject>();
	public Light ambientLight;
	public List<Light> lights = new ArrayList<Light>();

	public World(final ViewPlane view) {
		backgroundColor = new Color(0.0f, 0.0f, 0.0f, 1.0f);
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
		double zw = 100;
		double x, y;

		ArrayIntegerMatrix ret = new ArrayIntegerMatrix(vp.hRes, vp.vRes);
		Ray ray = new Ray(new Point3(0, 0, 0));

		for (int row = 0; row < vp.vRes; row++) { // up
			for (int col = 0; col < vp.hRes; col++) { // across
				final Vector3d vector = new Vector3d(vp.pixelSize
						* (col - 0.5 * (vp.hRes - 1.0)), vp.pixelSize
						* (row - 0.5 * (vp.vRes - 1.0)), 1);
				ray.direction = Vector3d.normalize(vector);

				Color pixelColor = tracer.traceRay(ray, objects);
				if (pixelColor == null) {
					pixelColor = backgroundColor;
				}

				ret.put(col, vp.vRes - row - 1, pixelColor.toInt());
			}
		}

		return ret;
	}

}
