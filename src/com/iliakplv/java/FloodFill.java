package com.iliakplv.java;

public class FloodFill {

	private static final int SIZE_X = 10;
	private static final int SIZE_Y = 10;

	private int[][] bitmab = new int[SIZE_Y][SIZE_X];


	public void floodFill(int targetColor, int newColor, int x, int y) {
		if (targetColor == newColor) return;
		if (x < 0 || x >= SIZE_X || y < 0 || y >= SIZE_X) return;
		if (bitmab[x][y] != targetColor) return;

		bitmab[x][y] = newColor;

		floodFill(targetColor, newColor, x - 1, y);
		floodFill(targetColor, newColor, x + 1, y);
		floodFill(targetColor, newColor, x, y - 1);
		floodFill(targetColor, newColor, x, y + 1);
	}


	public void init() {

		if (SIZE_X == SIZE_Y) {
			for (int x = 0; x < SIZE_X; x++) {
				bitmab[x][x] = 1;
			}
			for (int y = 0; y < SIZE_Y; y++) {
				bitmab[y][SIZE_X - y - 1] = 1;
			}
		}

	}


	public void print() {
		System.out.println();
		for (int y = 0; y < SIZE_Y; y++) {
			for (int x = 0; x < SIZE_X; x++) {
				System.out.print((bitmab[y][x] != 0 ? bitmab[y][x] : ".") + " ");
			}
			System.out.println();
		}
		System.out.println();
	}


	public static void main(String[] args) {
		FloodFill ff = new FloodFill();
		ff.init();
		ff.print();
		ff.floodFill(0, 8, 4, 3);
		ff.print();
	}

}
