package ar.edu.itba.it.cg.yart.transforms;

public class Matrix4d {

	public final double m00, m01, m02, m03;
	public final double m10, m11, m12, m13;
	public final double m20, m21, m22, m23;
	public final double m30, m31, m32, m33;

	private static Matrix4d reflectXMatrix = Matrix4d.reflectXMatrix();
	private static Matrix4d reflectYMatrix = Matrix4d.reflectYMatrix();
	private static Matrix4d reflectZMatrix = Matrix4d.reflectZMatrix();

	// Creates the identity matrix
	public Matrix4d() {
		this.m00 = this.m11 = this.m22 = this.m33 = 1;
		this.m01 = this.m02 = this.m03 = 0;
		this.m10 = this.m12 = this.m13 = 0;
		this.m20 = this.m21 = this.m23 = 0;
		this.m30 = this.m31 = this.m32 = 0;
	}

	public Matrix4d(final double m00, final double m01, final double m02,
			final double m03, final double m10, final double m11,
			final double m12, final double m13, final double m20,
			final double m21, final double m22, final double m23,
			final double m30, final double m31, final double m32,
			final double m33) {
		this.m00 = m00;
		this.m01 = m01;
		this.m02 = m02;
		this.m03 = m03;
		this.m10 = m10;
		this.m11 = m11;
		this.m12 = m12;
		this.m13 = m13;
		this.m20 = m20;
		this.m21 = m21;
		this.m22 = m22;
		this.m23 = m23;
		this.m30 = m30;
		this.m31 = m31;
		this.m32 = m32;
		this.m33 = m33;
	}

	private Matrix4d rightMultiply(final Matrix4d matrix) {
		final double m00 = (this.m00 * matrix.m00) + (this.m01 * matrix.m10)
				+ (this.m02 * matrix.m20) + (this.m03 * matrix.m30);
		final double m01 = (this.m00 * matrix.m01) + (this.m01 * matrix.m11)
				+ (this.m02 * matrix.m21) + (this.m03 * matrix.m31);
		final double m02 = (this.m00 * matrix.m02) + (this.m01 * matrix.m12)
				+ (this.m02 * matrix.m22) + (this.m03 * matrix.m32);
		final double m03 = (this.m00 * matrix.m03) + (this.m01 * matrix.m13)
				+ (this.m02 * matrix.m23) + (this.m03 * matrix.m33);

		final double m10 = (this.m10 * matrix.m00) + (this.m11 * matrix.m10)
				+ (this.m12 * matrix.m20) + (this.m13 * matrix.m30);
		final double m11 = (this.m10 * matrix.m01) + (this.m11 * matrix.m11)
				+ (this.m12 * matrix.m21) + (this.m13 * matrix.m31);
		final double m12 = (this.m10 * matrix.m02) + (this.m11 * matrix.m12)
				+ (this.m12 * matrix.m22) + (this.m13 * matrix.m32);
		final double m13 = (this.m10 * matrix.m03) + (this.m11 * matrix.m13)
				+ (this.m12 * matrix.m23) + (this.m13 * matrix.m33);

		final double m20 = (this.m20 * matrix.m00) + (this.m21 * matrix.m10)
				+ (this.m22 * matrix.m20) + (this.m23 * matrix.m30);
		final double m21 = (this.m20 * matrix.m01) + (this.m21 * matrix.m11)
				+ (this.m22 * matrix.m21) + (this.m23 * matrix.m31);
		final double m22 = (this.m20 * matrix.m02) + (this.m21 * matrix.m12)
				+ (this.m22 * matrix.m22) + (this.m23 * matrix.m32);
		final double m23 = (this.m20 * matrix.m03) + (this.m21 * matrix.m13)
				+ (this.m22 * matrix.m23) + (this.m23 * matrix.m33);

		final double m30 = (this.m30 * matrix.m00) + (this.m31 * matrix.m10)
				+ (this.m32 * matrix.m20) + (this.m33 * matrix.m30);
		final double m31 = (this.m30 * matrix.m01) + (this.m31 * matrix.m11)
				+ (this.m32 * matrix.m21) + (this.m33 * matrix.m31);
		final double m32 = (this.m30 * matrix.m02) + (this.m31 * matrix.m12)
				+ (this.m32 * matrix.m22) + (this.m33 * matrix.m32);
		final double m33 = (this.m30 * matrix.m03) + (this.m31 * matrix.m13)
				+ (this.m32 * matrix.m23) + (this.m33 * matrix.m33);

		return new Matrix4d(m00, m01, m02, m03, m10, m11, m12, m13, m20, m21,
				m22, m23, m30, m31, m32, m33);
	}

	public Matrix4d leftMultiply(final Matrix4d matrix) {
		final double m00 = (matrix.m00 * this.m00) + (matrix.m01 * this.m10)
				+ (matrix.m02 * this.m20) + (matrix.m03 * this.m30);
		final double m01 = (matrix.m00 * this.m01) + (matrix.m01 * this.m11)
				+ (matrix.m02 * this.m21) + (matrix.m03 * this.m31);
		final double m02 = (matrix.m00 * this.m02) + (matrix.m01 * this.m12)
				+ (matrix.m02 * this.m22) + (matrix.m03 * this.m32);
		final double m03 = (matrix.m00 * this.m03) + (matrix.m01 * this.m13)
				+ (matrix.m02 * this.m23) + (matrix.m03 * this.m33);

		final double m10 = (matrix.m10 * this.m00) + (matrix.m11 * this.m10)
				+ (matrix.m12 * this.m20) + (matrix.m13 * this.m30);
		final double m11 = (matrix.m10 * this.m01) + (matrix.m11 * this.m11)
				+ (matrix.m12 * this.m21) + (matrix.m13 * this.m31);
		final double m12 = (matrix.m10 * this.m02) + (matrix.m11 * this.m12)
				+ (matrix.m12 * this.m22) + (matrix.m13 * this.m32);
		final double m13 = (matrix.m10 * this.m03) + (matrix.m11 * this.m13)
				+ (matrix.m12 * this.m23) + (matrix.m13 * this.m33);

		final double m20 = (matrix.m20 * this.m00) + (matrix.m21 * this.m10)
				+ (matrix.m22 * this.m20) + (matrix.m23 * this.m30);
		final double m21 = (matrix.m20 * this.m01) + (matrix.m21 * this.m11)
				+ (matrix.m22 * this.m21) + (matrix.m23 * this.m31);
		final double m22 = (matrix.m20 * this.m02) + (matrix.m21 * this.m12)
				+ (matrix.m22 * this.m22) + (matrix.m23 * this.m32);
		final double m23 = (matrix.m20 * this.m03) + (matrix.m21 * this.m13)
				+ (matrix.m22 * this.m23) + (matrix.m23 * this.m33);

		final double m30 = (matrix.m30 * this.m00) + (matrix.m31 * this.m10)
				+ (matrix.m32 * this.m20) + (matrix.m33 * this.m30);
		final double m31 = (matrix.m30 * this.m01) + (matrix.m31 * this.m11)
				+ (matrix.m32 * this.m21) + (matrix.m33 * this.m31);
		final double m32 = (matrix.m30 * this.m02) + (matrix.m31 * this.m12)
				+ (matrix.m32 * this.m22) + (matrix.m33 * this.m32);
		final double m33 = (matrix.m30 * this.m03) + (matrix.m31 * this.m13)
				+ (matrix.m32 * this.m23) + (matrix.m33 * this.m33);

		return new Matrix4d(m00, m01, m02, m03, m10, m11, m12, m13, m20, m21,
				m22, m23, m30, m31, m32, m33);
	}

	public Matrix4d transform(final double dx, final double dy, final double dz) {
		final Matrix4d transformMatrix = Matrix4d.transformMatrix(dx, dy, dz);
		return this.rightMultiply(transformMatrix);
	}

	public Matrix4d scale(final double a, final double b, final double c) {
		final Matrix4d scaleMatrix = Matrix4d.scaleMatrix(a, b, c);
		return this.rightMultiply(scaleMatrix);
	}

	public Matrix4d rotateX(final double degrees) {
		final Matrix4d rotateXMatrix = Matrix4d.rotateXMatrix(degrees);
		return this.rightMultiply(rotateXMatrix);
	}

	public Matrix4d rotateY(final double degrees) {
		final Matrix4d rotateYMatrix = Matrix4d.rotateYMatrix(degrees);
		return this.rightMultiply(rotateYMatrix);
	}

	public Matrix4d rotateZ(final double degrees) {
		final Matrix4d rotateZMatrix = Matrix4d.rotateZMatrix(degrees);
		return this.rightMultiply(rotateZMatrix);
	}

	public Matrix4d reflectX() {
		return this.rightMultiply(reflectXMatrix);
	}

	public Matrix4d reflectY() {
		return this.rightMultiply(reflectYMatrix);
	}

	public Matrix4d reflectZ() {
		return this.rightMultiply(reflectZMatrix);
	}
	
	public Matrix4d inverse() {
		final double m00 = (this.m11*this.m22*this.m33) + (this.m12*this.m23*this.m31) + (this.m13*this.m21*this.m32) -
				(this.m11*this.m23*this.m32) - (this.m12*this.m21*this.m33) - (this.m13*this.m22*this.m31);
		final double m01 = (this.m01*this.m23*this.m32) + (this.m02*this.m21*this.m33) + (this.m03*this.m22*this.m31) -
				(this.m01*this.m22*this.m33) - (this.m02*this.m23*this.m31) - (this.m03*this.m21*this.m32);
		final double m02 = (this.m01*this.m12*this.m33) + (this.m02*this.m13*this.m31) + (this.m03*this.m11*this.m32) -
				(this.m01*this.m13*this.m32) - (this.m02*this.m11*this.m33) - (this.m03*this.m12*this.m31);
		final double m03 = (this.m01*this.m13*this.m33) + (this.m02*this.m11*this.m23) + (this.m03*this.m12*this.m21) -
				(this.m01*this.m12*this.m23) - (this.m02*this.m13*this.m21) - (this.m03*this.m11*this.m22);
		
		final double m10 = (this.m10*this.m23*this.m32) + (this.m12*this.m20*this.m33) + (this.m13*this.m22*this.m30) -
				(this.m10*this.m22*this.m33) - (this.m12*this.m23*this.m30) - (this.m13*this.m20*this.m32);
		final double m11 = (this.m00*this.m22*this.m33) + (this.m02*this.m23*this.m30) + (this.m03*this.m20*this.m32) -
				(this.m00*this.m23*this.m32) - (this.m02*this.m20*this.m33) - (this.m03*this.m22*this.m30);
		final double m12 = (this.m00*this.m13*this.m32) + (this.m02*this.m10*this.m33) + (this.m03*this.m12*this.m30) -
				(this.m00*this.m12*this.m33) - (this.m02*this.m13*this.m30) - (this.m03*this.m10*this.m32);
		final double m13 = (this.m00*this.m12*this.m23) + (this.m02*this.m13*this.m20) + (this.m03*this.m10*this.m22) -
				(this.m00*this.m13*this.m22) - (this.m02*this.m10*this.m23) - (this.m03*this.m12*this.m20);
		
		final double m20 = (this.m10*this.m21*this.m33) + (this.m11*this.m23*this.m30) + (this.m13*this.m20*this.m31) -
				(this.m10*this.m23*this.m31) - (this.m11*this.m20*this.m33) - (this.m13*this.m21*this.m30);
		final double m21 = (this.m00*this.m23*this.m31) + (this.m01*this.m20*this.m33) + (this.m03*this.m21*this.m30) -
				(this.m00*this.m21*this.m33) - (this.m01*this.m23*this.m30) - (this.m03*this.m20*this.m31);
		final double m22 = (this.m00*this.m11*this.m33) + (this.m01*this.m13*this.m31) + (this.m03*this.m10*this.m31) -
				(this.m00*this.m13*this.m31) - (this.m01*this.m10*this.m33) - (this.m03*this.m11*this.m30);
		final double m23 = (this.m00*this.m13*this.m21) + (this.m01*this.m10*this.m23) + (this.m03*this.m11*this.m20) -
				(this.m00*this.m11*this.m23) - (this.m01*this.m13*this.m20) - (this.m03*this.m10*this.m21);
		final double m30 = (this.m10*this.m22*this.m31) + (this.m11*this.m20*this.m32) + (this.m12*this.m21*this.m30) -
				(this.m10*this.m21*this.m32) - (this.m11*this.m22*this.m30) - (this.m12*this.m20*this.m31);
		final double m31 = (this.m00*this.m21*this.m32) + (this.m01*this.m22*this.m30) + (this.m02*this.m20*this.m31) -
				(this.m00*this.m22*this.m31) - (this.m01*this.m20*this.m32) - (this.m02*this.m21*this.m30);
		final double m32 = (this.m00*this.m12*this.m31) + (this.m01*this.m10*this.m32) + (this.m02*this.m11*this.m30) -
				(this.m00*this.m11*this.m32) - (this.m01*this.m12*this.m30) - (this.m02*this.m10*this.m31);
		final double m33 = (this.m00*this.m11*this.m22) + (this.m01*this.m12*this.m20) + (this.m02*this.m10*this.m21) -
				(this.m00*this.m12*this.m21) - (this.m01*this.m10*this.m22) - (this.m02*this.m11*this.m20);
		final double determinant = this.determinant();
		
		return new Matrix4d(m00, m01, m02, m03,
							m10, m11, m12, m13,
							m20, m21, m22, m23,
							m30, m31, m32, m33).scale(1/determinant);
	}
	
	public Matrix4d transpose() {
		final double m00 = this.m00;
		final double m10 = this.m01;
		final double m20 = this.m02;
		final double m30 = this.m03;
		final double m01 = this.m10;
		final double m11 = this.m11;
		final double m21 = this.m12;
		final double m31 = this.m13;
		final double m02 = this.m20;
		final double m12 = this.m21;
		final double m22 = this.m22;
		final double m32 = this.m23;
		final double m03 = this.m30;
		final double m13 = this.m31;
		final double m23 = this.m32;
		final double m33 = this.m33;
		
		return new Matrix4d(m00, m01, m02, m03,
							m10, m11, m12, m13,
							m20, m21, m22, m23,
							m30, m31, m32, m33);
	}

	public double determinant() {
		final double value = m03 * m12 * m21 * m30 - m02 * m13 * m21 * m30
				- m03 * m11 * m22 * m30 + m01 * m13 * m22 * m30 + m02 * m11
				* m23 * m30 - m01 * m12 * m23 * m30 - m03 * m12 * m20 * m31
				+ m02 * m13 * m20 * m31 + m03 * m10 * m22 * m31 - m00 * m13
				* m22 * m31 - m02 * m10 * m23 * m31 + m00 * m12 * m23 * m31
				+ m03 * m11 * m20 * m32 - m01 * m13 * m20 * m32 - m03 * m10
				* m21 * m32 + m00 * m13 * m21 * m32 + m01 * m10 * m23 * m32
				- m00 * m11 * m23 * m32 - m02 * m11 * m20 * m33 + m01 * m12
				* m20 * m33 + m02 * m10 * m21 * m33 - m00 * m12 * m21 * m33
				- m01 * m10 * m22 * m33 + m00 * m11 * m22 * m33;
		return value;
	}

	private static Matrix4d transformMatrix(final double dx, final double dy,
			final double dz) {
		return new Matrix4d(1, 0, 0, dx, 0, 1, 0, dy, 0, 0, 1, dz, 0, 0, 0, 1);
	}

	public static Matrix4d scaleMatrix(final double a, final double b,
			final double c) {
		return new Matrix4d(a, 0, 0, 0, 0, b, 0, 0, 0, 0, c, 0, 0, 0, 0, 1);
	}
	
	private Matrix4d scale(final double value) {
		return new Matrix4d(this.m00 * value, this.m01 * value, this.m02 * value, this.m03 * value,
							this.m10 * value, this.m11 * value, this.m12 * value, this.m13 * value,
							this.m20 * value, this.m21 * value, this.m22 * value, this.m23 * value,
							this.m30 * value, this.m31 * value, this.m32 * value, this.m33 * value);
	}

	private static Matrix4d rotateXMatrix(final double degrees) {
		return new Matrix4d(1, 0, 0, 0, 0, Math.cos(degrees),
				-Math.sin(degrees), 0, 0, Math.sin(degrees), Math.cos(degrees),
				0, 0, 0, 0, 1);
	}

	private static Matrix4d rotateYMatrix(final double degrees) {
		return new Matrix4d(Math.cos(degrees), 0, Math.sin(degrees), 0, 0, 1,
				0, 0, -Math.sin(degrees), 0, Math.cos(degrees), 0, 0, 0, 0, 1);
	}

	private static Matrix4d rotateZMatrix(final double degrees) {
		return new Matrix4d(Math.cos(degrees), -Math.sin(degrees), 0, 0,
				Math.sin(degrees), Math.cos(degrees), 0, 0, 0, 0, 1, 0, 0, 0,
				0, 1);
	}

	// These methods should not be used directly as long as you do not need a
	// new instance of the matrix.
	private static Matrix4d reflectXMatrix() {
		return new Matrix4d(-1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1);
	}

	private static Matrix4d reflectYMatrix() {
		return new Matrix4d(1, 0, 0, 0, 0, -1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1);
	}

	private static Matrix4d reflectZMatrix() {
		return new Matrix4d(1, 0, 0, 0, 0, 1, 0, 0, 0, 0, -1, 0, 0, 0, 0, 1);
	}

}
