package se.tribestar.mobil14.lab2;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

public class Sprite {
	private int posX, posY; // position
	private Drawable sprite; // a representation of the icon image
	private int screenWidth, screenHeight; // screen sizes
	private int width, height;

	public Sprite(int posX, int posY, Drawable sprite, int screenWidth, int screenHeight) {
		this.posX = posX;
		this.posY = posY;
		this.sprite = sprite;
		this.screenWidth = screenWidth;
		this.screenHeight = screenHeight;
		this.width = sprite.getIntrinsicWidth();
		this.height = sprite.getIntrinsicHeight();
	}

	public Sprite(int posX, int posY, Drawable sprite, int screenWidth, int screenHeight, float partOfScreenWidth) {
		this.posX = posX;
		this.posY = posY;
		this.sprite = sprite;
		this.screenWidth = screenWidth;
		this.screenHeight = screenHeight;
		setSize(partOfScreenWidth);
	}

	public void setPosition(int posX, int posY) {
		this.posX = posX;
		this.posY = posY;
	}

	public void setSize(float partOfScreenWidth) {
		this.width = (int) (screenWidth * partOfScreenWidth);
		this.height = (int) (width * sprite.getIntrinsicWidth() / sprite.getIntrinsicHeight());
	}

	public void setPositionCenter() {
		setPositionCenterX();
		setPositionCenterY();
	}

	public void setPositionCenterY() {
		this.posY = screenHeight / 2 - height / 2;
	}

	public void setPositionCenterX() {
		this.posX = screenWidth / 2 - width / 2;
	}

	public int getPosX() {
		return posX;
	}

	public int getPosY() {
		return posY;
	}

	public int getWidth() {
		return this.width;
	}

	public int getHeight() {
		return this.height;
	}

	/**
	 * Move the icon (dX, dY) in pixels
	 */
	public void move(int dX, int dY) {
		posX += dX;
		posY += dY;
	}

	public Rect getIconBounds() {
		return new Rect(posX, posY, posX + width, posY + height);
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
