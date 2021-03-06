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
import cellularautomata.automata.BigIntAether4D;
import cellularautomata.evolvinggrid.EvolvingNumberGrid2D;
import cellularautomata.evolvinggrid.EvolvingNumberGrid4D;
import cellularautomata.grid.MinAndMax;
import cellularautomata.grid2d.ObjectGrid2D;
import cellularautomata.numbers.BigInt;

public class Aether4DPatternComparingImgMaker {
	
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
		int step = 288;
		path += "Aether4D/PatternComparing/" + step;
		BigInt initialValue = new BigInt("-100000000000000000000000000");
		BigInt leap = BigInt.ONE;
		ColorMapper colorMapper = new GrayscaleMapper(0);
		while (true) {
			EvolvingNumberGrid4D<BigInt> ca = new BigIntAether4D(initialValue).asymmetricSection();
			for (int i = 0; i < step; i++) {
				System.out.println("Computing initial value " + initialValue + " step " + (i+1));
				ca.nextStep();
			}
			EvolvingNumberGrid2D<BigInt> crossSection = ca.crossSectionAtYZ(0, 0);
			//create image
			MinAndMax<BigInt> minMax = crossSection.getEvenOddPositionsMinAndMax(false);
			if (minMax != null) {
				ObjectGrid2D<Color> mappedGrid = colorMapper.getMappedGrid(crossSection, minMax.getMin(), minMax.getMax());
				ImgMaker.createEvenOddImageLeftToRight(mappedGrid, false, mappedGrid.getMinX(), mappedGrid.getMaxX(), mappedGrid.getMinY(), 
						mappedGrid.getMaxY(), 0, 0, path, "Ae4D" + initialValue + "_" + step + ".png");
			} else {
				ImgMaker.createEmptyImage(crossSection.getMinX(), crossSection.getMaxX(), crossSection.getMinY(), 
						crossSection.getMaxY(), 0, 0, path, "Ae4D" + initialValue + "_" + step + ".png");
			}			
			initialValue = initialValue.add(leap);
		}
	}
	
}
