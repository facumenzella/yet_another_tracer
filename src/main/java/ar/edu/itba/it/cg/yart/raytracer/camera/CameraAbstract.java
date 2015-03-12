package ar.edu.itba.it.cg.yart.raytracer.camera;

import ar.edu.itba.it.cg.yart.geometry.Point3;
import ar.edu.itba.it.cg.yart.geometry.Vector3d;
import ar.edu.itba.it.cg.yart.raytracer.Tracer;
import ar.edu.itba.it.cg.yart.raytracer.world.World;

public abstract class CameraAbstract implements Camera{

	protected Point3 eye;
	protected Point3 lookat;
	protected Vector3d up;
	protected Vector3d u, v, w;
	protected Tracer tracer;
	float exposue_time;

	protected CameraAbstract(final Tracer tracer, final Point3 eye, 
			final Point3 lookat, final Vector3d up) {
		this.tracer = tracer;
		this.eye = eye;
		this.lookat = lookat;
		this.up = up;
		computeUVW();
	}

	public void computeUVW() {
		this.w = eye.sub(lookat);
		this.w = Vector3d.normalize(w);
		this.u = up.cross(w);
		u = Vector3d.normalize(u);
		v = w.cross(u);
	}

	public abstract void renderScene(final World world);

}
