package ar.edu.itba.it.cg.yart.tracer.camera;

import ar.edu.itba.it.cg.yart.geometry.Point3d;
import ar.edu.itba.it.cg.yart.geometry.Vector3d;
import ar.edu.itba.it.cg.yart.tracer.ViewPlane;


public abstract class CameraAbstract implements Camera {

	protected Point3d eye;
	protected Point3d lookat;
	protected Vector3d up;
	protected Vector3d u, v, w;
	float exposue_time;
	protected boolean viewPlaneInvalidated = true;
	protected ViewPlane viewPlane;

	protected CameraAbstract(final Point3d eye, final Point3d lookat, final Vector3d up) {
		setViewParameters(eye, lookat, up);
	}
	
	private void checkForSingularity() {
		// we check if the camera is pointing exactly down
		if (eye.x == lookat.x && eye.y == lookat.y) {
			this.u = new Vector3d(0, 1, 0);
			this.v = new Vector3d(1, 0, 1);
			this.w = new Vector3d(0, 0, 1);
		}
	}

	private void computeUVW() {
		this.w = eye.sub(lookat);
		this.w = w.normalizedVector();
		this.u = up.cross(w);
		this.u = u.normalizedVector();
		this.v = this.w.cross(this.u);
	}
	
	@Override
	public void setViewParameters(Point3d eye, Point3d lookAt, Vector3d up) {
		this.eye = eye;
		this.lookat = lookAt;
		this.up = up;
		computeUVW();
		checkForSingularity();
		invalidateViewPlane();
	}
	
	@Override
	public void invalidateViewPlane() {
		viewPlaneInvalidated = true;
	}
	
	@Override
	public boolean isViewPlaneInvalid() {
		return viewPlaneInvalidated;
	}

}
