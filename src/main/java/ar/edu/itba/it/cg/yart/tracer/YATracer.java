package ar.edu.itba.it.cg.yart.tracer;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ar.edu.itba.it.cg.yart.YartDefaults;
import ar.edu.itba.it.cg.yart.acceleration_estructures.fkdtree.Stack;
import ar.edu.itba.it.cg.yart.geometry.Point3d;
import ar.edu.itba.it.cg.yart.geometry.Vector3d;
import ar.edu.itba.it.cg.yart.matrix.ArrayIntegerMatrix;
import ar.edu.itba.it.cg.yart.tracer.buckets.Bucket;
import ar.edu.itba.it.cg.yart.tracer.buckets.BucketRenderAction;
import ar.edu.itba.it.cg.yart.tracer.camera.Camera;
import ar.edu.itba.it.cg.yart.tracer.camera.PinholeCamera;
import ar.edu.itba.it.cg.yart.tracer.strategy.TracerStrategy;
import ar.edu.itba.it.cg.yart.tracer.world.World;

public class YATracer implements Tracer {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(YartDefaults.LOG_FILE);

	private final int cores;

	private final RenderResult renderResult;
	private World world;

	// Default parameters
	private Point3d eye = YartDefaults.DEFAULT_EYE;
	private Point3d lookat = YartDefaults.DEFAULT_LOOKAT;
	private Vector3d up = YartDefaults.DEFAULT_UP;

	private int hRes;
	private int vRes;
	private int bucketSize;
	private int numSamples;
	private double gamma;
	private double gammaInv;

	private TracerCallbacks callbacks = new TracerCallbacks() {
		@Override public void onBucketStarted(final Bucket bucket) {}
		@Override public void onRenderFinished(RenderResult result) {}
		@Override public void onBucketFinished(Bucket bucket, RenderResult result) {}
	};
	
	private final ForkJoinPool pool;
	private Bucket[] buckets;
	private Camera camera;
	private Stack[] stacks;

	public YATracer(final RenderResult renderResult,
			final int bucketSize, final double tMax, final double distance,
			final int zoom, final int numSamples, final int cores, final TracerStrategy strategy) {
		this.cores = cores;
		this.bucketSize = bucketSize;
		this.pool = new ForkJoinPool(this.cores);
		this.stacks = new Stack[this.cores];
		for (int i = 0; i < stacks.length; i++) {
			this.stacks[i] = new Stack();
		}
		this.renderResult = renderResult;

		setResolution(YartDefaults.DEFAULT_XRES, YartDefaults.DEFAULT_YRES);
		setNumSamples(numSamples);
		setCamera(new PinholeCamera(eye, lookat, up, distance, zoom, tMax, strategy));
		setGamma(2.2);
	}

	public void finishRaytracer() {
		pool.shutdown();
		try {
			pool.awaitTermination(1, TimeUnit.MINUTES);
		} catch (InterruptedException e) {
			LOGGER.error("Couldn't terminate threads");
		}
	}

	public RenderResult serialRender() {
		preprocessWorld();
		renderResult.setTriangles(world.getTriangleCount());
		renderResult.startRender();
		ArrayIntegerMatrix result = new ArrayIntegerMatrix(hRes, vRes);
		renderResult.setPixels(result);

		this.buckets = getBuckets(bucketSize, bucketSize);
		int i = 0;
		while (i < buckets.length) {
			Bucket bucket = buckets[i];
			callbacks.onBucketStarted(bucket);
			camera.renderScene(bucket, this, result, this.stacks[0]);

			callbacks.onBucketFinished(bucket, renderResult);
			i++;
		}

		renderResult.finishRender();
		callbacks.onRenderFinished(renderResult);

		return renderResult;
	}

	@Override
	public RenderResult render() {
		return this.render(this.world);
	}

	private RenderResult render(final World world) {
		preprocessWorld();
		renderResult.setTriangles(world.getTriangleCount());
		ArrayIntegerMatrix result = new ArrayIntegerMatrix(hRes, vRes);
		renderResult.startRender();
		renderResult.setPixels(result);

		this.buckets = getBuckets(bucketSize, bucketSize);

		int totals = buckets.length;
		final CountDownLatch latch = new CountDownLatch(totals);

		AtomicInteger index = new AtomicInteger(-1);
		for (int i = 0; i < this.cores; i++) {
			pool.submit(new BucketRenderAction(buckets, this, result,
					new TracerCallbacks() {
						@Override public void onBucketStarted(Bucket bucket) {
							callbacks.onBucketStarted(bucket);
						}
						
						@Override public void onRenderFinished(RenderResult result) {}

						@Override
						public void onBucketFinished(Bucket bucket,
								RenderResult result) {
							callbacks.onBucketFinished(bucket, renderResult);
							latch.countDown();
						}
					}, this.stacks[i], index, 0, buckets.length));
		}

		try {
			latch.await();
		} catch (InterruptedException e) {
			System.out.println("We fucked up");
		} finally {
			renderResult.finishRender();
			callbacks.onRenderFinished(renderResult);
		}

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
	public void setCallbacks(final TracerCallbacks callbacks) {
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

	public Bucket[] getBuckets(final int xBucketsSize, final int yBucketsSize) {
		int xBuckets = (int) Math.ceil(hRes / ((float) xBucketsSize));
		int yBuckets = (int) Math.ceil(vRes / ((float) yBucketsSize));
		Bucket[] buckets = new Bucket[xBuckets * yBuckets];

		int bucket = 0;

		for (int i = 0; i < yBuckets; i++) {
			for (int j = 0; j < xBuckets; j++) {
				int width = xBucketsSize;
				int height = yBucketsSize;

				if (j == (xBuckets - 1))
					width = hRes - j * xBucketsSize;

				if (i == (yBuckets - 1))
					height = vRes - i * yBucketsSize;

				buckets[bucket++] = new Bucket(j * xBucketsSize, i
						* yBucketsSize, width, height);
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

		if (camera != null)
			camera.invalidateViewPlane();
	}

	@Override
	public void setViewParameters(final Point3d eye, final Point3d lookAt,
			final Vector3d up) {
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

	@Override
	public void setGamma(final double gamma) {
		this.gamma = gamma;
		this.gammaInv = 1 / gamma;
	}

	@Override
	public double getGamma() {
		return gamma;
	}

	@Override
	public double getGammaInv() {
		return gammaInv;
	}

}
