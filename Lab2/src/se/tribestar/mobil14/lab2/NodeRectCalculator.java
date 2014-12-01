package se.tribestar.mobil14.lab2;

import java.util.Arrays;

import android.graphics.Rect;

public class NodeRectCalculator {

	private static final int[] realIndices = { 0, 3, 6, 8, 10, 12, 16, 17, 18, 21, 22, 23, 25, 26, 27, 30, 31, 32, 36,
			38, 40, 42, 45, 48 }; // awesome hardcodning

	/**
	 * Generate nodes.
	 * 
	 * @param x
	 *            the x pixel coordinate of the left side of the board
	 * @param y
	 *            the y pixel coordinate of the top side the board
	 * @param width
	 *            the width of the board
	 * @return
	 */
	public static Node[] generateNodes(int x, int y, int width) {
		Node[] nodes = new Node[24];
		int rectSize = width / 7;
		int indx = 0; // 0-48
		int nodeIndx = 0; // 0-23
		for (int i = 0; i < 7; ++i) {
			int left = x + i * rectSize;
			int right = left + rectSize;
			for (int j = 0; j < 7; ++j, ++indx) {
				if (Arrays.asList(realIndices).contains(indx)) {
					int top = y + j * rectSize;
					int bottom = top + rectSize;
					Rect rect = new Rect(left, top, right, bottom);
					nodes[nodeIndx] = new Node(rect);
				}
			}
		}
		return nodes;
	}
}
