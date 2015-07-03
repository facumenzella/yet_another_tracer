package ar.edu.itba.it.cg.yart.tracer.camera;

import ar.edu.itba.it.cg.yart.acceleration_estructures.fkdtree.Stack;
import ar.edu.itba.it.cg.yart.geometry.Point3d;
import ar.edu.itba.it.cg.yart.geometry.Vector3d;
import ar.edu.itba.it.cg.yart.matrix.ArrayIntegerMatrix;
import ar.edu.itba.it.cg.yart.tracer.Tracer;
import ar.edu.itba.it.cg.yart.tracer.ViewPlane;
import ar.edu.itba.it.cg.yart.tracer.buckets.Bucket;

public interface Camera {
	
	public void renderScene(final Bucket bucket, final Tracer rayTracer, final ArrayIntegerMatrix result, final Stack stack);
	public void setViewParameters(final Point3d eye, final Point3d lookAt, final Vector3d up);
	public ViewPlane calculateViewPlane(final int hRes, final int vRes);
	public boolean isViewPlaneInvalid();
	public void invalidateViewPlane();

}
