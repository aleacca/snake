package snake;


import java.util.Random;
import java.util.stream.*;
import javax.swing.*;
import java.util.ArrayList;

public class App {
	public static void main(String[] args){
		int boardWidth = 600;
		int boardHeight = boardWidth;
		
		JFrame frame = new JFrame("Snake game");
		frame.setVisible(true);
		frame.setSize(boardWidth, boardHeight);
		frame.setLocationRelativeTo(null);
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		SnakeGame snakeGame = new SnakeGame(boardWidth, boardHeight);
		frame.add(snakeGame);
		frame.pack();
		snakeGame.requestFocus(); // da il focus dei KeyEvents a snakeGame
		
		ArrayList<String> a = new ArrayList<String>();
		a.add("kirby");
		String b = "kirby";
		a.add(b);
		
		
	}
}
