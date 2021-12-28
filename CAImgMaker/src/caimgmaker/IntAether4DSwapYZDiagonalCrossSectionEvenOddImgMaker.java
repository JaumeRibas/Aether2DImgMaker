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
import cellularautomata.automata.aether.IntAether4DAsymmetricSectionSwap;
import cellularautomata.grid3d.IntGrid3D;
import cellularautomata.grid4d.Grid4D;
import cellularautomata.grid4d.IntGrid4D;
import cellularautomata.model4d.ActionableModel4D;
import cellularautomata.model4d.ActionableModel4DYZDiagonalCrossSection;

public class IntAether4DSwapYZDiagonalCrossSectionEvenOddImgMaker {
	
	public static void main(String[] args) throws Exception {
//		args = new String[]{"-613566757", "0", "D:/data/test", "0"/*, "0", "0"*/};//debug
		if (args.length < 2) {
			System.err.println("You must specify an initial value and a z offset from y for the cross section.");
		} else {
			int initialValue = 0;
			boolean isRestore = false;
			String path;
			int initialStep = 0;
			int zOffsetFromY = 0;
//			int xScanInitialIndex = 0;
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
			BigInteger tmp = new BigInteger(args[1]);
			if (tmp.compareTo(BigInteger.valueOf(Integer.MAX_VALUE)) <= 0 
					&& tmp.compareTo(BigInteger.valueOf(Integer.MIN_VALUE)) >= 0) {
				zOffsetFromY = tmp.intValue();
			} else {
				System.err.println("Z offset from y out of int range.");
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
//						xScanInitialIndex = Integer.parseInt(args[4]);
						zScanInitialIndex = Integer.parseInt(args[5]);
						isScanInitialIndexesDefined = true;
//						nextArgIndex = 6;
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
			ActionableModel4D<IntGrid4D> ca;
			if (isRestore) {
				ca = new IntAether4DAsymmetricSectionSwap(initValOrBackupPath, path);
			} else {
				ca = new IntAether4DAsymmetricSectionSwap(initialValue, Constants.ONE_GB*8, path);
			}
			boolean finished = false;
			while ((ca.getStep() < initialStep || !checkCrossSectionInBounds(ca, zOffsetFromY)) && !finished) {
				finished = !ca.nextStep();
				System.out.println("step: " + ca.getStep());
			}
			if (!checkCrossSectionInBounds(ca, zOffsetFromY)) {
				System.err.println("Z offset from y " + zOffsetFromY + " is out of bounds in all steps of Aether 4D with initial value " + initialValue + ".");
			} else {
				ColorMapper colorMapper = new GrayscaleMapper(0);
				ImgMaker imgMaker = null;
				if (isMillisecondsBetweenBackupsDefined) {
					imgMaker = new ImgMaker(millisecondsBetweenBackups);
				} else {
					imgMaker = new ImgMaker();
				}
				String backupPath = path + ca.getSubfolderPath() + "/backups";
				ActionableModel4DYZDiagonalCrossSection<IntGrid4D, IntGrid3D> crossSection = 
						new ActionableModel4DYZDiagonalCrossSection<IntGrid4D, IntGrid3D>(ca, zOffsetFromY);
				String imagesPath = path + crossSection.getSubfolderPath();
				int crossSectionZ = 0;
				if (isScanInitialIndexesDefined) {
					imgMaker.createXScanningAndZCrossSectionImagesFromEvenOddXY(
							crossSection, zScanInitialIndex, crossSectionZ, colorMapper, ImgMakerConstants.HD_HEIGHT/2, ImgMakerConstants.HD_HEIGHT/2, 
							imagesPath, backupPath);
				} else {
					imgMaker.createXScanningAndZCrossSectionImagesFromEvenOddXY(
							crossSection, crossSectionZ, colorMapper, ImgMakerConstants.HD_HEIGHT/2, ImgMakerConstants.HD_HEIGHT/2, 
							imagesPath, backupPath);
				}				
			}		
		}
	}
	
	private static boolean checkCrossSectionInBounds(Grid4D grid, int zOffsetFromY) {
		int y = grid.getMinY();
		int maxY = grid.getMaxY();
		int crossSectionZ = y + zOffsetFromY;
		while (y <= maxY && (crossSectionZ < grid.getMinZAtY(y) || crossSectionZ > grid.getMaxZAtY(y))) {
			y++;
			crossSectionZ++;
		}
		return y <= maxY;
	}
	
}
