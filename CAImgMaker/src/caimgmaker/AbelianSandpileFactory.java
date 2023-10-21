/* Aether2DImgMaker -- console app to generate images of the Aether cellular automaton in 2D
    Copyright (C) 2017-2023 Jaume Ribas

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

import caimgmaker.args.Args;
import caimgmaker.args.GridParameterValue;
import caimgmaker.args.ImageGenerationMode;
import caimgmaker.args.InitialConfigParameterValue.InitialConfigType;
import cellularautomata.automata.IntAbelianSandpileSingleSource2D;
import cellularautomata.model.Model;
import cellularautomata.numbers.BigInt;

public final class AbelianSandpileFactory {
	
	private AbelianSandpileFactory() {}
	
	public static Model create(Args args) throws FileNotFoundException, ClassNotFoundException, IOException {
		Model model = null;
		if (args.memorySafe) {
			System.out.printf(Resources.MEMORY_SAFE_NOT_SUPPORTED_FOR_THIS_MODEL_MESSAGE_FORMAT, args.model);
		} else if (args.initialConfiguration == null && args.backupToRestorePath == null) {
			System.out.printf(Resources.INITIAL_CONFIG_NEEDED_MESSAGE_FORMAT, args.model);
//		} else if (args.backupToRestorePath != null && args.grid == null) { //uncomment if more grid types become supported
//			System.out.printf(gridTypeNeededToRestoreMessageFormat);
		} else if (args.imgGenerationMode == ImageGenerationMode.TOPPLING_ALTERNATION_COMPLIANCE) {
			System.out.printf(Resources.IMG_GEN_MODE_NOT_SUPPORTED_MESSAGE_FORMAT, args.model);
		} else {
			if (args.grid == null) {
				args.grid = new GridParameterValue(2);//default to 2D
			}
			switch (args.grid.dimension) {
				case 2:
					if (args.grid.side == null) {
						if (args.backupToRestorePath == null) {
							if (args.initialConfiguration.type == InitialConfigType.SINGLE_SOURCE) {
								if (args.initialConfiguration.singleSource.compareTo(BigInt.valueOf(Integer.MAX_VALUE)) <= 0
										&& args.initialConfiguration.singleSource.compareTo(BigInt.ZERO) >= 0) {
									model = new IntAbelianSandpileSingleSource2D(args.initialConfiguration.singleSource.intValue());
								} else {
									System.out.printf(Resources.SINGLE_SOURCE_OUT_OF_RANGE_MESSAGE_FORMAT, 0, Integer.MAX_VALUE);
								}
							} else {
								System.out.printf(Resources.INITIAL_CONFIG_NOT_SUPPORTED_MESSAGE_FORMAT, args.model);
							}
						} else {
							model = new IntAbelianSandpileSingleSource2D(args.backupToRestorePath);
						}
					} else {
						System.out.printf(Resources.GRID_NOT_SUPPORTED_MESSAGE_FORMAT, args.model);
					}
					break;
				default:
					System.out.printf(Resources.GRID_NOT_SUPPORTED_MESSAGE_FORMAT, args.model);
			}
		}
		return model;
	}
	
}
