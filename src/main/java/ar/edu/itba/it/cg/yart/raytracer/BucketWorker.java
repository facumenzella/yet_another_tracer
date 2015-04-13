package ar.edu.itba.it.cg.yart.raytracer;

import ar.edu.itba.it.cg.yart.matrix.ArrayIntegerMatrix;
import ar.edu.itba.it.cg.yart.raytracer.SimpleRayTracer.RaytracerCallbacks;
import ar.edu.itba.it.cg.yart.raytracer.camera.Camera;
import ar.edu.itba.it.cg.yart.raytracer.tracer.Tracer;
import ar.edu.itba.it.cg.yart.raytracer.world.World;

public class BucketWorker implements Runnable{

	private final Camera camera;
	private final Bucket bucket;
	private final World world;
	private final ViewPlane viewPlane;
	private final ArrayIntegerMatrix result;
	private final RaytracerCallbacks callback;
	private final Tracer tracer;
	
	public BucketWorker(final Bucket bucket, final Camera camera, final World world, 
			final ViewPlane viewPlane, final ArrayIntegerMatrix result, final RaytracerCallbacks callback, final Tracer tracer) {
		this.bucket = bucket;
		this.camera = camera;
		this.world = world;
		this.viewPlane = viewPlane;
		this.result = result;
		this.callback = callback;
		this.tracer = tracer;
	}
	
	@Override
	public void run() {
		camera.renderScene(bucket, world, result, viewPlane, tracer);
		callback.onBucketFinished(bucket, result);
	}

}
