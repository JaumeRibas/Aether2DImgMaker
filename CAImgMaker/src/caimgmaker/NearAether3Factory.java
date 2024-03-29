/* Aether2DImgMaker -- console app to generate images of the Aether cellular automaton in 2D
    Copyright (C) 2017-2024 Jaume Ribas

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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ResourceBundle;

import caimgmaker.args.Args;
import caimgmaker.args.GridParameterValue;
import caimgmaker.args.ImageGenerationMode;
import caimgmaker.args.InitialConfigParameterValue.InitialConfigType;
import cellularautomata.automata.nearaether.SimpleBigIntNearAether3_3D;
import cellularautomata.model.Model;

public final class NearAether3Factory {
	
	private NearAether3Factory() {}
	
	public static Model create(Args args, ResourceBundle messages) throws FileNotFoundException, ClassNotFoundException, IOException {
		Model model = null;
		if (args.memorySafe) {
			System.out.printf(messages.getString("memory-safe-not-supported-for-this-model-format"), args.model, Args.MEMORY_SAFE);
		} else if (args.initialConfiguration == null && args.backupToRestorePath == null) {
			System.out.printf(messages.getString("initial-config-needed-format"), args.model);
//		} else if (args.backupToRestorePath != null && args.grid == null) { //uncomment if more grid types become supported
//			System.out.printf(gridTypeNeededToRestoreMessageFormat);
		} else if (args.imgGenerationMode == ImageGenerationMode.TOPPLING_ALTERNATION_COMPLIANCE) {
			System.out.printf(messages.getString("img-gen-mode-not-supported-format"), args.model);
		} else {
			if (args.grid == null) {
				args.grid = new GridParameterValue(3);//default
			}
			switch (args.grid.dimension) {
				case 3:
					if (args.grid.side == null) {
						if (args.backupToRestorePath == null) {
							if (args.initialConfiguration.type == InitialConfigType.SINGLE_SOURCE) {
								model = new SimpleBigIntNearAether3_3D(args.initialConfiguration.singleSource);
							} else {
								System.out.printf(messages.getString("initial-config-not-supported-format"), args.model);
							}
						} else {
							model = new SimpleBigIntNearAether3_3D(args.backupToRestorePath);
						}
					} else {
						System.out.printf(messages.getString("grid-not-supported-format"), args.model);
					}
					break;
				default:
					System.out.printf(messages.getString("grid-not-supported-format"), args.model);
			}
		}
		return model;
	}
	
}
