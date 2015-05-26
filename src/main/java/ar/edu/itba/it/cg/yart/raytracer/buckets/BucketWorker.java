package ar.edu.itba.it.cg.yart.raytracer.buckets;

import java.util.Deque;

import ar.edu.itba.it.cg.yart.acceleration_estructures.fkdtree.Stack;
import ar.edu.itba.it.cg.yart.matrix.ArrayIntegerMatrix;
import ar.edu.itba.it.cg.yart.raytracer.SimpleRayTracer.RaytracerCallbacks;
import ar.edu.itba.it.cg.yart.raytracer.interfaces.RayTracer;

public class BucketWorker implements Runnable {

	private final RayTracer raytracer;
	private final ArrayIntegerMatrix result;
	private final RaytracerCallbacks callback;
	private final Deque<Bucket> buckets;
	private final Stack stack;

	public BucketWorker(final Deque<Bucket> buckets, RayTracer raytracer,
			final ArrayIntegerMatrix result, final RaytracerCallbacks callback,
			final Stack stack) {

		this.buckets = buckets;
		this.result = result;
		this.callback = callback;
		this.raytracer = raytracer;
		this.stack = stack;
	}

	@Override
	public void run() {
		boolean emptyQueue = false;
		Bucket bucket = buckets.poll();
		while (!emptyQueue) {
			raytracer.getCamera().renderScene(bucket, raytracer, result, stack);
			callback.onBucketFinished(bucket, null);
			emptyQueue = buckets.isEmpty();
			if (!emptyQueue) {
				bucket = buckets.poll();
			}
		}
	}

}