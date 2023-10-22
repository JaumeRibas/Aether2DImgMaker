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
import java.util.ResourceBundle;

import caimgmaker.args.Args;
import caimgmaker.args.GridParameterValue;
import caimgmaker.args.ImageGenerationMode;
import caimgmaker.args.InitialConfigParameterValue.InitialConfigType;
import cellularautomata.automata.nearaether.IntNearAether1_3D;
import cellularautomata.model.Model;
import cellularautomata.numbers.BigInt;

public final class NearAether1Factory {

	private NearAether1Factory() {}

	public static Model create(Args args, ResourceBundle messages) throws FileNotFoundException, ClassNotFoundException, IOException {
		Model model = null;
		if (args.memorySafe) {
			System.out.printf(messages.getString("memory-safe-not-supported-for-this-model-message-format"), args.model);
		} else if (args.initialConfiguration == null && args.backupToRestorePath == null) {
			System.out.printf(messages.getString("initial-config-needed-message-format"), args.model);
//		} else if (args.backupToRestorePath != null && args.grid == null) { //uncomment if more grid types become supported
//			System.out.printf(gridTypeNeededToRestoreMessageFormat);
		} else if (args.imgGenerationMode == ImageGenerationMode.TOPPLING_ALTERNATION_COMPLIANCE) {
			System.out.printf(messages.getString("img-gen-mode-not-supported-message-format"), args.model);
		} else {
			if (args.grid == null) {
				args.grid = new GridParameterValue(3);//default
			}
			switch (args.grid.dimension) {
				case 3:
					if (args.grid.side == null) {
						if (args.backupToRestorePath == null) {
							if (args.initialConfiguration.type == InitialConfigType.SINGLE_SOURCE) {
								if (args.initialConfiguration.singleSource.compareTo(BigInt.valueOf(IntNearAether1_3D.MAX_INITIAL_VALUE)) <= 0
										&& args.initialConfiguration.singleSource.compareTo(BigInt.valueOf(IntNearAether1_3D.MIN_INITIAL_VALUE)) >= 0) {
									model = new IntNearAether1_3D(args.initialConfiguration.singleSource.intValue());
								} else {
									System.out.printf(messages.getString("single-source-out-of-range-message-format"), IntNearAether1_3D.MIN_INITIAL_VALUE, IntNearAether1_3D.MAX_INITIAL_VALUE);
								}
							} else {
								System.out.printf(messages.getString("initial-config-not-supported-message-format"), args.model);
							}
						} else {
							model = new IntNearAether1_3D(args.backupToRestorePath);
						}
					} else {
						System.out.printf(messages.getString("grid-not-supported-message-format"), args.model);
					}
					break;
				default:
					System.out.printf(messages.getString("grid-not-supported-message-format"), args.model);
			}
		}
		return model;
	}
	
}
