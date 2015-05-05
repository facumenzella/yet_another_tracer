package ar.edu.itba.it.cg.yart.transforms;

public class Matrix4d {

	public double m00, m01, m02, m03;
	public double m10, m11, m12, m13;
	public double m20, m21, m22, m23;
	public double m30, m31, m32, m33;
	
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
	
	public Matrix4d(final double m00, final double m01, final double m02, final double m03, 
			final double m10, final double m11, final double m12, final double m13, 
			final double m20, final double m21, final double m22, final double m23, 
			final double m30, final double m31, final double m32, final double m33) {
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
		
	private void rightMultiply(final Matrix4d matrix) {
		final double m00 = (this.m00 * matrix.m00) + (this.m01 * matrix.m10) + (this.m02 * matrix.m20) + (this.m03 * matrix.m30);
		final double m01 = (this.m00 * matrix.m01) + (this.m01 * matrix.m11) + (this.m02 * matrix.m21) + (this.m03 * matrix.m31);
		final double m02 = (this.m00 * matrix.m02) + (this.m01 * matrix.m12) + (this.m02 * matrix.m22) + (this.m03 * matrix.m32);
		final double m03 = (this.m00 * matrix.m03) + (this.m01 * matrix.m13) + (this.m02 * matrix.m23) + (this.m03 * matrix.m33);

		final double m10 = (this.m10 * matrix.m00) + (this.m11 * matrix.m10) + (this.m12 * matrix.m20) + (this.m13 * matrix.m30);
		final double m11 = (this.m10 * matrix.m01) + (this.m11 * matrix.m11) + (this.m12 * matrix.m21) + (this.m13 * matrix.m31);
		final double m12 = (this.m10 * matrix.m02) + (this.m11 * matrix.m12) + (this.m12 * matrix.m22) + (this.m13 * matrix.m32);
		final double m13 = (this.m10 * matrix.m03) + (this.m11 * matrix.m13) + (this.m12 * matrix.m23) + (this.m13 * matrix.m33);
		
		final double m20 = (this.m20 * matrix.m00) + (this.m21 * matrix.m10) + (this.m22 * matrix.m20) + (this.m23 * matrix.m30);
		final double m21 = (this.m20 * matrix.m01) + (this.m21 * matrix.m11) + (this.m22 * matrix.m21) + (this.m23 * matrix.m31);
		final double m22 = (this.m20 * matrix.m02) + (this.m21 * matrix.m12) + (this.m22 * matrix.m22) + (this.m23 * matrix.m32);
		final double m23 = (this.m20 * matrix.m03) + (this.m21 * matrix.m13) + (this.m22 * matrix.m23) + (this.m23 * matrix.m33);
		
		final double m30 = (this.m30 * matrix.m00) + (this.m31 * matrix.m10) + (this.m32 * matrix.m20) + (this.m33 * matrix.m30);
		final double m31 = (this.m30 * matrix.m01) + (this.m31 * matrix.m11) + (this.m32 * matrix.m21) + (this.m33 * matrix.m31);
		final double m32 = (this.m30 * matrix.m02) + (this.m31 * matrix.m12) + (this.m32 * matrix.m22) + (this.m33 * matrix.m32);
		final double m33 = (this.m30 * matrix.m03) + (this.m31 * matrix.m13) + (this.m32 * matrix.m23) + (this.m33 * matrix.m33);
		
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
	
	private void leftMultiply(final Matrix4d matrix) {
		matrix.rightMultiply(this);
	}
	
	public void transform(final double dx, final double dy, final double dz) {
		final Matrix4d transformMatrix = Matrix4d.transformMatrix(dx, dy, dz);
		this.rightMultiply(transformMatrix);
	}
	
	public void scale(final double a, final double b, final double c) {
		final Matrix4d scaleMatrix = Matrix4d.scaleMatrix(a, b, c);
		this.rightMultiply(scaleMatrix);
	}
	
	public void rotateX(final double degrees) {
		final Matrix4d rotateXMatrix = Matrix4d.rotateXMatrix(degrees);
		this.rightMultiply(rotateXMatrix);
	}
	
	public void rotateY(final double degrees) {
		final Matrix4d rotateYMatrix = Matrix4d.rotateYMatrix(degrees);
		this.rightMultiply(rotateYMatrix);
	}
	
	public void rotateZ(final double degrees) {
		final Matrix4d rotateZMatrix = Matrix4d.rotateZMatrix(degrees);
		this.rightMultiply(rotateZMatrix);
	}
	
	public void reflectX() {
		this.rightMultiply(reflectXMatrix);
	}
	
	public void reflectY() {
		this.rightMultiply(reflectYMatrix);
	}
	
	public void reflectZ() {
		this.rightMultiply(reflectZMatrix);
	}
	
	public void inverse() {
		this.m00 = this.m00; this.m01 = this.m10; this.m02 = this.m20; this.m03 = this.m30;
		this.m01 = this.m10; this.m11 = this.m11; this.m12 = this.m21; this.m13 = this.m31;
		this.m02 = this.m20; this.m21 = this.m12; this.m22 = this.m22; this.m23 = this.m32;
		this.m03 = this.m30; this.m31 = this.m13; this.m32 = this.m23; this.m33 = this.m33;
	}
	
	private static Matrix4d transformMatrix(final double dx, final double dy, final double dz) {
		return new Matrix4d(1, 0, 0, dx,
				0, 1, 0, dy,
				0, 0, 1, dz,
				0, 0, 0, 1);
	}
	
	private static Matrix4d scaleMatrix(final double a, final double b, final double c) {
		return new Matrix4d(a, 0, 0, 0,
				0, b, 0, 0,
				0, 0, c, 0,
				0, 0, 0, 1);
	}
	
	private static Matrix4d rotateXMatrix(final double degrees) {
		return new Matrix4d(1, 0, 0, 0,
				0, Math.cos(degrees), -Math.sin(degrees), 0,
				0, Math.sin(degrees), Math.cos(degrees), 0,
				0, 0, 0, 1);
	}
	
	private static Matrix4d rotateYMatrix(final double degrees) {
		return new Matrix4d(Math.cos(degrees), 0, Math.sin(degrees), 0,
				0, 1, 0, 0,
				-Math.sin(degrees), 0, Math.cos(degrees), 0,
				0, 0, 0, 1);
	}
	
	private static Matrix4d rotateZMatrix(final double degrees) {
		return new Matrix4d(Math.cos(degrees), -Math.sin(degrees), 0, 0,
				Math.sin(degrees), Math.cos(degrees), 0, 0,
				0, 0, 1, 0,
				0, 0, 0, 1);
	}
	
	// These methods should not be used directly as long as you do not need a new instance of the matrix.
	
	private static Matrix4d reflectXMatrix() {
		return new Matrix4d(-1, 0, 0, 0,
				0, 1, 0, 0,
				0, 0, 1, 0,
				0, 0, 0, 1);
	}
	
	private static Matrix4d reflectYMatrix() {
		return new Matrix4d(1, 0, 0, 0,
				0, -1, 0, 0,
				0, 0, 1, 0,
				0, 0, 0, 1);
	}
	
	private static Matrix4d reflectZMatrix() {
		return new Matrix4d(1, 0, 0, 0,
				0, 1, 0, 0,
				0, 0, -1, 0,
				0, 0, 0, 1);
	}
	
}
