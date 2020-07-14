package com.e.captiancoin.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import java.util.ArrayList;
import java.util.Random;

public class CaptainCoin extends ApplicationAdapter {
	SpriteBatch batch;
	Texture background;
	Texture[] man;
	int manState = 0;
	int pause = 0;
	float gravity = 0.3f;
	float velocity = 0;
	int manY = 0;
	Rectangle manShape;
	int score = 0;
	BitmapFont scoreFont;
	int gameState = 0;
	BitmapFont gameOverFont;
	BitmapFont playAgainFont;
	BitmapFont startFont;
	Texture dizzy;

	//coin
	ArrayList<Integer> coinXs = new ArrayList<>();
	ArrayList<Integer> coinYs = new ArrayList<>();
	ArrayList<Rectangle> coinShape = new ArrayList<>();
	Texture coin;
	int coinCount;
	Random random;

	//bomb
	ArrayList<Integer> bombXs = new ArrayList<>();
	ArrayList<Integer> bombYs = new ArrayList<>();
	ArrayList<Rectangle> bombShape = new ArrayList<>();
	Texture bomb;
	int bombCount;


	@Override
	public void create () {
		batch = new SpriteBatch();
		background = new Texture("bg.png");
		man = new Texture[4];
		man[0] = new Texture("frame-1.png");
		man[1] = new Texture("frame-2.png");
		man[2] = new Texture("frame-3.png");
		man[3] = new Texture("frame-4.png");
		dizzy = new Texture("dizzy.png");

		//set default location for man
		manY = Gdx.graphics.getHeight() / 2;

		random = new Random();
		coin = new Texture("coin.png");
		bomb = new Texture("bomb.png");
		scoreFont = new BitmapFont();
		scoreFont.setColor(Color.WHITE);
		scoreFont.getData().setScale(10);
	}

	public void makeCoin(){
		float coinHeight = random.nextFloat() * Gdx.graphics.getHeight();
		coinYs.add((int)coinHeight);
		coinXs.add(Gdx.graphics.getWidth());
	}

	public void makeBomb(){
		float bombHeight = random.nextFloat() * Gdx.graphics.getHeight();
		bombYs.add((int)bombHeight);
		bombXs.add(Gdx.graphics.getWidth());
	}

	@Override
	public void render () {
		batch.begin();
		batch.draw(background,0,0,Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		if (gameState == 1){

			//bomb
			if( bombCount < 200){
				bombCount += score / 2 ;
			} else {
				bombCount = 0;
				makeBomb();
			}
			bombShape.clear();
			for (int i =0; i < bombXs.size(); i++){
				batch.draw(bomb, bombXs.get(i), bombYs.get(i));
				bombXs.set(i, bombXs.get(i) -6 );
				bombShape.add(new Rectangle(bombXs.get(i), bombYs.get(i), bomb.getWidth(), bomb.getHeight()));
			}

			//coin
			if(coinCount < 100){
				coinCount++;
			} else{
				coinCount = 0;
				makeCoin();
			}
			coinShape.clear();
			for (int i =0; i < coinXs.size(); i++){
				batch.draw(coin, coinXs.get(i), coinYs.get(i));
				coinXs.set(i, coinXs.get(i) -4 );
				coinShape.add(new Rectangle(coinXs.get(i), coinYs.get(i), coin.getWidth(), coin.getHeight()));
			}

			// set walking speed render
			if (pause < 8){
				pause++;
			} else {
				pause = 0;
				if (manState < 3 ){
					manState++;
				} else {
					manState = 0;
				}
			}// end walking speed

			//jump
			if (Gdx.input.justTouched()){
				velocity = -10;
			}

			//man
			velocity += gravity;
			manY -= velocity;
			if(manY <= 0){
				manY = 0;
			}



		} else if (gameState == 0) {
			//start game
			startFont = new BitmapFont();
			startFont.setColor(Color.RED);
			startFont.getData().setScale(10);
			startFont.draw(batch,"Tap to Play",(float) (Gdx.graphics.getWidth() / 6), Gdx.graphics.getHeight() / 3);


			if (Gdx.input.justTouched()) {
				score = 0;
				gameState = 1;

			}
		} else if ( gameState == 2 ){
			//gameOver
			if( Gdx.input.justTouched()) {
				manY = Gdx.graphics.getHeight() / 2;
				velocity = 0;
				coinXs.clear();
				coinYs.clear();
				coinShape.clear();
				coinCount = 0;
				bombXs.clear();
				bombYs.clear();
				bombShape.clear();
				bombCount = 0;
				score = 0;
				gameState = 1;
			}

		}

		if (gameState == 2 ){
			batch.draw(dizzy, (Gdx.graphics.getWidth() - dizzy.getWidth()) / 2, (Gdx.graphics.getHeight() - dizzy.getHeight()) / 2);
			gameOverFont = new BitmapFont();
			gameOverFont.setColor(Color.RED);
			gameOverFont.getData().setScale(10);
			gameOverFont.draw(batch, "Game Over", (float) (Gdx.graphics.getWidth() / 5.5), (float) (Gdx.graphics.getHeight() / 1.5));
			playAgainFont = new BitmapFont();
			playAgainFont.setColor(Color.RED);
			playAgainFont.getData().setScale(6);
			playAgainFont.draw(batch,"Tap to Play Again",(float) (Gdx.graphics.getWidth() / 6), Gdx.graphics.getHeight() / 3);

		} else {
			batch.draw(man[manState], Gdx.graphics.getWidth() / 3 - man[manState].getWidth() / 2, manY);
		}
		manShape = new Rectangle(Gdx.graphics.getWidth() / 3 - man[manState].getWidth()/2, manY, man[manState].getWidth(), man[manState].getHeight());

		//coin collision
		for(int i =0; i < coinShape.size(); i++){
			if(Intersector.overlaps(manShape, coinShape.get(i))){
				score++;
				coinShape.remove(i);
				coinXs.remove(i);
				coinYs.remove(i);
				break;
			}
		}

		//bomb collision
		for(int i =0; i < bombShape.size(); i++){
			if(Intersector.overlaps(manShape, bombShape.get(i))){
			Gdx.app.log("BOMB", "Ye DED!!");
			gameState = 2;
			}
		}

		scoreFont.draw(batch, String.valueOf(score), 100,200);
		batch.end();
	} // end render
	
	@Override
	public void dispose () {
		batch.dispose();
	}
}
