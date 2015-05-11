package ar.edu.itba.it.cg.yart.raytracer;

import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;
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
import ar.edu.itba.it.cg.yart.raytracer.tracer.ColorTracer;
import ar.edu.itba.it.cg.yart.raytracer.tracer.SimpleColorTracer;
import ar.edu.itba.it.cg.yart.raytracer.world.World;
import ar.edu.itba.it.cg.yart.utils.YartExecutorFactory;

public class SimpleRayTracer implements RayTracer {

	private final int cores;
	
	private final RenderResult renderResult;
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
	private Deque<Bucket> buckets;
	private Camera camera;
	private ColorTracer tracer;


	public interface RaytracerCallbacks {
		public void onBucketFinished(final Bucket bucket,
				final ArrayIntegerMatrix result);

		public void onRenderFinished(final ArrayIntegerMatrix result);
	}

	
	public SimpleRayTracer(final RenderResult renderResult, final int bucketSize, final double tMax, final double distance, final int zoom, final int numSamples, final int cores) {
		this.cores = cores;
		this.bucketSize = bucketSize;
		this.executor = YartExecutorFactory.newFixedThreadPool(this.cores);
		
		this.renderResult = renderResult;
		
		setResolution(800, 600);
		setNumSamples(numSamples);
		setCamera(new PinholeCamera(eye, lookat, up, distance, zoom));
	}

	public RenderResult serialRender() {
		preprocessWorld();
		renderResult.startRender();
		ArrayIntegerMatrix result = new ArrayIntegerMatrix(hRes, vRes);
		renderResult.setPixels(result);
		
		this.buckets = getBuckets(bucketSize, bucketSize);

		while (!buckets.isEmpty()) {
			Bucket bucket = buckets.poll();

			camera.renderScene(bucket, this, result);

			if (callbacks != null) {
				callbacks.onBucketFinished(bucket, result);
			}
		}

		if (callbacks != null) {
			callbacks.onRenderFinished(result);
		}
		
		renderResult.finishRender();

		return renderResult;
	}
	
	@Override
	public RenderResult render() {
		return this.render(this.world);
	}

	private RenderResult render(final World world) {
		preprocessWorld();
		renderResult.startRender();
		ArrayIntegerMatrix result = new ArrayIntegerMatrix(hRes, vRes);
		renderResult.setPixels(result);
		
		this.buckets = getBuckets(bucketSize, bucketSize);
		
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
							if (callbacks != null) {
								callbacks.onBucketFinished(bucket, result);
							}
							latch.countDown();
						}
						}));
		}

		try {
			latch.await();
		} catch (InterruptedException e) {
			System.out.println("We fucked up");
		} finally {
			if (callbacks != null)
				callbacks.onRenderFinished(result);
		}

		renderResult.finishRender();
		return renderResult;
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
		Deque<Bucket> buckets = new ConcurrentLinkedDeque<Bucket>();
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
	public ColorTracer getTracer() {
		if (tracer == null) {
			tracer = new SimpleColorTracer();
		}
		
		return tracer;
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
		
		if (camera != null)
			camera.invalidateViewPlane();
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
		if (this.camera != camera && camera != null) {
			this.camera = camera;
			camera.setViewParameters(eye, lookat, up);
		}
	}

	@Override
	public Camera getCamera() {
		return camera;
	}

	@Override
	public ViewPlane getViewPlane() {
		if (camera != null) {
			return camera.calculateViewPlane(hRes, vRes);
		}
		
		return null;
	}
	
	private void preprocessWorld() {
		renderResult.startPreprocessing();
		world.preprocess();
		renderResult.finishPreprocessing();
	}
}
