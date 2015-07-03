package ar.edu.itba.it.cg.yart.raytracer.buckets;

import java.util.concurrent.atomic.AtomicInteger;

import ar.edu.itba.it.cg.yart.acceleration_estructures.fkdtree.Stack;
import ar.edu.itba.it.cg.yart.matrix.ArrayIntegerMatrix;
import ar.edu.itba.it.cg.yart.raytracer.YATracer.RaytracerCallbacks;
import ar.edu.itba.it.cg.yart.raytracer.interfaces.Tracer;

public class BucketWorker implements Runnable {

	private final Tracer raytracer;
	private final ArrayIntegerMatrix result;
	private final RaytracerCallbacks callback;
	private final Bucket[] buckets;
	private AtomicInteger index;
	private final Stack stack;

	public BucketWorker(final Bucket[] buckets, Tracer raytracer,
			final ArrayIntegerMatrix result, final RaytracerCallbacks callback,
			final Stack stack, final AtomicInteger index) {

		this.buckets = buckets;
		this.result = result;
		this.callback = callback;
		this.raytracer = raytracer;
		this.stack = stack;
		this.index = index;
	}

	@Override
	public void run() {
		int i;
		while ((i = index.incrementAndGet()) < this.buckets.length) {
			Bucket bucket = buckets[i];
			callback.onBucketStarted(bucket);
			raytracer.getCamera().renderScene(bucket, raytracer, result, stack);
			callback.onBucketFinished(bucket, null);
		}
	}
}
