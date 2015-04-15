package ar.edu.itba.it.cg.yart.raytracer.interfaces;

import java.util.Queue;

import ar.edu.itba.it.cg.yart.matrix.ArrayIntegerMatrix;
import ar.edu.itba.it.cg.yart.raytracer.SimpleRayTracer.RaytracerCallbacks;
import ar.edu.itba.it.cg.yart.raytracer.buckets.Bucket;
import ar.edu.itba.it.cg.yart.raytracer.world.World;

public interface RayTracer {
	
	public ArrayIntegerMatrix render();
	public ArrayIntegerMatrix serialRender();
	public void setWorld(final World w);
	public Queue<Bucket> getBuckets();
	public int getHorizontalRes();
	public int getVerticalRes();
	public int getBucketSize();
	public void setCallbacks(final RaytracerCallbacks callbacks);
}
