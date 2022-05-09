package lmc.concentration;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import javax.imageio.stream.FileImageOutputStream;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.UIManager;

import lmc.fileio.FileIO;

public class Concentration extends JFrame {

	private static final long serialVersionUID = 1L;

	private static final String TITLE = "Concentration";
	private static final String TURN = "Turn:";
	private static final String PLAYER = "PLAYER";
	private static final String COMPUTER = "COMPUTER";
	private String SCORE = "Score: ";
	private String MOVES = "Moves: ";
	
	private String matchSound = "matched.wav";
	private String noMatchSound = "noMatch.wav";
	

	private TileButton [][] tileGrid = null;

	private Opponent ai = null;

	private TileButton choice1 = null;
	private TileButton choice2 = null;
	private TileButton aiGrab1 = null;
	private TileButton aiGrab2 = null;
	private TileButton aiChoice1 = null;
	private TileButton aiChoice2 = null;

	private JPanel titlePanel = null;
	private JPanel centerPanel = null;
	private JPanel scoreBoard = null;
	private JPanel playerPanel = null;
	private JPanel turnPanel = null;
	private JPanel aiPanel = null;

	private JLabel playerTitle = null;
	private JLabel scoreLabelP = null;
	private JLabel moveLabelP = null;
	private JLabel aiTitle = null;
	private JLabel scoreLabelAI = null;
	private JLabel moveLabelAI = null;
	private JLabel turnLabel = null;
	private JLabel turnHolder = null;

	private Random randObj = new Random();

	private String difficulty = null;

	private boolean vsMode = false;
	private boolean playerTurn = true;
	private boolean abruptExit = false;

	private int totalMatched = 0;
	private int playerScore = 0;
	private int playerMoves = 0;
	private int aiScore = 0;
	private int aiMoves = 0;
	private int aiDifficulty = 0;

	private int tileSize = 50; // tile size in pixels
	private int gridsizeH = 0;   
	private int gridsizeW = 0;   

	public Concentration()
	{
		difficultySelect();
		opponentSelect();
		if (vsMode)
		{
			aiDifficultySelect();
		}

		// set the tileSize and MaxTiles in the TileButton class 
		TileButton.setTileSizeAndMaxTiles(tileSize, gridsizeH*gridsizeW);
		initGui();
	}

	private void initGui()
	{
		// Create the label and place it in frame
		JPanel topPanel = new JPanel();
		topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
		topPanel.setBackground(Color.BLACK);

		JLabel gameLabel = gameTitle(TITLE);

		this.add(topPanel, BorderLayout.PAGE_START);
		
		titlePanel = new JPanel();
		titlePanel.setLayout(new BorderLayout());
		titlePanel.setBackground(Color.BLACK);
		topPanel.add(titlePanel);
		titlePanel.add(gameLabel);

		scoreBoard = new JPanel();
		scoreBoard.setLayout(new BorderLayout());
		scoreBoard.setBackground(Color.WHITE);
		topPanel.add(scoreBoard);

		playerPanel = new JPanel();
		playerPanel.setLayout(new BoxLayout(playerPanel, BoxLayout.Y_AXIS));
		playerPanel.setBackground(Color.WHITE);
		scoreBoard.add(playerPanel, BorderLayout.LINE_START);

		turnPanel = new JPanel();
		turnPanel.setLayout(new BoxLayout(turnPanel, BoxLayout.Y_AXIS));
		turnPanel.setBackground(Color.WHITE);
		scoreBoard.add(turnPanel, BorderLayout.CENTER);

		if (vsMode)
		{
			aiPanel = new JPanel();
			aiPanel.setLayout(new BoxLayout(aiPanel, BoxLayout.Y_AXIS));
			aiPanel.setBackground(Color.WHITE);
			scoreBoard.add(aiPanel, BorderLayout.LINE_END);
		}


		playerTitle = titleMaker(PLAYER);
		scoreLabelP = labelMaker(SCORE + playerScore);
		moveLabelP = labelMaker(MOVES + playerMoves);
		playerPanel.add(playerTitle);
		playerPanel.add(scoreLabelP);
		playerPanel.add(moveLabelP);


		turnLabel = titleMaker(TURN);
		turnHolder = labelMaker(PLAYER);
		turnPanel.add(turnLabel);
		turnPanel.add(turnHolder);

		if (vsMode)
		{
			aiTitle = titleMaker(COMPUTER);
			scoreLabelAI = labelMaker(SCORE + aiScore);
			moveLabelAI = labelMaker(MOVES + aiMoves);
			aiPanel.add(aiTitle);
			aiPanel.add(scoreLabelAI);
			aiPanel.add(moveLabelAI);
		}


		//topPanel.add(scoreLabelP);
		//topPanel.add(moveLabelP);

		// create the center in our frame
		setGrid();

		JPanel bottom = new JPanel();
		JButton resetBtn = new JButton("Restart");

		resetBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent eClick) {
				restart();	// randomize
			}
		});

		bottom.setLayout(new GridLayout(1, 2));
		bottom.add(resetBtn, BorderLayout.LINE_END);

		add(bottom, BorderLayout.PAGE_END);

		// the JFrame for the game
		this.setTitle(TITLE);
		this.setResizable(false);
		this.pack();
		this.setLocationRelativeTo(null);
		this.setVisible(true);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
	}

	private void difficultySelect()
	{
		// option to input difficulty
		difficulty = JOptionPane.showInputDialog(null, "Input Board Diffculty: Easy, Medium, Hard", "Difficulty", JOptionPane.DEFAULT_OPTION);

		// difficulty option changes size of game board and player moves
		if (difficulty.equalsIgnoreCase("Easy")) 
		{
			gridsizeH = 4;
			gridsizeW = 4;
			playerMoves = 15;
			aiMoves = 15;
		}
		else if (difficulty.equalsIgnoreCase("Medium")) 
		{
			gridsizeH = 6;
			gridsizeW = 6;
			playerMoves = 50;
			aiMoves = 50;
		}
		else if (difficulty.equalsIgnoreCase("Hard")) 
		{
			gridsizeH = 8;
			gridsizeW = 8;
			playerMoves = 75;
			aiMoves = 75;
		}
	}
	
	private void aiDifficultySelect()
	{
		// option to input difficulty
		difficulty = JOptionPane.showInputDialog(null, "Input AI Diffculty: Easy, Medium, Hard", "AI Difficulty", JOptionPane.DEFAULT_OPTION);

		// difficulty option changes size of game board and player moves
		if (difficulty.equalsIgnoreCase("Easy")) 
		{
			aiDifficulty = 3;
			ai = new Opponent(aiDifficulty);
		}
		else if (difficulty.equalsIgnoreCase("Medium")) 
		{
			aiDifficulty = 5;
			ai = new Opponent(aiDifficulty);
		}
		else if (difficulty.equalsIgnoreCase("Hard")) 
		{
			aiDifficulty = 7;
			ai = new Opponent(aiDifficulty);
		}
	}

	// choose opponent before starting the game
	private void opponentSelect()
	{
		// choose between a.i. and singleplayer
		String msg = "Play against an A.I. opponent? ('no' implies single-player experience)";
		int option = JOptionPane.showConfirmDialog(this, msg, "Opponent Selection", JOptionPane.YES_NO_OPTION);

		if (option == JOptionPane.YES_OPTION)
		{
			vsMode = true;
		}
		else if (option == JOptionPane.NO_OPTION)
		{
			vsMode = false;
		}
		else if (option == JOptionPane.CLOSED_OPTION)
		{
			System.exit(0);
		}
	}

	// method to restart the game
	private void restart()
	{
		abruptExit = true;
		difficultySelect();
		opponentSelect();
		if (vsMode)
		{
			aiDifficultySelect();
		}
		

		totalMatched = 0;
		
		playerScore = 0;
		scoreLabelP.setText(SCORE + playerScore);
		moveLabelP.setText(MOVES + playerMoves);
		
		if (vsMode)
		{
			if (aiPanel != null)
			{
				scoreBoard.remove(aiPanel);
				aiPanel = null;
			}
			aiPanel = new JPanel();
			aiPanel.setLayout(new BoxLayout(aiPanel, BoxLayout.Y_AXIS));
			aiPanel.setBackground(Color.WHITE);
			scoreBoard.add(aiPanel, BorderLayout.LINE_END);
			
			aiTitle = titleMaker(COMPUTER);
			scoreLabelAI = labelMaker(SCORE + aiScore);
			moveLabelAI = labelMaker(MOVES + aiMoves);
			aiPanel.add(aiTitle);
			aiPanel.add(scoreLabelAI);
			aiPanel.add(moveLabelAI);
			
			aiScore = 0;
			System.out.println("triggered");
			scoreLabelAI.setText(SCORE + aiScore);
			scoreLabelAI.paintImmediately(scoreLabelAI.getVisibleRect());
			moveLabelAI.setText(MOVES + aiMoves);
			moveLabelAI.paintImmediately(moveLabelAI.getVisibleRect());
			System.out.println(aiScore);
			System.out.println(aiMoves);
			
			ai = new Opponent(aiDifficulty);
		}
		if (!vsMode && aiPanel != null)
		{
			scoreBoard.remove(aiPanel);
			aiPanel = null;
			ai = null;
		}
		
		playerTurn = true;
		System.out.println(playerTurn);
		remove(centerPanel);
		centerPanel = null;
		setGrid();
		centerPanel.updateUI();
		this.pack();
	}

	private JLabel gameTitle(String title)
	{
		JLabel gameLabel = new JLabel(title);
		Font titleFont = new Font(Font.SANS_SERIF, Font.BOLD, 28);
		gameLabel.setFont(titleFont);
		gameLabel.setBackground(Color.BLACK);
		gameLabel.setForeground(Color.WHITE);
		gameLabel.setOpaque(true);
		gameLabel.setHorizontalAlignment(JLabel.CENTER);

		return gameLabel;
	}

	private JLabel titleMaker(String title)
	{
		JLabel gameLabel = new JLabel(title);
		Font titleFont = new Font(Font.SANS_SERIF, Font.BOLD, 20);
		gameLabel.setFont(titleFont);
		gameLabel.setBackground(Color.WHITE);
		gameLabel.setForeground(Color.BLACK);
		gameLabel.setOpaque(true);
		gameLabel.setHorizontalAlignment(JLabel.CENTER);

		return gameLabel;
	}

	private JLabel labelMaker(String title)
	{
		JLabel gameLabel = new JLabel(title);
		Font titleFont = new Font(Font.SANS_SERIF, Font.PLAIN, 18);
		gameLabel.setFont(titleFont);
		gameLabel.setBackground(Color.WHITE);
		gameLabel.setForeground(Color.BLACK);
		gameLabel.setOpaque(true);
		gameLabel.setHorizontalAlignment(JLabel.CENTER);

		return gameLabel;
	}

	// functions just like divideImage in SlidingTiles, just leaves each button blank at a set size
	private void setGrid()
	{
		centerPanel = new JPanel();
		centerPanel.setLayout(new GridLayout(gridsizeH, gridsizeW));
		add(centerPanel, BorderLayout.CENTER);

		centerPanel.removeAll();

		int ir, ic;
		tileGrid = new TileButton[gridsizeH][gridsizeW];

		for (ir = 0; ir < gridsizeH; ir++) // for each row
		{
			for (ic = 0; ic < gridsizeW; ic++) // create each tile in row
			{
				tileGrid[ir][ic] = new TileButton(ir, ic);
				tileGrid[ir][ic].setPreferredSize(new Dimension(tileSize, tileSize));

				tileGrid[ir][ic].addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent eClick) {
						//System.out.println("Count of listeners: " + ((JButton) eClick.getSource()).getActionListeners().length);
						clickedTile((TileButton)eClick.getSource());

					}
				});

				centerPanel.add(tileGrid[ir][ic]);
			}
		}

		// set game board depending on chosen difficulty
		setGame();

		centerPanel.revalidate();
	}

	private void setGame()
	{
		ArrayList<Integer> tileIDs = new ArrayList<Integer>();
		int id = 0;

		for (int i = 0; i < (gridsizeH*gridsizeW)/2; ++i)
		{
			tileIDs.add((Integer)i);
			tileIDs.add((Integer)i);
		}

		for (int ir = 0; ir < gridsizeH; ir++) // for each row
		{
			for (int ic = 0; ic < gridsizeW; ic++) // for each tile in row
			{
				// get random id from list, no replacement 
				id = tileIDs.remove(randObj.nextInt(tileIDs.size()));

				// set each tile to their own id and corresponding image
				tileGrid[ir][ic].setImage(setTileValue(id), id);
			}
		}
	}

	private void clickedTile(TileButton cTile)
	{
		// do nothing if two are already selected
		if (choice2 != null);

		// check if theres already a tile selected, if so, set the second selection and make a decision
		else if (choice1 != null && cTile != choice1 && !cTile.selected() && playerTurn)
		{	
			choice2 = cTile;
			choice2.showImage();

			// if the two chosen image ids match, increase score and reset the selections
			if (choice1.getID() == choice2.getID())
			{
				// change score, then change score label
				playerScore++;
				scoreLabelP.setText(SCORE + playerScore);
				FileIO.playAudioClip(matchSound);
				totalMatched++;
				
				// change moves remaining, then change moves label
				playerMoves--;
				moveLabelP.setText(MOVES + playerMoves);

				choice1.setStatus(true);
				choice2.setStatus(true);
				
				if (vsMode)
				{
					ai.removeFromMem(choice1);
					ai.removeFromMem(choice2);
				}

				choice1 = null;
				choice2 = null;

				// player has won!
				if (playerScore == (gridsizeH * gridsizeW) / 2)
				{
					// code for winning the game
					String msg = "Congratulations! Play Again?";
					int option = JOptionPane.showConfirmDialog(this, msg, "You Win", JOptionPane.YES_NO_OPTION);

					if (option == JOptionPane.YES_OPTION)
					{
						restart();
					}
					else
					{
						System.exit(0);
					}
				}
			}

			// if they dont match, reset the gui and tile selections
			else
			{	
				// since they don't match, let the computer grab them for it's memory
				if (vsMode)
				{
					aiGrab1 = choice1;
					aiGrab2 = choice2;
				}
				
				// change moves remaining, then change moves label
				playerMoves--;
				moveLabelP.setText(MOVES + playerMoves);
				FileIO.playAudioClip(noMatchSound);

				// actionlistener that rehides and resets the images after 1 second
				ActionListener waitAndHide = new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						choice1.hideImage();
						choice2.hideImage();
						choice1 = null;
						choice2 = null;
					}
				};
				Timer timer = new Timer(1000, waitAndHide);
				timer.setRepeats(false);
				timer.start();
			}

			// game over
			if (playerMoves == 0 && vsMode == false)
			{
				// code for game over, restart game
				String msg = "Game Over, Play Again?";
				int option = JOptionPane.showConfirmDialog(this, msg, "Game Over", JOptionPane.YES_NO_OPTION);

				if (option == JOptionPane.YES_OPTION)
				{
					restart();
				}
				else
				{
					System.exit(0);
				}
			}
			else if (vsMode == true && totalMatched == gridsizeH*gridsizeW / 2)
			{
				//System.out.println("got Here" + totalMatched);
				if (aiScore > playerScore)
				{
					// code for game over, restart game
					String msg = "You Lose! \nYour Score: " + playerScore + " : Comp Score: " + aiScore + " \nPlay Again?";
					int option = JOptionPane.showConfirmDialog(this, msg, "Game Over", JOptionPane.YES_NO_OPTION);

					if (option == JOptionPane.YES_OPTION)
						{
							restart();
						}
						else
						{
							System.exit(0);
						}
				}
				else if (playerScore > aiScore)
				{
					// code for game over, restart game
					String msg = "You Win! \nYour Score: " + playerScore + " : Comp Score: " + aiScore + " \nPlay Again?";
					int option = JOptionPane.showConfirmDialog(this, msg, "Game Over", JOptionPane.YES_NO_OPTION);

					if (option == JOptionPane.YES_OPTION)
						{
							restart();
						}
						else
						{
							System.exit(0);
						}
				}
				else if (playerScore == aiScore)
				{
					// code for game over, restart game
					String msg = "It's a Tie! \nYour Score: " + playerScore + " : Comp Score: " + aiScore + " \nPlay Again?";
					int option = JOptionPane.showConfirmDialog(this, msg, "Game Over", JOptionPane.YES_NO_OPTION);

					if (option == JOptionPane.YES_OPTION)
						{
							restart();
						}
						else
						{
							System.exit(0);
						}
				}
			}
			
			if (vsMode && abruptExit == false)
			{
				playerTurn = false;
			}


			// code for ai, executed after players selections
			if (vsMode && playerTurn == false)
			{
				playerTurn = false;
				// add unmatched, grabbed tiles to memory
				if (aiGrab1 != null)
				{
					ai.addToMem(aiGrab1);
					ai.addToMem(aiGrab2);
					aiGrab1 = null;
					aiGrab2 = null;
				}

				// set gui text to display ai turn
				turnHolder.setText(COMPUTER);

						// check in memory if a match exists, if so, pull both as two selections
						//System.out.println(ai.isMatchWithinMem());
						if (ai.isMatchWithinMem() <= ai.getMemorySize())
						{
							//System.out.println(ai.isMatchWithinMem());
							aiChoice1 = ai.getMemIndexedMatch(ai.isMatchWithinMem());
							//System.out.println(aiChoice1);
							aiChoice2 = ai.getMemIndexedMatch(ai.getMatchFromMem(aiChoice1));
						}

						// if no such match exists, select tiles from random
						else
						{
							// grab random coords to select a tile from random
							int x1 = randObj.nextInt(gridsizeW);
							int y1 = randObj.nextInt(gridsizeH);
							aiChoice1 = tileGrid[x1][y1];
							
							// make sure the selected tile is not already selected
							if (aiChoice1.selected() || aiChoice1 == null)
							{
								while (aiChoice1.selected() || aiChoice1 == null)
								{
									x1 = randObj.nextInt(gridsizeW);
									y1 = randObj.nextInt(gridsizeH);
									aiChoice1 = tileGrid[x1][y1];
								}
							}

							//System.out.println(ai.getMatchFromMem(aiChoice1) + " " + ai.getMemorySize());
							//System.out.println(ai.getMatchFromMem(aiChoice1) <= ai.getMemorySize());
							// check if a match for the random selection exists in ai's memory
							if (ai.getMatchFromMem(aiChoice1) <= ai.getMemorySize())
							{
								//System.out.println(ai.getMatchFromMem(aiChoice1) + " " + ai.getMemorySize());
								// if so, make that the ai's second selection
								aiChoice2 = ai.getMemIndexedMatch(ai.getMatchFromMem(aiChoice1));
								//System.out.println("it did it");
							}
							else
							{
								//System.out.println(totalMatched);
								// avoid infinte looping when trying to make final decision
								if (totalMatched == (gridsizeH*gridsizeW / 2) - 1)
								{
									for (int ir = 0; ir < gridsizeH; ir++) // for each row
									{
										for (int ic = 0; ic < gridsizeW; ic++) // for each tile in row
										{
											if (tileGrid[ir][ic].getID() == aiChoice1.getID() && tileGrid[ir][ic] != aiChoice1)
											{
												//System.out.println("found it");
												aiChoice2 = tileGrid[ir][ic];
											}
										}
									}
								}
								else
								{
									// if not, make another random selection that is not the same as the first
									int x2 = randObj.nextInt(gridsizeW);

									while (x1 == x2)
									{
										x2 = randObj.nextInt(gridsizeW);
									}

									int y2 = randObj.nextInt(gridsizeW);

									while (y1 == y2)
									{
										y2 = randObj.nextInt(gridsizeW);
									}

									aiChoice2 = tileGrid[x2][y2];
									
									// make sure the selected tile is not already selected
									if (aiChoice2.selected() || aiChoice2 == null)
									{
										while (aiChoice2.selected() || aiChoice2 == null)
										{
											x2 = randObj.nextInt(gridsizeW);

											while (x1 == x2)
											{
												x2 = randObj.nextInt(gridsizeW);
											}

											y2 = randObj.nextInt(gridsizeW);

											while (y1 == y2)
											{
												y2 = randObj.nextInt(gridsizeW);
											}

											aiChoice2 = tileGrid[x2][y2];
										}
									}
								}
							}
						}
				
						//System.out.println("AI's first choice is equal to: " + aiChoice1);
						//System.out.println("AI's second choice is equal to: " + aiChoice2);
						// check for a match between ai's selections
						// if the two chosen image ids match, increase score and reset the selections
						//System.out.println("got here");
						if (aiChoice1.getID() == aiChoice2.getID())
						{
							// change score, then change score label
							aiScore++;
							totalMatched++;

							aiChoice1.setStatus(true);
							aiChoice2.setStatus(true);
							
							// actionlistener that rehides and resets the images after 1 second
							ActionListener waitAndHide = new ActionListener() {

								@Override
								public void actionPerformed(ActionEvent e) {
									aiChoice1 = null;
									aiChoice2 = null;
									playerTurn = true;
									turnHolder.setText(PLAYER);
								}
							};
							Timer timer = new Timer(4000, waitAndHide);
							timer.setRepeats(false);
							timer.start();

							// AI has won!
							if (aiScore == (gridsizeH * gridsizeW) / 2)
							{
								// code for winning the game
								String msg = "The Computer has Won! Play Again?";
								int option = JOptionPane.showConfirmDialog(this, msg, "You Win", JOptionPane.YES_NO_OPTION);

								if (option == JOptionPane.YES_OPTION)
								{
									restart();
								}
								else
								{
									System.exit(0);
								}
							}
						}

						else
						{
							// actionlistener that rehides and resets the images after 1 second
							ActionListener waitAndHide = new ActionListener() {

								@Override
								public void actionPerformed(ActionEvent e) {
									ai.addToMem(aiChoice1);
									ai.addToMem(aiChoice2);
									//System.out.println("hiding");
									aiChoice1.hideImage();
									aiChoice2.hideImage();
									aiChoice1 = null;
									aiChoice2 = null;
									playerTurn = true;
									turnHolder.setText(PLAYER);
								}
							};
							Timer timer = new Timer(4000, waitAndHide);
							timer.setRepeats(false);
							timer.start();
						}

				// reflect the above selections elegantly in the gui
				ActionListener aiTimer1 = new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						//System.out.println("showing first");
						aiChoice1.showImage();
						//System.out.println(aiChoice1.getID() + " showed");
					}
				};
				Timer timerc1 = new Timer(2000, aiTimer1);
				timerc1.setRepeats(false);
				//System.out.println("got here1");
				timerc1.start();

				ActionListener aiTimer2 = new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						//System.out.println("showing second");
						aiChoice2.showImage();
						//System.out.println(aiChoice2.getID() + " showed");

						// set turn back to player
						// change moves remaining, then change moves and score label
						if (aiChoice1.getID() == aiChoice2.getID())
						{
							scoreLabelAI.setText(SCORE + aiScore);
							FileIO.playAudioClip(matchSound);
						}
						else
						{
							FileIO.playAudioClip(noMatchSound);
						}
						aiMoves--;
						moveLabelAI.setText(MOVES + aiMoves);
					}
				};
				Timer timerc2 = new Timer(3000, aiTimer2);
				timerc2.setRepeats(false);
				//System.out.println("got here2");
				timerc2.start();

				// game over
				if (playerMoves == 0 && aiMoves == 0 && vsMode == true)
				{
					// code for game over, restart game
					String msg = "Game Over, Final Score: " + playerScore + ". \nPlay Again?";
					int option = JOptionPane.showConfirmDialog(this, msg, "Game Over", JOptionPane.YES_NO_OPTION);

					if (option == JOptionPane.YES_OPTION)
					{
						restart();
					}
					else
					{
						System.exit(0);
					}
				}
				else if (totalMatched == gridsizeH*gridsizeW / 2)
				{
					//System.out.println("got Here" + totalMatched);
					if (aiScore > playerScore)
					{
						// code for game over, restart game
						String msg = "You Lose! \nYour Score: " + playerScore + " : Comp Score: " + aiScore + " \nPlay Again?";
						int option = JOptionPane.showConfirmDialog(this, msg, "Game Over", JOptionPane.YES_NO_OPTION);

						if (option == JOptionPane.YES_OPTION)
							{
								restart();
							}
							else
							{
								System.exit(0);
							}
					}
					else if (playerScore > aiScore)
					{
						// code for game over, restart game
						String msg = "You Win! \nYour Score: " + playerScore + " : Comp Score: " + aiScore + " \nPlay Again?";
						int option = JOptionPane.showConfirmDialog(this, msg, "Game Over", JOptionPane.YES_NO_OPTION);

						if (option == JOptionPane.YES_OPTION)
							{
								restart();
							}
							else
							{
								System.exit(0);
							}
					}
					else if (playerScore == aiScore)
					{
						// code for game over, restart game
						String msg = "It's a Tie! \nYour Score: " + playerScore + " : Comp Score: " + aiScore + " \nPlay Again?";
						int option = JOptionPane.showConfirmDialog(this, msg, "Game Over", JOptionPane.YES_NO_OPTION);

						if (option == JOptionPane.YES_OPTION)
							{
								restart();
							}
							else
							{
								System.exit(0);
							}
					}
				}
			}
		}
		
		// if not, set the first selection and wait for the second
		else if (choice1 == null && !cTile.selected() && playerTurn)
		{
			choice1 = cTile;
			choice1.showImage();
		}
		
		abruptExit = false;
	}

	private ImageIcon setTileValue(int id)
	{
		ImageIcon returnIcon = null;

		// create a buffered image, draw a shape to use as the tile value, then translate the buffered image into an ImageIcon
		BufferedImage tileImg = new BufferedImage(tileSize-7, tileSize-7, BufferedImage.TYPE_INT_ARGB);
		Graphics2D gObj = tileImg.createGraphics();
		gObj.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		// we need to draw a different shape/color for each possible button ID (0-31)
		if (id == 0)
		{
			gObj.setColor(Color.BLUE); // change for each id
			gObj.fillOval(0, 0, tileSize-7, tileSize-7); // change for each id
			gObj.dispose();
			returnIcon = new ImageIcon(tileImg);
		}
		else if (id == 1)
		{
			gObj.setColor(Color.BLACK); // change for each id
			gObj.fillOval(0, 0, tileSize-7, tileSize-7); // change for each id
			gObj.dispose();
			returnIcon = new ImageIcon(tileImg);
		}
		else if (id == 2)
		{
			gObj.setColor(Color.CYAN); // change for each id
			gObj.fillOval(0, 0, tileSize-7, tileSize-7); // change for each id
			gObj.dispose();
			returnIcon = new ImageIcon(tileImg);
		}
		else if (id == 3)
		{
			gObj.setColor(Color.GREEN); // change for each id
			gObj.fillOval(0, 0, tileSize-7, tileSize-7); // change for each id
			gObj.dispose();
			returnIcon = new ImageIcon(tileImg);
		}
		else if (id == 4)
		{
			gObj.setColor(Color.MAGENTA); // change for each id
			gObj.fillOval(0, 0, tileSize-7, tileSize-7); // change for each id
			gObj.dispose();
			returnIcon = new ImageIcon(tileImg);
		}
		else if (id == 5)
		{
			gObj.setColor(Color.ORANGE); // change for each id
			gObj.fillOval(0, 0, tileSize-7, tileSize-7); // change for each id
			gObj.dispose();
			returnIcon = new ImageIcon(tileImg);
		}
		else if (id == 6)
		{
			gObj.setColor(Color.PINK); // change for each id
			gObj.fillOval(0, 0, tileSize-7, tileSize-7); // change for each id
			gObj.dispose();
			returnIcon = new ImageIcon(tileImg);
		}
		else if (id == 7)
		{
			gObj.setColor(Color.RED); // change for each id
			gObj.fillOval(0, 0, tileSize-7, tileSize-7); // change for each id
			gObj.dispose();
			returnIcon = new ImageIcon(tileImg);
		}
		else if (id == 8)
		{
			gObj.setColor(Color.WHITE); // change for each id
			gObj.fillOval(0, 0, tileSize-7, tileSize-7); // change for each id
			gObj.dispose();
			returnIcon = new ImageIcon(tileImg);
		}
		else if (id == 9)
		{
			gObj.setColor(Color.YELLOW); // change for each id
			gObj.fillOval(0, 0, tileSize-7, tileSize-7); // change for each id
			gObj.dispose();
			returnIcon = new ImageIcon(tileImg);
		}
		else if (id == 10)
		{
			gObj.setColor(Color.BLUE); // change for each id
			gObj.fillRect(0, 0, tileSize-7, tileSize-7); // change for each id
			gObj.dispose();
			returnIcon = new ImageIcon(tileImg);
		}
		else if (id == 11)
		{
			gObj.setColor(Color.BLACK); // change for each id
			gObj.fillRect(0, 0, tileSize-7, tileSize-7); // change for each id			
			gObj.dispose();
			returnIcon = new ImageIcon(tileImg);
		}
		else if (id == 12)
		{
			gObj.setColor(Color.CYAN); // change for each id
			gObj.fillRect(0, 0, tileSize-7, tileSize-7); // change for each id
			gObj.dispose();
			returnIcon = new ImageIcon(tileImg);
		}
		else if (id == 13)
		{
			gObj.setColor(Color.GREEN); // change for each id
			gObj.fillRect(0, 0, tileSize-7, tileSize-7); // change for each id
			gObj.dispose();
			returnIcon = new ImageIcon(tileImg);
		}
		else if (id == 14)
		{
			gObj.setColor(Color.MAGENTA); // change for each id
			gObj.fillRect(0, 0, tileSize-7, tileSize-7); // change for each id
			gObj.dispose();
			returnIcon = new ImageIcon(tileImg);
		}
		else if (id == 15)
		{
			gObj.setColor(Color.ORANGE); // change for each id
			gObj.fillRect(0, 0, tileSize-7, tileSize-7); // change for each id
			gObj.dispose();
			returnIcon = new ImageIcon(tileImg);
		}
		else if (id == 16)
		{
			gObj.setColor(Color.PINK); // change for each id
			gObj.fillRect(0, 0, tileSize-7, tileSize-7); // change for each id
			gObj.dispose();
			returnIcon = new ImageIcon(tileImg);
		}
		else if (id == 17)
		{
			gObj.setColor(Color.RED); // change for each id
			gObj.fillRect(0, 0, tileSize-7, tileSize-7); // change for each id
			gObj.dispose();
			returnIcon = new ImageIcon(tileImg);
		}
		else if (id == 18)
		{
			gObj.setColor(Color.WHITE); // change for each id
			gObj.fillRect(0, 0, tileSize-7, tileSize-7); // change for each id
			gObj.dispose();
			returnIcon = new ImageIcon(tileImg);
		}
		else if (id == 19)
		{
			gObj.setColor(Color.YELLOW); // change for each id
			gObj.fillRect(0, 0, tileSize-7, tileSize-7); // change for each id
			gObj.dispose();
			returnIcon = new ImageIcon(tileImg);
		}
		else if (id == 20)
		{
			gObj.setColor(Color.DARK_GRAY); // change for each id
			gObj.fillRect(0, 0, tileSize-7, tileSize-7); // change for each id
			gObj.dispose();
			returnIcon = new ImageIcon(tileImg);
		}
		else if (id == 21)
		{
			gObj.setColor(Color.RED); // change for each id
			gObj.drawRect(0, 0, tileSize-9, tileSize-9); // change for each id
			gObj.dispose();
			returnIcon = new ImageIcon(tileImg);
		}
		else if (id == 22)
		{
			gObj.setColor(Color.DARK_GRAY); // change for each id
			gObj.fillOval(0, 0, tileSize-9, tileSize-9); // change for each id
			gObj.dispose();
			returnIcon = new ImageIcon(tileImg);
		}
		else if (id == 23)
		{
			gObj.setColor(Color.RED); // change for each id
			gObj.drawOval(0, 0, tileSize-9, tileSize-9); // change for each id
			gObj.dispose();
			returnIcon = new ImageIcon(tileImg);
		}
		else if (id == 24)
		{
			gObj.setColor(Color.BLUE); // change for each id
			gObj.drawRect(0, 0, tileSize-9, tileSize-9); // change for each id
			gObj.dispose();
			returnIcon = new ImageIcon(tileImg);
		}
		else if (id == 25)
		{
			gObj.setColor(Color.ORANGE); // change for each id
			gObj.drawRect(0, 0, tileSize-9, tileSize-9); // change for each id
			gObj.dispose();
			returnIcon = new ImageIcon(tileImg);
		}
		else if (id == 26)
		{
			gObj.setColor(Color.MAGENTA); // change for each id
			gObj.drawRect(0, 0, tileSize-9, tileSize-9); // change for each id
			gObj.dispose();
			returnIcon = new ImageIcon(tileImg);
		}
		else if (id == 27)
		{
			gObj.setColor(Color.BLUE); // change for each id
			gObj.drawOval(0, 0, tileSize-9, tileSize-9); // change for each id
			gObj.dispose();
			returnIcon = new ImageIcon(tileImg);
		}
		else if (id == 28)
		{
			gObj.setColor(Color.BLACK); // change for each id
			gObj.drawOval(0, 0, tileSize-9, tileSize-9); // change for each id
			gObj.dispose();
			returnIcon = new ImageIcon(tileImg);
		}
		else if (id == 29)
		{
			gObj.setColor(Color.MAGENTA); // change for each id
			gObj.drawOval(0, 0, tileSize-9, tileSize-9); // change for each id
			gObj.dispose();
			returnIcon = new ImageIcon(tileImg);
		}
		else if (id == 30)
		{
			gObj.setColor(Color.ORANGE); // change for each id
			gObj.drawOval(0, 0, tileSize-9, tileSize-9); // change for each id
			gObj.dispose();
			returnIcon = new ImageIcon(tileImg);
		}
		else if (id == 31)
		{
			gObj.setColor(Color.BLACK); // change for each id
			gObj.drawRect(0, 0, tileSize-9, tileSize-9); // change for each id
			gObj.dispose();
			returnIcon = new ImageIcon(tileImg);
		}

		// return image to be set to tilebutton
		return returnIcon;
	}

	public static void main(String[] args) {

		try 
		{
			String className = UIManager.getSystemLookAndFeelClassName();
			//System.out.println(className);
			UIManager.setLookAndFeel(className);
		} catch (Exception traceback)
		{
			System.err.println(traceback);
		}

		EventQueue.invokeLater(new Runnable() {

			@Override
			public void run() {

				new Concentration();
			}	// end of run
		});	
	}

}
