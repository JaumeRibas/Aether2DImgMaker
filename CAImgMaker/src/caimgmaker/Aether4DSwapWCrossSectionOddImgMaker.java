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
import cellularautomata.Constants;
import cellularautomata.automata.aether.Aether4DAsymmetricSectionSwap;
import cellularautomata.evolvinggrid4d.ActionableEvolvingGrid4D;
import cellularautomata.evolvinggrid4d.ActionableEvolvingGrid4DWCrossSection;
import cellularautomata.grid3d.LongGrid3D;
import cellularautomata.grid4d.LongGrid4D;

public class Aether4DSwap3DCrossSectionOddImgMaker {
	
	public static void main(String[] args) throws Exception {
//		args = new String[]{"-2000", "1", "D:/data/test", "0", "0", "0"};//debug
		if (args.length < 2) {
			System.err.println("You must specify an initial value and a coordinate for the cross section.");
		} else {
			long initialValue = 0;
			boolean isRestore = false;
			String path;
			int initialStep = 0;
			int crossSectionW = 0;
			int xScanInitialIndex = 0;
			int zScanInitialIndex = 0;
			boolean isScanInitialIndexesDefined = false;
			long millisecondsBetweenBackups = 0;
			boolean isMillisecondsBetweenBackupsDefined = false;
			String initValOrBackupPath = args[0];
			if (initValOrBackupPath.matches("-?\\d+")) {
				BigInteger tmp = new BigInteger(initValOrBackupPath);
				if (tmp.compareTo(BigInteger.valueOf(Long.MAX_VALUE)) <= 0 
						&& tmp.compareTo(BigInteger.valueOf(Long.MIN_VALUE)) >= 0) {
					initialValue = tmp.longValue();
				} else {
					System.err.println("Initial value out of range.");
					return;
				}
			} else {
				isRestore = true;
			}
			BigInteger tmp = new BigInteger(args[1]);
			if (tmp.compareTo(BigInteger.valueOf(Integer.MAX_VALUE)) <= 0 
					&& tmp.compareTo(BigInteger.valueOf(Integer.MIN_VALUE)) >= 0) {
				crossSectionW = tmp.intValue();
				if (crossSectionW < 0)
					crossSectionW = -crossSectionW;
			} else {
				System.err.println("Cross section coordinate out of int range.");
				return;
			}
			if (args.length > 2) {
				path = args[2];
				char lastCharacter = path.charAt(path.length() - 1); 
				if (lastCharacter != '/' && lastCharacter != '\\') {
					path += "/";
				}
				if (args.length > 3) {
					initialStep = Integer.parseInt(args[3]);
					int nextArgIndex = 4;
					if (args.length > 5) {
						xScanInitialIndex = Integer.parseInt(args[4]);
						zScanInitialIndex = Integer.parseInt(args[5]);
						isScanInitialIndexesDefined = true;
						nextArgIndex = 6;
					}
					if (args.length > nextArgIndex) {
						millisecondsBetweenBackups = Long.parseLong(args[nextArgIndex]);
						isMillisecondsBetweenBackupsDefined = true;
					}
				}
			} else {
				path = "./";
			}
			ActionableEvolvingGrid4D<LongGrid4D> ca;
			if (isRestore) {
				ca = new Aether4DAsymmetricSectionSwap(initValOrBackupPath, path);
			} else {
				ca = new Aether4DAsymmetricSectionSwap(initialValue, Constants.ONE_GB*8, path);
			}
			boolean finished = false;
			while ((ca.getStep() < initialStep || crossSectionW > ca.getMaxW()) && !finished) {
				finished = !ca.nextStep();
				System.out.println("step: " + ca.getStep());
			}
			if (crossSectionW > ca.getMaxW()) {
				System.err.println("Cross section coordinate " + crossSectionW + " is outside all steps of Aether 4D with initial value " + initialValue + ".");
			} else {
				ColorMapper colorMapper = new GrayscaleMapper(0);
				ImgMaker imgMaker = null;
				if (isMillisecondsBetweenBackupsDefined) {
					imgMaker = new ImgMaker(millisecondsBetweenBackups);
				} else {
					imgMaker = new ImgMaker();
				}
				String backupPath = path + ca.getSubFolderPath() + "/backups";
				ActionableEvolvingGrid4DWCrossSection<LongGrid4D, LongGrid3D> xSection = 
						new ActionableEvolvingGrid4DWCrossSection<LongGrid4D, LongGrid3D>(ca, crossSectionW);
				String imagesPath = path + xSection.getSubFolderPath();
				if (isScanInitialIndexesDefined) {
					imgMaker.createXZScanningAndZCrossSectionOddImagesFromLongGrid3D(
							xSection, xScanInitialIndex, zScanInitialIndex, 0, colorMapper, colorMapper, ImgMakerConstants.HD_HEIGHT/2, ImgMakerConstants.HD_HEIGHT/2, 
							imagesPath, backupPath);
				} else {
					imgMaker.createXZScanningAndZCrossSectionOddImagesFromLongGrid3D(
							xSection, 0, colorMapper, colorMapper, ImgMakerConstants.HD_HEIGHT/2, ImgMakerConstants.HD_HEIGHT/2, 
							imagesPath, backupPath);
				}				
			}		
		}		
	}
	
}
