package se.tribestar.mobil14.sensorbloom;

import java.io.IOException;

import android.content.Context;
import android.graphics.drawable.Drawable;

public class Flower {

	private Sprite[] leftSprites;
	private Sprite[] rightSprites;
	private static final int numFlowers = 10;

	public Flower(Context context, int xRes, int yRes) {
		leftSprites = new Sprite[numFlowers];
		rightSprites = new Sprite[numFlowers];
		loadFlowerSprites(context, xRes, yRes, false); // load left side flowers
		loadFlowerSprites(context, xRes, yRes, true); // load right side flowers
	}

	public Sprite getSprite(float accelX) {
		// V.log("AccelX: " + (-accelX / 10));
		boolean right = accelX <= 0f;
		int tilt = (int) Math.round(Math.abs(accelX));
		if (tilt >= numFlowers)
			tilt = numFlowers - 1;
		if (right)
			return rightSprites[tilt];
		else
			return leftSprites[tilt];
	}

	private void loadFlowerSprites(Context context, int xRes, int yRes, boolean right) {
		try {
			for (int i = 0; i < numFlowers; i++) {
				V.log("Getting pic nr " + i);
				Drawable drawable = fetchDrawable(i, right, context);
				Sprite sprite = new Sprite(0, 0, drawable, xRes, yRes, 1f);
				sprite.setPositionCenter();
				if (right)
					rightSprites[i] = sprite;
				else
					leftSprites[i] = sprite;
			}
		} catch (IOException e) {
			V.log("Couldn't find flower pictures.");
		}
	}

	private Drawable fetchDrawable(int i, boolean right, Context context) throws IOException {
		String filePath = "flower_" + (right ? "right" : "left") + "0" + i + ".png";
		return Drawable.createFromStream(context.getAssets().open(filePath), null);

	}

}
