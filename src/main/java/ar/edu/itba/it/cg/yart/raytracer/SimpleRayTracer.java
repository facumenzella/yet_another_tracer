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
import ar.edu.itba.it.cg.yart.raytracer.tracer.SimpleColorTracer;
import ar.edu.itba.it.cg.yart.raytracer.world.World;
import ar.edu.itba.it.cg.yart.utils.YartExecutorFactory;

public class SimpleRayTracer implements RayTracer {

	private World world;

	private int hRes;
	private int vRes;
	private int bucketSize;
	private RaytracerCallbacks callbacks;
	private final ExecutorService executor;
	private final Deque<Bucket> buckets;
	final Camera camera;
	
	final static private int THREADS = 2;


	public interface RaytracerCallbacks {
		public void onBucketFinished(final Bucket bucket,
				final ArrayIntegerMatrix result);

		public void onRenderFinished(final ArrayIntegerMatrix result);
	}

	
	public SimpleRayTracer(final int hRes, final int vRes, final double fov, final int bucketSize, 
			final double tMax, final double distance, final int zoom, final int numSamples) {

		// TODO : change how we create the world
		this.hRes = hRes;
		this.vRes = vRes;
		this.bucketSize = bucketSize;
		this.executor = YartExecutorFactory.newFixedThreadPool(THREADS); // TODO change after tests
		this.buckets = getBuckets();		
//		final Point3 eye = new Point3(0,0,200);
//		final Point3 lookat = new Point3(0,0,0); // point where we look at
//		final Vector3d up = new Vector3d(0,1,0); // up vector, rotates around the camera z-axis
		final Point3 eye = new Point3(0,100,300);
		final Point3 lookat = new Point3(0,0,0); // point where we look at
		final Vector3d up = new Vector3d(0,1,0); // up vector, rotates around the camera z-axis
		this.camera = new PinholeCamera(eye, lookat, up, distance, zoom, hRes, vRes, fov, numSamples);
	}

	public ArrayIntegerMatrix serialRender() {
		return this.serialRender(this.world);
	}
	
	private ArrayIntegerMatrix serialRender(final World world) {
		ArrayIntegerMatrix result = new ArrayIntegerMatrix(hRes, vRes);
		ViewPlane viewPlane = camera.getViewPlane();

		while (!buckets.isEmpty()) {
			Bucket bucket = buckets.poll();

			world.getActiveCamera().renderScene(bucket, world, result,
					viewPlane, new SimpleColorTracer());

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
		ViewPlane viewPlane = camera.getViewPlane();

		int totals = buckets.size();
		final CountDownLatch latch = new CountDownLatch(totals);

		int i = 0;
		while (i < THREADS) {
			i++;

			final Camera camera = world.getActiveCamera();
			executor.submit(new BucketWorker(buckets, camera, world, viewPlane,
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
					}, new SimpleColorTracer()));
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
	public void setWorld(final World world) {
		this.world = world;
		world.addCamera(this.camera);
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

	public Deque<Bucket> getBuckets() {
		Deque<Bucket> buckets = new ConcurrentLinkedDeque<Bucket>();
		int xBuckets = (int) Math.ceil(hRes / ((float) bucketSize));
		int yBuckets = (int) Math.ceil(vRes / ((float) bucketSize));

		for (int i = 0; i < yBuckets; i++) {
			for (int j = 0; j < xBuckets; j++) {
				int width = bucketSize;
				int height = bucketSize;

				if (j == (xBuckets - 1))
					width = hRes - j * bucketSize;

				if (i == (yBuckets - 1))
					height = vRes - i * bucketSize;

				buckets.add(new Bucket(j * bucketSize, i * bucketSize, width, height));
			}
		}

		return buckets;
	}
}
