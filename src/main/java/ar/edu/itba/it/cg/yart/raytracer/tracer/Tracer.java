package ar.edu.itba.it.cg.yart.raytracer.tracer;

import ar.edu.itba.it.cg.yart.geometry.Point3d;
import ar.edu.itba.it.cg.yart.geometry.Vector3d;
import ar.edu.itba.it.cg.yart.raytracer.RenderResult;
import ar.edu.itba.it.cg.yart.raytracer.ViewPlane;
import ar.edu.itba.it.cg.yart.raytracer.buckets.Bucket;
import ar.edu.itba.it.cg.yart.raytracer.camera.Camera;
import ar.edu.itba.it.cg.yart.raytracer.world.World;

public interface Tracer {
	
	public RenderResult render();
	public RenderResult serialRender();
	public void setWorld(final World w);
	public World getWorld();
	public int getHorizontalRes();
	public int getVerticalRes();
	public int getBucketSize();
	public void setCallbacks(final TracerCallbacks callbacks);
	
	public ViewPlane getViewPlane();
	public int getNumSamples();
	public void setNumSamples(final int numSamples);
	public void setResolution(final int xRes, final int yRes);
	public void setViewParameters(final Point3d eye, final Point3d lookAt, final Vector3d up);
	public void setCamera(final Camera camera);
	public Camera getCamera();
	public void setGamma(final double gamma);
	public double getGamma();
	public double getGammaInv();
	
	public interface TracerCallbacks {
		public void onBucketStarted(final Bucket bucket);
		public void onBucketFinished(final Bucket bucket,
				final RenderResult result);

		public void onRenderFinished(final RenderResult result);
	}
}
