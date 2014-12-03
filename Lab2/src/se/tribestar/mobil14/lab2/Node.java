package se.tribestar.mobil14.lab2;

import android.graphics.Rect;

public class Node {

	private Sprite sprite;
	private boolean hasPlayer;

	private int playerColor;

	private Rect bounds;

	private NMMRules rules = new NMMRules();

	public Node(Rect rect) {
		this.bounds = rect;
		this.sprite = null;
		hasPlayer = false;
		playerColor = rules.EMPTY_SPACE;
	}

	public int getPlayerColor() {
		return playerColor;
	}

	public boolean setPlayer(Sprite sprite, int color) {
		if (!hasPlayer) {
			this.sprite = sprite;
			this.playerColor = color;
			hasPlayer = true;
			return hasPlayer;
		} else
			return false;
	}

	public Sprite getSprite() {
		return sprite;
	}

	public boolean hasPlayer() {
		return hasPlayer;
	}

	public boolean removePlayer() {
		sprite = null;
		playerColor = rules.EMPTY_SPACE;
		hasPlayer = false;
		return hasPlayer;
	}

	public boolean isWithinBounds(int x, int y) {
		if (bounds.contains(x, y))
			return true;
		else
			return false;
	}

	public Rect getRect() {
		return bounds;
	}
}
