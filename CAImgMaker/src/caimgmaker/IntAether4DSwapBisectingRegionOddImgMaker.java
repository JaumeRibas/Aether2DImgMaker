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
import cellularautomata.automata.IntAether4DAsymmetricSectionSwap;
import cellularautomata.evolvinggrid.ActionableEvolvingGrid4D;
import cellularautomata.evolvinggrid.ActionableEvolvingGrid4DZCrossSection;
import cellularautomata.grid.CAConstants;
import cellularautomata.grid3d.IntGrid3D;
import cellularautomata.grid4d.IntGrid4D;

public class IntAether4DSwapBisectingRegionOddImgMaker {
	
	public static void main(String[] args) throws Exception {
//		args = new String[]{"-2000", "D:/data/test", "0", "3", "6"};//debug
		if (args.length == 0) {
			System.err.println("You must specify an initial value.");
		} else {
			int initialValue = 0;
			boolean isRestore = false;
			String path;
			int initialStep = 0;
			int xScanInitialIndex = 0;
			int zScanInitialIndex = 0;
			boolean isScanInitialIndexesDefined = false;
			long millisecondsBetweenBackups = 0;
			boolean isMillisecondsBetweenBackupsDefined = false;
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
					int nextArgIndex = 3;
					if (args.length > 4) {
						xScanInitialIndex = Integer.parseInt(args[3]);
						zScanInitialIndex = Integer.parseInt(args[4]);
						isScanInitialIndexesDefined = true;
						nextArgIndex = 5;
					}
					if (args.length > nextArgIndex) {
						millisecondsBetweenBackups = Long.parseLong(args[nextArgIndex]);
						isMillisecondsBetweenBackupsDefined = true;
					}
				}
			} else {
				path = "./";
			}
			ActionableEvolvingGrid4D<IntGrid4D> ca;
			if (isRestore) {
				ca = new IntAether4DAsymmetricSectionSwap(initValOrBackupPath, path);
			} else {
				ca = new IntAether4DAsymmetricSectionSwap(initialValue, CAConstants.ONE_GB*8, path);
			}
			boolean finished = false;
			while (ca.getStep() < initialStep && !finished) {
				finished = !ca.nextStep();
				System.out.println("step: " + ca.getStep());
			}
			ColorMapper colorMapper = new GrayscaleMapper(0);
			ImgMaker imgMaker = null;
			if (isMillisecondsBetweenBackupsDefined) {
				imgMaker = new ImgMaker(millisecondsBetweenBackups);
			} else {
				imgMaker = new ImgMaker();
			}
			ActionableEvolvingGrid4DZCrossSection<IntGrid4D, IntGrid3D> xSection = 
					new ActionableEvolvingGrid4DZCrossSection<IntGrid4D, IntGrid3D>(ca, 0);
			String imgsPath = path + xSection.getSubFolderPath() + "/img";
			String backupPath = path + ca.getSubFolderPath() + "/backups";
			if (isScanInitialIndexesDefined) {
				imgMaker.createXZScanningAndZCrossSectionOddImagesFromIntGrid3D(
						xSection, xScanInitialIndex, zScanInitialIndex, 0, colorMapper, colorMapper, ImgMakerConstants.HD_WIDTH/2, ImgMakerConstants.HD_HEIGHT/2, 
						imgsPath, backupPath);
			} else {
				imgMaker.createXZScanningAndZCrossSectionOddImagesFromIntGrid3D(
						xSection, 0, colorMapper, colorMapper, ImgMakerConstants.HD_WIDTH/2, ImgMakerConstants.HD_HEIGHT/2, 
						imgsPath, backupPath);
			}
			
		}		
	}
	
}
