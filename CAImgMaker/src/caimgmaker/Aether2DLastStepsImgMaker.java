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
import caimgmaker.colormap.GrayscaleMapper;
import cellularautomata.automata.aether.Aether2D;
import cellularautomata.grid2d.LongGrid2D;
import cellularautomata.grid2d.ObjectGrid2D;
import cellularautomata.grid2d.SubareaGrid;

public class Aether2DLastStepsImgMaker {
	
	public static void main(String[] args) throws Exception {
//		args = new String[]{"D:/data/test"};//debug
		String path;
		if (args.length > 0) {
			path = args[0];
			char lastCharacter = path.charAt(path.length() - 1); 
			if (lastCharacter != '/' && lastCharacter != '\\') {
				path += "/";
			}
		} else {
			path = "./";
		}
		ColorMapper colorMapper = new GrayscaleMapper(0);
		createAether2DLastStepsImages(colorMapper, 100, 100, path);		
	}
	
	public static void createAether2DLastStepsImages(ColorMapper colorMapper, int imageWidth, int imageHeight, String path) throws Exception {
		int i = -156250000;
		String imgPath = path + new Aether2D(0).getName() 
				+ "/img/lats_steps/" + colorMapper.getClass().getSimpleName() + "/";
		while (true) {
			long initialValue = i;
			System.out.println("Initial value: " + initialValue);
			Aether2D ae = new Aether2D(initialValue);
			while (ae.nextStep());
			long lastStep = ae.getStep() - 1;
			System.out.println("Last step: " + lastStep);
			LongGrid2D asymmetricSection = ae.asymmetricSection();
			SubareaGrid<LongGrid2D> subareaGrid = new SubareaGrid<LongGrid2D>(asymmetricSection, imageWidth, imageHeight);
			int subareasGridMinX = subareaGrid.getMinX();
			int subareasGridMaxX = subareaGrid.getMaxX();
			for (int subareasX = subareasGridMinX; subareasX <= subareasGridMaxX; subareasX++) {
				int subareasGridLocalMinY = subareaGrid.getMinY(subareasX);
				int subareasGridLocalMaxY = subareaGrid.getMaxY(subareasX);
				for (int subareasY = subareasGridLocalMinY; subareasY <= subareasGridLocalMaxY; subareasY++) {
					System.out.println("Subarea " + subareasX + "," + subareasY);
					int framedGridMinX = subareasX * imageWidth;
					int framedGridMaxX = framedGridMinX + imageWidth - 1;
					int framedGridMinY = subareasY * imageHeight;
					int framedGridMaxY = framedGridMinY + imageHeight - 1;
					LongGrid2D subarea = subareaGrid.getSubareaAtPosition(subareasX, subareasY);
					long[] minAndMaxValue = subarea.getMinAndMax();
					System.out.println("Min value: " + minAndMaxValue[0] + System.lineSeparator() + "Max value: " + minAndMaxValue[1]);
					ObjectGrid2D<Color> colorGrid = colorMapper.getMappedGrid(subarea, minAndMaxValue[0], minAndMaxValue[1]);
					ImgMaker.createImage(colorGrid, framedGridMinX, framedGridMaxX, framedGridMinY, framedGridMaxY, 
							imageWidth, imageHeight, imgPath + subareasX + "," + subareasY, 
							ae.getName() + "_" + initialValue + ".png");
				}						
			}
			//i-=10;
			break;
		}
	}
	
}
