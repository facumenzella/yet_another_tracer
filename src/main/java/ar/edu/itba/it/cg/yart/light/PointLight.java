	package ar.edu.itba.it.cg.yart.light;
	
	import ar.edu.itba.it.cg.yart.acceleration_estructures.fkdtree.Stack;
	import ar.edu.itba.it.cg.yart.color.Color;
	import ar.edu.itba.it.cg.yart.geometry.Point3d;
	import ar.edu.itba.it.cg.yart.geometry.Vector3d;
	import ar.edu.itba.it.cg.yart.raytracer.Ray;
	import ar.edu.itba.it.cg.yart.raytracer.ShadeRec;
	import ar.edu.itba.it.cg.yart.transforms.Matrix4d;
	
	public class PointLight extends AbstractLight {
	
		private final double ls;
		private final Color color;
		private Vector3d location;
		private Point3d point;
	
		public PointLight(final double ls, final Color color,
				final Vector3d location) {
			super();
			this.ls = ls;
			this.color = color;
			this.location = location;
			this.point = new Point3d(location.x, location.y, location.z);
		}
	
		public PointLight(final double ls, final Color color) {
			this(ls, color, new Vector3d(0, 0, 0));
		}
	
		@Override
		public Vector3d getDirection(final ShadeRec sr) {
			final double dx = location.x - sr.hitPoint.x;
			final double dy = location.y - sr.hitPoint.y;
			final double dz = location.z - sr.hitPoint.z;
			final double length = Math.sqrt(dx * dx + dy * dy + dz * dz);
			return new Vector3d(dx / length, dy / length, dz / length);
		}
	
		@Override
		public Color L(final ShadeRec sr) {
			final double r = color.r * ls;
			final double g = color.g * ls;
			final double b = color.b * ls;
			return new Color(r, g, b, color.a);
		}
	
		@Override
		public boolean inShadow(final Ray ray, final ShadeRec sr, final Stack stack) {
			double t;
	
			final double dx = point.x - ray.origin.x;
			final double dy = point.y - ray.origin.y;
			final double dz = point.z - ray.origin.z;
	
			final double d = Math.sqrt(dx * dx + dy * dy + dz * dz);
	
			t = sr.world.getTree().traceShadowHit(ray, stack);
			if (t != Double.NEGATIVE_INFINITY && t < d) {
				return true;
			}
			return false;
		}
	
		@Override
		public void applyTransformation(Matrix4d matrix) {
			this.point = this.point.transformByMatrix(matrix);
			location = new Vector3d(point.x, point.y, point.z);
		}
	}
