package lmc.concentration;

import javax.swing.ImageIcon;
import javax.swing.JButton;

public class TileButton extends JButton {

	private static int tileSize = 0;
	private static int maxTiles = 0;
	
	private boolean selected = false;
	
	public ImageIcon imageIcon = null;
	private int id = 0;
	private int row = 0;
	private int col = 0;
	
	public TileButton(int irow, int icol)
	{
		row = irow;
		col = icol;
	}
	
	public int getID()
	{
		return id;
	}
	
	public boolean selected()
	{
		return selected;
	}
	
	public static void setTileSizeAndMaxTiles(int tSize, int mxTiles)
	{
		tileSize = tSize;
		maxTiles = mxTiles;
	}
	
	public void setImage(ImageIcon img, int ID)
	{
		this.imageIcon = img;
		this.id = ID;
	}
	
	public void setStatus(boolean status)
	{
		selected = status;
	}
	
	public void showImage()
	{
		setIcon(imageIcon);
	}
	
	public void hideImage()
	{
		setIcon(null);
	}
}
