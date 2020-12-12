/* Aether2DImgMaker -- console app to generate images of the Aether cellular automaton in 2D
    Copyright (C) 2017-2020 Jaume Ribas

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

import java.io.IOException;

import caimgmaker.colormap.ColorGrid2D;
import cellularautomata.grid.GridProcessor;

public class EvenOddImageRenderingProcessor implements GridProcessor<ColorGrid2D>{

	private boolean isEven;
	private int minX;
	private int maxX;
	private int minY;
	private int maxY;
	private String path;
	private String name;
	private int gridPositionSize;
	private byte[] pixelData;
	private int canvasTopMargin;
	private int imageWidth;
	private int imageHeight;
	private int framedGridWidth;
	private int framedGridHeight;

	public EvenOddImageRenderingProcessor(boolean isEven, int minX, int maxX, int minY, int maxY, int minWidth, int minHeight, String path, String name) throws Exception {
		this.isEven = isEven;
		this.minX = minX;
		this.maxX = maxX;
		this.minY = minY;
		this.maxY = maxY;
		this.path = path;
		this.name = name;
		this.gridPositionSize = ImgMaker.getGridPositionSize(minX, maxX, minY, maxY, minWidth, minHeight);
		framedGridWidth = maxX - minX + 1;
		framedGridHeight = maxY - minY + 1;
		
		int framedGridWidthInPixels = framedGridWidth * gridPositionSize;
		int framedGridHeightInPixels = framedGridHeight * gridPositionSize;
		
		imageWidth = Math.max(framedGridWidthInPixels, minWidth);
		imageHeight = Math.max(framedGridHeightInPixels, minHeight);	
		
		
		long longByteCount = (long)imageWidth * imageHeight * 3;
		if (longByteCount > Integer.MAX_VALUE)
			throw new Exception("Integer max value exceeded");
		int byteCount = (int)longByteCount;
		pixelData = new byte[byteCount];	
		
		
		canvasTopMargin = imageHeight - framedGridHeightInPixels;
	}

	@Override
	public void beforeProcessing() {
		
	}

	@Override
	public void afterProcessing() throws IOException {	
		ImgMaker.saveAsPngImage(pixelData, imageWidth, imageHeight, path, name);
	}

	@Override
	public void processGridBlock(ColorGrid2D gridBlock) throws Exception {
		int regionMinX = gridBlock.getMinX();
		int regionMaxX = gridBlock.getMaxX();
		int framedRegionMinX, framedRegionMaxX;
		if (minX < regionMinX) {
			framedRegionMinX = regionMinX;
		} else {
			framedRegionMinX = minX;
		}
		if (maxX > regionMaxX) {
			framedRegionMaxX = regionMaxX;
		} else {
			framedRegionMaxX = maxX;
		}
		
		for (int x = framedRegionMinX, xx = x - minX; x <= framedRegionMaxX; x++, xx++) {
			
			int regionMaxY = gridBlock.getMaxY(x);
			int regionMinY = gridBlock.getMinY(x);
			int framedRegionMinYAtX, framedRegionMaxYAtX;
			if (maxY > regionMaxY) {
				framedRegionMaxYAtX = regionMaxY;
			} else {
				framedRegionMaxYAtX = maxY;
			}
			if (minY < regionMinY) {
				framedRegionMinYAtX = regionMinY;
			} else {
				framedRegionMinYAtX = minY;
			}
			boolean isPositionEven = (framedRegionMinYAtX+x)%2 == 0;
			if (isEven != isPositionEven) { 
				framedRegionMinYAtX++;
			}
			for (int hBandIndex = 0; hBandIndex < gridPositionSize; hBandIndex++) {			
				for (int y = framedRegionMinYAtX, yy = y - minY; y <= framedRegionMaxYAtX; y+=2, yy+=2) {
					java.awt.Color c = gridBlock.getColorAtPosition(x, y);
					byte r = (byte) c.getRed(), g = (byte) c.getGreen(), b = (byte) c.getBlue();
					
					int framedGridSquentialIndex = (framedGridHeight - yy - 1) * framedGridWidth + xx;
					int dataIndex = (((framedGridSquentialIndex / framedGridWidth) * gridPositionSize * imageWidth)
							+ ((framedGridSquentialIndex % framedGridWidth) * gridPositionSize) 
							+ (hBandIndex * imageWidth)
							+ (canvasTopMargin * imageWidth)) * 3;
					
					for (int vBandIndex = 0; vBandIndex < gridPositionSize; vBandIndex++) {
						pixelData[dataIndex++] = r;
						pixelData[dataIndex++] = g;
						pixelData[dataIndex++] = b;
					}				
				}
			}
			
		}
	}

}
