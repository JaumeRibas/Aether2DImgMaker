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

import java.math.BigInteger;

import caimgmaker.colormap.ColorMapper;
import caimgmaker.colormap.GrayscaleMapper;
import cellularautomata.automata.IntSpreadIntegerValue2D;
import cellularautomata.evolvinggrid.EvolvingIntGrid2D;

public class SIV2DImgMaker {
	
	public static void main(String[] args) throws Exception {
//		args = new String[]{"1000000", "0", "D:/data/test"};//debug
		if (args.length == 0) {
			System.err.println("You must specify an initial value.");
		} else {
			int initialValue;
			int backgroundValue = 0;
			String strInitialValue = args[0];
			if (strInitialValue.matches("-?\\d+")) {
				BigInteger tmp = new BigInteger(strInitialValue);
				if (tmp.compareTo(BigInteger.valueOf(Integer.MAX_VALUE)) <= 0 
						&& tmp.compareTo(BigInteger.valueOf(Integer.MIN_VALUE)) >= 0) {
					initialValue = tmp.intValue();
				} else {
					System.err.println("Initial value out of range.");
					return;
				}
			} else {
				System.err.println("Initial value must be a valid integer in base ten.");
				return;
			}
			if (args.length > 1) {
				String strBackgroundValue = args[1];
				if (strBackgroundValue.matches("-?\\d+")) {
					BigInteger tmp = new BigInteger(strBackgroundValue);
					if (tmp.compareTo(BigInteger.valueOf(Integer.MAX_VALUE)) <= 0 
							&& tmp.compareTo(BigInteger.valueOf(Integer.MIN_VALUE)) >= 0) {
						backgroundValue = tmp.intValue();
					} else {
						System.err.println("Background value out of range.");
						return;
					}
				} else {
					System.err.println("Background value must be a valid integer in base ten.");
					return;
				}
			}
			EvolvingIntGrid2D ca = new IntSpreadIntegerValue2D(initialValue, backgroundValue).asymmetricSection();
			String path;
			long initialStep = 0;
			if (args.length > 2) {
				path = args[2];
				char lastCharacter = path.charAt(path.length() - 1); 
				if (lastCharacter != '/' && lastCharacter != '\\') {
					path += "/";
				}
				if (args.length > 3) {
					String strInitialStep = args[3];
					if (strInitialStep.matches("-?\\d+")) {
						BigInteger tmp = new BigInteger(strInitialStep);
						if (tmp.compareTo(BigInteger.valueOf(Integer.MAX_VALUE)) <= 0 
								&& tmp.compareTo(BigInteger.valueOf(Integer.MIN_VALUE)) >= 0) {
							initialStep = tmp.longValue();
						} else {
							System.err.println("Initial step out of range.");
							return;
						}
					} else {
						System.err.println("Initial step must be a valid integer in base ten.");
						return;
					}
				}
			} else {
				path = "./";
			}
			boolean finished = false;
			while (ca.getStep() < initialStep && !finished) {
				finished = !ca.nextStep();
				System.out.println("step: " + ca.getStep());
			}
			ColorMapper colorMapper = new GrayscaleMapper(0);
			path += ca.getSubFolderPath() + "/img";	
			ImgMaker imgMaker = new ImgMaker();
			imgMaker.createImages(ca, colorMapper, ImgMakerConstants.HD_WIDTH/2, ImgMakerConstants.HD_HEIGHT/2, path);
		}		
	}
	
}
