package aoe;

/**
 * The location on the 2D space.
 * 
 * @author ZHS
 */
public class Location
{
	// <editor-fold defaultstate="collapsed" desc="Init">

	/**
	 * The x dimension.
	 */
	private int x;
	/**
	 * The y dimension.
	 */
	private int y;

	/**
	 * Creates a new location at (0, 0).
	 */
	public Location()
	{
		this.x = 0;
		this.y = 0;
	}

	/**
	 * Creates a new location at (x, y).
	 * @param x The x dimension.
	 * @param y The y dimension.
	 */
	public Location(int x, int y)
	{
		this.x = x;
		this.y = y;
	}
	// </editor-fold>
	// <editor-fold defaultstate="collapsed" desc="Getter & Setter">

	public void setXY(int x, int y)
	{
		this.x = x;
		this.y = y;
	}

	public int getX()
	{
		return x;
	}

	public void setX(int x)
	{
		this.x = x;
	}

	public int getY()
	{
		return y;
	}

	public void setY(int y)
	{
		this.y = y;
	}
	// </editor-fold>
}
