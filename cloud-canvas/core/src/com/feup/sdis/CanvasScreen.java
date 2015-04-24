package com.feup.sdis;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector3;

public class CanvasScreen implements Screen {

	// TODO change this
	private int CANVAS_WIDTH = 400;
	private int CANVAS_HEIGHT = 400;

	private final CloudCanvas game;

	private OrthographicCamera camera;

	private Texture dropImage;
	private Texture bucketImage;

	private Sound dropSound;
	private Music rainMusic;

	Pixmap pixmap;
	Texture texture;

	private boolean touching;
	private Vector3 lastTouchPos, touchPos;

	private int viewportWidth, viewportHeight;

	public CanvasScreen(final CloudCanvas game) {
		this.game = game;

		viewportWidth = Gdx.graphics.getWidth();
		viewportHeight = Gdx.graphics.getHeight();

		// create the camera and the SpriteBatch
		camera = new OrthographicCamera();
		camera.setToOrtho(false, viewportWidth, viewportHeight);

		dropImage = new Texture(Gdx.files.internal("droplet.png"));
		bucketImage = new Texture(Gdx.files.internal("bucket.png"));

		dropSound = Gdx.audio.newSound(Gdx.files.internal("drop.wav"));
		rainMusic = Gdx.audio.newMusic(Gdx.files.internal("rain.mp3"));
		rainMusic.setLooping(true);

		pixmap = new Pixmap(CANVAS_WIDTH, CANVAS_HEIGHT, Format.RGBA8888);
		pixmap.setColor(1, 1, 1, 1);
		pixmap.fillRectangle(0, 0, CANVAS_WIDTH, CANVAS_HEIGHT);

		texture = new Texture(pixmap, true);

		touching = false;
		touchPos = new Vector3();
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0.7f, 0.7f, 0.7f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		// tell the camera to update its matrices.
		camera.update();

		// tell the SpriteBatch to render in the
		// coordinate system specified by the camera.
		game.spriteBatch.setProjectionMatrix(camera.combined);

		game.spriteBatch.begin();
		game.spriteBatch.draw(texture, 0, 0);
		game.spriteBatch.end();

		if (touching && touchPos != lastTouchPos) {
			game.shapeRenderer.begin(ShapeType.Filled);

			pixmap.setColor(0, 0, 0, 1);
			pixmap.drawLine((int) lastTouchPos.x, (int) lastTouchPos.y,
					(int) touchPos.x, (int) touchPos.y);
			texture.draw(pixmap, 0, 0);

			game.shapeRenderer.end();
		}

		// process user input
		if (Gdx.input.isTouched()) {
			touching = true;

			if (lastTouchPos != null)
				lastTouchPos.set(touchPos);

			touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);

			if (lastTouchPos == null)
				lastTouchPos = new Vector3(touchPos);

			// camera.unproject(lastTouchPos);
			// camera.unproject(touchPos);
		} else {
			touching = false;

			lastTouchPos = null;
		}

		int temp = 4;

		if (Gdx.input.isKeyPressed(Keys.E)) {
			viewportWidth -= temp;
			viewportHeight -= temp;
		}

		if (Gdx.input.isKeyPressed(Keys.F)) {
			viewportWidth += temp;
			viewportHeight += temp;
		}

		camera.setToOrtho(false, viewportWidth, viewportHeight);
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void show() {
		// start the playback of the background music
		// when the screen is shown
		rainMusic.play();
	}

	@Override
	public void hide() {
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void dispose() {
		dropImage.dispose();
		bucketImage.dispose();
		dropSound.dispose();
		rainMusic.dispose();
	}

}