package se.tribestar.mobil14.sensorbloom;

import java.io.IOException;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

public class Flower {

	private Sprite[][] sprites;
	private static final int numFlowers = 10;
	private static final int numExplFrames = 19;

	private static final int numTypes = 5;

	private static final int TYPE_LEFT = 0;
	private static final int TYPE_RIGHT = 1;
	private static final int TYPE_LEFTBLADE = 2;
	private static final int TYPE_RIGHTBLADE = 3;
	private static final int TYPE_EXPLOSION = 4;

	private static final String[] filenames = { "flower_left", "flower_right", "blades_left", "blades_right",
			"explosion" };

	private Context context;
	private int xRes, yRes;

	private boolean exploded = false; // Have we exploded?
	private float explFrameTime = 0f;

	public Flower(Context context, int xRes, int yRes) {
		this.context = context;
		this.xRes = xRes;
		this.yRes = yRes;

		// Set up sprite arrays.
		sprites = new Sprite[numTypes][];
		for (int i = 0; i < numTypes - 1; i++)
			sprites[i] = new Sprite[numFlowers];
		sprites[TYPE_EXPLOSION] = new Sprite[numExplFrames];

		loadSprites();
	}

	public void draw(float accelX, Canvas canvas, float deltaTime) {
		int baseType = (accelX <= 0f) ? TYPE_RIGHT : TYPE_LEFT;
		int bladeType = (accelX <= 0f) ? TYPE_RIGHTBLADE : TYPE_LEFTBLADE;
		int tilt = (int) Math.round(Math.abs(accelX));
		if (tilt >= numFlowers)
			tilt = numFlowers - 1;

		sprites[baseType][tilt].draw(canvas);
		if (!exploded)
			sprites[bladeType][tilt].draw(canvas);
		else {
			explFrameTime += deltaTime; // <---Animation
			int frame = (int) Math.round(explFrameTime / (1f / 30f));// 30fps
			frame = frame >= numExplFrames ? numExplFrames - 1 : frame;
			sprites[TYPE_EXPLOSION][frame].draw(canvas);
		}

	}

	public void explode() {
		V.log("EXPLODE!");
		exploded = true;
	}

	private void loadSprites() {
		try {
			for (int type = 0; type < numTypes; type++) {
				Sprite[] spriteArray = sprites[type];
				for (int i = 0; i < spriteArray.length; i++) {
					Drawable drawable = fetchDrawable(type, i);
					Sprite sprite = new Sprite(0, 0, drawable, xRes, yRes, 1f);
					sprite.setPositionCenter();
					spriteArray[i] = sprite;
				}

			}

		} catch (IOException e) {
			V.log("Couldn't find flower pictures.");
		}
	}

	private Drawable fetchDrawable(int type, int i) throws IOException {
		String filePath = getFullFileName(type, i);
		return Drawable.createFromStream(context.getAssets().open(filePath), null);

	}

	private String getFullFileName(int type, int index) {
		return filenames[type] + (index < 10 ? "0" : "") + index + ".png";
	}

}
