package ar.edu.itba.it.cg.yart.raytracer;

import ar.edu.itba.it.cg.yart.geometry.Point3d;
import ar.edu.itba.it.cg.yart.geometry.Vector3d;
import ar.edu.itba.it.cg.yart.light.Sample;
import ar.edu.itba.it.cg.yart.light.materials.Material;
import ar.edu.itba.it.cg.yart.raytracer.world.World;

public class ShadeRec {
	
	public boolean hitObject;
	public Point3d hitPoint = null;			//world coordinates of hit point
	public Point3d localHitPoint;			//later for textures
	public Vector3d normal = null;			//normal at hit point
	public Material material;
	public Ray ray;							//for specular highlights
	public int depth = 0;						//recursion depth
	public double t = -Double.NEGATIVE_INFINITY;
	public Vector3d dir;					//for area lights
	public double u;						//coordinates for textures
	public double v;						//coordinates for textures
	public World world;						//world reference for shading

	// Used by area lights
	public Vector3d wi = null;
	public Sample sample = null;
	
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
		this.u = sr.u;
		this.v = sr.v;
		this.world = sr.world;
		this.t = sr.t;
	}
	

}
