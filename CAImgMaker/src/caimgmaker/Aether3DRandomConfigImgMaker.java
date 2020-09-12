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

import java.math.BigInteger;

import caimgmaker.colormap.ColorMapper;
import caimgmaker.colormap.GrayscaleMapper;
import cellularautomata.automata.Aether3DRandomConfiguration;

public class Aether3DRandomConfigImgMaker {
	
	public static void main(String[] args) throws Exception {
//		args = new String[]{"21", "0", "-1000", "D:/data/test"};//debug
		if (args.length < 3) {
			System.err.println("You must specify an initial side and a min and max values.");
		} else {
			int initialSide;
			int minValue;
			int maxValue;
			String strInitialSide = args[0];
			if (strInitialSide.matches("-?\\d+")) {
				BigInteger tmp = new BigInteger(strInitialSide);
				if (tmp.compareTo(BigInteger.valueOf(Integer.MAX_VALUE)) <= 0 
						&& tmp.compareTo(BigInteger.ONE) >= 0) {
					initialSide = tmp.intValue();
				} else {
					System.err.println("Initial side out of range.");
					return;
				}
				String strMinValue = args[1];
				if (strMinValue.matches("-?\\d+")) {
					tmp = new BigInteger(strMinValue);
					if (tmp.compareTo(BigInteger.valueOf(Integer.MAX_VALUE)) <= 0 
							&& tmp.compareTo(BigInteger.valueOf(Integer.MIN_VALUE)) >= 0) {
						minValue = tmp.intValue();
					} else {
						System.err.println("Min value out of range.");
						return;
					}
					String strMaxValue = args[2];
					if (strMaxValue.matches("-?\\d+")) {
						tmp = new BigInteger(strMaxValue);
						if (tmp.compareTo(BigInteger.valueOf(Integer.MAX_VALUE)) <= 0 
								&& tmp.compareTo(BigInteger.valueOf(Integer.MIN_VALUE)) >= 0) {
							maxValue = tmp.intValue();
						} else {
							System.err.println("Max value out of range.");
							return;
						}
					} else {
						System.err.println("Max value must be a valid integer in base ten.");
						return;
					}
				} else {
					System.err.println("Min value must be a valid integer in base ten.");
					return;
				}
			} else {
				System.err.println("Initial side must be a valid integer in base ten.");
				return;
			}
			if (minValue > maxValue) {
				int swap = minValue;
				minValue = maxValue;
				maxValue = swap;
			}
			Aether3DRandomConfiguration ca = new Aether3DRandomConfiguration(initialSide, minValue, maxValue);
			String path;
			if (args.length > 2) {
				path = args[3];
				char lastCharacter = path.charAt(path.length() - 1); 
				if (lastCharacter != '/' && lastCharacter != '\\') {
					path += "/";
				}
			} else {
				path = "./";
			}
			ColorMapper colorMapper = new GrayscaleMapper(0);
			path += ca.getSubFolderPath();	
			ImgMaker imgMaker = new ImgMaker();
			imgMaker.createScanningAndCrossSectionImages(ca, 0, colorMapper, colorMapper, ImgMakerConstants.HD_HEIGHT/2, ImgMakerConstants.HD_HEIGHT/2, 
					path + "/img", path + "/backups");
		}		
	}
	
}
