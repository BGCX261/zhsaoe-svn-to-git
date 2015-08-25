package aoe;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.font.TextLayout;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Arrays;
import java.util.Comparator;
import javax.swing.JTextField;
import javax.swing.JToolBar;

/**
 * Draw the AOE graph and the result chart, and process user events.
 *
 * @author ZHS
 */
public class AOEDrawer
{
	// <editor-fold defaultstate="collapsed" desc="Init">

	/**
	 * The graph to draw
	 */
	private Graph aoe;

	/**
	 * Creates a new AOEDrawer.
	 * @param tbarPointProperties The used control
	 * @param txtPointName The used control
	 * @param tbarEdgeProperties The used control
	 * @param txtEdgeLength The used control
	 */
	public AOEDrawer(JToolBar tbarPointProperties, JTextField txtPointName, JToolBar tbarEdgeProperties, JTextField txtEdgeLength)
	{
		aoe = new Graph();
		this.tbarPointProperties = tbarPointProperties;
		this.txtPointName = txtPointName;
		this.tbarEdgeProperties = tbarEdgeProperties;
		this.txtEdgeLength = txtEdgeLength;
		mouseLocation = new Location();
		downLocation = new Location();
		tbarPointProperties.setVisible(false);
		tbarEdgeProperties.setVisible(false);
		toolState = EnumToolStates.Choose;
		selectedPoint = null;
		hightLightPoint = null;
		selectedEdge = null;
		hightLightEdge = null;
	}
	// </editor-fold>
	// <editor-fold defaultstate="collapsed" desc="Changed">
	/**
	 * Whether it's changed
	 */
	private boolean changed = false;

	/**
	 * Get whether it's changed
	 * @return True if changed, otherwise false
	 */
	public boolean isChanged()
	{
		return changed;
	}

	/**
	 * Set whether it's changed
	 * @param changed
	 */
	public void setChanged(boolean changed)
	{
		this.changed = changed;
	}
	// </editor-fold>
	// <editor-fold defaultstate="collapsed" desc="Clear">

	/**
	 * Clear it
	 */
	public void clear()
	{
		aoe.clear();
		tbarPointProperties.setVisible(false);
		tbarEdgeProperties.setVisible(false);
		selectedPoint = null;
		hightLightPoint = null;
		selectedEdge = null;
		hightLightEdge = null;
		changed = false;
	}
	// </editor-fold>
	// <editor-fold defaultstate="collapsed" desc="Related User Controls">
	private JToolBar tbarPointProperties;
	private JTextField txtPointName;
	private JToolBar tbarEdgeProperties;
	private JTextField txtEdgeLength;
	// </editor-fold>
	// <editor-fold defaultstate="collapsed" desc="MovePoint">

	private void moveSelectedPoint(int x, int y)
	{
		if (selectedPoint != null)
		{
			selectedPoint.setLocation(x, y);
		}
	}
	// </editor-fold>
	// <editor-fold defaultstate="collapsed" desc="ColorSettings">
	private Color fontColor = Color.black;
	private Color importantEdgeFontColor = Color.red;
	private Color backGroundColor = Color.white;
	private Color selecterColor = Color.black;
	private Color pointColor = Color.lightGray;
	private Color edgeColor = Color.black;
	private Color importantEdgeColor = Color.blue;
	private Color demoEdgeColor = Color.black;
	private Color gridColor = Color.black;
	private Color earliestLineColor = Color.green;
	private Color latestLineColor = Color.pink;

	// <editor-fold defaultstate="collapsed" desc="Getter & Setter">
	public Color getBackGroundColor()
	{
		return backGroundColor;
	}

	public void setBackGroundColor(Color backGroundColor)
	{
		this.backGroundColor = backGroundColor;
	}

	public Color getDemoEdgeColor()
	{
		return demoEdgeColor;
	}

	public void setDemoEdgeColor(Color demoEdgeColor)
	{
		this.demoEdgeColor = demoEdgeColor;
	}

	public Color getEarliestLineColor()
	{
		return earliestLineColor;
	}

	public void setEarliestLineColor(Color earliestLineColor)
	{
		this.earliestLineColor = earliestLineColor;
	}

	public Color getEdgeColor()
	{
		return edgeColor;
	}

	public void setEdgeColor(Color edgeColor)
	{
		this.edgeColor = edgeColor;
	}

	public Color getFontColor()
	{
		return fontColor;
	}

	public void setFontColor(Color fontColor)
	{
		this.fontColor = fontColor;
	}

	public Color getGridColor()
	{
		return gridColor;
	}

	public void setGridColor(Color gridColor)
	{
		this.gridColor = gridColor;
	}

	public Color getImportantEdgeColor()
	{
		return importantEdgeColor;
	}

	public void setImportantEdgeColor(Color importantEdgeColor)
	{
		this.importantEdgeColor = importantEdgeColor;
	}

	public Color getLatestLineColor()
	{
		return latestLineColor;
	}

	public void setLatestLineColor(Color latestLineColor)
	{
		this.latestLineColor = latestLineColor;
	}

	public Color getPointColor()
	{
		return pointColor;
	}

	public void setPointColor(Color pointColor)
	{
		this.pointColor = pointColor;
	}

	public Color getSelecterColor()
	{
		return selecterColor;
	}

	public void setSelecterColor(Color selecterColor)
	{
		this.selecterColor = selecterColor;
	}
	// </editor-fold>
	// </editor-fold>
	// <editor-fold defaultstate="collapsed" desc="SetProperties">

	public void updateProperties()
	{
		if (selectedPoint != null)
		{
			if (selectedPoint.getName() == null ? txtPointName.getText() != null : !selectedPoint.getName().equals(txtPointName.getText()))
			{
				selectedPoint.setName(txtPointName.getText());
				changed = true;
			}
		}
		else if (selectedEdge != null)
		{
			try
			{
				int length = Integer.parseInt(txtEdgeLength.getText());
				if (length >= 0 && length != selectedEdge.getLength())
				{
					selectedEdge.setLength(length);
					aoe.solve();
					changed = true;
				}
			}
			catch (Exception e)
			{
			}
		}
	}
	// </editor-fold>
	// <editor-fold defaultstate="collapsed" desc="GetByPoint">
	private Point selectedPoint;
	private Point hightLightPoint;
	private Edge selectedEdge;
	private Edge hightLightEdge;

	private Point getPoint(int x, int y)
	{
		if (aoe.getPoints().isEmpty())
		{
			return null;
		}
		for (Point point : aoe.getPoints())
		{
			int dxi = x - point.getLocation().getX();
			int dyi = y - point.getLocation().getY();
			if (dxi * dxi + dyi * dyi <= pointRadius * pointRadius)
			{
				return point;
			}
		}
		return null;
	}

	private Edge getEdge(int x, int y)
	{
		for (Edge edge : aoe.getEdges())
		{
			int x1 = edge.getFrom().getLocation().getX();
			int y1 = edge.getFrom().getLocation().getY();
			int x2 = edge.getTo().getLocation().getX();
			int y2 = edge.getTo().getLocation().getY();
			int dx = x1 - x2;
			int dy = y1 - y2;
			int len = (int) Math.sqrt(dx * dx + dy * dy);
			x1 -= dx * pointRadius / len;
			y1 -= dy * pointRadius / len;
			x2 += dx * pointRadius / len;
			y2 += dy * pointRadius / len;
			if (Math.abs((y2 - y1) * x - (x2 - x1) * y - x1 * y2 + x2 * y1) / Math.sqrt((y2 - y1) * (y2 - y1) + (x2 - x1) * (x2 - x1)) <= pointRadius && (x - x1) * (x2 - x1) + (y - y1) * (y2 - y1) >= 0 && (x - x2) * (x1 - x2) + (y - y2) * (y1 - y2) >= 0)
			{
				return edge;
			}
		}
		return null;
	}

	private void selectPoint(int x, int y)
	{
		Point point = getPoint(x, y);
		selectPoint(point);
	}

	private void selectPoint(Point point)
	{
		updateProperties();
		selectedPoint = point;
		selectedEdge = null;
		tbarPointProperties.setVisible(point != null);
		tbarEdgeProperties.setVisible(false);
		if (point != null)
		{
			txtPointName.setText(point.getName());
			txtPointName.requestFocus();
			txtPointName.selectAll();
		}
	}

	private void selectEdge(int x, int y)
	{
		Edge edge = getEdge(x, y);
		selectEdge(edge);
	}

	private void selectEdge(Edge edge)
	{
		updateProperties();
		selectedEdge = edge;
		selectedPoint = null;
		tbarEdgeProperties.setVisible(edge != null);
		tbarPointProperties.setVisible(false);
		if (edge != null)
		{
			txtEdgeLength.setText(Integer.toString(edge.getLength()));
			txtEdgeLength.requestFocus();
			txtEdgeLength.selectAll();
		}
	}
	// </editor-fold>
	// <editor-fold defaultstate="collapsed" desc="Graph">
	private int pointRadius = 10;
	private int arrowSize = 10;
	private EnumToolStates toolState;
	// <editor-fold defaultstate="collapsed" desc="Getter&Setter">

	public int getArrowSize()
	{
		return arrowSize;
	}

	public void setArrowSize(int arrowSize)
	{
		this.arrowSize = arrowSize;
	}

	public void setLeftButtonDown()
	{
		if (leftButtonDown)
		{
			return;
		}
		leftButtonDown = true;
	}

	public void setRightButtonDown()
	{
		if (rightButtonDown)
		{
			return;
		}
		rightButtonDown = true;
	}

	public int getPointRadius()
	{
		return pointRadius;
	}

	public void setPointRadius(int pointRadius)
	{
		this.pointRadius = pointRadius;
	}

	public EnumToolStates getToolState()
	{
		return toolState;
	}

	public void setToolState(EnumToolStates toolState)
	{
		this.toolState = toolState;
	}
	// </editor-fold>

	public Image getGraphImage(int width, int height)
	{
		if (width <= 0 || height <= 0)
		{
			return null;
		}
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2D = image.createGraphics();
		g2D.setColor(backGroundColor);
		g2D.fillRect(0, 0, image.getWidth(), image.getHeight());
		if (aoe.getPoints().isEmpty())
		{
			String emptyString = "Nothing. Choose \"Add Point\" tool to add a point.";
			TextLayout tl = new TextLayout(emptyString, g2D.getFont(), g2D.getFontRenderContext());
			g2D.setColor(fontColor);
			g2D.drawString(emptyString, (int) (width - tl.getBounds().getWidth()) / 2, (int) (height - tl.getBounds().getHeight()) / 2);
			return image;
		}
		for (Point point : aoe.getPoints())
		{
			g2D.setColor(pointColor);
			DrawPoint(g2D, point);
		}
		if (hightLightPoint != null)
		{
			g2D.setColor(selecterColor);
			DrawHighLightPoint(g2D, hightLightPoint);
		}
		if (selectedPoint != null)
		{
			g2D.setColor(selecterColor);
			DrawSelectedPoint(g2D, selectedPoint);
		}
		for (Edge edge : aoe.getEdges())
		{
			if (edge.isImportant)
			{
				g2D.setColor(importantEdgeColor);
			}
			else
			{
				g2D.setColor(edgeColor);
			}
			DrawEdge(g2D, edge.getFrom(), edge.getTo());
		}
		if (hightLightEdge != null)
		{
			g2D.setColor(selecterColor);
			DrawHightLightEdge(g2D, hightLightEdge.getFrom(), hightLightEdge.getTo());
		}
		if (selectedEdge != null)
		{
			g2D.setColor(selecterColor);
			DrawSelectedEdge(g2D, selectedEdge.getFrom(), selectedEdge.getTo());
		}
		switch (toolState)
		{
			case AddEdge:
				if (leftButtonDown && selectedPoint != null)
				{
					if (aoe.canAddEdge(selectedPoint, hightLightPoint))
					{
						g2D.setColor(demoEdgeColor);
						DrawEdge(g2D, selectedPoint, hightLightPoint);
					}
					else
					{
						g2D.setColor(demoEdgeColor);
						DrawDemoEdge(g2D, selectedPoint, mouseLocation);
					}
				}
				break;
		}
		for (Point point : aoe.getPoints())
		{
			g2D.setColor(fontColor);
			g2D.drawString(point.getName() + '(' + point.earliest + '/' + point.latest + ')', point.getLocation().getX() + pointRadius, point.getLocation().getY());
		}
		for (Edge edge : aoe.getEdges())
		{
			g2D.setColor(fontColor);
			g2D.drawString(Integer.toString(edge.getLength()), (edge.getFrom().getLocation().getX() + edge.getTo().getLocation().getX()) / 2, (edge.getFrom().getLocation().getY() + edge.getTo().getLocation().getY()) / 2);
		}
		g2D.dispose();
		return image;
	}

	private void DrawPoint(Graphics2D g2D, Point point)
	{
		g2D.fillOval(point.getLocation().getX() - pointRadius, point.getLocation().getY() - pointRadius, pointRadius * 2, pointRadius * 2);
	}

	private void DrawSelectedPoint(Graphics2D g2D, Point point)
	{
		g2D.drawRect(point.getLocation().getX() - pointRadius - 1, point.getLocation().getY() - pointRadius - 1, pointRadius * 2 + 2, pointRadius * 2 + 2);
	}

	private void DrawHighLightPoint(Graphics2D g2D, Point point)
	{
		g2D.drawLine(point.getLocation().getX() - pointRadius, point.getLocation().getY() - pointRadius, point.getLocation().getX() - pointRadius * 3 / 4, point.getLocation().getY() - pointRadius);
		g2D.drawLine(point.getLocation().getX() - pointRadius, point.getLocation().getY() - pointRadius, point.getLocation().getX() - pointRadius, point.getLocation().getY() - pointRadius * 3 / 4);
		g2D.drawLine(point.getLocation().getX() - pointRadius, point.getLocation().getY() + pointRadius, point.getLocation().getX() - pointRadius * 3 / 4, point.getLocation().getY() + pointRadius);
		g2D.drawLine(point.getLocation().getX() - pointRadius, point.getLocation().getY() + pointRadius, point.getLocation().getX() - pointRadius, point.getLocation().getY() + pointRadius * 3 / 4);
		g2D.drawLine(point.getLocation().getX() + pointRadius, point.getLocation().getY() - pointRadius, point.getLocation().getX() + pointRadius * 3 / 4, point.getLocation().getY() - pointRadius);
		g2D.drawLine(point.getLocation().getX() + pointRadius, point.getLocation().getY() - pointRadius, point.getLocation().getX() + pointRadius, point.getLocation().getY() - pointRadius * 3 / 4);
		g2D.drawLine(point.getLocation().getX() + pointRadius, point.getLocation().getY() + pointRadius, point.getLocation().getX() + pointRadius * 3 / 4, point.getLocation().getY() + pointRadius);
		g2D.drawLine(point.getLocation().getX() + pointRadius, point.getLocation().getY() + pointRadius, point.getLocation().getX() + pointRadius, point.getLocation().getY() + pointRadius * 3 / 4);
	}

	private void DrawEdge(Graphics2D g2D, Point from, Point to)
	{
		int x1 = from.getLocation().getX();
		int y1 = from.getLocation().getY();
		int x2 = to.getLocation().getX();
		int y2 = to.getLocation().getY();
		int dx = x1 - x2;
		int dy = y1 - y2;
		int len = (int) Math.sqrt(dx * dx + dy * dy);
		if (len < pointRadius * 2)
		{
			return;
		}
		x1 -= dx * pointRadius / len;
		y1 -= dy * pointRadius / len;
		x2 += dx * pointRadius / len;
		y2 += dy * pointRadius / len;
		dx = dx * arrowSize / len;
		dy = dy * arrowSize / len;
		g2D.drawLine(x1, y1, x2, y2);
		g2D.drawLine(x2, y2, x2 + dx - dy / 2, y2 + dy + dx / 2);
		g2D.drawLine(x2, y2, x2 + dx + dy / 2, y2 + dy - dx / 2);
	}

	private void DrawDemoEdge(Graphics2D g2D, Point from, Location to)
	{
		int x1 = from.getLocation().getX();
		int y1 = from.getLocation().getY();
		int x2 = to.getX();
		int y2 = to.getY();
		int dx = x1 - x2;
		int dy = y1 - y2;
		int len = (int) Math.sqrt(dx * dx + dy * dy);
		if (len < pointRadius)
		{
			return;
		}
		x1 -= dx * pointRadius / len;
		y1 -= dy * pointRadius / len;
		dx = dx * arrowSize / len;
		dy = dy * arrowSize / len;
		g2D.drawLine(x1, y1, x2, y2);
		g2D.drawLine(x2, y2, x2 + dx - dy / 2, y2 + dy + dx / 2);
		g2D.drawLine(x2, y2, x2 + dx + dy / 2, y2 + dy - dx / 2);
	}

	private void DrawSelectedEdge(Graphics2D g2D, Point from, Point to)
	{
		int x1 = from.getLocation().getX();
		int y1 = from.getLocation().getY();
		int x2 = to.getLocation().getX();
		int y2 = to.getLocation().getY();
		int dx = x2 - x1;
		int dy = y2 - y1;
		int len = (int) Math.sqrt(dx * dx + dy * dy);
		if (len < pointRadius * 2)
		{
			return;
		}
		x1 += dx * pointRadius / len;
		y1 += dy * pointRadius / len;
		x2 -= dx * pointRadius / len;
		y2 -= dy * pointRadius / len;
		dx = dx * arrowSize / len;
		dy = dy * arrowSize / len;
		g2D.drawLine(x1 - dy, y1 + dx, x1 + dy, y1 - dx);
		g2D.drawLine(x1 + dy, y1 - dx, x2 + dy, y2 - dx);
		g2D.drawLine(x2 + dy, y2 - dx, x2 - dy, y2 + dx);
		g2D.drawLine(x2 - dy, y2 + dx, x1 - dy, y1 + dx);
	}

	private void DrawHightLightEdge(Graphics2D g2D, Point from, Point to)
	{
		int x1 = from.getLocation().getX();
		int y1 = from.getLocation().getY();
		int x2 = to.getLocation().getX();
		int y2 = to.getLocation().getY();
		int dx = x2 - x1;
		int dy = y2 - y1;
		int len = (int) Math.sqrt(dx * dx + dy * dy);
		if (len < pointRadius * 2)
		{
			return;
		}
		x1 += dx * (pointRadius + 1) / len;
		y1 += dy * (pointRadius + 1) / len;
		x2 -= dx * (pointRadius + 1) / len;
		y2 -= dy * (pointRadius + 1) / len;
		dx = dx * (arrowSize - 1) / len;
		dy = dy * (arrowSize - 1) / len;
		g2D.drawLine(x1 - dy, y1 + dx, x1 - dy + dx * 3 / 4, y1 + dx + dy * 3 / 4);
		g2D.drawLine(x1 - dy, y1 + dx, x1 - dy + dy * 3 / 4, y1 + dx - dx * 3 / 4);
		g2D.drawLine(x1 + dy, y1 - dx, x1 + dy + dx * 3 / 4, y1 - dx + dy * 3 / 4);
		g2D.drawLine(x1 + dy, y1 - dx, x1 + dy - dy * 3 / 4, y1 - dx + dx * 3 / 4);
		g2D.drawLine(x2 + dy, y2 - dx, x2 + dy - dx * 3 / 4, y2 - dx - dy * 3 / 4);
		g2D.drawLine(x2 + dy, y2 - dx, x2 + dy - dy * 3 / 4, y2 - dx + dx * 3 / 4);
		g2D.drawLine(x2 - dy, y2 + dx, x2 - dy - dx * 3 / 4, y2 + dx - dy * 3 / 4);
		g2D.drawLine(x2 - dy, y2 + dx, x2 - dy + dy * 3 / 4, y2 + dx - dx * 3 / 4);
	}
	// </editor-fold>
	// <editor-fold defaultstate="collapsed" desc="Chart">

	public Image getChartImage(int width, int height)
	{
		if (width <= 0 || height <= 0)
		{
			return null;
		}
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2D = image.createGraphics();
		g2D.setColor(backGroundColor);
		g2D.fillRect(0, 0, image.getWidth(), image.getHeight());
		int latest = 0;
		for (Point point : aoe.getPoints())
		{
			latest = Math.max(latest, point.latest);
		}
		if (latest == 0 || aoe.getEdges().isEmpty())
		{
			String emptyString = "Nothing. Add some edge or set some edge's length positive.";
			TextLayout tl = new TextLayout(emptyString, g2D.getFont(), g2D.getFontRenderContext());
			g2D.setColor(fontColor);
			g2D.drawString(emptyString, (int) (width - tl.getBounds().getWidth()) / 2, (int) (height - tl.getBounds().getHeight()) / 2);
			return image;
		}
		Object[] edgeArray = aoe.getEdges().toArray();
		Arrays.sort(edgeArray, new Comparator()
		{

			public int compare(Object o1, Object o2)
			{
				Edge e1 = (Edge) o1;
				Edge e2 = (Edge) o2;
				if (e1.getFrom().earliest == e2.getFrom().earliest)
				{
					if (e1.getTo().latest - e1.getLength() == e2.getTo().latest - e2.getLength())
					{
						return e1.getLength() - e2.getLength();
					}
					return (e1.getTo().latest - e1.getLength()) - (e2.getTo().latest - e2.getLength());
				}
				return e1.getFrom().earliest - e2.getFrom().earliest;
			}
		});
		int marginTop = 20;
		int marginBottom = 40;
		int marginLeft = 50;
		int marginRight = 20;
		int textLeft = 10;
		int chartWidth = width - marginLeft - marginRight;
		int chartHeight = height - marginTop - marginBottom;
		int gridWidth = chartWidth / latest;
		int gridHeight = chartHeight / edgeArray.length;
		int lineWidth = 5;
		int lineParding = 5;
		int lineMarginTop = (gridHeight - lineWidth * 2 - lineParding) / 2;
		int legendLength = 50;
		int legendHeight = 10;
		for (int i = 0; i <= latest; i++)
		{
			g2D.setColor(fontColor);
			g2D.drawString(Integer.toString(i), marginLeft + i * gridWidth, marginTop - 3);
			g2D.setColor(gridColor);
			g2D.drawLine(marginLeft + i * gridWidth, marginTop, marginLeft + i * gridWidth, marginTop + edgeArray.length * gridHeight);
		}
		for (int i = 0; i < edgeArray.length; i++)
		{
			Edge edge = (Edge) edgeArray[i];
			g2D.setColor(gridColor);
			g2D.drawLine(marginLeft, marginTop + i * gridHeight, marginLeft + latest * gridWidth, marginTop + i * gridHeight);
			g2D.setColor(earliestLineColor);
			g2D.fillRect(marginLeft + edge.getFrom().earliest * gridWidth, marginTop + i * gridHeight + lineMarginTop, edge.getLength() * gridWidth, lineWidth);
			g2D.setColor(latestLineColor);
			g2D.fillRect(marginLeft + (edge.getTo().latest - edge.getLength()) * gridWidth, marginTop + i * gridHeight + lineWidth + lineParding + lineMarginTop, edge.getLength() * gridWidth, lineWidth);
			if (edge.isImportant)
			{
				g2D.setColor(importantEdgeFontColor);
			}
			else
			{
				g2D.setColor(fontColor);
			}
			g2D.drawString(edge.getFrom().getName() + " - " + edge.getTo().getName(), textLeft, marginTop + i * gridHeight + lineWidth + lineParding + lineMarginTop);
		}
		g2D.setColor(gridColor);
		g2D.drawLine(marginLeft, marginTop + edgeArray.length * gridHeight, marginLeft + latest * gridWidth, marginTop + edgeArray.length * gridHeight);
		g2D.setColor(earliestLineColor);
		g2D.fillRect(marginLeft, marginTop + edgeArray.length * gridHeight + legendHeight, legendLength, lineWidth);
		g2D.setColor(latestLineColor);
		g2D.fillRect(marginLeft, marginTop + edgeArray.length * gridHeight + lineWidth + lineParding + legendHeight, legendLength, lineWidth);
		g2D.setColor(fontColor);
		g2D.drawString("Earliest", marginLeft + legendLength, marginTop + edgeArray.length * gridHeight + lineWidth + legendHeight);
		g2D.drawString("Latest", marginLeft + legendLength, marginTop + edgeArray.length * gridHeight + lineWidth + lineParding + lineWidth + legendHeight);
		g2D.dispose();
		return image;
	}
	// </editor-fold>
	// <editor-fold defaultstate="collapsed" desc="Event">
	private Location mouseLocation;
	private Location downLocation;
	private boolean leftButtonDown;
	private boolean rightButtonDown;

	public void setDownLocation(int x, int y)
	{
		leftButtonDown = true;
		downLocation.setXY(x, y);
		switch (toolState)
		{
			case Choose:
				selectPoint(x, y);
				if (selectedPoint == null)
				{
					selectEdge(x, y);
				}
				break;
			case AddPoint:
				selectPoint(aoe.addPoint(new Point(x, y)));
				changed = true;
				break;
			case AddEdge:
				selectPoint(x, y);
				break;
			case Delete:
				selectPoint(x, y);
				if (selectedPoint != null)
				{
					aoe.removePoint(selectedPoint);
					tbarPointProperties.setVisible(false);
					changed = true;
					selectedPoint = null;
					hightLightPoint = null;
				}
				else
				{
					selectEdge(x, y);
					if (selectedEdge != null)
					{
						aoe.removeEdge(selectedEdge);
						tbarEdgeProperties.setVisible(false);
						changed = true;
						selectedEdge = null;
						hightLightEdge = null;
					}
				}
				break;
		}
	}

	public void setMouseLocation(int x, int y)
	{
		switch (toolState)
		{
			case Choose:
				hightLightPoint = getPoint(x, y);
				hightLightEdge = getEdge(x, y);
				if (leftButtonDown && selectedPoint != null)
				{
					moveSelectedPoint(x, y);
					changed = true;
				}
				break;
			case AddEdge:
				hightLightPoint = getPoint(x, y);
				if (leftButtonDown && !aoe.canAddEdge(selectedPoint, hightLightPoint))
				{
					hightLightPoint = null;
				}
				break;
			case MoveAll:
				if (leftButtonDown)
				{
					for (Point point : aoe.getPoints())
					{
						point.setLocation(point.getLocation().getX() + x - mouseLocation.getX(), point.getLocation().getY() + y - mouseLocation.getY());
					}
					changed = true;
				}
				break;
			case Delete:
				hightLightPoint = getPoint(x, y);
				hightLightEdge = getEdge(x, y);
				break;
			default:
		}
		mouseLocation.setXY(x, y);
	}

	public void setLeftButtonUp()
	{
		if (!leftButtonDown)
		{
			return;
		}
		leftButtonDown = false;
		switch (toolState)
		{
			case AddEdge:
				selectEdge(aoe.addEdge(selectedPoint, getPoint(mouseLocation.getX(), mouseLocation.getY()), 0));
				changed = true;
				break;
		}
		setMouseLocation(mouseLocation.getX(), mouseLocation.getY());
	}
	// </editor-fold>
	// <editor-fold defaultstate="collapsed" desc="Save Load">

	public boolean saveToFile(String fileName)
	{
		if (aoe.saveToFile(fileName))
		{
			changed = false;
			return true;
		}
		return false;
	}

	public boolean readFromFile(File file)
	{
		Graph newAoe = new Graph();
		if (newAoe.readFromFile(file))
		{
			selectedPoint = null;
			hightLightPoint = null;
			selectedEdge = null;
			hightLightEdge = null;
			changed = false;
			aoe = newAoe;
			return true;
		}
		return false;
	}
	// </editor-fold>
}
