/*
 * Main.java
 *
 * Created on 2010-9-6, 12:17:41
 */
package aoe;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import javax.swing.JColorChooser;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

/**
 *
 * @author Administrator
 */
public class Main extends javax.swing.JFrame
{

	private String settingFileName = "settings";
	private AOEDrawer aoe;
	private String windowTitle = "AOE";
	private String currentFileName = "Untitled";
	private boolean isNewFile = true;
	private Cursor pointCursor;
	private Cursor edgeCursor;
	private Cursor deleteCursor;

	/** Creates new form Main */
	public Main()
	{
		initComponents();
		init();
	}

	// <editor-fold defaultstate="collapsed" desc="Form">
	private void init()
	{
		aoe = new AOEDrawer(tbarPointProperties, txtPointName, tbarEdgeProperties, txtEdgeLength);
		createCursors();
		if (!loadSetings())
		{
			saveSetings();
		}
		setLocationRelativeTo(null);
		dlgChart.setLocationRelativeTo(null);
		dlgSettings.setLocationRelativeTo(null);
		tbtnChoose.setSelected(true);
		fc.setFileFilter(new FileFilter()
		{

			@Override
			public boolean accept(File f)
			{
				if (f.isDirectory() || f.getName().toLowerCase().endsWith(".aoe"))
				{
					return true;
				}
				return false;
			}

			@Override
			public String getDescription()
			{
				return "AOE Files.";
			}
		});
		updateWindowTitle();
		redrawGraph();
	}

	private void setCurrentFileName(String currentFileName)
	{
		this.currentFileName = currentFileName;
		updateWindowTitle();
	}

	private void updateWindowTitle()
	{
		if (aoe.isChanged())
		{
			setTitle("*" + currentFileName + " - " + windowTitle);
		}
		else
		{
			setTitle(currentFileName + " - " + windowTitle);
		}
	}
	// </editor-fold>
	// <editor-fold defaultstate="collapsed" desc="Settings">

	private boolean saveSetings()
	{
		try
		{
			String fileName = AppPathGetter.getAppPath() + settingFileName;
			BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
			writer.write(Integer.toString(aoe.getPointColor().getRGB()));
			writer.newLine();
			writer.write(Integer.toString(aoe.getEdgeColor().getRGB()));
			writer.newLine();
			writer.write(Integer.toString(aoe.getImportantEdgeColor().getRGB()));
			writer.newLine();
			writer.close();
			return true;
		}
		catch (Exception e)
		{
		}
		return false;
	}

	private boolean loadSetings()
	{
		try
		{
			String fileName = AppPathGetter.getAppPath() + settingFileName;
			BufferedReader reader = new BufferedReader(new FileReader(fileName));
			aoe.setPointColor(new Color(Integer.parseInt(reader.readLine())));
			aoe.setEdgeColor(new Color(Integer.parseInt(reader.readLine())));
			aoe.setImportantEdgeColor(new Color(Integer.parseInt(reader.readLine())));
			reader.close();
			return true;
		}
		catch (Exception e)
		{
		}
		return false;
	}
	// </editor-fold>
	// <editor-fold defaultstate="collapsed" desc="Init">

	private void createCursors()
	{
		Toolkit tk = Toolkit.getDefaultToolkit();
		BufferedImage image;
		Graphics g;

		image = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
		g = image.createGraphics();
		g.setColor(Color.black);
		g.drawOval(0, 0, 31, 31);
		g.drawLine(14, 14, 18, 18);
		g.drawLine(14, 18, 18, 14);
		g.dispose();
		pointCursor = tk.createCustomCursor(image, new java.awt.Point(16, 16), "Point Cursor");

		image = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
		g = image.createGraphics();
		g.setColor(Color.black);
		g.drawLine(0, 0, 31, 31);
		g.drawLine(0, 31, 31, 0);
		g.dispose();
		edgeCursor = tk.createCustomCursor(image, new java.awt.Point(16, 16), "Edge Cursor");

		image = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
		g = image.createGraphics();
		g.setColor(Color.black);
		g.drawOval(11, 11, 9, 9);
		g.drawLine(0, 0, 31, 31);
		g.drawLine(0, 31, 31, 0);
		g.dispose();
		deleteCursor = tk.createCustomCursor(image, new java.awt.Point(16, 16), "Delete Cursor");
	}
	// </editor-fold>
	// <editor-fold defaultstate="collapsed" desc="Redraw">

	private void redrawGraph()
	{
		((ImagePanel) pnlAOE).setImage(aoe.getGraphImage(pnlAOE.getWidth(), pnlAOE.getHeight()));
		updateWindowTitle();
	}

	private void redrawChart()
	{
		((ImagePanel) pnlChart).setImage(aoe.getChartImage(pnlChart.getWidth(), pnlChart.getHeight()));
	}
	// </editor-fold>
	// <editor-fold defaultstate="collapsed" desc="Save">

	private boolean saveFile()
	{
		String fileName;
		if (!isNewFile)
		{
			fileName = currentFileName;
		}
		else
		{
			fc.setSelectedFile(new File(currentFileName));
			if (fc.showSaveDialog(this) != JFileChooser.APPROVE_OPTION)
			{
				return false;
			}
			fileName = fc.getSelectedFile().getPath();
			if (!fileName.toLowerCase().endsWith(".aoe"))
			{
				fileName += ".aoe";
			}
		}
		if (aoe.saveToFile(fileName))
		{
			isNewFile = false;
			setCurrentFileName(fileName);
			return true;
		}
		else
		{
			JOptionPane.showMessageDialog(this, "Save Failed", "AOE", JOptionPane.ERROR_MESSAGE);
			return false;
		}
	}

	private boolean saveAsFile()
	{
		String fileName;
		fc.setSelectedFile(new File(currentFileName));
		if (fc.showSaveDialog(this) != JFileChooser.APPROVE_OPTION)
		{
			return false;
		}
		fileName = fc.getSelectedFile().getPath();
		if (!fileName.toLowerCase().endsWith(".aoe"))
		{
			fileName += ".aoe";
		}
		if (aoe.saveToFile(fileName))
		{
			isNewFile = false;
			setCurrentFileName(fileName);
			return true;
		}
		else
		{
			JOptionPane.showMessageDialog(this, "Save Failed", "AOE", JOptionPane.ERROR_MESSAGE);
			return false;
		}
	}
	// </editor-fold>

	/** This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btngrpTool = new javax.swing.ButtonGroup();
        fc = new javax.swing.JFileChooser();
        dlgChart = new javax.swing.JDialog(this);
        pnlChart = new ImagePanel();
        dlgSettings = new javax.swing.JDialog(this);
        jPanel2 = new javax.swing.JPanel();
        pnlColorSettings = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        pnlPointColor = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        pnlEdgeColor = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        pnlImportantEdgeColor = new javax.swing.JPanel();
        tbar = new javax.swing.JToolBar();
        tbarTool = new javax.swing.JToolBar();
        btnNew = new javax.swing.JButton();
        btnLoad = new javax.swing.JButton();
        btnSave = new javax.swing.JButton();
        btnSaveAs = new javax.swing.JButton();
        spt1 = new javax.swing.JToolBar.Separator();
        tbtnChoose = new javax.swing.JToggleButton();
        tbtnAddPoint = new javax.swing.JToggleButton();
        tbtnAddEdge = new javax.swing.JToggleButton();
        tbtnMoveAll = new javax.swing.JToggleButton();
        tbtnDelete = new javax.swing.JToggleButton();
        spt2 = new javax.swing.JToolBar.Separator();
        tbtnChart = new javax.swing.JToggleButton();
        spt3 = new javax.swing.JToolBar.Separator();
        tbtnSettings = new javax.swing.JToggleButton();
        spt4 = new javax.swing.JToolBar.Separator();
        tbarEdgeProperties = new javax.swing.JToolBar();
        lblEdgeProperties = new javax.swing.JLabel();
        spt5 = new javax.swing.JToolBar.Separator();
        lblEdgeLength = new javax.swing.JLabel();
        txtEdgeLength = new javax.swing.JTextField();
        spt6 = new javax.swing.JToolBar.Separator();
        tbarPointProperties = new javax.swing.JToolBar();
        lblPointProperties = new javax.swing.JLabel();
        spt7 = new javax.swing.JToolBar.Separator();
        lblPointName = new javax.swing.JLabel();
        txtPointName = new javax.swing.JTextField();
        spt8 = new javax.swing.JToolBar.Separator();
        pnlAOE = new ImagePanel();

        dlgChart.setTitle("Chart");
        dlgChart.setMinimumSize(new java.awt.Dimension(600, 400));
        dlgChart.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentHidden(java.awt.event.ComponentEvent evt) {
                dlgChartComponentHidden(evt);
            }
        });

        pnlChart.setBackground(new java.awt.Color(255, 255, 255));
        pnlChart.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                pnlChartComponentResized(evt);
            }
        });

        javax.swing.GroupLayout pnlChartLayout = new javax.swing.GroupLayout(pnlChart);
        pnlChart.setLayout(pnlChartLayout);
        pnlChartLayout.setHorizontalGroup(
            pnlChartLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 574, Short.MAX_VALUE)
        );
        pnlChartLayout.setVerticalGroup(
            pnlChartLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 390, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout dlgChartLayout = new javax.swing.GroupLayout(dlgChart.getContentPane());
        dlgChart.getContentPane().setLayout(dlgChartLayout);
        dlgChartLayout.setHorizontalGroup(
            dlgChartLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlChart, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        dlgChartLayout.setVerticalGroup(
            dlgChartLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlChart, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        dlgSettings.setTitle("Settings");
        dlgSettings.setMinimumSize(new java.awt.Dimension(200, 260));
        dlgSettings.setResizable(false);
        dlgSettings.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentHidden(java.awt.event.ComponentEvent evt) {
                dlgSettingsComponentHidden(evt);
            }
            public void componentShown(java.awt.event.ComponentEvent evt) {
                dlgSettingsComponentShown(evt);
            }
        });

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Color:"));

        pnlColorSettings.setLayout(new java.awt.GridLayout(6, 1));

        jLabel5.setText("Point Color:");
        pnlColorSettings.add(jLabel5);

        pnlPointColor.setBackground(new java.awt.Color(255, 255, 255));
        pnlPointColor.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        pnlPointColor.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                pnlPointColorMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout pnlPointColorLayout = new javax.swing.GroupLayout(pnlPointColor);
        pnlPointColor.setLayout(pnlPointColorLayout);
        pnlPointColorLayout.setHorizontalGroup(
            pnlPointColorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 234, Short.MAX_VALUE)
        );
        pnlPointColorLayout.setVerticalGroup(
            pnlPointColorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 32, Short.MAX_VALUE)
        );

        pnlColorSettings.add(pnlPointColor);

        jLabel6.setText("Edge Color:");
        pnlColorSettings.add(jLabel6);

        pnlEdgeColor.setBackground(new java.awt.Color(255, 255, 255));
        pnlEdgeColor.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        pnlEdgeColor.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                pnlEdgeColorMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout pnlEdgeColorLayout = new javax.swing.GroupLayout(pnlEdgeColor);
        pnlEdgeColor.setLayout(pnlEdgeColorLayout);
        pnlEdgeColorLayout.setHorizontalGroup(
            pnlEdgeColorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 234, Short.MAX_VALUE)
        );
        pnlEdgeColorLayout.setVerticalGroup(
            pnlEdgeColorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 32, Short.MAX_VALUE)
        );

        pnlColorSettings.add(pnlEdgeColor);

        jLabel7.setText("Important Edge Color:");
        pnlColorSettings.add(jLabel7);

        pnlImportantEdgeColor.setBackground(new java.awt.Color(255, 255, 255));
        pnlImportantEdgeColor.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        pnlImportantEdgeColor.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                pnlImportantEdgeColorMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout pnlImportantEdgeColorLayout = new javax.swing.GroupLayout(pnlImportantEdgeColor);
        pnlImportantEdgeColor.setLayout(pnlImportantEdgeColorLayout);
        pnlImportantEdgeColorLayout.setHorizontalGroup(
            pnlImportantEdgeColorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 234, Short.MAX_VALUE)
        );
        pnlImportantEdgeColorLayout.setVerticalGroup(
            pnlImportantEdgeColorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 32, Short.MAX_VALUE)
        );

        pnlColorSettings.add(pnlImportantEdgeColor);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnlColorSettings, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addComponent(pnlColorSettings, javax.swing.GroupLayout.DEFAULT_SIZE, 216, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout dlgSettingsLayout = new javax.swing.GroupLayout(dlgSettings.getContentPane());
        dlgSettings.getContentPane().setLayout(dlgSettingsLayout);
        dlgSettingsLayout.setHorizontalGroup(
            dlgSettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dlgSettingsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        dlgSettingsLayout.setVerticalGroup(
            dlgSettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dlgSettingsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        setMinimumSize(new java.awt.Dimension(800, 600));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        tbar.setFloatable(false);

        tbarTool.setBorder(null);
        tbarTool.setFloatable(false);

        btnNew.setText("New");
        btnNew.setFocusable(false);
        btnNew.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnNew.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnNew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNewActionPerformed(evt);
            }
        });
        tbarTool.add(btnNew);

        btnLoad.setText("Load");
        btnLoad.setFocusable(false);
        btnLoad.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnLoad.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnLoad.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLoadActionPerformed(evt);
            }
        });
        tbarTool.add(btnLoad);

        btnSave.setText("Save");
        btnSave.setFocusable(false);
        btnSave.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnSave.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });
        tbarTool.add(btnSave);

        btnSaveAs.setText("Save As");
        btnSaveAs.setFocusable(false);
        btnSaveAs.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnSaveAs.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnSaveAs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveAsActionPerformed(evt);
            }
        });
        tbarTool.add(btnSaveAs);
        tbarTool.add(spt1);

        btngrpTool.add(tbtnChoose);
        tbtnChoose.setText("Choose");
        tbtnChoose.setFocusable(false);
        tbtnChoose.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        tbtnChoose.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        tbtnChoose.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                tbtnToolStateChanged(evt);
            }
        });
        tbarTool.add(tbtnChoose);

        btngrpTool.add(tbtnAddPoint);
        tbtnAddPoint.setText("Add Point");
        tbtnAddPoint.setFocusable(false);
        tbtnAddPoint.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        tbtnAddPoint.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        tbtnAddPoint.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                tbtnToolStateChanged(evt);
            }
        });
        tbarTool.add(tbtnAddPoint);

        btngrpTool.add(tbtnAddEdge);
        tbtnAddEdge.setText("Add Edge");
        tbtnAddEdge.setFocusable(false);
        tbtnAddEdge.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        tbtnAddEdge.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        tbtnAddEdge.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                tbtnToolStateChanged(evt);
            }
        });
        tbarTool.add(tbtnAddEdge);

        btngrpTool.add(tbtnMoveAll);
        tbtnMoveAll.setText("Move All");
        tbtnMoveAll.setFocusable(false);
        tbtnMoveAll.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        tbtnMoveAll.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        tbtnMoveAll.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                tbtnToolStateChanged(evt);
            }
        });
        tbarTool.add(tbtnMoveAll);

        btngrpTool.add(tbtnDelete);
        tbtnDelete.setText("Delete");
        tbtnDelete.setFocusable(false);
        tbtnDelete.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        tbtnDelete.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        tbtnDelete.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                tbtnToolStateChanged(evt);
            }
        });
        tbarTool.add(tbtnDelete);
        tbarTool.add(spt2);

        tbtnChart.setText("Chart");
        tbtnChart.setFocusable(false);
        tbtnChart.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        tbtnChart.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        tbtnChart.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tbtnChartActionPerformed(evt);
            }
        });
        tbarTool.add(tbtnChart);
        tbarTool.add(spt3);

        tbtnSettings.setText("Settings");
        tbtnSettings.setFocusable(false);
        tbtnSettings.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        tbtnSettings.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        tbtnSettings.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tbtnSettingsActionPerformed(evt);
            }
        });
        tbarTool.add(tbtnSettings);
        tbarTool.add(spt4);

        tbar.add(tbarTool);

        tbarEdgeProperties.setBorder(null);
        tbarEdgeProperties.setFloatable(false);

        lblEdgeProperties.setFont(new java.awt.Font("宋体", 1, 12));
        lblEdgeProperties.setText("Edge Properties: ");
        tbarEdgeProperties.add(lblEdgeProperties);
        tbarEdgeProperties.add(spt5);

        lblEdgeLength.setText("Length: ");
        tbarEdgeProperties.add(lblEdgeLength);

        txtEdgeLength.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPropertyActionPerformed(evt);
            }
        });
        txtEdgeLength.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtPropertyFocusLost(evt);
            }
        });
        tbarEdgeProperties.add(txtEdgeLength);
        tbarEdgeProperties.add(spt6);

        tbar.add(tbarEdgeProperties);

        tbarPointProperties.setBorder(null);
        tbarPointProperties.setFloatable(false);

        lblPointProperties.setFont(new java.awt.Font("宋体", 1, 12));
        lblPointProperties.setText("Point Properties: ");
        tbarPointProperties.add(lblPointProperties);
        tbarPointProperties.add(spt7);

        lblPointName.setText("Name: ");
        tbarPointProperties.add(lblPointName);

        txtPointName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPropertyActionPerformed(evt);
            }
        });
        txtPointName.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtPropertyFocusLost(evt);
            }
        });
        tbarPointProperties.add(txtPointName);
        tbarPointProperties.add(spt8);

        tbar.add(tbarPointProperties);

        pnlAOE.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                pnlAOEMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                pnlAOEMouseReleased(evt);
            }
        });
        pnlAOE.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                pnlAOEComponentResized(evt);
            }
        });
        pnlAOE.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                pnlAOEMouseDragged(evt);
            }
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                pnlAOEMouseMoved(evt);
            }
        });

        javax.swing.GroupLayout pnlAOELayout = new javax.swing.GroupLayout(pnlAOE);
        pnlAOE.setLayout(pnlAOELayout);
        pnlAOELayout.setHorizontalGroup(
            pnlAOELayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 872, Short.MAX_VALUE)
        );
        pnlAOELayout.setVerticalGroup(
            pnlAOELayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 386, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tbar, javax.swing.GroupLayout.DEFAULT_SIZE, 872, Short.MAX_VALUE)
            .addComponent(pnlAOE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(tbar, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlAOE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
	// <editor-fold defaultstate="collapsed" desc="pnlEvent">
    private void pnlAOEMousePressed(java.awt.event.MouseEvent evt)//GEN-FIRST:event_pnlAOEMousePressed
    {//GEN-HEADEREND:event_pnlAOEMousePressed
		switch (evt.getButton())
		{
			case MouseEvent.BUTTON1:
				aoe.setDownLocation(evt.getX(), evt.getY());
				redrawGraph();
				redrawChart();
				break;
			case MouseEvent.BUTTON2:
				tbtnMoveAll.setSelected(true);
				break;
			case MouseEvent.BUTTON3:
				tbtnChoose.setSelected(true);
				break;
		}
    }//GEN-LAST:event_pnlAOEMousePressed

    private void pnlAOEMouseReleased(java.awt.event.MouseEvent evt)//GEN-FIRST:event_pnlAOEMouseReleased
    {//GEN-HEADEREND:event_pnlAOEMouseReleased
		aoe.setLeftButtonUp();
		redrawGraph();
		redrawChart();
    }//GEN-LAST:event_pnlAOEMouseReleased

	private void pnlAOEMouseMoved(java.awt.event.MouseEvent evt)//GEN-FIRST:event_pnlAOEMouseMoved
	{//GEN-HEADEREND:event_pnlAOEMouseMoved
		aoe.setMouseLocation(evt.getX(), evt.getY());
		redrawGraph();
	}//GEN-LAST:event_pnlAOEMouseMoved

	private void pnlAOEMouseDragged(java.awt.event.MouseEvent evt)//GEN-FIRST:event_pnlAOEMouseDragged
	{//GEN-HEADEREND:event_pnlAOEMouseDragged
		aoe.setMouseLocation(evt.getX(), evt.getY());
		redrawGraph();
	}//GEN-LAST:event_pnlAOEMouseDragged

	private void pnlAOEComponentResized(java.awt.event.ComponentEvent evt)//GEN-FIRST:event_pnlAOEComponentResized
	{//GEN-HEADEREND:event_pnlAOEComponentResized
		redrawGraph();
	}//GEN-LAST:event_pnlAOEComponentResized
	// </editor-fold>
	// <editor-fold defaultstate="collapsed" desc="Tool">
	private void tbtnToolStateChanged(javax.swing.event.ChangeEvent evt)//GEN-FIRST:event_tbtnToolStateChanged
	{//GEN-HEADEREND:event_tbtnToolStateChanged
		if (tbtnChoose.isSelected())
		{
			pnlAOE.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
			aoe.setToolState(EnumToolStates.Choose);
		}
		else if (tbtnAddPoint.isSelected())
		{
			pnlAOE.setCursor(pointCursor);
			aoe.setToolState(EnumToolStates.AddPoint);
		}
		else if (tbtnAddEdge.isSelected())
		{
			pnlAOE.setCursor(edgeCursor);
			aoe.setToolState(EnumToolStates.AddEdge);
		}
		else if (tbtnMoveAll.isSelected())
		{
			pnlAOE.setCursor(new java.awt.Cursor(java.awt.Cursor.MOVE_CURSOR));
			aoe.setToolState(EnumToolStates.MoveAll);
		}
		else if (tbtnDelete.isSelected())
		{
			pnlAOE.setCursor(deleteCursor);
			aoe.setToolState(EnumToolStates.Delete);
		}
	}//GEN-LAST:event_tbtnToolStateChanged
	// </editor-fold>
	// <editor-fold defaultstate="collapsed" desc="txtProperty">
	private void txtPropertyActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_txtPropertyActionPerformed
	{//GEN-HEADEREND:event_txtPropertyActionPerformed
		aoe.updateProperties();
		redrawGraph();
		redrawChart();
	}//GEN-LAST:event_txtPropertyActionPerformed

	private void txtPropertyFocusLost(java.awt.event.FocusEvent evt)//GEN-FIRST:event_txtPropertyFocusLost
	{//GEN-HEADEREND:event_txtPropertyFocusLost
		aoe.updateProperties();
		redrawGraph();
		redrawChart();
	}//GEN-LAST:event_txtPropertyFocusLost
	// </editor-fold>
	// <editor-fold defaultstate="collapsed" desc="New Load Save">
	private void btnNewActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_btnNewActionPerformed
	{//GEN-HEADEREND:event_btnNewActionPerformed
		if (aoe.isChanged())
		{
			switch (JOptionPane.showConfirmDialog(this, "Modifies has been made. Save changes?", "AOE", JOptionPane.YES_NO_CANCEL_OPTION))
			{
				case JOptionPane.YES_OPTION:
					if (!saveFile())
					{
						return;
					}
					break;
				case JOptionPane.CANCEL_OPTION:
					return;
			}
		}
		aoe.clear();
		isNewFile = true;
		setCurrentFileName("Untitled");
		updateWindowTitle();
		redrawGraph();
		redrawChart();
	}//GEN-LAST:event_btnNewActionPerformed

	private void btnLoadActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_btnLoadActionPerformed
	{//GEN-HEADEREND:event_btnLoadActionPerformed
		if (aoe.isChanged())
		{
			switch (JOptionPane.showConfirmDialog(this, "Modifies has been made. Save changes?", "AOE", JOptionPane.YES_NO_CANCEL_OPTION))
			{
				case JOptionPane.YES_OPTION:
					if (!saveFile())
					{
						return;
					}
					break;
				case JOptionPane.CANCEL_OPTION:
					return;
			}
		}
		fc.setSelectedFile(null);
		if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
		{
			if (aoe.readFromFile(fc.getSelectedFile()))
			{
				isNewFile = false;
				setCurrentFileName(fc.getSelectedFile().getPath());
				updateWindowTitle();
				redrawGraph();
				redrawChart();
			}
			else
			{
				JOptionPane.showMessageDialog(this, "Load Failed", "AOE", JOptionPane.ERROR_MESSAGE);
			}
		}
	}//GEN-LAST:event_btnLoadActionPerformed

	private void btnSaveActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_btnSaveActionPerformed
	{//GEN-HEADEREND:event_btnSaveActionPerformed
		saveFile();
	}//GEN-LAST:event_btnSaveActionPerformed

	private void btnSaveAsActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_btnSaveAsActionPerformed
	{//GEN-HEADEREND:event_btnSaveAsActionPerformed
		saveAsFile();
	}//GEN-LAST:event_btnSaveAsActionPerformed
	// </editor-fold>
	// <editor-fold defaultstate="collapsed" desc="Chart">
	private void tbtnChartActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_tbtnChartActionPerformed
	{//GEN-HEADEREND:event_tbtnChartActionPerformed
		dlgChart.setVisible(tbtnChart.isSelected());
	}//GEN-LAST:event_tbtnChartActionPerformed

	private void dlgChartComponentHidden(java.awt.event.ComponentEvent evt)//GEN-FIRST:event_dlgChartComponentHidden
	{//GEN-HEADEREND:event_dlgChartComponentHidden
		tbtnChart.setSelected(false);
	}//GEN-LAST:event_dlgChartComponentHidden

	private void pnlChartComponentResized(java.awt.event.ComponentEvent evt)//GEN-FIRST:event_pnlChartComponentResized
	{//GEN-HEADEREND:event_pnlChartComponentResized
		redrawChart();
	}//GEN-LAST:event_pnlChartComponentResized
	// </editor-fold>
	// <editor-fold defaultstate="collapsed" desc="Settings">
	private void tbtnSettingsActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_tbtnSettingsActionPerformed
	{//GEN-HEADEREND:event_tbtnSettingsActionPerformed
		dlgSettings.setVisible(tbtnSettings.isSelected());
	}//GEN-LAST:event_tbtnSettingsActionPerformed

	private void dlgSettingsComponentHidden(java.awt.event.ComponentEvent evt)//GEN-FIRST:event_dlgSettingsComponentHidden
	{//GEN-HEADEREND:event_dlgSettingsComponentHidden
		tbtnSettings.setSelected(false);
	}//GEN-LAST:event_dlgSettingsComponentHidden

	private void dlgSettingsComponentShown(java.awt.event.ComponentEvent evt)//GEN-FIRST:event_dlgSettingsComponentShown
	{//GEN-HEADEREND:event_dlgSettingsComponentShown
		pnlPointColor.setBackground(aoe.getPointColor());
		pnlEdgeColor.setBackground(aoe.getEdgeColor());
		pnlImportantEdgeColor.setBackground(aoe.getImportantEdgeColor());
	}//GEN-LAST:event_dlgSettingsComponentShown

	private void pnlPointColorMouseClicked(java.awt.event.MouseEvent evt)//GEN-FIRST:event_pnlPointColorMouseClicked
	{//GEN-HEADEREND:event_pnlPointColorMouseClicked
		Color color = JColorChooser.showDialog(dlgSettings, "Select Point Color", aoe.getPointColor());
		if (color != null)
		{
			pnlPointColor.setBackground(color);
			aoe.setPointColor(color);
			redrawGraph();
		}
	}//GEN-LAST:event_pnlPointColorMouseClicked

	private void pnlEdgeColorMouseClicked(java.awt.event.MouseEvent evt)//GEN-FIRST:event_pnlEdgeColorMouseClicked
	{//GEN-HEADEREND:event_pnlEdgeColorMouseClicked
		Color color = JColorChooser.showDialog(dlgSettings, "Select Edge Color", aoe.getEdgeColor());
		if (color != null)
		{
			pnlEdgeColor.setBackground(color);
			aoe.setEdgeColor(color);
			redrawGraph();
		}
	}//GEN-LAST:event_pnlEdgeColorMouseClicked

	private void pnlImportantEdgeColorMouseClicked(java.awt.event.MouseEvent evt)//GEN-FIRST:event_pnlImportantEdgeColorMouseClicked
	{//GEN-HEADEREND:event_pnlImportantEdgeColorMouseClicked
		Color color = JColorChooser.showDialog(dlgSettings, "Select Important Edge Color", aoe.getImportantEdgeColor());
		if (color != null)
		{
			pnlImportantEdgeColor.setBackground(color);
			aoe.setImportantEdgeColor(color);
			redrawGraph();
		}
	}//GEN-LAST:event_pnlImportantEdgeColorMouseClicked
	// </editor-fold>
	// <editor-fold defaultstate="collapsed" desc="FormEvents">
	private void formWindowClosing(java.awt.event.WindowEvent evt)//GEN-FIRST:event_formWindowClosing
	{//GEN-HEADEREND:event_formWindowClosing
		if (aoe.isChanged())
		{
			switch (JOptionPane.showConfirmDialog(this, "Modifies has been made. Save changes?", "AOE", JOptionPane.YES_NO_CANCEL_OPTION))
			{
				case JOptionPane.YES_OPTION:
					if (!saveFile())
					{
						return;
					}
					break;
				case JOptionPane.CANCEL_OPTION:
					return;
			}
		}
		saveSetings();
		this.dispose();
	}//GEN-LAST:event_formWindowClosing
	// </editor-fold>

	/**
	 * @param args the command line arguments
	 */
	public static void main(String args[])
	{
		java.awt.EventQueue.invokeLater(new Runnable()
		{

			public void run()
			{
				new Main().setVisible(true);
			}
		});
	}
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnLoad;
    private javax.swing.JButton btnNew;
    private javax.swing.JButton btnSave;
    private javax.swing.JButton btnSaveAs;
    private javax.swing.ButtonGroup btngrpTool;
    private javax.swing.JDialog dlgChart;
    private javax.swing.JDialog dlgSettings;
    private javax.swing.JFileChooser fc;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JLabel lblEdgeLength;
    private javax.swing.JLabel lblEdgeProperties;
    private javax.swing.JLabel lblPointName;
    private javax.swing.JLabel lblPointProperties;
    private javax.swing.JPanel pnlAOE;
    public javax.swing.JPanel pnlChart;
    private javax.swing.JPanel pnlColorSettings;
    private javax.swing.JPanel pnlEdgeColor;
    private javax.swing.JPanel pnlImportantEdgeColor;
    private javax.swing.JPanel pnlPointColor;
    private javax.swing.JToolBar.Separator spt1;
    private javax.swing.JToolBar.Separator spt2;
    private javax.swing.JToolBar.Separator spt3;
    private javax.swing.JToolBar.Separator spt4;
    private javax.swing.JToolBar.Separator spt5;
    private javax.swing.JToolBar.Separator spt6;
    private javax.swing.JToolBar.Separator spt7;
    private javax.swing.JToolBar.Separator spt8;
    private javax.swing.JToolBar tbar;
    private javax.swing.JToolBar tbarEdgeProperties;
    private javax.swing.JToolBar tbarPointProperties;
    private javax.swing.JToolBar tbarTool;
    private javax.swing.JToggleButton tbtnAddEdge;
    private javax.swing.JToggleButton tbtnAddPoint;
    private javax.swing.JToggleButton tbtnChart;
    private javax.swing.JToggleButton tbtnChoose;
    private javax.swing.JToggleButton tbtnDelete;
    private javax.swing.JToggleButton tbtnMoveAll;
    private javax.swing.JToggleButton tbtnSettings;
    private javax.swing.JTextField txtEdgeLength;
    private javax.swing.JTextField txtPointName;
    // End of variables declaration//GEN-END:variables
}
