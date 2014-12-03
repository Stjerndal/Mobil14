package se.tribestar.mobil14.lab2;

import android.graphics.Rect;

public class NodeRectCalculator {

	private static final int[] realIndices = { 0, 3, 6, 8, 10, 12, 16, 17, 18, 21, 22, 23, 25, 26, 27, 30, 31, 32, 36,
			38, 40, 42, 45, 48 }; // awesome hardcodning

	/**
	 * Generate nodes, and gives them the appropriate pixel Rect bounds. A
	 * square board is assumed.
	 * 
	 * @param x
	 *            the x pixel coordinate of the left side of the board
	 * @param y
	 *            the y pixel coordinate of the top side the board
	 * @param sideLength
	 *            the sideLength of the square board
	 * @return an array of nodes with correct rect bounds.
	 */
	public static Node[] generateNodes(int x, int y, int sideLength) {
		Node[] nodes = new Node[24];
		float rectSize = sideLength / 7.0f;
		int indx = 0; // 0-48
		int nodeIndx = 0; // 0-23
		// loop through every one of the 49 possible node positions
		for (int i = 0; i < 7; ++i) {
			float top = y + i * rectSize;
			float bottom = top + rectSize;
			for (int j = 0; j < 7; ++j, ++indx) {
				// only add the node if it is one of the 24 valid positions.
				if (realIndices[nodeIndx] == indx) {
					float left = x + j * rectSize;
					float right = left + rectSize;
					Rect rect = new Rect((int) left, (int) top, (int) right, (int) bottom);
					nodes[nodeIndx] = new Node(rect);
					nodeIndx++;
				}
			}
		}
		return nodes;
	}
}
