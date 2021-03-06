package ar.edu.itba.it.cg.yart.tracer;

import ar.edu.itba.it.cg.yart.geometry.Point3d;
import ar.edu.itba.it.cg.yart.geometry.Vector3d;
import ar.edu.itba.it.cg.yart.tracer.buckets.Bucket;
import ar.edu.itba.it.cg.yart.tracer.camera.Camera;
import ar.edu.itba.it.cg.yart.tracer.tonemapper.ToneMapper;
import ar.edu.itba.it.cg.yart.tracer.world.World;

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
	public void setToneMapper(final ToneMapper toneMapper);
	public ToneMapper getToneMapper();
	
	public interface TracerCallbacks {
		public void onBucketStarted(final Bucket bucket);
		public void onBucketFinished(final Bucket bucket,
				final RenderResult result);

		public void onRenderFinished(final RenderResult result);
	}
}
