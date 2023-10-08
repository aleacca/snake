package snake;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

//SnakeGame e' un JPanel, SnakeGame e' un ActionListener e KeyListener
public class SnakeGame extends JPanel implements ActionListener, KeyListener { 
	
	int boardWidth;
	int boardHeight;
	static int tileSize = 25;
	static int defSpeed = 1;
	static int refreshRate = 100;
	ArrayList<Tile> snakeBody;
	
	class Tile {
		int x;
		int y;
		Tile(int x, int y){
			this.x = x;
			this.y = y;
		}
		Tile(){
			this.x = 0;
			this.y = 0;
		}
	}
	
	Tile snakeHead;	//la classe SnakeGame ha una campo snakeHead
	Tile apple;
	
	//game logic
	
	int eatenApples = 0;
	Timer loopTimer; //timer per il refresh del gioco
	int velX;
	int velY;	
	
	Random random; //generatore mele
	
	SnakeGame(int boardWidth, int boardHeight){ //costruttore SnakeGame
		this.boardWidth = boardWidth;
		this.boardHeight = boardHeight;
		setPreferredSize(new Dimension(boardWidth,boardHeight)); //setto le dimensioni del game
		setBackground(Color.black);
		addKeyListener(this); // ricevera' keyevents da this(se stesso); addKeyListener e' un metodo di JComponent
		setFocusable(true); // focus per la ricezione di KeyEvents;;; di default e' true
		
		snakeHead = new Tile(5,5);
		apple = new Tile();
		//voglio che la posizione sia in unita', poi moltiplico per tileSize solo nel draw!!
		
		snakeBody = new ArrayList<Tile>();
		//snakeBody.add(snakeHead);
		
		random = new Random();
		placeApple();
		
		velX = 0;
		velY = 0;
		
		
		//mettendo this come argomento, gli sto dicendo di triggerare ActionPerformed per ogni loop
		loopTimer = new Timer(refreshRate, this); 
		loopTimer.start();
		
	}
	
	//questa funzione ridefinisce quella di JPanel,  
	public void paintComponent(Graphics g) {	
		super.paintComponent(g); //funzione di JPanel
		draw(g);
	}
	
	// ATTENZIONE MOLTO BENE: paintComponent e draw non sono legati a snakeHead, ma funzionano a prescindere se creo un oggetto Tile o meno
	// paintComponent viene invocato autonomamente -nota come non ho mai chiamato paintComponent()-
	// inoltre draw e' ingannevole: non e' il metodo di JPanel, lo sto definendo io, infatti se lo chiamo 'disegna' funziona lo stesso
	
	private void draw(Graphics g) {
		//Disegno una grid
		for(int i = 0; i <= boardHeight/tileSize; i++) {
			g.drawLine(i * tileSize, 0, i * tileSize, boardHeight);
			g.drawLine(0, i*tileSize, boardWidth, i*tileSize);
		}
		
		//head
		g.setColor(Color.green);
		g.fillRect(snakeHead.x * tileSize, snakeHead.y * tileSize, tileSize, tileSize);
		
		//apple
		g.setColor(Color.red);
		g.fillRect(apple.x * tileSize, apple.y * tileSize, tileSize, tileSize);
		
		//snakeBody
		
		g.setColor(Color.GREEN);
		for(int i=0; i < snakeBody.size(); i++) {
			g.fillRect(snakeBody.get(i).x * tileSize, snakeBody.get(i).y * tileSize, tileSize, tileSize);
		}
	}
	
	private boolean collide(Tile t1, Tile t2) {
		if(t1.x == t2.x && t1.y == t2.y) {return true;}
		return false;
	}
	
	private void placeApple() {
		apple.x = random.nextInt(boardWidth/tileSize);
		apple.y = random.nextInt(boardHeight/tileSize);
	}
	
	private void moveBody() {
		//muove ogni casella copiando la posizione della precendente
		for(int j = snakeBody.size()-1; j > 0; j--) { 
			Tile t = snakeBody.get(j);
			Tile tp = snakeBody.get(j-1);
			t.x = tp.x;
			t.y = tp.y;
		}
		//snakeBody.get(0).
	}
	
	private void move() {
		boolean death = checkBounds() || checkTangle();
		if(death) throw new RuntimeException("you died");
		
		moveBody();
		
		checkEat();

		snakeHead.x += velX;
		snakeHead.y += velY;
	}
	
	// se collide con una mela aggiunge un pezzo al corpo
	private void checkEat() {
		if(collide(snakeHead,apple)) {
			Tile bodyTile = new Tile( apple.x, apple.y );
			snakeBody.add(bodyTile);
			placeApple();
		}
		
	}
	
	// scandisce l'array del corpo per capire se si è scontrato
	private boolean checkTangle() {
		for(int i=1; i < snakeBody.size(); i++) {
			if(collide( snakeBody.get(i), snakeHead )) {
				return true;
			}
		}
		return false;
	}
	
	// controlla che non sia fuori dalla finestra(solo la testa)
	private boolean checkBounds() {
		if(snakeHead.x < 0 || snakeHead.x >= boardWidth/tileSize ||
				snakeHead.y < 0  || snakeHead.y >= boardHeight/tileSize) {
			return true;
		}
		return false;
	}
	
	// Action Listener
	
	@Override 
	public void actionPerformed(ActionEvent e) { //dopo evento 
		move();
		repaint();  // refresha finestra	
		System.out.println(e);
	}

	// KeyListener
	
	@Override
	public void keyPressed(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_UP && velY == 0 ) { 
			velX = 0;
			velY = -defSpeed;
		}
		else if(e.getKeyCode() == KeyEvent.VK_DOWN && velY == 0) { 
			velX = 0;
			velY = defSpeed;
		}
		else if(e.getKeyCode() == KeyEvent.VK_LEFT && velX == 0 ) { 
			velX = -defSpeed;
			velY = 0;
		}
		else if(e.getKeyCode() == KeyEvent.VK_RIGHT && velX == 0) { 
			velX = defSpeed;
			velY = 0;
		}
		
				
	}
	// non servono
	@Override
	public void keyTyped(KeyEvent e) {}
	@Override
	public void keyReleased(KeyEvent e) {}
	// non servono
	
	
	
	 
	
}
