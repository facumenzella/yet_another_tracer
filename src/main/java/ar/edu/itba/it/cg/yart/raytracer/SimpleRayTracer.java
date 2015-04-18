package ar.edu.itba.it.cg.yart.raytracer;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;

import ar.edu.itba.it.cg.yart.geometry.Point3;
import ar.edu.itba.it.cg.yart.geometry.Vector3d;
import ar.edu.itba.it.cg.yart.matrix.ArrayIntegerMatrix;
import ar.edu.itba.it.cg.yart.raytracer.buckets.Bucket;
import ar.edu.itba.it.cg.yart.raytracer.buckets.BucketWorker;
import ar.edu.itba.it.cg.yart.raytracer.camera.Camera;
import ar.edu.itba.it.cg.yart.raytracer.camera.PinholeCamera;
import ar.edu.itba.it.cg.yart.raytracer.interfaces.RayTracer;
import ar.edu.itba.it.cg.yart.raytracer.tracer.SimpleTracer;
import ar.edu.itba.it.cg.yart.raytracer.world.World;
import ar.edu.itba.it.cg.yart.utils.YartExecutorFactory;

public class SimpleRayTracer implements RayTracer {

	private final int cores;
	
	private World world;
	
	// Default parameters
	private Point3 eye = new Point3(0,0,200);
	private Point3 lookat = new Point3(0,0,0);
	private Vector3d up = new Vector3d(0,1,0);

	private int hRes;
	private int vRes;
	private int bucketSize;
	private int numSamples;
	
	private RaytracerCallbacks callbacks;
	private final ExecutorService executor;
	private final Deque<Bucket> buckets;
	private Camera camera;

	public interface RaytracerCallbacks {
		public void onBucketFinished(final Bucket bucket,
				final ArrayIntegerMatrix result);

		public void onRenderFinished(final ArrayIntegerMatrix result);
	}

	
	public SimpleRayTracer(final int xBucketSize, final int ysBucketSize,
			final double tMax, final double distance, final int zoom, final int numSamples, final int cores) {
		this.cores = cores;
		// TODO : change how we create the world
		setResolution(800, 600);
		setNumSamples(numSamples);
		this.executor = YartExecutorFactory.newFixedThreadPool(this.cores);
		this.buckets = getBuckets(xBucketSize, ysBucketSize);
		
		setCamera(new PinholeCamera(eye, lookat, up, distance, zoom, hRes, vRes, numSamples));
	}

	public ArrayIntegerMatrix serialRender() {
		return this.serialRender(this.world);
	}
	
	private ArrayIntegerMatrix serialRender(final World world) {
		ArrayIntegerMatrix result = new ArrayIntegerMatrix(hRes, vRes);

		while (!buckets.isEmpty()) {
			Bucket bucket = buckets.poll();

			camera.renderScene(bucket, world, result, new SimpleTracer(), numSamples);

			if (callbacks != null) {
				callbacks.onBucketFinished(bucket, result);
			}
		}

		if (callbacks != null) {
			callbacks.onRenderFinished(result);
		}

		return result;
	}
	
	@Override
	public ArrayIntegerMatrix render() {
		return this.render(this.world);
	}

	private ArrayIntegerMatrix render(final World world) {
		ArrayIntegerMatrix result = new ArrayIntegerMatrix(hRes, vRes);
		
		int totals = buckets.size();
		final CountDownLatch latch = new CountDownLatch(totals);

		for (int i = 0; i < this.cores; i++) {
			executor.submit(new BucketWorker(buckets, this,
					result, new RaytracerCallbacks() {

						@Override
						public void onRenderFinished(ArrayIntegerMatrix result) {
							// TODO fix this
							return;
						}

						@Override
						public void onBucketFinished(Bucket bucket,
								ArrayIntegerMatrix result) {
							callbacks.onBucketFinished(bucket, result);
							latch.countDown();
						}
					}, new SimpleTracer()));
		}

		try {
			latch.await();
		} catch (InterruptedException e) {
			System.out.println("We fucked up");
		} finally {
			callbacks.onRenderFinished(result);
		}

		return result;
	}
	
	@Override
	public World getWorld() {
		return world;
	}
	
	@Override
	public void setWorld(final World world) {
		this.world = world;
	}
	
	@Override
	public void setCallbacks(final RaytracerCallbacks callbacks) {
		this.callbacks = callbacks;
	}
	
	@Override
	public int getHorizontalRes() {
		return hRes;
	}
	
	@Override
	public int getVerticalRes() {
		return vRes;
	}
	
	
	@Override	
	public int getBucketSize() {
		return bucketSize;
	}

	public Deque<Bucket> getBuckets(final int xBucketsSize, final int yBucketsSize) {
		Deque<Bucket> buckets = new ArrayDeque<Bucket>();
		int xBuckets = (int) Math.ceil(hRes / ((float) xBucketsSize));
		int yBuckets = (int) Math.ceil(vRes / ((float) yBucketsSize));

		for (int i = 0; i < yBuckets; i++) {
			for (int j = 0; j < xBuckets; j++) {
				int width = xBucketsSize;
				int height = yBucketsSize;

				if (j == (xBuckets - 1))
					width = hRes - j * xBucketsSize;

				if (i == (yBuckets - 1))
					height = vRes - i * yBucketsSize;

				buckets.add(new Bucket(j * xBucketsSize, i * yBucketsSize, width, height));
			}
		}

		return buckets;
	}
	
	@Override
	public int getNumSamples() {
		return numSamples;
	}
	
	@Override
	public void setNumSamples(final int numSamples) {
		this.numSamples = numSamples;
	}
	
	@Override
	public void setResolution(final int hRes, final int vRes) {
		this.hRes = hRes;
		this.vRes = vRes;
	}

	@Override
	public void setViewParameters(final Point3 eye, final Point3 lookAt, final Vector3d up) {
		this.eye = eye;
		this.lookat = lookAt;
		this.up = up;
		
		if (camera != null) {
			camera.setViewParameters(eye, lookAt, up);
		}
	}
	
	@Override
	public void setCamera(Camera camera) {
		this.camera = camera;
		
		if (camera != null) {
			camera.setViewParameters(eye, lookat, up);
		}
	}

	@Override
	public Camera getCamera() {
		return camera;
	}
}
