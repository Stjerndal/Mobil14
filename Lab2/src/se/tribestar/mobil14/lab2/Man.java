package se.tribestar.mobil14.lab2;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

public class Man {

	private float posX, posY; // position
	private Drawable sprite; // a representation of the icon image

	public Man(float posX, float posY, Drawable sprite) {
		this.posX = posX;
		this.posY = posY;
		this.sprite = sprite;
	}

	public void setPosition(float posX, float posY) {
		this.posX = posX;
		this.posY = posY;
	}

	public float getPosX() {
		return posX;
	}

	public float getPosY() {
		return posY;
	}

	/**
	 * Move the icon (dX, dY) in pixels
	 */
	public void move(int dX, int dY) {
		posX += dX;
		posY += dY;
	}

	public Rect getIconBounds() {
		return new Rect((int) posX, (int) posY, (int) posX + sprite.getIntrinsicWidth(), (int) posY
				+ sprite.getIntrinsicHeight());
	}

	/**
	 * Paint this icon on the specified canvas
	 */
	public void draw(Canvas canvas) {
		// Where to draw the icon
		sprite.setBounds(this.getIconBounds());
		// Draw
		sprite.draw(canvas);
	}
}
