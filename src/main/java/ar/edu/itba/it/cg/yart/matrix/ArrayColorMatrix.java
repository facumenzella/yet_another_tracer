package ar.edu.itba.it.cg.yart.matrix;

import ar.edu.itba.it.cg.yart.color.Color;

public final class ArrayColorMatrix {

	public final Color[] matrix;
	public final int cols;
	public final int rows;
	
	public ArrayColorMatrix(final int cols, final int rows) {
		this.cols = cols;
		this.rows = rows;
		this.matrix = new Color[rows * cols];

		for (int i = 0; i < matrix.length; i++) {
			matrix[i] = new Color(Color.BLACK);
		}
	}

	public void put(final int index, final Color value) {
		this.matrix[index].set(value);
	}
	
	public void put(final int col, final int row, final Color value) {
		final int index =  (row * cols) + col;
		this.matrix[index].set(value);
	}

	public Color get(final int index) {
		return this.matrix[index];
	}

	public Color get(final int col, final int row) {
		final int index = (row * cols) + col;
		return this.matrix[index];
	}
	
	public int cols() {
		return this.cols;
	}
	
	public int rows() {
		return this.rows;
	}
}
