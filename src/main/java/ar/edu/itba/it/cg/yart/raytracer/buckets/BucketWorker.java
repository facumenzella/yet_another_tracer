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
	private final Bucket[] buckets;
	private final Stack stack;
	private final int seed;
	private final int factor;

	public BucketWorker(final Bucket[] buckets, RayTracer raytracer,
			final ArrayIntegerMatrix result, final RaytracerCallbacks callback,
			final Stack stack, final int seed, final int factor) {

		this.buckets = buckets;
		this.result = result;
		this.callback = callback;
		this.raytracer = raytracer;
		this.stack = stack;
		this.seed = seed;
		this.factor = factor;
	}

	@Override
	public void run() {
		int max = buckets.length / this.factor;
		for (int iterator = 0; iterator < max; iterator++) {
			Bucket bucket = buckets[iterator * factor + this.seed];
			raytracer.getCamera().renderScene(bucket, raytracer, result, stack);
			callback.onBucketFinished(bucket, null);

		}
	}
}
