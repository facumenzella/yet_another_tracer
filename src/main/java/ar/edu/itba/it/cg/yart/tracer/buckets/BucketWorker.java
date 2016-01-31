package ar.edu.itba.it.cg.yart.tracer.buckets;

import java.util.concurrent.atomic.AtomicInteger;

import ar.edu.itba.it.cg.yart.acceleration_estructures.fkdtree.Stack;
import ar.edu.itba.it.cg.yart.matrix.ArrayColorMatrix;
import ar.edu.itba.it.cg.yart.tracer.Tracer;
import ar.edu.itba.it.cg.yart.tracer.Tracer.TracerCallbacks;

public class BucketWorker implements Runnable {

	private final Tracer raytracer;
	private final ArrayColorMatrix result;
	private final TracerCallbacks callback;
	private final Bucket[] buckets;
	private AtomicInteger index;
	private final Stack stack;

	public BucketWorker(final Bucket[] buckets, Tracer raytracer,
						final ArrayColorMatrix result, final TracerCallbacks callback,
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
