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
		checkForSingularity();
	}
	
	private void checkForSingularity() {
		// we check if the camera is pointing exactly down
		if (eye.x == lookat.x && eye.z == lookat.z) {
			this.u = new Vector3d(0, 0, 1);
			this.v = new Vector3d(1, 1, 0);
			this.w = new Vector3d(0, 1, 0);
		}
	}

	private void computeUVW() {
		this.w = eye.sub(lookat);
		this.w = Vector3d.normalize(w);
		this.u = up.cross(w);
		this.u = Vector3d.normalize(this.u);
		this.v = this.w.cross(this.u);
	}

	public abstract void renderScene(final World world);

}
