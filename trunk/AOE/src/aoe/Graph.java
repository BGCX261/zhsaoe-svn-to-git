package aoe;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;

/**
 * The AOE class, manage the graph and solve the problem.
 *
 * @author ZHS
 */
public class Graph
{
	// <editor-fold defaultstate="collapsed" desc="Init">

	/**
	 * Creates a new AOE.
	 */
	public Graph()
	{
		points = new ArrayList<Point>();
		edges = new ArrayList<Edge>();
	}
	// </editor-fold>
	// <editor-fold defaultstate="collapsed" desc="Clear">

	/**
	 * Clear points and edges.
	 */
	public void clear()
	{
		points.clear();
		edges.clear();
	}
	// </editor-fold>
	// <editor-fold defaultstate="collapsed" desc="Solve">

	/**
	 * Get the earliest time of the point.
	 * @param point The point.
	 * @return The time.
	 */
	private int getEarliest(Point point)
	{
		if (point.earliest < 0)
		{
			point.earliest = 0;
			for (Edge edge : point.getInEdges())
			{
				point.earliest = Math.max(getEarliest(edge.getFrom()) + edge.getLength(), point.earliest);
			}
		}
		return point.earliest;
	}

	/**
	 * Get the earliest time of the point.
	 * @param point The point.
	 * @return The time.
	 */
	private int getLatest(Point point)
	{
		if (point.latest < 0)
		{
			for (Edge edge : point.getOutEdges())
			{
				if (point.latest < 0 || getLatest(edge.getTo()) - edge.getLength() < point.latest)
				{
					point.latest = getLatest(edge.getTo()) - edge.getLength();
				}
			}
			if (point.latest < 0)
			{
				point.latest = point.earliest;
			}
		}
		return point.latest;
	}

	/**
	 * Solve the AOE problem. Fill all points' earliest and latest time.
	 */
	public void solve()
	{
		for (Point point : points)
		{
			point.earliest = -1;
			point.latest = -1;
		}
		for (Point point : points)
		{
			if (point.earliest < 0)
			{
				getEarliest(point);
			}
		}
		for (Point point : points)
		{
			if (point.latest < 0)
			{
				getLatest(point);
			}
		}
		for (Edge edge : edges)
		{
			edge.isImportant = (edge.getTo().latest - edge.getFrom().earliest == edge.getLength());
		}
	}
	// </editor-fold>
	// <editor-fold defaultstate="collapsed" desc="Point">
	/**
	 * The point collection.
	 */
	private ArrayList<Point> points;

	public ArrayList<Point> getPoints()
	{
		return points;
	}

	/**
	 * Add a point.
	 * @param point The point to be added.
	 * @return The added point.
	 */
	public Point addPoint(Point point)
	{
		this.points.add(point);
		solve();
		return point;
	}

	/**
	 * Remove the point.
	 * @param point The point to be removed.
	 */
	public void removePoint(Point point)
	{
		for (int i = edges.size() - 1; i >= 0; i--)
		{
			if (edges.get(i).getFrom() == point || edges.get(i).getTo() == point)
			{
				removeEdge(i);
			}
		}
		points.remove(point);
		solve();
	}
	// </editor-fold>
	// <editor-fold defaultstate="collapsed" desc="Edge">
	/**
	 * The edge collection.
	 */
	private ArrayList<Edge> edges;

	public ArrayList<Edge> getEdges()
	{
		return edges;
	}

	/**
	 * Judeg wether from one point can reach the other point.
	 * @param from The start point.
	 * @param to The to point.
	 * @return True if can, otherwise false.
	 */
	private boolean canReach(Point from, Point to)
	{
		from.visited = true;
		for (Edge edge : from.getOutEdges())
		{
			if (edge.getTo() == to)
			{
				return true;
			}
			if (!edge.getTo().visited && canReach(edge.getTo(), to))
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * Judeg wether the graph is a DAG if a new edge is added.
	 * @param from The start point of the edge.
	 * @param to The end point of the edge.
	 * @return True if can, otherwise false.
	 */
	public boolean canAddEdge(Point from, Point to)
	{
		if (from == null || to == null || !points.contains(from) || !points.contains(to) || from.isPointTo(to) || from == to)
		{
			return false;
		}
		for (Point point : points)
		{
			point.visited = false;
		}
		if (canReach(to, from))
		{
			return false;
		}
		return true;
	}

	/**
	 * Add an edge.
	 * @param from The start point of the edge.
	 * @param to The end point of the edge.
	 * @param length The length of the edge.
	 * @return The added edge if added, otherwise null.
	 */
	public Edge addEdge(Point from, Point to, int length)
	{
		if (canAddEdge(from, to))
		{
			Edge edge = new Edge(from, to, length);
			from.addOutEdge(edge);
			to.addInEdge(edge);
			this.edges.add(edge);
			solve();
			return edge;
		}
		return null;
	}

	/**
	 * Remove the edge.
	 * @param index The index of the edge to be removed.
	 */
	public void removeEdge(int index)
	{
		edges.get(index).getFrom().removeOutEdge(edges.get(index));
		edges.get(index).getTo().removeInEdge(edges.get(index));
		edges.remove(index);
		solve();
	}

	/**
	 * Remove the edge.
	 * @param edge The edge to be removed.
	 */
	public void removeEdge(Edge edge)
	{
		edge.getFrom().removeOutEdge(edge);
		edge.getTo().removeInEdge(edge);
		edges.remove(edge);
		solve();
	}
	// </editor-fold>
	// <editor-fold defaultstate="collapsed" desc="Save Load">

	public boolean saveToFile(String fileName)
	{
		try
		{
			BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
			writer.write(Integer.toString(points.size()));
			writer.newLine();
			for (Point point : points)
			{
				writer.write(point.getName());
				writer.newLine();
				writer.write(Integer.toString(point.getLocation().getX()));
				writer.newLine();
				writer.write(Integer.toString(point.getLocation().getY()));
				writer.newLine();
			}
			for (Point point : points)
			{
				writer.write(Integer.toString(point.getOutEdges().size()));
				writer.newLine();
				for (Edge edge : point.getOutEdges())
				{
					writer.write(Integer.toString(points.indexOf(edge.getTo())));
					writer.newLine();
					writer.write(Integer.toString(edge.getLength()));
					writer.newLine();
				}
			}
			writer.close();
			return true;
		}
		catch (Exception e)
		{
		}
		return false;
	}

	public boolean readFromFile(File file)
	{
		try
		{
			BufferedReader reader = new BufferedReader(new FileReader(file));
			int numOfPoint = Integer.parseInt(reader.readLine());
			clear();
			for (int i = 0; i < numOfPoint; i++)
			{
				Point point = new Point();
				point.setName(reader.readLine());
				point.setLocation(Integer.parseInt(reader.readLine()), Integer.parseInt(reader.readLine()));
				points.add(point);
			}
			for (int i = 0; i < numOfPoint; i++)
			{
				int numOfEdge = Integer.parseInt(reader.readLine());
				for (int j = 0; j < numOfEdge; j++)
				{
					int to = Integer.parseInt(reader.readLine());
					int len = Integer.parseInt(reader.readLine());
					Edge edge = new Edge(points.get(i), points.get(to), len);
					edges.add(edge);
					points.get(i).addOutEdge(edge);
					points.get(to).addInEdge(edge);
				}
			}
			reader.close();
			solve();
			return true;
		}
		catch (Exception e)
		{
		}
		return false;
	}
	// </editor-fold>
}
