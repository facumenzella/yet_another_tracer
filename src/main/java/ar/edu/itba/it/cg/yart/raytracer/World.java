package ar.edu.itba.it.cg.yart.raytracer;

import java.util.ArrayList;
import java.util.List;

import ar.edu.itba.it.cg.yart.color.Color;
import ar.edu.itba.it.cg.yart.geometry.GeometricObject;
import ar.edu.itba.it.cg.yart.geometry.Point3;
import ar.edu.itba.it.cg.yart.geometry.Vector3;

public class World {

	public Tracer tracer = new Tracer();
	public List<GeometricObject> objects = new ArrayList<GeometricObject>();
	
	public void addObject(final GeometricObject object) {
		if (object != null) {
			objects.add(object);
		}
	}
	
	public int[][] render(final ViewPlane vp) {
		double zw = 100;
		double x, y;
		Ray ray = new Ray(new Point3(0, 0, zw), new Vector3(0, 0, -1));
		
		int[][] ret = new int[vp.hRes][vp.vRes];
		
		for (int row = 0; row < vp.vRes; row++) {
			for (int col = 0; col < vp.hRes; col++) {
				x = vp.pixelSize * (col - 0.5 * (vp.hRes - 1.0));
				y = vp.pixelSize * (row - 0.5 * (vp.vRes - 1.0));
				ray.origin.x = x;
				ray.origin.y = y;
				
				Color pixelColor = tracer.traceRay(ray, objects);
				ret[col][vp.vRes - row - 1] = pixelColor.toInt();
			}
		}
		
		return ret;
	}
	
}
