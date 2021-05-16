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
import cellularautomata.automata.BigIntAether4D;
import cellularautomata.evolvinggrid.EvolvingNumberGrid3D;
import cellularautomata.evolvinggrid.SymmetricEvolvingNumberGrid4D;
import cellularautomata.numbers.BigInt;

public class BigIntAether4DBisectingRegionOddImgMaker {
	
	public static void main(String[] args) throws Exception {
//		args = new String[]{"2000", "D:/data/test"};//debug
		if (args.length == 0) {
			System.err.println("You must specify an initial value.");
		} else {
			int scanInitialXIndex = 0;
			boolean isScanInitialZIndexDefined = false;
			BigInt initialValue = new BigInt(args[0]);
			SymmetricEvolvingNumberGrid4D<BigInt> ca = new BigIntAether4D(initialValue);
			String path;
			int initialStep = 0;
			if (args.length > 1) {
				path = args[1];
				char lastCharacter = path.charAt(path.length() - 1); 
				if (lastCharacter != '/' && lastCharacter != '\\') {
					path += "/";
				}
				if (args.length > 2) {
					initialStep = Integer.parseInt(args[2]);
					if (args.length > 3) {
						scanInitialXIndex = Integer.parseInt(args[3]);
						isScanInitialZIndexDefined = true;
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
			path += ca.getName() + "/" + new Timestamp(System.currentTimeMillis()).toString().replace(":", "");
			FileUtils.writeStringToFile(new File(path + "/initialValue.txt"), initialValue.toString(), Charset.forName("UTF8"));
			String backupPath = path + "/backups";
			EvolvingNumberGrid3D<BigInt> bisectingRegion = ca.asymmetricSection().crossSectionAtZ(0);
			String imagesPath = path + "/bisecting_region/img";
			ImgMaker imgMaker = new ImgMaker();
			if (isScanInitialZIndexDefined) {
				imgMaker.createXScanningAndZCrossSectionOddImages(bisectingRegion, scanInitialXIndex, 
						0, colorMapper, colorMapper, ImgMakerConstants.HD_WIDTH/2, ImgMakerConstants.HD_HEIGHT/2, imagesPath, backupPath);
			} else {
				imgMaker.createXScanningAndZCrossSectionOddImages(bisectingRegion, 
						0, colorMapper, colorMapper, ImgMakerConstants.HD_WIDTH/2, ImgMakerConstants.HD_HEIGHT/2, imagesPath, backupPath);
			}
		}		
	}
	
}
