package ar.edu.itba.it.cg.yart.raytracer.camera;

import ar.edu.itba.it.cg.yart.color.Color;
import ar.edu.itba.it.cg.yart.geometry.Point2d;
import ar.edu.itba.it.cg.yart.geometry.Point3;
import ar.edu.itba.it.cg.yart.geometry.Vector3d;
import ar.edu.itba.it.cg.yart.raytracer.Ray;
import ar.edu.itba.it.cg.yart.raytracer.Tracer;
import ar.edu.itba.it.cg.yart.raytracer.ViewPlane;
import ar.edu.itba.it.cg.yart.raytracer.world.World;

public class PinholeCamera extends CameraAbstract {

	private final double distance;
	
	public PinholeCamera(final Tracer tracer, final Point3 eye, 
			final Point3 lookat, final Vector3d up, final double distance) {
		super(tracer, eye, lookat, up);
		this.distance = distance;
	}

	@Override
	public void renderScene(World world) {
		// TODO : i don't care about up and lookat factors. I am the king of the world
		// Its almost working, but its not finished
		Color color;
		ViewPlane vp = world.vp;
		Point2d sp = new Point2d(0, 0);
		Point2d pp;
		Ray ray = new Ray(this.eye);
		for (int row = 0; row < vp.vRes; row++) { // up
			for (int col = 0; col < vp.hRes; col++) { // across
				final double x = vp.pixelSize * (col - 0.5 * vp.hRes + sp.x);
				final double y = vp.pixelSize * (row - 0.5 * vp.vRes + sp.y);
				pp = new Point2d(x,y);
				ray.direction = this.rayDirection(pp);

				color = tracer.traceRay(ray, world.objects);
				if (color == null) {
					color = world.backgroundColor;
				}

				world.ret.put(col, vp.hRes - row - 1, color.toInt());
			}
		}

	}
	
	private Vector3d rayDirection(final Point2d p) {
		final Vector3d dir = (u.scale(p.x)).add(v.scale(p.y)).sub(w.scale(distance));
		return Vector3d.normalize(dir);
	}

}
