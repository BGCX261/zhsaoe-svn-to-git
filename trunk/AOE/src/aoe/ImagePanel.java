package aoe;

import java.awt.Graphics;
import java.awt.Image;
import javax.swing.JPanel;

/**
 * The panel that can show an image.
 *
 * @author Administrator
 */
public class ImagePanel extends JPanel
{

	/**
	 * The shown image.
	 */
	private Image image;

	/**
	 * Get the image to be shown.
	 * @return
	 */
	public Image getImage()
	{
		return image;
	}

	/**
	 * Set the image to be shown.
	 * @param image The image to be shown.
	 */
	public void setImage(Image image)
	{
		this.image = image;
		repaint();
	}

	@Override
	protected void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		if (image != null)
		{
			g.drawImage(image, 0, 0, null);
		}
	}
}
