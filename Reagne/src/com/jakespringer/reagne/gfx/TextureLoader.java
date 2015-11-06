package com.jakespringer.reagne.gfx;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.color.ColorSpace;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

import javax.imageio.ImageIO;

import org.lwjgl.BufferUtils;

import static org.lwjgl.opengl.GL11.*;

/**
 * A utility class to load textures for OpenGL. This source is based on a
 * texture that can be found in the Java Gaming (www.javagaming.org) Wiki. It
 * has been simplified slightly for explicit 2D graphics use.
 *
 * OpenGL uses a particular image format. Since the imageArray that are loaded
 * from disk may not match this format this loader introduces a intermediate
 * image which the source image is copied into. In turn, this image is used as
 * source for the OpenGL texture.
 *
 * @author Kevin Glass
 * @author Brian Matzon
 */
public class TextureLoader {

	/**
	 * The table of textures that have been loaded in this loader
	 */
	private static HashMap<String, ArrayList<Texture>> table = new HashMap<String, ArrayList<Texture>>();
	/**
	 * The color model including alpha for the GL image
	 */
	private static ColorModel glAlphaColorModel;
	/**
	 * The color model for the GL image
	 */
	private static ColorModel glColorModel;
	/**
	 * Scratch buffer for texture ID's
	 */
	private static IntBuffer textureIDBuffer = BufferUtils.createIntBuffer(1);

	/**
	 * Create a new texture loader based on the game panel
	 */
	static {
		glAlphaColorModel = new ComponentColorModel(ColorSpace.getInstance(ColorSpace.CS_sRGB), new int[] { 8, 8, 8, 8 }, true, false, ComponentColorModel.TRANSLUCENT, DataBuffer.TYPE_BYTE);

		glColorModel = new ComponentColorModel(ColorSpace.getInstance(ColorSpace.CS_sRGB), new int[] { 8, 8, 8, 0 }, false, false, ComponentColorModel.OPAQUE, DataBuffer.TYPE_BYTE);
	}

	/**
	 * Convert the buffered image to a texture
	 *
	 * @param bufferedImage
	 *            The image to convert to a texture
	 * @param texture
	 *            The texture to store the data into
	 * @return A buffer containing the data
	 */
	private static ByteBuffer convertImageData(BufferedImage bufferedImage, Texture texture) {
		ByteBuffer imageBuffer;
		WritableRaster raster;
		BufferedImage texImage;

		int texWidth = 2;
		int texHeight = 2;

		// find the closest power of 2 for the width and height
		// of the produced texture
		while (texWidth < bufferedImage.getWidth()) {
			texWidth *= 2;
		}
		while (texHeight < bufferedImage.getHeight()) {
			texHeight *= 2;
		}

		texture.setTextureHeight(texHeight);
		texture.setTextureWidth(texWidth);

		// create a raster that can be used by OpenGL as a source
		// for a texture
		if (bufferedImage.getColorModel().hasAlpha()) {
			raster = Raster.createInterleavedRaster(DataBuffer.TYPE_BYTE, texWidth, texHeight, 4, null);
			texImage = new BufferedImage(glAlphaColorModel, raster, false, new Hashtable<Object, Object>());
		} else {
			raster = Raster.createInterleavedRaster(DataBuffer.TYPE_BYTE, texWidth, texHeight, 3, null);
			texImage = new BufferedImage(glColorModel, raster, false, new Hashtable<Object, Object>());
		}

		// copy the source image into the produced image
		Graphics g = texImage.getGraphics();
		g.setColor(new Color(0f, 0f, 0f, 0f));
		g.fillRect(0, 0, texWidth, texHeight);
		g.drawImage(bufferedImage, 0, 0, null);

		// build a byte buffer from the temporary image
		// that be used by OpenGL to produce a texture.
		byte[] data = ((DataBufferByte) texImage.getRaster().getDataBuffer()).getData();

		imageBuffer = ByteBuffer.allocateDirect(data.length);
		imageBuffer.order(ByteOrder.nativeOrder());
		imageBuffer.put(data, 0, data.length);
		imageBuffer.flip();

		return imageBuffer;
	}

	/**
	 * Create a new texture ID
	 *
	 * @return A new texture ID
	 */
	private static int createTextureID() {
		glGenTextures(textureIDBuffer);
		return textureIDBuffer.get(0);
	}

	/**
	 * Get the closest greater power of 2 to the fold number
	 *
	 * @param fold
	 *            The target number
	 * @return The power of 2
	 */
	private static int get2Fold(int fold) {
		int ret = 2;
		while (ret < fold) {
			ret *= 2;
		}
		return ret;
	}

	/**
	 * Load a texture
	 *
	 * @param resourceName
	 *            The location of the resource to load
	 * @return The loaded texture
	 * @throws IOException
	 *             Indicates a failure to access the resource
	 */
	public static ArrayList<Texture> getTexture(String resourceName, int n) throws IOException {
		ArrayList<Texture> tex = table.get(resourceName);
		if (tex != null) { return tex; }
		tex = getTexture(resourceName, n, GL_TEXTURE_2D, // target
				GL_RGBA, // dst pixel format
				GL_NEAREST, // min filter (unused)
				GL_NEAREST);
		table.put(resourceName, tex);
		return tex;
	}

	/**
	 * Load a texture into OpenGL from a image reference on disk.
	 *
	 * @param resourceName
	 *            The location of the resource to load
	 * @param target
	 *            The GL target to load the texture against
	 * @param dstPixelFormat
	 *            The pixel format of the screen
	 * @param minFilter
	 *            The minimising filter
	 * @param magFilter
	 *            The magnification filter
	 * @return The loaded texture
	 * @throws IOException
	 *             Indicates a failure to access the resource
	 */
	public static ArrayList<Texture> getTexture(String resourceName, int n, int target, int dstPixelFormat, int minFilter, int magFilter) throws IOException {
		int srcPixelFormat;

		ArrayList<BufferedImage> images = loadImage(resourceName, n);
		ArrayList<Texture> textures = new ArrayList<Texture>();
		for (int i = 0; i < n; i++) {
			// create the texture ID for this texture
			int textureID = createTextureID();
			textures.add(i, new Texture(target, textureID));
			// bind this texture
			glBindTexture(target, textureID);

			BufferedImage bufferedImage = images.get(i);
			textures.get(i).setWidth(bufferedImage.getWidth());
			textures.get(i).setHeight(bufferedImage.getHeight());

			if (bufferedImage.getColorModel().hasAlpha()) {
				srcPixelFormat = GL_RGBA;
			} else {
				srcPixelFormat = GL_RGB;
			}

			// convert that image into a byte buffer of texture data
			ByteBuffer textureBuffer = convertImageData(bufferedImage, textures.get(i));

			if (target == GL_TEXTURE_2D) {
				glTexParameteri(target, GL_TEXTURE_MIN_FILTER, minFilter);
				glTexParameteri(target, GL_TEXTURE_MAG_FILTER, magFilter);
			}

			// produce a texture from the byte buffer
			glTexImage2D(target, 0, dstPixelFormat, get2Fold(bufferedImage.getWidth()), get2Fold(bufferedImage.getHeight()), 0, srcPixelFormat, GL_UNSIGNED_BYTE, textureBuffer);
		}
		return textures;
	}

	public static ArrayList<Texture> getTileSet(String resourceName, int x, int y, int target, int dstPixelFormat, int minFilter, int magFilter) throws IOException {
		int srcPixelFormat;

		ArrayList<BufferedImage> images = loadImage(resourceName, x, y);
		ArrayList<Texture> textures = new ArrayList<Texture>();
		for (int i = 0; i < x * y; i++) {
			// create the texture ID for this texture
			int textureID = createTextureID();
			textures.add(i, new Texture(target, textureID));
			// bind this texture
			glBindTexture(target, textureID);

			BufferedImage bufferedImage = images.get(i);
			textures.get(i).setWidth(bufferedImage.getWidth());
			textures.get(i).setHeight(bufferedImage.getHeight());

			if (bufferedImage.getColorModel().hasAlpha()) {
				srcPixelFormat = GL_RGBA;
			} else {
				srcPixelFormat = GL_RGB;
			}

			// convert that image into a byte buffer of texture data
			ByteBuffer textureBuffer = convertImageData(bufferedImage, textures.get(i));

			if (target == GL_TEXTURE_2D) {
				glTexParameteri(target, GL_TEXTURE_MIN_FILTER, minFilter);
				glTexParameteri(target, GL_TEXTURE_MAG_FILTER, magFilter);
			}

			// produce a texture from the byte buffer
			glTexImage2D(target, 0, dstPixelFormat, get2Fold(bufferedImage.getWidth()), get2Fold(bufferedImage.getHeight()), 0, srcPixelFormat, GL_UNSIGNED_BYTE, textureBuffer);
		}

		return textures;
	}

	/**
	 * Load a given resource as a buffered image
	 *
	 * @param ref
	 *            The location of the resource to load
	 * @return The loaded buffered image
	 * @throws IOException
	 *             Indicates a failure to find a resource
	 */
	private static ArrayList<BufferedImage> loadImage(String ref, int n) {
		BufferedImage image = null;
		try {
			image = ImageIO.read(new File(ref));
		} catch (IOException ex) {
			ex.printStackTrace();
			System.out.println(ref);
		}
		ArrayList<BufferedImage> imageArray = new ArrayList<BufferedImage>();
		for (int i = 0; i < n; i++) {
			double currentX = i * image.getWidth() * 1.0 / n;
			double nextX = (i + 1) * image.getWidth() * 1.0 / n;
			imageArray.add(image.getSubimage((int) currentX, 0, (int) (nextX - currentX), image.getHeight()));
		}
		return imageArray;
	}

	private static ArrayList<BufferedImage> loadImage(String ref, int x, int y) {
		BufferedImage image = null;
		try {
			image = ImageIO.read(new File(ref));
		} catch (IOException ex) {
			ex.printStackTrace();
			System.out.println(ref);
		}
		ArrayList<BufferedImage> imageArray = new ArrayList<BufferedImage>();
		for (int j = 0; j < y; j++) {
			for (int i = 0; i < x; i++) {
				double currentX = (double) i * image.getWidth() / x;
				double nextX = (double) (i + 1) * image.getWidth() / x;
				double currentY = (double) j * image.getHeight() / y;
				double nextY = (double) (j + 1) * image.getHeight() / y;
				imageArray.add(image.getSubimage((int) currentX, (int) currentY, (int) (nextX - currentX), (int) (nextY - currentY)));
			}
		}

		return imageArray;
	}
}
