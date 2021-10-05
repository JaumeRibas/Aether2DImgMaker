/* Aether2DImgMaker -- console app to generate images of the Aether cellular automaton in 2D
    Copyright (C) 2017-2021 Jaume Ribas

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

import java.awt.Color;

import caimgmaker.colormap.ColorMapper;
import cellularautomata.grid.GridProcessor;
import cellularautomata.grid2d.IntGrid2D;
import cellularautomata.grid2d.ObjectGrid2D;
import cellularautomata.grid3d.IntGrid3D;

public class IntGrid3DXCrossSectionEvenOddImgMakerProcessor implements GridProcessor<IntGrid3D> {

	private int x;
	private boolean imagesMade = false;
	private ColorMapper colorMapper;
	private String imgsPath;
	private String imgsName;
	private int imageWidth;
	private int imageHeight;
	private boolean isEven;

	public IntGrid3DXCrossSectionEvenOddImgMakerProcessor(int x, ColorMapper colorMapper, String imgsPath,
			String imgsName, boolean isEven, int imageWidth, int imageHeight) {
		this.x = x;
		this.colorMapper = colorMapper;
		this.imgsPath = imgsPath;
		this.imgsName = imgsName;
		this.isEven = isEven;
		this.imageWidth = imageWidth;
		this.imageHeight = imageHeight;
	}

	@Override
	public void beforeProcessing() throws Exception {
		imagesMade = false;
	}

	@Override
	public void afterProcessing() throws Exception {
			
	}

	@Override
	public void processGridBlock(IntGrid3D gridBlock) throws Exception {
		if (!imagesMade) {
			if(x >= gridBlock.getMinX() && x <= gridBlock.getMaxX()) {
				IntGrid2D crossSection = gridBlock.crossSectionAtX(x);			
				int[] evenMinAndMaxValue = crossSection.getEvenOddPositionsMinAndMax(isEven);
				if (evenMinAndMaxValue != null) {
					ObjectGrid2D<Color> colorGrid = colorMapper.getMappedGrid(crossSection, evenMinAndMaxValue[0], evenMinAndMaxValue[1]);
					ImgMaker.createImageFromEvenOrOddPositions(isEven, colorGrid, imageWidth, imageHeight, imgsPath + "even/", imgsName);
				} else {
					ImgMaker.createEmptyImage(crossSection.getMinX(), crossSection.getMaxX(), crossSection.getMinY(), crossSection.getMaxY(), 
							imageWidth, imageHeight, imgsPath + "even/", imgsName);
				}
				int[] oddMinAndMaxValue = crossSection.getEvenOddPositionsMinAndMax(!isEven);
				if (oddMinAndMaxValue != null) {
					ObjectGrid2D<Color> colorGrid = colorMapper.getMappedGrid(crossSection, oddMinAndMaxValue[0], oddMinAndMaxValue[1]);
					ImgMaker.createImageFromEvenOrOddPositions(!isEven, colorGrid, imageWidth, imageHeight, imgsPath + "odd/", imgsName);
				} else {
					ImgMaker.createEmptyImage(crossSection.getMinX(), crossSection.getMaxX(), crossSection.getMinY(), crossSection.getMaxY(), 
							imageWidth, imageHeight, imgsPath + "odd/", imgsName);
				}				
				imagesMade = true;
			}			
		}
	}
	
	public void setX(int x) {
		this.x = x;
	}

	public void setImgsName(String imgsName) {
		this.imgsName = imgsName;
	}

	public void setIsEven(boolean isEven) {
		this.isEven = isEven;
	}
	
	public void setImgsPath(String imgsPath) {
		this.imgsPath = imgsPath;
	}
	
}
