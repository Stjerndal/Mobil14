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
		playerColor = NMMRules.EMPTY_SPACE;
	}

	public int getPlayerColor() {
		return playerColor;
	}

	public boolean setPlayer(int color) {
		if (color == NMMRules.EMPTY_SPACE) {
			hasPlayer = false;
			setSprite(color);
			return true;
		}

		if (!hasPlayer) {
			V.log("Setting player " + color + ", does not have one");
			setSprite(color);
			this.playerColor = color;
			hasPlayer = true;
			return hasPlayer;
		} else {
			V.log("Tried setting player, but have one already: " + playerColor);
			return false;
		}

	}

	private void setSprite(int color) {
		if (color == NMMRules.EMPTY_SPACE)
			sprite = null;
		else {
			sprite = new Sprite(bounds.left, bounds.top, color == NMMRules.BLACK_MOVES
					|| color == NMMRules.BLACK_MARKER ? NMMView.blackMarker : NMMView.whiteMarker,
					NMMView.X_RESOLUTION, NMMView.Y_RESOLUTION, 0.12f);
		}
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
