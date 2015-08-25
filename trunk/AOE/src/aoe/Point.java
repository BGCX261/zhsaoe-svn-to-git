package aoe;

import java.util.ArrayList;

/**
 * The point class.
 *
 * @author ZHS
 */
public class Point
{

	// <editor-fold defaultstate="collapsed" desc="For AOE">
	/**
	 * The earliest time the event can happen.
	 */
	public int earliest;
	/**
	 * The latest time the event can happen.
	 */
	public int latest;
	/**
	 * Wether the point is visited. Used in some search algorithms.
	 */
	public boolean visited;
	// </editor-fold>
	// <editor-fold defaultstate="collapsed" desc="Init">

	/**
	 * Creates a new point at default location with no edges and no name.
	 */
	public Point()
	{
		outEdges = new ArrayList<Edge>();
		inEdges = new ArrayList<Edge>();
		location = new Location();
		name = "";
	}

	/**
	 * Creates a new point at location (x, y) with no edges and no name.
	 * @param x The initial x dimension.
	 * @param y The initial y dimension.
	 */
	public Point(int x, int y)
	{
		outEdges = new ArrayList<Edge>();
		inEdges = new ArrayList<Edge>();
		location = new Location(x, y);
		name = "";
	}

	/**
	 * Creates a new point at location <code>location</code> with no edges and no name.
	 * @param location The initial location.
	 */
	public Point(Location location)
	{
		outEdges = new ArrayList<Edge>();
		inEdges = new ArrayList<Edge>();
		this.location = location;
	}
	// </editor-fold>
	// <editor-fold defaultstate="collapsed" desc="Name">
	/**
	 * The point's name.
	 */
	private String name;

	/**
	 * Get the point's name.
	 * @return The point's name.
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Set the point's name.
	 * @param name The new name.
	 */
	public void setName(String name)
	{
		if (name == null)
		{
			name = "";
		}
		this.name = name;
	}
	// </editor-fold>
	// <editor-fold defaultstate="collapsed" desc="Location">
	/**
	 * The location of the point on image.
	 */
	private Location location;

	/**
	 * Get the point's location.
	 * @return The point's location.
	 */
	public Location getLocation()
	{
		return location;
	}

	/**
	 * Set the point's location.
	 * @param location The new location.
	 */
	public void setLocation(Location location)
	{
		if (location != null)
		{
			this.location.setXY(location.getX(), location.getY());
		}
	}

	/**
	 * Set the point's location.
	 * @param x The new x.
	 * @param y The new y.
	 */
	public void setLocation(int x, int y)
	{
		this.location.setXY(x, y);
	}
	// </editor-fold>
	// <editor-fold defaultstate="collapsed" desc="Out edge">
	/**
	 * Edges that come from this point.
	 */
	private ArrayList<Edge> outEdges;

	/**
	 * Add an out edge.
	 * @param edge The new edge.
	 */
	public void addOutEdge(Edge edge)
	{
		outEdges.add(edge);
	}

	/**
	 * Remove an out edge.
	 * @param edge The edge to be removed.
	 */
	public void removeOutEdge(Edge edge)
	{
		outEdges.remove(edge);
	}

	/**
	 * Remove all edges from this point to the given point.
	 * @param point The given point.
	 */
	public void removeOutEdge(Point point)
	{
		for (Edge edge : outEdges)
		{
			if (point == edge.getTo())
			{
				outEdges.remove(edge);
				return;
			}
		}
	}

	/**
	 * Get all out edges.
	 * @return All out edges.
	 */
	public ArrayList<Edge> getOutEdges()
	{
		return outEdges;
	}
	// </editor-fold>
	// <editor-fold defaultstate="collapsed" desc="In edge">
	/**
	 * Edges that point to this point.
	 */
	private ArrayList<Edge> inEdges;

	/**
	 * Add an in edge.
	 * @param edge The new edge.
	 */
	public void addInEdge(Edge edge)
	{
		inEdges.add(edge);
	}

	/**
	 * Remove an in edge.
	 * @param edge The in edge to be removed.
	 */
	public void removeInEdge(Edge edge)
	{
		inEdges.remove(edge);
	}

	/**
	 * Remove all edge that from the given point to this point.
	 * @param point The given point.
	 */
	public void removeInEdge(Point point)
	{
		for (Edge edge : inEdges)
		{
			if (point == edge.getFrom())
			{
				inEdges.remove(edge);
				return;
			}
		}
	}

	/**
	 * Get all in edges.
	 * @return All in edges.
	 */
	public ArrayList<Edge> getInEdges()
	{
		return inEdges;
	}
	// </editor-fold>
	// <editor-fold defaultstate="collapsed" desc="Jedge neighbor">

	/**
	 * Judeg wether there is an edge from this point to the given point.
	 * @param point The given point.
	 * @return True if exist, otherwise false.
	 */
	public boolean isPointTo(Point point)
	{
		for (Edge edge : outEdges)
		{
			if (point == edge.getTo())
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * Judeg wether there is an edge from the given point to this point.
	 * @param point The given point.
	 * @return True if exist, otherwise false.
	 */
	public boolean isPointedBy(Point point)
	{
		for (Edge edge : inEdges)
		{
			if (point == edge.getFrom())
			{
				return true;
			}
		}
		return false;
	}
	// </editor-fold>
}
