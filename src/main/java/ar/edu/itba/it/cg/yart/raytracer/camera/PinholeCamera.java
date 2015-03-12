package ar.edu.itba.it.cg.yart.raytracer.camera;

import ar.edu.itba.it.cg.yart.color.Color;
import ar.edu.itba.it.cg.yart.geometry.Point3;
import ar.edu.itba.it.cg.yart.geometry.Vector3d;
import ar.edu.itba.it.cg.yart.raytracer.Ray;
import ar.edu.itba.it.cg.yart.raytracer.Tracer;
import ar.edu.itba.it.cg.yart.raytracer.ViewPlane;
import ar.edu.itba.it.cg.yart.raytracer.world.World;

public class PinholeCamera extends CameraAbstract {

	public PinholeCamera(final Tracer tracer, final Point3 eye, 
			final Point3 lookat, final Vector3d up) {
		super(tracer, eye, lookat, up);
	}

	@Override
	public void renderScene(World world) {
		Color color;
		ViewPlane vp = world.vp;
		Ray ray = new Ray(this.eye);
		for (int row = 0; row < vp.vRes; row++) { // up
			for (int col = 0; col < vp.hRes; col++) { // across
				final Vector3d vector = new Vector3d(vp.pixelSize
						* (col - 0.5 * (vp.hRes - 1.0)), vp.pixelSize
						* (row - 0.5 * (vp.vRes - 1.0)), -60);
				ray.direction = Vector3d.normalize(vector);

				color = tracer.traceRay(ray, world.objects);
				if (color == null) {
					color = world.backgroundColor;
				}

				world.ret.put(col, vp.hRes - row - 1, color.toInt());
			}
		}

	}

	public Vector3d rayDirection(final Point3 point) {

		return null;
	}

}
