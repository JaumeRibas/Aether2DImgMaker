/* Aether2DImgMaker -- console app to generate images of the Aether cellular automaton in 2D
    Copyright (C) 2017-2018 Jaume Ribas

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

import caimgmaker.colormap.ColorMapper;
import caimgmaker.colormap.GrayscaleMapper;
import cellularautomata.automata.IntAether3DSwap;
import cellularautomata.automata.SymmetricIntCellularAutomaton3D;

public class Aether3DImgMaker {
	
	public static void main(String[] args) throws Exception {
//		args = new String[]{"-2000", "D:/data/test"};//debug
		if (args.length == 0) {
			System.err.println("You must specify an initial value.");
		} else {
			int initialValue = 0;
			SymmetricIntCellularAutomaton3D ca;
			String initValOrBackupPath = args[0];
			boolean isRestore = false;
			try {
				initialValue = Integer.parseInt(initValOrBackupPath);
			} catch (NumberFormatException ex) {
				isRestore = true;
			}
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
				}
			} else {
				path = "./";
			}
			if (isRestore) {
				ca = new IntAether3DSwap(initValOrBackupPath, path);
			} else {
				ca = new IntAether3DSwap(initialValue, Long.parseLong("10737418240"), path);//10GiB
//				ca = new IntAether3DSwap(initialValue, Long.parseLong("1048576"), path);//1MiB
			}
			boolean finished = false;
			while (ca.getStep() < initialStep && !finished) {
				finished = !ca.nextStep();
				System.out.println("Current step: " + ca.getStep());
			}
			path += ca.getSubFolderPath();
			ColorMapper colorMapper = new GrayscaleMapper(0);
			CAImgMaker imgMaker = new CAImgMaker();
			imgMaker.createScanningAndCrossSectionNonSymmetricImages(ca, 0, colorMapper, colorMapper, Constants.HD_WIDTH/2, Constants.HD_HEIGHT/2, 
					path + "/img/", path + "/backups/");

		}		
	}
	
}
