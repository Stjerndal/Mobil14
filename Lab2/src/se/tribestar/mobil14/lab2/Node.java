package se.tribestar.mobil14.lab2;

import android.graphics.Rect;

public class Node {

	private int screenWidth, screenHeight; // screen sizes
	private Sprite sprite;
	private boolean hasSprite;

	private Rect bounds;

	private int width, height;

	public Node(Rect rect) {
		this.bounds = rect;
		this.sprite = null;
		hasSprite = false;
	}

	public boolean setSprite(Sprite sprite) {
		if (!hasSprite) {
			this.sprite = sprite;
			hasSprite = true;
			return hasSprite;
		} else
			return false;
	}

	public Sprite getSprite() {
		return sprite;
	}

	public boolean hasSprite() {
		return hasSprite;
	}

	public boolean isWithinBounds(int x, int y) {
		if (bounds.contains(x, y))
			return true;
		else
			return false;
	}
}
