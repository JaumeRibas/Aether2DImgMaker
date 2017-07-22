/* CellularSharing2DImgMaker -- console app to generate images from the Cellular Sharing 2D cellular automaton
    Copyright (C) 2017 Jaume Ribas

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>
 */
package caimgmaker;

import java.awt.Point;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

import caimgmaker.colormap.ColorGrid2D;
import caimgmaker.colormap.HueMapper;
import caimgmaker.colormap.SymmetricColorGrid2D;
import cellularautomata.automata.CellularSharing2D;
import cellularautomata.automata.SymmetricLongCellularAutomaton2D;
import cellularautomata.grid.Grid2D;
import cellularautomata.grid.SymmetricGrid2D;
import cellularautomata.grid.SymmetricLongGrid2D;

public class CA2DImgMaker {
	
	private static final long IMGS_PER_FOLDER = 10000;	
	
	private static final int HD_WIDTH = 1920;
	private static final int HD_HEIGHT = 1080;
	
	public static void main(String[] args) throws Exception {
		args = new String[]{"1000", "D:/data"};//debug
		if (args.length == 0) {
			System.err.println("You must specify an initial value.");
		} else {
			long initialValue = Long.parseLong(args[0]);
			SymmetricLongCellularAutomaton2D ca = new CellularSharing2D(initialValue);
			SymmetricLongGrid2D abs = ca.absoluteGrid();
			String path;
			int initialStep = 0;
			if (args.length > 1) {
				path = args[1];
				char lastCharacter = path.charAt(path.length() - 1); 
				if (lastCharacter != '/' && lastCharacter != '\\') {
					path += "/";
				}
				if (args.length > 2) {
					initialStep = Integer.parseInt(args[2]);
				}
			} else {
				path = "./";
			}
			boolean finished = false;
			while (ca.getCurrentStep() < initialStep && !finished) {
				finished = !ca.nextStep();
				System.out.println("Current step: " + ca.getCurrentStep());
			}
			HueMapper colorMapper = new HueMapper();
			path += ca.getClass().getSimpleName() + "/" + initialValue + "/";			
			long currentStep = ca.getCurrentStep();
			int numberedFolder = (int) (currentStep/IMGS_PER_FOLDER);
			int folderImageCount = (int) (currentStep%IMGS_PER_FOLDER);
			CA2DImgMaker imgMaker = new CA2DImgMaker();
			do {
				System.out.println("Current step: " + currentStep);
				int maxY = ca.getMaxY(), maxX = ca.getMaxX();
				System.out.println("maxY=" + maxY + ", maxX=" + maxX);
				String imgPath = path;
				long[] minAndMaxValue = ca.getMinAndMaxValue(0);
				System.out.println("Min value " + minAndMaxValue[0] + " Max value " + minAndMaxValue[1]);
				SymmetricColorGrid2D colorGrid = colorMapper.getMappedSymmetricGrid(abs);
				imgMaker.createSliceImage(colorGrid, HD_WIDTH, HD_HEIGHT, imgPath + colorMapper.getClass().getSimpleName() 
						+ "/slice/"	+ numberedFolder, ca.getClass().getSimpleName() + "_" + currentStep + ".png");
				folderImageCount++;
				if (folderImageCount == IMGS_PER_FOLDER) {
					numberedFolder++;
					folderImageCount = 0;
				}			
				currentStep++;
			} while (ca.nextStep());
			System.out.println("Finished!");
		}		
	}
	
	private int getDotSize(SymmetricGrid2D grid, int preferredWidth, int preferredHeight) {
		int yDotSize = 1;
		int height = grid.getNonSymmetricMaxY() - grid.getNonSymmetricMinY() + 1;
		if (height > 0) {
			yDotSize = preferredHeight/height;
        }
		if (yDotSize == 0) yDotSize = 1;
		int xDotSize = 1;
		int width = grid.getNonSymmetricMaxX() - grid.getNonSymmetricMinX() + 1;
		if (width > 0) {
			xDotSize = preferredWidth/width;
        }
		if (xDotSize == 0) xDotSize = 1;
		return Math.min(xDotSize, yDotSize);
	}
	
	private int getDotSize(Grid2D grid, int preferredWidth, int preferredHeight) {
		int yDotSize = 1;
		int height = grid.getMaxY() - grid.getMinY() + 1;
		if (height > 0) {
			yDotSize = preferredHeight/height;
        }
		if (yDotSize == 0) yDotSize = 1;
		int xDotSize = 1;
		int width = grid.getMaxX() - grid.getMinX() + 1;
		if (width > 0) {
			xDotSize = preferredWidth/width;
        }
		if (xDotSize == 0) xDotSize = 1;
		return Math.min(xDotSize, yDotSize);
	}
	
	public void createSliceImage(SymmetricColorGrid2D grid, int preferredWidth, int preferredHeight, String path, String name) 
			throws Exception {
		int dotSize = getDotSize(grid, preferredWidth, preferredHeight);
		createSliceImage(grid, dotSize, path, name);
	}
	
	public void createSliceImage(SymmetricColorGrid2D grid, int dotSize, String path, String name) throws Exception {
		int maxX = grid.getNonSymmetricMaxX(), minX = grid.getNonSymmetricMinX(),
				maxY = grid.getNonSymmetricMaxY(), minY = grid.getNonSymmetricMinY();
		int width = (maxX - minX + 1) * dotSize;
		int height = (maxY - minY + 1) * dotSize;
		long longByteCount = (long)width * height * 3;
		if (longByteCount > Integer.MAX_VALUE)
			throw new Exception("Integer max value exceeded");
		int byteCount = (int)longByteCount;
		byte[] pixelData = new byte[byteCount];
		int dataIndex = 0;		
		for (int y = maxY; y >= minY; y--) {
			for (int i = 0; i < dotSize; i++) {
				for (int x = minX; x <= maxX; x++) {
					if (x >= y) {
						java.awt.Color c = grid.getNonSymmetricColorAt(x, y);
						for (int j = 0; j < dotSize; j++) {
							pixelData[dataIndex++] = (byte) c.getRed();
							pixelData[dataIndex++] = (byte) c.getGreen();
							pixelData[dataIndex++] = (byte) c.getBlue();
						}
					} else
						dataIndex += 3 * dotSize;					
				}
			}
		}
		saveAsPngImage(pixelData, width, height, path, name);
	}
	
	public void createSliceImage(ColorGrid2D grid, int preferredWidth, int preferredHeight, String path, String name) 
			throws Exception {
		int dotSize = getDotSize(grid, preferredWidth, preferredHeight);
		createImage(grid, dotSize, path, name);
	}
	
	public void createImage(ColorGrid2D grid, int dotSize, String path, String name) throws Exception {
		int maxX = grid.getMaxX(), minX = grid.getMinX(), maxY = grid.getMaxY(), minY = grid.getMinY();
		int width = (maxX - minX + 1) * dotSize;
		int height = (maxY - minY + 1) * dotSize;
		long longByteCount = (long)width * height * 3;
		if (longByteCount > Integer.MAX_VALUE)
			throw new Exception("Integer max value exceeded");
		int byteCount = (int)longByteCount;
		byte[] pixelData = new byte[byteCount];
		int dataIndex = 0;		
		for (int y = maxY; y >= minY; y--) {
			for (int i = 0; i < dotSize; i++) {
				for (int x = minX; x <= maxX; x++) {
					java.awt.Color c = grid.getColorAt(x,y);
					for (int j = 0; j < dotSize; j++) {
						pixelData[dataIndex++] = (byte) c.getRed();
						pixelData[dataIndex++] = (byte) c.getGreen();
						pixelData[dataIndex++] = (byte) c.getBlue();
					}
				}
			}
		}
		saveAsPngImage(pixelData, width, height, path, name);
	}
	
	public void saveAsPngImage(byte[] pixelData, int width, int height, String path, String name) throws IOException {
		DataBuffer buffer = new DataBufferByte(pixelData, pixelData.length);
		//3 bytes per pixel: red, green, blue
		WritableRaster raster = Raster.createInterleavedRaster(buffer, width, height, 3 * width, 3, new int[] {0, 1, 2}, (Point)null);
		ColorModel cm = new ComponentColorModel(ColorModel.getRGBdefault().getColorSpace(), false, true, Transparency.OPAQUE, DataBuffer.TYPE_BYTE); 
		BufferedImage image = new BufferedImage(cm, raster, true, null);
		File dir = new File(path);
		if (!dir.exists())
			dir.mkdirs();
		String pathName = path + "/" + name;
		System.out.println("Creating image at '" + pathName + "'");
		ImageIO.write(image, "png", new File(pathName));
	}
}
