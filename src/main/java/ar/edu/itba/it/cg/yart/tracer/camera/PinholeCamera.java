package ar.edu.itba.it.cg.yart.tracer.camera;

import java.util.concurrent.ThreadLocalRandom;

import ar.edu.itba.it.cg.yart.YartDefaults;
import ar.edu.itba.it.cg.yart.acceleration_estructures.fkdtree.Stack;
import ar.edu.itba.it.cg.yart.color.Color;
import ar.edu.itba.it.cg.yart.geometry.Point2d;
import ar.edu.itba.it.cg.yart.geometry.Point3d;
import ar.edu.itba.it.cg.yart.geometry.Vector3d;
import ar.edu.itba.it.cg.yart.matrix.ArrayColorMatrix;
import ar.edu.itba.it.cg.yart.samplers.DiskSampler;
import ar.edu.itba.it.cg.yart.tracer.Ray;
import ar.edu.itba.it.cg.yart.tracer.ShadeRec;
import ar.edu.itba.it.cg.yart.tracer.Tracer;
import ar.edu.itba.it.cg.yart.tracer.ViewPlane;
import ar.edu.itba.it.cg.yart.tracer.buckets.Bucket;
import ar.edu.itba.it.cg.yart.tracer.strategy.TracerStrategy;
import ar.edu.itba.it.cg.yart.tracer.world.World;

public class PinholeCamera extends CameraAbstract {

	private final double distance;
	private final double zoom;
	private final Point2d sp = new Point2d(0, 0);
	private final TracerStrategy strategy;
	private double tMax;
	
	private double maxX = 1;
	private double minX = -1;
	private double maxY = 1;
	private double minY = -1;

	private double fov = YartDefaults.DEFAULT_FOV;
	private double lensRadius = YartDefaults.DEFAULT_LENS_RADIUS;
	private double focalDistance = YartDefaults.DEFAULT_FOCAL_DISTANCE;

	private DiskSampler lensSampler;

	public PinholeCamera(final Point3d eye, final Point3d lookat,
			final Vector3d up, final double distance, final double zoom, final double tMax, final TracerStrategy strategy) {
		super(eye, lookat, up);
		this.distance = distance;
		this.zoom = zoom;
		this.tMax = tMax;
		this.strategy = strategy;
	}

	@Override
	public void renderScene(final Bucket bucket, Tracer rayTracer,
							final ArrayColorMatrix result, final Stack stack) {

		Color blackColor = Color.BLACK;
		ViewPlane viewPlane = rayTracer.getViewPlane();
		double adjustedPixelSize = viewPlane.pixelSize / zoom;
		final Color color = new Color(Color.BLACK);
		Ray ray = new Ray(new Point3d(0, 0, 0));
		Vector3d focusPoint = new Vector3d(0, 0, 0);
		final int n = (int) Math.sqrt((double) rayTracer.getNumSamples());
		final double invNumSamples = 1 / (double) rayTracer.getNumSamples();
		
		int xStart = bucket.getX();
		int xFinish = xStart + bucket.getWidth();
		int yStart = bucket.getY();
		int yFinish = yStart + bucket.getHeight();
		
		World world = rayTracer.getWorld();
		ShadeRec sr =  new ShadeRec(world);
		final double[] d = new double[3];
		
		int row = yStart;
		int col = xStart;
		while (row < yFinish) {
			int i = 0;
			int j = 0;
			color.set(Color.BLACK);
			while (i < n) {
				final double distributionX;
				final double distributionY;
				if (n == 1) {
					distributionX = distributionY = 0;
				} else {
					final double ri = ThreadLocalRandom.current().nextDouble();
					distributionX = (j + ri) / n;
					distributionY = (i + ri) / n;
				}
				final double x = adjustedPixelSize
						* (col - 0.5 * viewPlane.hRes + sp.x + distributionX);
				final double y = adjustedPixelSize
						* (0.5 * viewPlane.vRes - row + sp.y + distributionY);

//				Vector3d d = (u.scale(x)).add(v.scale(y)).sub(w.scale(distance)).normalizedVector();
				final double dx = u.x * x + v.x * y - w.x * distance;
				final double dy = u.y * x + v.y * y - w.y * distance;
				final double dz = u.z * x + v.z * y - w.z * distance;
				final double length = Math.sqrt(dx*dx + dy*dy + dz*dz);
				d[0] = dx / length;
				d[1] = dy / length;
				d[2] = dz / length;

				ray.direction = d;
				ray.origin.set(this.eye);

				// Apply depth of field, if enabled
				if (lensRadius > 0 && focalDistance > 0 && lensSampler != null) {
					focusPoint.set(ray.direction).scaleMe(focalDistance).addMe(ray.origin);
					Point2d sample = lensSampler.getSample();
					ray.origin.x += sample.x;
					ray.origin.z += sample.y;
					ray.direction[0] = focusPoint.x - ray.origin.x;
					ray.direction[1] = focusPoint.y - ray.origin.y;
					ray.direction[2] = focusPoint.z - ray.origin.z;
					double length2 = ray.direction[0] * ray.direction[0] + ray.direction[1] * ray.direction[1] + ray.direction[2] * ray.direction[2];
					ray.direction[0] /= length2;
					ray.direction[1] /= length2;
					ray.direction[2] /= length2;
				}

				sr.hitObject = false;
				Color c = world.getTree().traceRay(ray, sr, tMax, stack, strategy);
//				System.out.println(c);
				color.r += c.r;
				color.g += c.g;
				color.b += c.b;
				j++;
				if (j == n) {
					j = 0;
					i++;
				}
			}
			color.r *= invNumSamples;
			color.g *= invNumSamples;
			color.b *= invNumSamples;

			/*rayTracer.getToneMapper().mapMe(color);

			color.r = Math.pow(color.r, rayTracer.getGammaInv());
			color.g = Math.pow(color.g, rayTracer.getGammaInv());
			color.b = Math.pow(color.b, rayTracer.getGammaInv());

			// now we display the pixel
			int ret = 0;
			int alpha = (int) (color.a * 255);
			int red = (int) (color.r * 255);
			int green = (int) (color.g * 255);
			int blue = (int) (color.b * 255);

			ret = alpha << 24 | red << 16 | green << 8 | blue;

			final int index =  (row * result.cols) + col;
			result.matrix[index] = ret;
			result.put(col, row, ret);*/

			result.put((row * result.cols) + col, color);
			col++;
			if (col == xFinish) {
				col = xStart;
				row++;
			}
		}
	}

	public double getFov() {
		return fov;
	}

	public void setFov(double fov) {
		if (fov < 0) {
			fov = 0;
		} else if (fov >= 180) {
			fov = 179;
		}

		this.fov = fov;

		invalidateViewPlane();
	}

	public double getLensRadius() {
		return lensRadius;
	}

	/**
	 * Sets the lens radius. Pre-generates samples to retrieve them efficiently during rendering, so this is an
	 * expensive operation.
	 * @param lensRadius The lens radius. Must be a non-zero number. Negative numbers are made positive.
	 */
	public void setLensRadius(final double lensRadius) {
		this.lensRadius = Math.abs(lensRadius);
		lensSampler = new DiskSampler(1000, lensRadius);
	}

	public double getFocalDistance() {
		return focalDistance;
	}
	
	public void setFocalDistance(final double focalDistance) {
		this.focalDistance = Math.abs(focalDistance);
	}
	
	private double getPixelSize(final int hRes, final int vRes) {
		double pixelSize = 1;

		final double min = Math.min(hRes, vRes);
		// We need radians, not degrees!!!
		final double radians = Math.toRadians(fov / 2);
		double length = 2 * distance * Math.tan(radians);
		double mult = Math.min(Math.abs(maxX - minX) / 2, Math.abs(maxY - minY) / 2);
		
		length *= mult;

		if (min == hRes) {
			pixelSize = length / hRes;
		} else {
			pixelSize = length / vRes;
		}

		return Math.abs(pixelSize);
	}
	
	public void setScreenWindow(final double minX, final double maxX, final double minY, final double maxY) {
		this.maxX = maxX;
		this.minX = minX;
		this.maxY = maxY;
		this.minY = minY;
		invalidateViewPlane();
	}
	
	public void setTMax(final double tMax) {
		this.tMax = tMax;
	}

	@Override
	public ViewPlane calculateViewPlane(int hRes, int vRes) {
		if (isViewPlaneInvalid()) {
			viewPlane = new ViewPlane(hRes, vRes, getPixelSize(hRes, vRes));
			viewPlaneInvalidated = false;
		}
		return viewPlane;
	}

}
