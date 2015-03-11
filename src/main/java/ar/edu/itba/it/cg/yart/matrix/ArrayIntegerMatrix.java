package ar.edu.itba.it.cg.yart.matrix;

public final class ArrayIntegerMatrix {

	public final int[] matrix;
	private final int cols;
	private final int rows;
	
	public ArrayIntegerMatrix(final int rows, final int cols) {
		this.cols = cols;
		this.rows = rows;
		this.matrix = new int[rows * cols];
	}
	
	public void put(final int row, final int col, final int value) {
		final int index =  (row * cols) + col;
		this.matrix[index] = value;
	}
	
	public int get(final int row, final int col) {
		final int index = (row * cols) + col;
		return this.matrix[index];
	}
	
	public int cols() {
		return this.cols;
	}
	
	public int rows() {
		return this.rows;
	}
	
	public int[] result() {
		return this.matrix;
	}
}
