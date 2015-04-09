package ar.edu.itba.it.cg.yart.raytracer;

import java.util.List;

import ar.edu.itba.it.cg.yart.color.Color;
import ar.edu.itba.it.cg.yart.geometry.Point3;
import ar.edu.itba.it.cg.yart.geometry.Vector3d;
import ar.edu.itba.it.cg.yart.geometry.primitives.GeometricObject;
import ar.edu.itba.it.cg.yart.raytracer.world.World;

public class Tracer {
	
	//TODO: temporary location, it would be best if it was saved in World
	
	private final int maxDepth = 4; 
	
	public Color traceRay(final Ray ray, List<GeometricObject> objects, final World world) {
		if(ray.depth > maxDepth){
			return Color.blackColor();
		}
		ShadeRec sr = new ShadeRec(world);
		Vector3d normal = null;
		Point3 localHitPoint = null;
		double tMin = Double.POSITIVE_INFINITY;		
		
		for (int i = 0; i < objects.size(); i++) {
			GeometricObject object = objects.get(i);
			double t = object.hit(ray, sr);
			if (t != Double.NEGATIVE_INFINITY && t < tMin) {
				sr.hitObject = true;
				sr.material = objects.get(i).getMaterial();
				sr.hitPoint = ray.origin.add(ray.direction.scale(t));
				normal = sr.normal;
				localHitPoint = sr.localHitPoint;
				tMin = t;
			}
		}
		if (sr.hitObject) {
			sr.depth = ray.depth;
			sr.t = tMin;
			sr.normal = normal;
			sr.localHitPoint = localHitPoint;
			sr.ray = ray;
			return sr.material.shade(sr);
		}
		
		return sr.world.getBackgroundColor();
	}
	
}
