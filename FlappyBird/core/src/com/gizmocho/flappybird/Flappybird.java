package com.gizmocho.flappybird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import java.util.Random;

public class Flappybird extends ApplicationAdapter {
    SpriteBatch batch;
    Texture background;
    // ShapeRenderer shapeRenderer;

    Texture[] birds;

    int flapState = 0;
    float birdY = 0;
    float velocity = 0;
    Circle birdCircle ;
    int score = 0;
    int scoreTube = 0;
    BitmapFont font;
    int gamestate = 0;
    float gravity = 2;


    Texture topTube;
    Texture botTube;
    Texture gameover;

    float gap = 400;
    float maxTubeOffset =0;
    Random randomGenerator;

    float tubeVelocity = 4;
    int numOfTubes = 4;
    float[] tubeX = new float[numOfTubes];
    float[]  tubeOffset = new float [numOfTubes];

    float distanceBetweenTubes;
    Rectangle[] topTubeRectangles;
    Rectangle[] botTubeRectangles;


    @Override
    public void create () {

        batch = new SpriteBatch();
        background = new Texture("bg.png");
        gameover = new Texture("gameover.png");
        birdCircle = new Circle();
        //shapeRenderer = new ShapeRenderer();
        font  = new BitmapFont();
        font.setColor(Color.WHITE);
        font.getData().setScale(10);
        birds = new Texture[2];
        birds[0] = new Texture("bird.png");
        birds[1] = new Texture("bird2.png");

        topTube = new Texture("toptube.png");
        botTube = new Texture("bottomtube.png");
        maxTubeOffset = Gdx.graphics.getHeight() /2 -gap /2 -100;
        randomGenerator = new Random();
        distanceBetweenTubes = Gdx.graphics.getWidth()* 3/4;
        topTubeRectangles = new Rectangle[numOfTubes];
        botTubeRectangles = new Rectangle[numOfTubes];
        startGame();

    }

    public void startGame(){
        birdY = Gdx.graphics.getHeight()/2-birds[0].getHeight()/2;
        for(int i =0; i<numOfTubes;i++){
            tubeX[i] = Gdx.graphics.getWidth()/2 - topTube.getWidth() /2 + Gdx.graphics.getWidth() + i * distanceBetweenTubes;
            topTubeRectangles[i] = new Rectangle();
            botTubeRectangles[i] = new Rectangle();
        }
    }

    @Override
    public void render () {

        batch.begin();
        batch.draw(background,0,0,Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
        if(Gdx.input.justTouched()){
            Gdx.app.log("Tapped","flapped");  //this is just a log you don't really need this
           // gamestate = 1;
        }
        for(int i =0; i<numOfTubes;i++){
            if(tubeX[i] < -topTube.getWidth()) {
                tubeX[i] += numOfTubes * distanceBetweenTubes;
                tubeOffset[i] = (randomGenerator.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - gap - 300);
            }else{
                tubeX[i] = tubeX[i] - tubeVelocity;

            }

            tubeX[i] = tubeX[i]- tubeVelocity;
            batch.draw(topTube, tubeX[i],Gdx.graphics.getHeight()/2 + gap/ 2 + tubeOffset[i]); //+ tubeOffset
            batch.draw(botTube, tubeX[i],Gdx.graphics.getHeight()/2 - gap/ 2 - botTube.getHeight() + tubeOffset[i]);

            topTubeRectangles[i] = new Rectangle(tubeX[i],Gdx.graphics.getHeight()/2 + gap/ 2 + tubeOffset[i] , topTube.getWidth(),topTube.getHeight());
            botTubeRectangles[i] = new Rectangle(tubeX[i],Gdx.graphics.getHeight()/2 - gap/ 2 - botTube.getHeight() + tubeOffset[i],botTube.getWidth(),botTube.getHeight());
        }
          if (gamestate == 0){
            if(Gdx.input.justTouched()){
                gamestate =1;
           }
        }

        if (gamestate == 1){

            if(tubeX[scoreTube] < Gdx.graphics.getWidth() /2 ){
                score++;
                Gdx.app.log("score!" , String.valueOf(score));
                if(scoreTube <numOfTubes-1){
                    scoreTube++;
                }else{
                    scoreTube = 0 ;
                }
            }

            if(Gdx.input.justTouched()){
                velocity = -30;

            }
            if(birdY >0 ){

                velocity = velocity + gravity;
                birdY -= velocity;
            } else{
                gamestate=2;
            }

        }else if (gamestate == 0){

            if(Gdx.input.justTouched()){
                gamestate = 1;
            }
        }else if (gamestate ==2){
            batch.draw(gameover, Gdx.graphics.getWidth() /2 - gameover.getWidth()/2, Gdx.graphics.getHeight() /2 - gameover.getHeight()/2);
            if(Gdx.input.justTouched()){
                gamestate = 1;
                startGame();
                score =0;
                scoreTube =0;
                velocity= 0;
            }
        }


        if (flapState == 0){
            flapState = 1;
        }else{
            flapState = 0;
        }


        batch.draw(birds[flapState],Gdx.graphics.getWidth()/2 - birds[flapState].getWidth()/2,birdY);// the reason why is based on the draw dimensions just like droaeing in opengl
        font.draw(batch,String.valueOf(score), Gdx.graphics.getWidth() /2 -50 ,Gdx.graphics.getHeight() - 100 );
        batch.end();
        //imagine a square grid the position in android is based off of the bottom left so, in order to get the center of the screen
        //you need to half the square
        //figure out how to center it more 8/13/17
        //change the flap speed, research the animations
        //track why not double
        birdCircle.set(Gdx.graphics.getWidth() / 2, birdY+birds[flapState].getHeight() /2,birds[flapState].getWidth() /2 );
        // shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        //shapeRenderer.setColor(Color.RED);
        //shapeRenderer.circle(birdCircle.x,birdCircle.y,birdCircle.radius);
        for(int i =0; i<numOfTubes; i++){
            //  shapeRenderer.rect(tubeX[i],Gdx.graphics.getHeight()/2 + gap/ 2 + tubeOffset[i] , topTube.getWidth(),topTube.getHeight());
            //shapeRenderer.rect(tubeX[i],Gdx.graphics.getHeight()/2 - gap/ 2 - botTube.getHeight() + tubeOffset[i],botTube.getWidth(),botTube.getHeight());

            if(Intersector.overlaps(birdCircle,topTubeRectangles[i]) || Intersector.overlaps(birdCircle,botTubeRectangles[i])){
                Gdx.app.log("Collision", "brooooo");
                gamestate =2;
            }
        }
        // shapeRenderer.end();
    }

}
