package se.tribestar.mobil14.lab2;

import android.graphics.Rect;

/**
 * A position on the NMM Board.
 * 
 */
public class Node {

	private Sprite sprite; // visual documentation
	private boolean hasPlayer; // is occupied?

	private int playerColor; // occupied by what color?

	private Rect bounds; // pixel bounds

	// New node using rect bounds
	public Node(Rect rect) {
		this.bounds = rect;
		this.sprite = null;
		hasPlayer = false;
		playerColor = NMMRules.EMPTY_SPACE;
	}

	public int getPlayerColor() {
		return playerColor;
	}

	// mark the node by one player (or as empty)
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

	// Set the sprite to be either empty or as a gamepiece
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
		playerColor = NMMRules.EMPTY_SPACE;
		hasPlayer = false;
		return hasPlayer;
	}

	// check if two coordinates are within the bounds of the node
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
