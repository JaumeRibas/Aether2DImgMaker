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
import cellularautomata.automata.IntAether3D;
import cellularautomata.evolvinggrid.EvolvingIntGrid3D;

public class IntAether3DEvenOddImgMaker {
	
	public static void main(String[] args) throws Exception {
//		args = new String[]{"-1000000", "D:/data/test"};//, "150", "30", "10000"};//debug
		if (args.length == 0) {
			System.err.println("You must specify an initial value.");
		} else {
			int initialValue = 0;
			boolean isRestore = false;
			String path;
			int initialStep = 0;
			int xScanInitialIndex = 0;
			boolean isScanInitialZIndexDefined = false;	
			long millisecondsBetweenBackups = 0;
			boolean isBackupLeapDefined = false;
			String initValOrBackupPath = args[0];
			if (initValOrBackupPath.matches("-?\\d+")) {
				BigInteger tmp = new BigInteger(initValOrBackupPath);
				if (tmp.compareTo(BigInteger.valueOf(Integer.MAX_VALUE)) <= 0 
						&& tmp.compareTo(BigInteger.valueOf(Integer.MIN_VALUE)) >= 0) {
					initialValue = tmp.intValue();
				} else {
					System.err.println("Initial value out of range.");
					return;
				}
			} else {
				isRestore = true;
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
						xScanInitialIndex = Integer.parseInt(args[3]);
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
			EvolvingIntGrid3D ca;
			if (isRestore) {
				ca = new IntAether3D(initValOrBackupPath).asymmetricSection();
			} else {
				ca = new IntAether3D(initialValue).asymmetricSection();
			}
			boolean finished = false;
			while (ca.getStep() < initialStep && !finished) {
				finished = !ca.nextStep();
				System.out.println("step: " + ca.getStep());
			}
			ColorMapper colorMapper = new GrayscaleMapper(0);
			path += ca.getSubFolderPath();
			ImgMaker imgMaker = null;
			if (isBackupLeapDefined) {
				imgMaker = new ImgMaker(millisecondsBetweenBackups);
			} else {
				imgMaker = new ImgMaker();
			}
			if (isScanInitialZIndexDefined) {
				imgMaker.createXScanningAndZCrossSectionEvenOddImages(ca, 0, xScanInitialIndex, colorMapper, colorMapper, 
						ImgMakerConstants.HD_HEIGHT/2, ImgMakerConstants.HD_HEIGHT/2, path + "/img", path + "/backups");				
			} else {
				imgMaker.createXScanningAndZCrossSectionEvenOddImages(ca, 0, colorMapper, colorMapper, 
						ImgMakerConstants.HD_HEIGHT/2, ImgMakerConstants.HD_HEIGHT/2, path + "/img", path + "/backups");
			}
		}		
	}
	
}
