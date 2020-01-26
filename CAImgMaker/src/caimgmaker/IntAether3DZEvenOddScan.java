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
import cellularautomata.automata.IntAether3DSwap;
import cellularautomata.automata.SymmetricIntActionableCellularAutomaton3D;

public class IntAether3DZEvenOddScan {
	
	public static void main(String[] args) throws Exception {
//		args = new String[]{"-3000", "9999999", "D:/data/test"};//debug
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
			
			SymmetricIntActionableCellularAutomaton3D ca;
			if (isRestore) {
				ca = new IntAether3DSwap(initValOrBackupPath, path);
			} else {
				ca = new IntAether3DSwap(initialValue, Long.parseLong("8589934592"), path);//8GiB
//				ca = new IntAether3DSwap(initialValue, Long.parseLong("10737418240"), path);//10GiB
//				ca = new IntAether3DSwap(initialValue, Long.parseLong("1048576"), path);//1MiB
			}
			boolean finished = false;
			while (ca.getStep() < step && !finished) {
				finished = !ca.nextStep();
				System.out.println("Current step: " + ca.getStep());
			}
			path += ca.getSubFolderPath();
			ColorMapper colorMapper = new GrayscaleMapper(0);
			CAImgMaker imgMaker = new CAImgMaker();
			imgMaker.createZScanningNonsymmetricEvenOddImages(ca, colorMapper, path + "/img/");

		}		
	}
	
}
