package ar.edu.itba.it.cg.yart.raytracer;

import java.util.ArrayList;
import java.util.List;

import ar.edu.itba.it.cg.yart.color.Color;
import ar.edu.itba.it.cg.yart.geometry.GeometricObject;
import ar.edu.itba.it.cg.yart.geometry.Point3;
import ar.edu.itba.it.cg.yart.geometry.Vector3d;
import ar.edu.itba.it.cg.yart.matrix.ArrayIntegerMatrix;

public class World {

	private Color backgroundColor;
	public Tracer tracer = new Tracer();
	public List<GeometricObject> objects = new ArrayList<GeometricObject>();
	
	public World() {
		backgroundColor = new Color(0.0f, 0.0f, 0.0f, 1.0f);
	}
	
	public void setBackgroundColor(final Color color) {
		backgroundColor = new Color(color.r, color.g, color.b, color.a);
	}
	
	public void addObject(final GeometricObject object) {
		if (object != null) {
			objects.add(object);
		}
	}
	
	public ArrayIntegerMatrix render(final ViewPlane vp) {
		double zw = 100;
		double x, y;
		Ray ray = new Ray(new Point3(0, 0, zw), new Vector3d(0, 0, -1));
		
		ArrayIntegerMatrix ret = new ArrayIntegerMatrix(vp.hRes, vp.vRes);
		
		for (int row = 0; row < vp.vRes; row++) {
			for (int col = 0; col < vp.hRes; col++) {
				x = vp.pixelSize * (col - 0.5 * (vp.hRes - 1.0));
				y = vp.pixelSize * (row - 0.5 * (vp.vRes - 1.0));
				ray.origin.x = x;
				ray.origin.y = y;
				
				Color pixelColor = tracer.traceRay(ray, objects);
				
				if (pixelColor == null) {
					pixelColor = backgroundColor;
				}
				
				ret.put(col, vp.vRes - row - 1, pixelColor.toInt());
				
//				ret[col][vp.vRes - row - 1] = pixelColor.toInt();
			}
		}
		
		return ret;
	}
	
}
