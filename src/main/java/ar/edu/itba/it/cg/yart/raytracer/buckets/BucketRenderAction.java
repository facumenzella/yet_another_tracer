package ar.edu.itba.it.cg.yart.raytracer.buckets;

import static java.util.Arrays.asList;

import java.util.concurrent.RecursiveAction;
import java.util.concurrent.atomic.AtomicInteger;

import ar.edu.itba.it.cg.yart.acceleration_estructures.fkdtree.Stack;
import ar.edu.itba.it.cg.yart.matrix.ArrayIntegerMatrix;
import ar.edu.itba.it.cg.yart.raytracer.SimpleRayTracer.RaytracerCallbacks;
import ar.edu.itba.it.cg.yart.raytracer.interfaces.RayTracer;

public class BucketRenderAction extends RecursiveAction{

	private static final long serialVersionUID = -3222014814746266629L;
	
	private final RayTracer raytracer;
	private final ArrayIntegerMatrix result;
	private final RaytracerCallbacks callback;
	private final Bucket[] buckets;
	private AtomicInteger index;
	private final Stack stack;
	private final int splitSize;
	private final int min, max;
	
	public BucketRenderAction(final Bucket[] buckets, RayTracer raytracer,
			final ArrayIntegerMatrix result, final RaytracerCallbacks callback,
			final Stack stack, final AtomicInteger index, int min, int max) {
		this.buckets = buckets;
		this.result = result;
		this.callback = callback;
		this.raytracer = raytracer;
		this.stack = stack;
		this.index = index;
		// TODO test this out
		this.splitSize = max - min / 2;
		this.min = min;
		this.max = max;
	}
	
	@Override
	protected void compute() {
		try {
			if (max - min > splitSize) {
	            int mid = (min + max) >>> 1;
	            invokeAll(asList(new BucketRenderAction(this.buckets, this.raytracer, this.result, this.callback, this.stack, this.index, min, mid), 
	            		new BucketRenderAction(this.buckets, this.raytracer, this.result, this.callback, this.stack, this.index, mid, max)));
	        } else {
	        	int i;
	    		while ((i = index.incrementAndGet()) < this.buckets.length) {
	    			Bucket bucket = buckets[i];
	    			raytracer.getCamera().renderScene(bucket, raytracer, result, stack);
	    			callback.onBucketFinished(bucket, null);
	    		}
	        }
		} catch (Exception e) {
			e.printStackTrace();
		}
    }	
	
}