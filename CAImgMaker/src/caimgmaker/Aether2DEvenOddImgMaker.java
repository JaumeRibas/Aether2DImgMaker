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

import java.awt.Color;

import caimgmaker.colormap.ColorMapper;
import caimgmaker.colormap.HueWithBackgroundMapper;
import cellularautomata.automata.aether.Aether2D;
import cellularautomata.evolvinggrid2d.EvolvingLongGrid2D;

public class Aether2DEvenOddImgMaker {
	
	public static void main(String[] args) throws Exception {
//		args = new String[]{"10000000", "D:/data/test"};//debug
		if (args.length == 0) {
			System.err.println("You must specify an initial value.");
		} else {
			long initialValue = Long.parseLong(args[0]);
			EvolvingLongGrid2D ca = new Aether2D(initialValue).asymmetricSection();
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
			boolean finished = false;
			while (ca.getStep() < initialStep && !finished) {
				finished = !ca.nextStep();
				System.out.println("step: " + ca.getStep());
			}
			ColorMapper colorMapper = new HueWithBackgroundMapper(0, Color.BLACK);
			path += ca.getSubFolderPath() + "/img";	
			ImgMaker imgMaker = new ImgMaker();
			imgMaker.createEvenOddImages(ca, colorMapper, ImgMakerConstants.HD_HEIGHT/2, ImgMakerConstants.HD_HEIGHT/2, path);
		}		
	}
	
}
