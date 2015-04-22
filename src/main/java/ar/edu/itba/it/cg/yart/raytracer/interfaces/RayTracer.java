package ar.edu.itba.it.cg.yart.raytracer.interfaces;

import ar.edu.itba.it.cg.yart.geometry.Point3;
import ar.edu.itba.it.cg.yart.geometry.Vector3d;
import ar.edu.itba.it.cg.yart.matrix.ArrayIntegerMatrix;
import ar.edu.itba.it.cg.yart.raytracer.SimpleRayTracer.RaytracerCallbacks;
import ar.edu.itba.it.cg.yart.raytracer.ViewPlane;
import ar.edu.itba.it.cg.yart.raytracer.camera.Camera;
import ar.edu.itba.it.cg.yart.raytracer.tracer.Tracer;
import ar.edu.itba.it.cg.yart.raytracer.world.World;

public interface RayTracer {
	
	public ArrayIntegerMatrix render();
	public ArrayIntegerMatrix serialRender();
	public void setWorld(final World w);
	public World getWorld();
	public int getHorizontalRes();
	public int getVerticalRes();
	public int getBucketSize();
	public void setCallbacks(final RaytracerCallbacks callbacks);
	
	public ViewPlane getViewPlane();
	public Tracer getTracer();
	public int getNumSamples();
	public void setNumSamples(final int numSamples);
	public void setResolution(final int xRes, final int yRes);
	public void setViewParameters(final Point3 eye, final Point3 lookAt, final Vector3d up);
	public void setCamera(final Camera camera);
	public Camera getCamera();
}
