package ar.edu.itba.it.cg.yart.raytracer;

import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ar.edu.itba.it.cg.yart.YartConstants;
import ar.edu.itba.it.cg.yart.acceleration_estructures.fkdtree.Stack;
import ar.edu.itba.it.cg.yart.geometry.Point3d;
import ar.edu.itba.it.cg.yart.geometry.Vector3d;
import ar.edu.itba.it.cg.yart.matrix.ArrayIntegerMatrix;
import ar.edu.itba.it.cg.yart.raytracer.buckets.Bucket;
import ar.edu.itba.it.cg.yart.raytracer.buckets.BucketWorker;
import ar.edu.itba.it.cg.yart.raytracer.camera.Camera;
import ar.edu.itba.it.cg.yart.raytracer.camera.PinholeCamera;
import ar.edu.itba.it.cg.yart.raytracer.interfaces.RayTracer;
import ar.edu.itba.it.cg.yart.raytracer.world.World;
import ar.edu.itba.it.cg.yart.utils.YartExecutorFactory;

public class SimpleRayTracer implements RayTracer {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(YartConstants.LOG_FILE);

	private final int cores;
	
	private final RenderResult renderResult;
	private World world;
	
	// Default parameters
	private Point3d eye = new Point3d(200,0,0);
	private Point3d lookat = new Point3d(0,0,0);
	private Vector3d up = new Vector3d(0,0,1);

	private int hRes;
	private int vRes;
	private int bucketSize;
	private int numSamples;
	
	private RaytracerCallbacks callbacks = new RaytracerCallbacks() {
		
		@Override
		public void onRenderFinished(RenderResult result) {
		}
		
		@Override
		public void onBucketFinished(Bucket bucket, RenderResult result) {
		}
	};
	private final ExecutorService executor;
	private Deque<Bucket> buckets;
	private Camera camera;
	private Stack[] stacks;

	public interface RaytracerCallbacks {
		public void onBucketFinished(final Bucket bucket,
				final RenderResult result);

		public void onRenderFinished(final RenderResult result);
	}

	
	public SimpleRayTracer(final RenderResult renderResult, final int bucketSize, final double tMax, final double distance, final int zoom, final int numSamples, final int cores) {
		this.cores = cores;
		this.bucketSize = bucketSize;
		this.executor = YartExecutorFactory.newFixedThreadPool(this.cores);
		this.stacks = new Stack[this.cores];
		for (int i = 0; i < stacks.length; i++) {
			this.stacks[i] = new Stack();
		}
		this.renderResult = renderResult;
		
		setResolution(800, 600);
		setNumSamples(numSamples);
		setCamera(new PinholeCamera(eye, lookat, up, distance, zoom));
	}
	
	public void finishRaytracer() {
		executor.shutdown();
		try {
			executor.awaitTermination(1, TimeUnit.MINUTES);
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

		while (!buckets.isEmpty()) {
			Bucket bucket = buckets.poll();

			camera.renderScene(bucket, this, result, this.stacks[0]);

			callbacks.onBucketFinished(bucket, renderResult);
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
		
		int totals = buckets.size();
		final CountDownLatch latch = new CountDownLatch(totals);

		for (int i = 0; i < this.cores; i++) {
			executor.submit(new BucketWorker(buckets, this,
					result, new RaytracerCallbacks() {

						@Override
						public void onRenderFinished(RenderResult result) {
							// TODO fix this
							return;
						}

						@Override
						public void onBucketFinished(Bucket bucket,
								RenderResult result) {
							callbacks.onBucketFinished(bucket, renderResult);
							latch.countDown();
						}
						}, this.stacks[i]));
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
	public void setViewParameters(final Point3d eye, final Point3d lookAt, final Vector3d up) {
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
