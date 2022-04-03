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

import java.math.BigInteger;

import caimgmaker.colormap.ColorMapper;
import caimgmaker.colormap.GrayscaleMapper;
import cellularautomata.automata.nearaether.NearAether3Simple3D;

public class NearAether3_3DImgMaker {
	
	public static void main(String[] args) throws Exception {
//		args = new String[]{"527049830677415760", "D:/data/test"};//, "150", "30", "10000"};//debug
		if (args.length == 0) {
			System.err.println("You must specify an initial value.");
		} else {
			long initialValue = 0;
			String path;
			int initialStep = 0;
			int scanInitialZIndex = 0;
			boolean isScanInitialZIndexDefined = false;	
			long millisecondsBetweenBackups = 0;
			boolean isBackupLeapDefined = false;
			String strInitialValue = args[0];
			if (strInitialValue.matches("-?\\d+")) {
				BigInteger tmp = new BigInteger(strInitialValue);
				if (tmp.compareTo(BigInteger.valueOf(Long.MAX_VALUE)) <= 0 
						&& tmp.compareTo(BigInteger.valueOf(Long.MIN_VALUE)) >= 0) {
					initialValue = tmp.longValue();
				} else {
					System.err.println("Initial value out of range.");
					return;
				}
			} else {
				System.err.println("Initial value '" + strInitialValue + "' is not a valid integer.");
				return;
			}
			if (args.length > 1) {
				path = args[1];
				char lastCharacter = path.charAt(path.length() - 1); 
				if (lastCharacter != '/' && lastCharacter != '\\') {
					path += "/";
				}
				if (args.length > 2) {
					initialStep = Integer.parseInt(args[2]);
					if (args.length > 3) {
						scanInitialZIndex = Integer.parseInt(args[3]);
						isScanInitialZIndexDefined = true;
						if (args.length > 4) {
							millisecondsBetweenBackups = Long.parseLong(args[4]);
							isBackupLeapDefined = true;
						}
					}
				}
			} else {
				path = "./";
			}
			NearAether3Simple3D ca;
			ca = new NearAether3Simple3D(initialValue);
			boolean finished = false;
			while (ca.getStep() < initialStep && !finished) {
				finished = !ca.nextStep();
				System.out.println("step: " + ca.getStep());
			}
			path += ca.getSubfolderPath();
			ColorMapper colorMapper = new GrayscaleMapper(0);
			ImgMaker imgMaker = null;
			if (isBackupLeapDefined) {
				imgMaker = new ImgMaker(millisecondsBetweenBackups);
			} else {
				imgMaker = new ImgMaker();
			}
			int[] scanCoords = new int[3];
			if (isScanInitialZIndexDefined) {
				scanCoords[2] = scanInitialZIndex;
			}
			imgMaker.createScanningAndZCrossSectionImages(ca, scanCoords, 0, colorMapper, ImgMakerConstants.HD_HEIGHT/2, ImgMakerConstants.HD_HEIGHT/2, 
				path + "/img", path + "/backups");
		}		
	}
	
}
