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
import cellularautomata.automata.aether.IntAether3DAsymmetricSectionSwap;
import cellularautomata.evolvinggrid3d.ActionableEvolvingGrid3D;
import cellularautomata.grid3d.IntGrid3D;

public class IntAether3DSwapXScan {
	
	public static void main(String[] args) throws Exception {
//		args = new String[]{"-2000", "371", "D:/data/test"};//debug
		if (args.length < 2) {
			System.err.println("You must specify an initial value and a step to scan.");
		} else {
			int initialValue = 0;
			boolean isRestore = false;
			String path;
			int step = 0;
			
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
			step = Integer.parseInt(args[1]);
			if (args.length > 2) {
				path = args[2];
				char lastCharacter = path.charAt(path.length() - 1); 
				if (lastCharacter != '/' && lastCharacter != '\\') {
					path += "/";
				}
			} else {
				path = "./";
			}
			
			ActionableEvolvingGrid3D<IntGrid3D> ca;
			if (isRestore) {
				ca = new IntAether3DAsymmetricSectionSwap(initValOrBackupPath, path);
			} else {
				ca = new IntAether3DAsymmetricSectionSwap(initialValue, Constants.ONE_GB*8, path);
			}
			boolean finished = false;
			while (ca.getStep() < step && !finished) {
				finished = !ca.nextStep();
				System.out.println("step: " + ca.getStep());
			}
			path += ca.getSubFolderPath();
			ColorMapper colorMapper = new GrayscaleMapper(0);
			ImgMaker imgMaker = new ImgMaker();
			imgMaker.createXScanningImages(ca, colorMapper, ImgMakerConstants.HD_HEIGHT/2, ImgMakerConstants.HD_HEIGHT/2, 
					path + "/img");

		}		
	}
	
}
