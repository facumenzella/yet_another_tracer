package ar.edu.itba.it.cg.yart.raytracer;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;

import ar.edu.itba.it.cg.yart.geometry.Point3;
import ar.edu.itba.it.cg.yart.geometry.Vector3d;
import ar.edu.itba.it.cg.yart.matrix.ArrayIntegerMatrix;
import ar.edu.itba.it.cg.yart.raytracer.camera.Camera;
import ar.edu.itba.it.cg.yart.raytracer.camera.PinholeCamera;
import ar.edu.itba.it.cg.yart.raytracer.interfaces.RayTracer;
import ar.edu.itba.it.cg.yart.raytracer.world.World;
import ar.edu.itba.it.cg.yart.utils.YartExecutorFactory;

public class SimpleRayTracer implements RayTracer {

	private World world;
	
	private int hRes;
	private int vRes;
	private int bucketSize;
	private RaytracerCallbacks callbacks;
	private final ExecutorService executor;
	private final List<Bucket> buckets;
	final Camera camera;
	
	public interface RaytracerCallbacks {
		public void onBucketFinished(final Bucket bucket, final ArrayIntegerMatrix result);
		public void onRenderFinished(final ArrayIntegerMatrix result);
	}
	
	public SimpleRayTracer(final int hRes, final int vRes, final int bucketSize) {
		// TODO : change how we create the world
		this.hRes = hRes;
		this.vRes = vRes;
		this.bucketSize = bucketSize;
		this.executor = YartExecutorFactory.newFixedThreadPool(3); // TODO change after tests
		this.buckets = getBuckets();
		
		final Tracer tracer = new Tracer();
		final Point3 eye = new Point3(0,0,200);
		final Point3 lookat = new Point3(0,0,0); // point where we look at
		final Vector3d up = new Vector3d(0,1,0); // up vector, rotates around the camera z-axis

		final double distance = 500;
		final double zoom = 1;
		this.camera = new PinholeCamera(tracer, eye, lookat, up, distance, zoom, hRes, vRes);
	}

	public ArrayIntegerMatrix serialRender(final World world) {
		ArrayIntegerMatrix result = new ArrayIntegerMatrix(hRes, vRes);
		List<Bucket> buckets = getBuckets();
		ViewPlane viewPlane = new ViewPlane(hRes, vRes);
		
		while (!buckets.isEmpty()) {
			Bucket bucket = buckets.get(0);
			buckets.remove(0);
			
			world.getActiveCamera().renderScene(bucket, world, result, viewPlane);
			
			if (callbacks != null) {
				callbacks.onBucketFinished(bucket, result);
			}
		}
		
		if (callbacks != null) {
			callbacks.onRenderFinished(result);
		}
		
		return result;
	}
	
	public ArrayIntegerMatrix render() {
		return this.render(this.world);
	}

	@Override
	public ArrayIntegerMatrix render(final World world) {
		ArrayIntegerMatrix result = new ArrayIntegerMatrix(hRes, vRes);
		ViewPlane viewPlane = new ViewPlane(hRes, vRes);
		
		int totals = buckets.size();
        final CountDownLatch latch = new CountDownLatch(totals);
		
		while (!buckets.isEmpty()) {
			final Bucket bucket = buckets.get(0);
			buckets.remove(0);
			
			final Camera camera = world.getActiveCamera();
			executor.submit(new BucketWorker(bucket, camera, world, viewPlane, result, new RaytracerCallbacks() {
				
				@Override
				public void onRenderFinished(ArrayIntegerMatrix result) {
					// TODO fix this
					return;
				}
				
				@Override
				public void onBucketFinished(Bucket bucket, ArrayIntegerMatrix result) {
					callbacks.onBucketFinished(bucket, result);
					latch.countDown();
				}
			}));	
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

	@Override
	public List<Bucket> getBuckets() {
		List<Bucket> buckets = new LinkedList<Bucket>();
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
