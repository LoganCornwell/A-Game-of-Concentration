package lmc.concentration;

public class Opponent {

	// simulates limited 'memory' for ai to remember selected tiles
	private TileButton [] memory = null;
	
	private int size = 0;
	private int backupIndex = 0;
	
	public Opponent(int difficulty)
	{
		size = difficulty;
		memory = new TileButton [size];
		//System.out.println(size);
		//System.out.println(memory.toString());
	}
	
	public int getMemorySize()
	{
		return size;
	}
	
	// adds to memory 
	public void addToMem(TileButton selected)
	{
		boolean placed = false;
		
		// if selected tile button already resides in memory, do nothing else
		for (int i = 0; i < size; i++)
		{
			if (memory[i] == selected)
			{
				placed = true;
			}
		}
		
		// iterates through array to place in empty slot
		for (int i = 0; i < size && placed == false; i++)
		{
			if (memory[i] == null)
			{
				memory[i] = selected;
				placed = true;
			}
		}
		
		// if no empty slots available, overwrite each slot based on LRU
		if (placed == false)
		{
			memory[backupIndex] = selected;
			backupIndex = (backupIndex + 1) % size;
		}
	}
	
	public void removeFromMem(TileButton remove)
	{
		for (int i = 0; i < size; i++)
		{
			if (memory[i] == remove)
			{
				memory[i] = null;
			}
		}
	}
	
	// returns from memory the tile that matches a selected tile, null if it doesn't exist
	public int getMatchFromMem(TileButton selected)
	{
		int returnVal = size + 1;
		
		// iterates array to get the match to return
		for (int i = 0; i < size; i++)
		{
			if (memory[i] != null)
			{
				//System.out.println(selected == null);
				if (memory[i] != selected && memory[i].getID() == selected.getID())
				{
					returnVal = i;
				}
			}
			
		}
		
		return returnVal;
	}
	
	public int isMatchWithinMem()
	{
		int returnVal = size + 1;
		
		for (int i = 0; i < size; i++)
		{	
			if (memory[i] != null)
			{
				for (int j = 0; j < size && j != i; j++)
				{
					if (memory[j] != null)
					{
						//System.out.println(memory[i].getID());
						//System.out.println(memory[j].getID());
						if (memory[i].getID() == memory[j].getID())
						{
							returnVal = i;
						}
					}
				}
			}
		}
		
		return returnVal;
	}
	
	public TileButton getMemIndexedMatch(int index)
	{
		TileButton returnVal = memory[index];
		memory[index] = null;
		return returnVal;
	}
	
	/* redundant, removed unless found otherwise useful
	// checks memory array if ai's selected tile matches one found in memory
	public boolean checkMemForMatch(TileButton selected)
	{
		boolean matchFound = false;
		
		// iterates array to find a matching id (that is not the exact same tile)
		for (int i = 0; i < size; i++)
		{
			if (memory[i] != selected && memory[i].getID() == selected.getID())
			{
				matchFound = true;
			}
		}
		
		return matchFound;
	}
	*/
}
