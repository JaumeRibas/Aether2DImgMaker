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

import java.io.File;
import java.nio.charset.Charset;
import java.sql.Timestamp;

import org.apache.commons.io.FileUtils;

import caimgmaker.colormap.ColorMapper;
import caimgmaker.colormap.GrayscaleMapper;
import cellularautomata.automata.BigIntAether3DEnclosed;
import cellularautomata.numbers.BigInt;

public class BigIntAether3DEnclosedEvenOddImgMaker {
	
	public static void main(String[] args) throws Exception {
//		args = new String[]{"-1000", "101", "D:/data/test"};//, "150", "30", "10000"};//debug
		if (args.length < 1) {
			System.err.println("You must, at least, specify either an initial value and a grid side, or a path to a backup file to restore.");
			return;
		}
		BigInt initialValue = null;
		boolean isRestore = false;
		int gridSide = 0;
		String path;
		int initialStep = 0;
		int xScanInitialIndex = 0;
		boolean isScanInitialXIndexDefined = false;	
		long millisecondsBetweenBackups = 0;
		boolean isBackupLeapDefined = false;
		String initValOrBackupPath = args[0];
		int argIndex;
		if (initValOrBackupPath.matches("-?\\d+")) {
			initialValue = new BigInt(initValOrBackupPath);
			if (args.length < 2) {
				System.err.println("You must, at least, specify either an initial value and a grid side, or a path to a backup file to restore.");
				return;
			}
			if (args[1].matches("-?\\d+")) {
				BigInt bigGridSide = new BigInt(args[1]);
				if (bigGridSide.compareTo(BigInt.valueOf(Integer.MAX_VALUE)) > 0) {
					System.err.println("Grid side out of range.");
					return;
				}
				gridSide = bigGridSide.intValue();
			} else {
				System.err.println("Grid side must be a valid integer without commas.");
				return;
			}
			argIndex = 2;
		} else {
			isRestore = true;
			argIndex = 1;
		}
		if (args.length > argIndex) {
			path = args[argIndex];
			char lastCharacter = path.charAt(path.length() - 1); 
			if (lastCharacter != '/' && lastCharacter != '\\') {
				path += "/";
			}
			argIndex++;
			if (args.length > argIndex) {
				initialStep = Integer.parseInt(args[argIndex]);
				argIndex++;
				if (args.length > argIndex) {
					xScanInitialIndex = Integer.parseInt(args[argIndex]);
					isScanInitialXIndexDefined = true;
					argIndex++;
					if (args.length > argIndex) {
						millisecondsBetweenBackups = Long.parseLong(args[argIndex]);
						isBackupLeapDefined = true;
					}
				}
			}
		} else {
			path = "./";
		}
		BigIntAether3DEnclosed ca;
		if (isRestore) {
			ca = new BigIntAether3DEnclosed(initValOrBackupPath);
		} else {
			ca = new BigIntAether3DEnclosed(initialValue, gridSide);
		}
		gridSide = ca.getSide();
		boolean finished = false;
		while (ca.getStep() < initialStep && !finished) {
			finished = !ca.nextStep();
			System.out.println("step: " + ca.getStep());
		}
		ColorMapper colorMapper = new GrayscaleMapper(0);
		path += ca.getName() + "/" + gridSide + "/" + new Timestamp(System.currentTimeMillis()).toString().replace(":", "");
		FileUtils.writeStringToFile(new File(path + "/initialValue.txt"), ca.getInitialValue().toString(), Charset.forName("UTF8"));
		ImgMaker imgMaker = null;
		if (isBackupLeapDefined) {
			imgMaker = new ImgMaker(millisecondsBetweenBackups);
		} else {
			imgMaker = new ImgMaker();
		}
		int imageSide = (gridSide+1)/2;
		int tmp = ImgMakerConstants.HD_HEIGHT/gridSide;
		if (tmp > 1) {
			imageSide *= tmp;
		}
		if (isScanInitialXIndexDefined) {
			imgMaker.createXScanningAndZCrossSectionEvenOddImages(ca.asymmetricSection(), xScanInitialIndex, 0, colorMapper, colorMapper, 
					imageSide, imageSide, path + "/img", path + "/backups");				
		} else {
			imgMaker.createXScanningAndZCrossSectionEvenOddImages(ca.asymmetricSection(), 0, colorMapper, colorMapper, 
					imageSide, imageSide, path + "/img", path + "/backups");
		}
	}
	
}
