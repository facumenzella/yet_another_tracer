package ar.edu.itba.it.cg.yart.raytracer.camera;

import ar.edu.itba.it.cg.yart.geometry.Point3;
import ar.edu.itba.it.cg.yart.geometry.Vector3d;
import ar.edu.itba.it.cg.yart.matrix.ArrayIntegerMatrix;
import ar.edu.itba.it.cg.yart.raytracer.ViewPlane;
import ar.edu.itba.it.cg.yart.raytracer.buckets.Bucket;
import ar.edu.itba.it.cg.yart.raytracer.tracer.Tracer;
import ar.edu.itba.it.cg.yart.raytracer.world.World;

public interface Camera {
	
	public ViewPlane getViewPlane();
	public void renderScene(final Bucket bucket, final World world,
			final ArrayIntegerMatrix result, final Tracer tracer, final int numSamples);
	
	public void setViewParameters(final Point3 eye, final Point3 lookAt, final Vector3d up);
}
