//Cobey Kromer
//8/24/21
//Used https://www.youtube.com/watch?v=VmChebZcb2U&t=601s as a tool(did not just copy/paste)

package application;
	
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;


public class Main extends Application {
	
	//fields
	static int speed = 5; 
	static int foodcolor = 0; 
	static int width = 20; //20 corners wide
	static int height = 20; //20 corners high
	static int foodX = 0; 
	static int foodY = 0; 
	static int cornerSize = 25; 
	static List<Corner> snake = new ArrayList<>(); 
	static Dir direction = Dir.left; 
	static boolean gameOver = false; 
	static Random rand = new Random(); 
	
	//inner enum for the directions
	public enum Dir
	{
		left, right, up, down
	}
	
	//inner class for the parts of the snake, its the coordinates of the upper corner
	public static class Corner
	{
		int x; 
		int y; 
		
		public Corner(int x, int y)
		{
			this.x = x; 
			this.y = y; 
		}
	}
	
	
	@Override
	public void start(Stage primaryStage) {
		try {
			//place the first food
			newFood();
			//root pane
			VBox root = new VBox();
			//canvas is something that can display graphics
			Canvas c = new Canvas(width * cornerSize, height * cornerSize); 
			//graphics context used to issue draw calls to the canvas
			GraphicsContext gc = c.getGraphicsContext2D(); 
			//add the canvas to the root pane
			root.getChildren().add(c); 
			
			//Animation timer is being used instead of timeline becasue the snake moves around
			//It gets called each frame(handle method)
			new AnimationTimer()
			{
				long lastTick = 0;

				
				public void handle(long now) 
				{
					//first frame
					if(lastTick == 0)
					{
						lastTick = now;
						tick(gc);
						return;
					}
				
					//as the speed increases, the number that now-lasttick needs to be greater than decreases,
					//thus having the snake move faster
					if(now - lastTick > 1000000000 / speed)
					{
						lastTick = now; 
						tick(gc);
					}
				}
			}.start(); 
						
			
			Scene scene = new Scene(root,width * cornerSize,height * cornerSize);
			
			//control
			scene.addEventFilter(KeyEvent.KEY_PRESSED, key ->{
				if(key.getCode() == KeyCode.W)
				{
					direction = Dir.up; 
				}
				if(key.getCode() == KeyCode.S)
				{
					direction = Dir.down; 
				}
				if(key.getCode() == KeyCode.A)
				{
					direction = Dir.left; 
				}
				if(key.getCode() == KeyCode.D)
				{
					direction = Dir.right; 
				} 
				if(key.getCode() == KeyCode.SPACE)
				{
					restart(); 
				}
				
			});
			
			
			//add snake parts
			snake.add(new Corner(width / 2, height / 2)); 
			snake.add(new Corner(width / 2, height / 2)); 
			snake.add(new Corner(width / 2, height / 2)); 

			
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.setTitle("SNAKE GAME");
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	//restart
	public static void restart()
	{
		gameOver = false; 
		snake.clear();
		snake.add(new Corner(width / 2, height / 2)); 
		snake.add(new Corner(width / 2, height / 2)); 
		snake.add(new Corner(width / 2, height / 2)); 
		speed = 5; 
		newFood();
		
	}
	
	//tick
	public static void tick(GraphicsContext gc)
	{
		//gameover
		if(gameOver)
		{
			gc.setFill(Color.RED);
			gc.setFont(new Font("", 50));
			gc.fillText("GAME OVER", 100, 250);
			gc.setFont(new Font("", 30));
			gc.fillText("Space to Restart",135, 300);
			return; 
		}
		
		//change the x and y fields of the parts of the snake besides the head
		for(int i = snake.size() - 1; i >= 1; i--)
		{
			snake.get(i).x = snake.get(i - 1).x; 
			snake.get(i).y = snake.get(i - 1).y; 
		}
		
		//control direction of snake
		switch(direction)
		{
		case up:
			snake.get(0).y--; 
			if(snake.get(0).y < 0)
				gameOver = true; 
			break; 
		case down:
			snake.get(0).y++; 
			if(snake.get(0).y >= height)
				gameOver = true; 
			break; 
		case left:
			snake.get(0).x--; 
			if(snake.get(0).x < 0)
				gameOver = true; 
			break; 
		case right:
			snake.get(0).x++; 
			if(snake.get(0).x >= width)
				gameOver = true; 
			break; 
		}
			
		//eat food
		if(foodX == snake.get(0).x &&  foodY == snake.get(0).y)
		{
			//x,y are -1, -1 because the corner will be updated in the next frame and we dont want it to be visible
			snake.add(new Corner(-1,-1)); 
			newFood(); 
		}
		
		//self destroy
		for(int i = 1; i < snake.size(); i++)
		{
			if(snake.get(0).x == snake.get(i).x && snake.get(0).y == snake.get(i).y)
				gameOver = true; 
		}
		
		//fill background
		gc.setFill(Color.BLACK); 
		gc.fillRect(0,0,width * cornerSize,height * cornerSize); 
		
		//score
		gc.setFill(Color.WHITE); 
		gc.setFont(new Font("", 30)); 
		//speed may start at 5 but is increased to 6 when the first food is produced
		gc.fillText("Score: " + (speed - 6), 10, 30);		
		
		//random food color
		Color cc = Color.WHITE;

		switch (foodcolor) 
		{
		case 0:
			cc = Color.PURPLE;
			break;
		case 1:
			cc = Color.LIGHTBLUE;
			break;
		case 2:
			cc = Color.YELLOW;
			break;
		case 3:
			cc = Color.PINK;
			break;
		case 4:
			cc = Color.ORANGE;
			break;
		}
		gc.setFill(cc);
		gc.fillOval(foodX * cornerSize, foodY * cornerSize, cornerSize, cornerSize);

		//snake colored
		for (Corner c : snake) {
			gc.setFill(Color.LIGHTGREEN);
			gc.fillRect(c.x * cornerSize, c.y * cornerSize, cornerSize - 1, cornerSize - 1);
			gc.setFill(Color.GREEN);
			gc.fillRect(c.x * cornerSize, c.y * cornerSize, cornerSize - 2, cornerSize - 2);

		}

	}
	
	//food
	public static void newFood()
	{
		
		start: while(true)
		{
			foodX = rand.nextInt(width);
			foodY = rand.nextInt(height); 
			
			//make sure that the new food is not placed on
			//a square that the snake is currently on
			for(Corner c : snake)	
			{
				if(c.x == foodX && c.y == foodY)
					continue start; 
			}
			
			//new food color
			foodcolor = rand.nextInt(5); 
			//increase speed
			speed++; 
			break; 
		}
	}
	
	public static void main(String[] args) 
	{
		launch(args);
	}
}












