package ar.edu.itba.it.cg.yart.raytracer;

import ar.edu.itba.it.cg.yart.geometry.Point3;
import ar.edu.itba.it.cg.yart.geometry.Vector3d;
import ar.edu.itba.it.cg.yart.light.materials.Material;
import ar.edu.itba.it.cg.yart.raytracer.world.World;

public class ShadeRec {
	
	public boolean hitObject;
	public Point3 hitPoint = null;			//world coordinates of hit point
	public Point3 localHitPoint;			//later for textures
	public Vector3d normal = null;			//normal at hit point
	public Material material;
	public Ray ray;							//for specular highlights
	public int depth = 0;						//recursion depth
	public double t = 0;
	public Vector3d dir;					//for area lights
	public World world;						//world reference for shading
	
	public ShadeRec(final World world) {
		hitObject = false;
		this.world = world;
	}
	
	public ShadeRec(final ShadeRec sr) {
		this.hitObject = sr.hitObject;
		this.hitPoint = sr.hitPoint;
		this.localHitPoint = sr.localHitPoint;
		this.normal = sr.normal;
		this.ray = sr.ray;
		this.material = sr.material;
		this.depth = sr.depth;
		this.dir = sr.dir;
		this.world = sr.world;
		this.t = sr.t;
	}
	

}
