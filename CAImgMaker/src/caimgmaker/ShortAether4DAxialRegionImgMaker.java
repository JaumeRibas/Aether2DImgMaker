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
import cellularautomata.automata.ShortAether4D;
import cellularautomata.automata.SymmetricShortCellularAutomaton4D;

public class ShortAether4DAxialRegionImgMaker {
	
	public static void main(String[] args) throws Exception {
//		args = new String[]{"-200", "D:/data/test"};//debug
		if (args.length == 0) {
			System.err.println("You must specify an initial value.");
		} else {
			short initialValue;
			int scanInitialZIndex = 0;
			boolean isScanInitialZIndexDefined = false;
			BigInteger tmp = new BigInteger(args[0]);
			if (tmp.compareTo(BigInteger.valueOf(Short.MAX_VALUE)) <= 0 
					&& tmp.compareTo(BigInteger.valueOf(Short.MIN_VALUE)) >= 0) {
				initialValue = tmp.shortValue();
				SymmetricShortCellularAutomaton4D ca = new ShortAether4D(initialValue);
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
							scanInitialZIndex = Integer.parseInt(args[3]);
							isScanInitialZIndexDefined = true;
						}
					}
				} else {
					path = "./";
				}
				boolean finished = false;
				while (ca.getStep() < initialStep && !finished) {
					finished = !ca.nextStep();
					System.out.println("Current step: " + ca.getStep());
				}
				ColorMapper colorMapper = new GrayscaleMapper(0);
				path += ca.getSubFolderPath() + "/img/axial-region";	
				CAImgMaker imgMaker = new CAImgMaker();
				if (isScanInitialZIndexDefined) {
					imgMaker.createScanningAndCrossSectionImages(ca, ca.nonsymmetricSection().crossSectionAtZ(0), scanInitialZIndex, 
							0, colorMapper, colorMapper, Constants.HD_WIDTH/2, Constants.HD_HEIGHT/2, path);
				} else {
					imgMaker.createScanningAndCrossSectionImages(ca, ca.nonsymmetricSection().crossSectionAtZ(0), 
							0, colorMapper, colorMapper, Constants.HD_WIDTH/2, Constants.HD_HEIGHT/2, path);
				}
			} else {
				System.err.println("Initial value out of range.");
			}
		}		
	}
	
}
