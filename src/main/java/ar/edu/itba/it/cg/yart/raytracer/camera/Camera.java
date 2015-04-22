package ar.edu.itba.it.cg.yart.raytracer.camera;

import ar.edu.itba.it.cg.yart.geometry.Point3;
import ar.edu.itba.it.cg.yart.geometry.Vector3d;
import ar.edu.itba.it.cg.yart.matrix.ArrayIntegerMatrix;
import ar.edu.itba.it.cg.yart.raytracer.ViewPlane;
import ar.edu.itba.it.cg.yart.raytracer.buckets.Bucket;
import ar.edu.itba.it.cg.yart.raytracer.interfaces.RayTracer;

public interface Camera {
	
	public void renderScene(final Bucket bucket, final RayTracer rayTracer, final ArrayIntegerMatrix result);
	public void setViewParameters(final Point3 eye, final Point3 lookAt, final Vector3d up);
	public ViewPlane calculateViewPlane(final int hRes, final int vRes);
	public boolean isViewPlaneInvalid();
	public void invalidateViewPlane();
}
