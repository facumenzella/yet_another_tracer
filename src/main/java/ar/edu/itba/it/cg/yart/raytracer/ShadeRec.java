package ar.edu.itba.it.cg.yart.raytracer;

import ar.edu.itba.it.cg.yart.geometry.Point3;
import ar.edu.itba.it.cg.yart.geometry.Vector3d;

public class ShadeRec {
	
	public boolean hitObject;
	public Point3 hitPoint = null;			//world coordinates of hit point
	public Point3 localHitPoint;			//later for textures
	public Vector3d normal = null;			//normal at hit point
//	public Material material;
	public Ray ray;							//for specular highlights
	public int depth = 0;						//recursion depth
	public Vector3d dir;					//for area lights
	public World world;						//world reference for shading
	
	public ShadeRec(final World world) {
		hitObject = false;
		this.world = world;
	}
	
	public ShadeRec(final ShadeRec sr) {
		hitObject = sr.hitObject;
		hitPoint = sr.hitPoint;
		localHitPoint = sr.localHitPoint;
		normal = sr.normal;
		ray = sr.ray;
//		material = sr.material;
		depth = sr.depth;
		dir = sr.dir;
		world = sr.world;
	}
	

}
