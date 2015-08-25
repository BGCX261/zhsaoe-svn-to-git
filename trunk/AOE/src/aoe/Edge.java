package aoe;

/**
 * The edge class.
 *
 * @author ZHS
 */
public class Edge
{
	// <editor-fold defaultstate="collapsed" desc="Init">

	/**
	 * The start point of the edge.
	 */
	private Point from;
	/**
	 * The end point of the edge.
	 */
	private Point to;
	/**
	 * The length of the edge.
	 */
	private int length;
	/**
	 * Wether the edge is important.
	 */
	public boolean isImportant;

	/**
	 * Creates a new edge which from <code>from</code>, to <code>to</code>, length is <code>length</code>.
	 * @param from The start point of the edge.
	 * @param to The end point of the edge.
	 * @param length The length of the edge.
	 */
	public Edge(Point from, Point to, int length)
	{
		this.from = from;
		this.to = to;
		this.length = length;
	}
	// </editor-fold>
	// <editor-fold defaultstate="collapsed" desc="Getter & Setter">
	public Point getFrom()
	{
		return from;
	}

	public void setFrom(Point from)
	{
		this.from = from;
	}

	public int getLength()
	{
		return length;
	}

	public void setLength(int length)
	{
		this.length = length;
	}

	public Point getTo()
	{
		return to;
	}

	public void setTo(Point to)
	{
		this.to = to;
	}
	// </editor-fold>
}
