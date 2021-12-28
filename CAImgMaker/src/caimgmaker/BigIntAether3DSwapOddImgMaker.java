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
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.sql.Timestamp;

import org.apache.commons.io.FileUtils;

import caimgmaker.colormap.ColorMapper;
import caimgmaker.colormap.GrayscaleMapper;
import cellularautomata.automata.aether.BigIntAether3DAsymmetricSectionSwap;
import cellularautomata.numbers.BigInt;

public class BigIntAether3DSwapOddImgMaker {
	
	public static void main(String[] args) throws Exception {
//		args = new String[]{"-999999999999999999999999999", "50", "C:/data/test"};//, "150", "30", "10000"};//debug
		if (args.length < 2) {
			System.err.println("You must specify an initial value and a maximum grid side in heap.");
		} else {
			BigInt initialValue = null;
			boolean isRestore = false;
			int maxGridSideInHeap = 0;
			String path;
			int initialStep = 0;
			int scanInitialZIndex = 0;
			boolean isScanInitialZIndexDefined = false;	
			long millisecondsBetweenBackups = 0;
			boolean isBackupLeapDefined = false;
			String initValOrBackupPath = args[0];
			if (initValOrBackupPath.matches("-?\\d+")) {
				initialValue = new BigInt(initValOrBackupPath);
			} else {
				isRestore = true;
			}
			if (args[1].matches("\\d+")) {
				BigInteger tmp = new BigInteger(args[1]);
				if (tmp.compareTo(BigInteger.valueOf(Integer.MAX_VALUE)) <= 0) {
					maxGridSideInHeap = tmp.intValue();
				} else {
					System.err.println("Maximum grid side in heap out of range.");
					return;
				}
			} else {
				System.err.println("The maximum grid side in heap must be a positive integer.");
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
					if (args.length > 3) {
						scanInitialZIndex = Integer.parseInt(args[4]);
						isScanInitialZIndexDefined = true;
						if (args.length > 4) {
							millisecondsBetweenBackups = Long.parseLong(args[5]);
							isBackupLeapDefined = true;
						}
					}
				}
			} else {
				path = "./";
			}
			path += "Aether3D/" + new Timestamp(System.currentTimeMillis()).toString().replace(":", "");
			BigIntAether3DAsymmetricSectionSwap ca;
			if (isRestore) {
				ca = new BigIntAether3DAsymmetricSectionSwap(initValOrBackupPath, path);
			} else {
				ca = new BigIntAether3DAsymmetricSectionSwap(initialValue, maxGridSideInHeap, path);
			}
			boolean finished = false;
			while (ca.getStep() < initialStep && !finished) {
				finished = !ca.nextStep();
				System.out.println("step: " + ca.getStep());
			}
			FileUtils.writeStringToFile(new File(path + "/initialValue.txt"), ca.getInitialValue().toString(), Charset.forName("UTF8"));
			ColorMapper colorMapper = new GrayscaleMapper(0);
			ImgMaker imgMaker = null;
			if (isBackupLeapDefined) {
				imgMaker = new ImgMaker(millisecondsBetweenBackups);
			} else {
				imgMaker = new ImgMaker();
			}
			if (isScanInitialZIndexDefined) {
				imgMaker.createXScanningAndZCrossSectionOddImages(ca, scanInitialZIndex, 0, colorMapper, 
						ImgMakerConstants.HD_WIDTH/2, ImgMakerConstants.HD_HEIGHT/2, path + "/img", path + "/backups");
			} else {
				imgMaker.createXScanningAndZCrossSectionOddImages(ca, 0, colorMapper, 
						ImgMakerConstants.HD_WIDTH/2, ImgMakerConstants.HD_HEIGHT/2, path + "/img", path + "/backups");
			}
			
		}		
	}
	
}
