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
import cellularautomata.automata.siv.IntSpreadIntegerValue;
import cellularautomata.automata.siv.IntSpreadIntegerValue2D;
import cellularautomata.automata.siv.LongSpreadIntegerValue1D;
import cellularautomata.automata.siv.LongSpreadIntegerValue2D;
import cellularautomata.automata.siv.LongSpreadIntegerValue3D;
import cellularautomata.automata.siv.LongSpreadIntegerValue4D;
import cellularautomata.model.Model;
import cellularautomata.numbers.BigInt;

public final class SpreadIntegerValueFactory {
	
	private SpreadIntegerValueFactory() {}
	
	public static Model create(Args args, ResourceBundle messages) throws FileNotFoundException, ClassNotFoundException, IOException {
		Model model = null;
		if (args.memorySafe) {
			System.out.printf(messages.getString("memory-safe-not-supported-for-this-model-format"), args.model, Args.MEMORY_SAFE);
		} else if (args.initialConfiguration == null && args.backupToRestorePath == null) {
			System.out.printf(messages.getString("initial-config-needed-format"), args.model);
		} else if (args.backupToRestorePath != null && args.grid == null) {
			System.out.printf(messages.getString("grid-type-needed-in-order-to-restore-format"));
		} else if (args.imgGenerationMode == ImageGenerationMode.TOPPLING_ALTERNATION_COMPLIANCE) {
			System.out.printf(messages.getString("img-gen-mode-not-supported-format"), args.model);
		} else {
			if (args.grid == null) {
				args.grid = new GridParameterValue(2);//default to 2D
			}
			switch (args.grid.dimension) {
				case 1:
					model = create1d(args, messages);
					break;
				case 2:
					model = create2d(args, messages);
					break;
				case 3:
					model = create3d(args, messages);
					break;
				case 4:
					model = create4d(args, messages);
					break;
				default:
					model = createNd(args, messages);
					break;
			}
		}
		return model;
	}
	
	private static Model create1d(Args args, ResourceBundle messages) throws FileNotFoundException, ClassNotFoundException, IOException {
		Model model = null;
		if (args.grid.side == null) {
			if (args.backupToRestorePath == null) {
				if (args.initialConfiguration.type == InitialConfigType.SINGLE_SOURCE) {
					if (args.initialConfiguration.singleSource.compareTo(BigInt.valueOf(Long.MAX_VALUE)) <= 0
							&& args.initialConfiguration.singleSource.compareTo(BigInt.valueOf(Long.MIN_VALUE)) >= 0) {
						model = new LongSpreadIntegerValue1D(args.initialConfiguration.singleSource.longValue(), 0); //TODO support background value?
					} else {
						System.out.printf(messages.getString("single-source-out-of-range-format"), Long.MIN_VALUE, Long.MAX_VALUE);
					}								
				} else {
					System.out.printf(messages.getString("initial-config-not-supported-format"), args.model);
				}
			} else {
				model = new LongSpreadIntegerValue1D(args.backupToRestorePath);
			}
		} else {
			System.out.printf(messages.getString("grid-not-supported-format"), args.model);
		}
		return model;
	}
	
	private static Model create2d(Args args, ResourceBundle messages) throws FileNotFoundException, ClassNotFoundException, IOException {
		Model model = null;
		if (args.grid.side == null) {
			if (args.backupToRestorePath == null) {
				if (args.initialConfiguration.type == InitialConfigType.SINGLE_SOURCE) {
					if (args.initialConfiguration.singleSource.compareTo(BigInt.valueOf(Integer.MAX_VALUE)) <= 0
							&& args.initialConfiguration.singleSource.compareTo(BigInt.valueOf(Integer.MIN_VALUE)) >= 0) {
						model = new IntSpreadIntegerValue2D(args.initialConfiguration.singleSource.intValue(), 0);
					} else if (args.initialConfiguration.singleSource.compareTo(BigInt.valueOf(Long.MAX_VALUE)) <= 0
							&& args.initialConfiguration.singleSource.compareTo(BigInt.valueOf(Long.MIN_VALUE)) >= 0) {
						model = new LongSpreadIntegerValue2D(args.initialConfiguration.singleSource.longValue(), 0);
					} else {
						System.out.printf(messages.getString("single-source-out-of-range-format"), Long.MIN_VALUE, Long.MAX_VALUE);
					}
				} else {
					System.out.printf(messages.getString("initial-config-not-supported-format"), args.model);
				}
			} else {
				try {
					model = new IntSpreadIntegerValue2D(args.backupToRestorePath);
				} catch (Exception ex) {
					model = new LongSpreadIntegerValue2D(args.backupToRestorePath);
				}
			}
		} else {
			System.out.printf(messages.getString("grid-not-supported-format"), args.model);
		}
		return model;
	}
	
	private static Model create3d(Args args, ResourceBundle messages) throws FileNotFoundException, ClassNotFoundException, IOException {
		Model model = null;
		if (args.grid.side == null) {
			if (args.backupToRestorePath == null) {
				if (args.initialConfiguration.type == InitialConfigType.SINGLE_SOURCE) {
					if (args.initialConfiguration.singleSource.compareTo(BigInt.valueOf(Long.MAX_VALUE)) <= 0
							&& args.initialConfiguration.singleSource.compareTo(BigInt.valueOf(Long.MIN_VALUE)) >= 0) {
						model = new LongSpreadIntegerValue3D(args.initialConfiguration.singleSource.longValue());
					} else {
						System.out.printf(messages.getString("single-source-out-of-range-format"), Long.MIN_VALUE, Long.MAX_VALUE);
					}
				} else {
					System.out.printf(messages.getString("initial-config-not-supported-format"), args.model);
				}
			} else {
				model = new LongSpreadIntegerValue3D(args.backupToRestorePath);
			}
		} else {
			System.out.printf(messages.getString("grid-not-supported-format"), args.model);
		}
		return model;
	}
	
	private static Model create4d(Args args, ResourceBundle messages) throws FileNotFoundException, ClassNotFoundException, IOException {
		Model model = null;
		if (args.grid.side == null) {
			if (args.backupToRestorePath == null) {
				if (args.initialConfiguration.type == InitialConfigType.SINGLE_SOURCE) {
					if (args.initialConfiguration.singleSource.compareTo(BigInt.valueOf(Long.MAX_VALUE)) <= 0
							&& args.initialConfiguration.singleSource.compareTo(BigInt.valueOf(Long.MIN_VALUE)) >= 0) {
						model = new LongSpreadIntegerValue4D(args.initialConfiguration.singleSource.longValue());
					} else {
						System.out.printf(messages.getString("single-source-out-of-range-format"), Long.MIN_VALUE, Long.MAX_VALUE);
					}
				} else {
					System.out.printf(messages.getString("initial-config-not-supported-format"), args.model);
				}
			} else {
				model = new LongSpreadIntegerValue4D(args.backupToRestorePath);
			}
		} else {
			System.out.printf(messages.getString("grid-not-supported-format"), args.model);
		}
		return model;
	}
	
	private static Model createNd(Args args, ResourceBundle messages) throws FileNotFoundException, ClassNotFoundException, IOException {
		Model model = null;
		if (args.grid.side == null) {
			if (args.backupToRestorePath == null) {
				if (args.initialConfiguration.type == InitialConfigType.SINGLE_SOURCE) {
					if (args.initialConfiguration.singleSource.compareTo(BigInt.valueOf(Integer.MAX_VALUE)) <= 0
							&& args.initialConfiguration.singleSource.compareTo(BigInt.valueOf(Integer.MIN_VALUE)) >= 0) {
						model = new IntSpreadIntegerValue(args.grid.dimension, args.initialConfiguration.singleSource.intValue(), 0);
					} else {
						System.out.printf(messages.getString("single-source-out-of-range-format"), Integer.MIN_VALUE, Integer.MAX_VALUE);
					}
				} else {
					System.out.printf(messages.getString("initial-config-not-supported-format"), args.model);
				}
			} else {
				model = new IntSpreadIntegerValue(args.backupToRestorePath);
			}
		} else {
			System.out.printf(messages.getString("grid-not-supported-format"), args.model);
		}
		return model;
	}
	
}
