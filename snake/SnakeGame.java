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
	static int refreshRate = 420;
	ArrayList<Tile> snakeBody;
	boolean keyInput; //prevents two keyinputs during one single action
	boolean shiftInput;
	
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
	boolean death;
	
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
		
		keyInput = true;
		shiftInput = true;
		death = false;
		
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
		
		g.setColor(Color.cyan);
		for(int i=0; i < snakeBody.size(); i++) {
			g.fillRect(snakeBody.get(i).x * tileSize, snakeBody.get(i).y * tileSize, tileSize, tileSize);
		}
		Font gameOvFont = new Font("comicSans",Font.BOLD,40);
		Font scoreFont = new Font("Arial",Font.PLAIN,16);
		
		g.setFont(scoreFont);
		g.setColor(Color.pink);
		g.drawString("Score: " + eatenApples , 10 , 20);
		
		if(death) {
			g.setColor(Color.red);
			g.setFont(gameOvFont);
			g.drawString("Game Over", boardWidth/2 - 120, boardHeight/2);
			g.setFont(scoreFont);
			g.drawString("Score: " + eatenApples , boardWidth/2 -40 , boardHeight/2 + 40);
		}
	}
	
	private boolean collide(Tile t1, Tile t2) {
		if(t1.x == t2.x && t1.y == t2.y) {return true;}
		return false;
	}
	
	private boolean inSnake(Tile t) {
		for(int i=0; i<snakeBody.size(); i++) {
			if(collide(snakeBody.get(i), t) || collide(snakeHead, apple)) {
				return true;
			}
			
		}
		return false;
	}
	
	private void placeApple() {
		do {
			apple.x = random.nextInt(boardWidth/tileSize);
			apple.y = random.nextInt(boardHeight/tileSize);
		}while(inSnake(apple));
	}
	private void moveBody() {
		if(snakeBody.size()!=0) {
		//muove ogni casella copiando la posizione della precendente
			for(int j = snakeBody.size()-1; j > 0; j--) { 
				Tile tp = snakeBody.get(j-1);
				Tile t = new Tile(tp.x, tp.y);
				snakeBody.set(j, t);
			}
			Tile first = snakeBody.get(0);
			first.x = snakeHead.x;
			first.y = snakeHead.y;
		}
	}
	
	private void move() {
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
			eatenApples++;
			if((eatenApples%6) == 0) {
				loopTimer.setDelay((int)(loopTimer.getDelay()/1.2));
			}
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
		death = checkBounds() || checkTangle();
		if(death) loopTimer.stop();
		else move();
		repaint();  // refresha finestra	
		System.out.println(e);
		keyInput = true;
	}

	// KeyListener
	
	@Override
	public void keyPressed(KeyEvent e) {
		System.out.println(e);
		int code = e.getKeyCode();
		if(code == KeyEvent.VK_SHIFT && shiftInput) {
			loopTimer.setDelay(loopTimer.getDelay()/2);
			shiftInput = false;
		}
		
		if(keyInput) {
			
			if(code == KeyEvent.VK_UP && velY == 0 ) { 
				velX = 0;
				velY = -defSpeed;
			}
			else if(code == KeyEvent.VK_DOWN && velY == 0) { 
				velX = 0;
				velY = defSpeed;
			}
			else if(code == KeyEvent.VK_LEFT && velX == 0 ) { 
				velX = -defSpeed;
				velY = 0;
			}
			else if(code == KeyEvent.VK_RIGHT && velX == 0) { 
				velX = defSpeed;
				velY = 0;
			}
			else {return;} //inputs different from directions won't buffer
			keyInput = false;
			
		}
				
	}
	// non servono
	@Override
	public void keyTyped(KeyEvent e) {
		
	}
	@Override
	public void keyReleased(KeyEvent e) {
		int code = e.getKeyCode();
		if(code == KeyEvent.VK_SHIFT) {
			loopTimer.setDelay(loopTimer.getDelay()*2);
			shiftInput = true;
		}
	}
	// non servono
	
	
	
	 
	
}