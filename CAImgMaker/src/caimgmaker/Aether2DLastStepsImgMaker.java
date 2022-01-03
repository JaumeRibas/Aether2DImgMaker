/* Aether2DImgMaker -- console app to generate images of the Aether cellular automaton in 2D
    Copyright (C) 2017-2022 Jaume Ribas

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
import cellularautomata.model2d.LongModel2D;
import cellularautomata.model2d.ObjectModel2D;
import cellularautomata.model2d.SubareaModel;

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
		String imgPath = path + new Aether2D(0).getName() + "/2D"
				+ "/img/lats_steps/" + colorMapper.getColormapName() + "/";
		while (true) {
			long initialValue = i;
			System.out.println("Initial value: " + initialValue);
			Aether2D ae = new Aether2D(initialValue);
			while (ae.nextStep());
			long lastStep = ae.getStep() - 1;
			System.out.println("Last step: " + lastStep);
			LongModel2D asymmetricSection = ae.asymmetricSection();
			SubareaModel<LongModel2D> subareaModel = new SubareaModel<LongModel2D>(asymmetricSection, imageWidth, imageHeight);
			int subareasModelMinX = subareaModel.getMinX();
			int subareasModelMaxX = subareaModel.getMaxX();
			for (int subareasX = subareasModelMinX; subareasX <= subareasModelMaxX; subareasX++) {
				int subareasModelLocalMinY = subareaModel.getMinY(subareasX);
				int subareasModelLocalMaxY = subareaModel.getMaxY(subareasX);
				for (int subareasY = subareasModelLocalMinY; subareasY <= subareasModelLocalMaxY; subareasY++) {
					System.out.println("Subarea " + subareasX + "," + subareasY);
					int framedModelMinX = subareasX * imageWidth;
					int framedModelMaxX = framedModelMinX + imageWidth - 1;
					int framedModelMinY = subareasY * imageHeight;
					int framedModelMaxY = framedModelMinY + imageHeight - 1;
					LongModel2D subarea = subareaModel.getSubareaAtPosition(subareasX, subareasY);
					long[] minAndMaxValue = subarea.getMinAndMax();
					System.out.println("Min value: " + minAndMaxValue[0] + System.lineSeparator() + "Max value: " + minAndMaxValue[1]);
					ObjectModel2D<Color> colorModel = colorMapper.getMappedModel(subarea, minAndMaxValue[0], minAndMaxValue[1]);
					ImgMaker.createImage(colorModel, framedModelMinX, framedModelMaxX, framedModelMinY, framedModelMaxY, 
							imageWidth, imageHeight, imgPath + subareasX + "," + subareasY, 
							ae.getName() + "_" + initialValue + ".png");
				}						
			}
			//i-=10;
			break;
		}
	}
	
}
