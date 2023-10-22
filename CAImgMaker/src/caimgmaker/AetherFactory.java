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
import cellularautomata.automata.aether.BigIntAether2D;
import cellularautomata.automata.aether.BigIntAether2DTopplingAlternationCompliance;
import cellularautomata.automata.aether.BigIntAether3D;
import cellularautomata.automata.aether.BigIntAether3DCubicGrid;
import cellularautomata.automata.aether.BigIntAether3DTopplingAlternationCompliance;
import cellularautomata.automata.aether.BigIntAether4D;
import cellularautomata.automata.aether.BigIntAether4DTopplingAlternationCompliance;
import cellularautomata.automata.aether.FileBackedLongAether1D;
import cellularautomata.automata.aether.FileBackedLongAether2D;
import cellularautomata.automata.aether.FileBackedLongAether3D;
import cellularautomata.automata.aether.FileBackedLongAether4D;
import cellularautomata.automata.aether.FileBackedLongAether5D;
import cellularautomata.automata.aether.IntAether2D;
import cellularautomata.automata.aether.IntAether2DRandomConfiguration;
import cellularautomata.automata.aether.IntAether2DTopplingAlternationCompliance;
import cellularautomata.automata.aether.IntAether3D;
import cellularautomata.automata.aether.IntAether3DRandomConfiguration;
import cellularautomata.automata.aether.IntAether3DTopplingAlternationCompliance;
import cellularautomata.automata.aether.IntAether4D;
import cellularautomata.automata.aether.IntAether4DTopplingAlternationCompliance;
import cellularautomata.automata.aether.IntAether5D;
import cellularautomata.automata.aether.IntAether5DTopplingAlternationCompliance;
import cellularautomata.automata.aether.LongAether1D;
import cellularautomata.automata.aether.LongAether1DTopplingAlternationCompliance;
import cellularautomata.automata.aether.LongAether2D;
import cellularautomata.automata.aether.LongAether2DTopplingAlternationCompliance;
import cellularautomata.automata.aether.LongAether3D;
import cellularautomata.automata.aether.LongAether3DCubicGrid;
import cellularautomata.automata.aether.LongAether3DTopplingAlternationCompliance;
import cellularautomata.automata.aether.LongAether4D;
import cellularautomata.automata.aether.LongAether4DTopplingAlternationCompliance;
import cellularautomata.automata.aether.LongAether5D;
import cellularautomata.automata.aether.LongAether5DTopplingAlternationCompliance;
import cellularautomata.model.Model;
import cellularautomata.numbers.BigInt;

public final class AetherFactory {
	
	private AetherFactory() {}
	
	public static Model create(Args args, ResourceBundle messages) throws FileNotFoundException, ClassNotFoundException, IOException {
		Model model = null;
		if (args.initialConfiguration == null && args.backupToRestorePath == null) {
			System.out.printf(messages.getString("initial-config-needed-message-format"), args.model);
		} else if (args.backupToRestorePath != null && args.grid == null) {
			System.out.printf(messages.getString("grid-type-needed-in-order-to-restore-message-format"));//TODO Add AetherImgMakerBackup class with parameters
		} else if (args.memorySafe && args.initialConfiguration.type != InitialConfigType.SINGLE_SOURCE) {
			System.out.printf(messages.getString("memory-safe-not-supported-for-this-initial-config-message-format"), args.model);
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
				case 5:
					model = create5d(args, messages);
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
					if (args.imgGenerationMode == ImageGenerationMode.TOPPLING_ALTERNATION_COMPLIANCE) {
						if (args.memorySafe) {
							System.out.printf(messages.getString("memory-safe-not-supported-with-these-params-message-format"));
						} else {
							if (args.initialConfiguration.singleSource.compareTo(BigInt.valueOf(LongAether1DTopplingAlternationCompliance.MAX_INITIAL_VALUE)) <= 0
									&& args.initialConfiguration.singleSource.compareTo(BigInt.valueOf(LongAether1DTopplingAlternationCompliance.MIN_INITIAL_VALUE)) >= 0) {
								model = new LongAether1DTopplingAlternationCompliance(args.initialConfiguration.singleSource.longValue());
							} else {
								System.out.printf(messages.getString("single-source-out-of-range-message-format"), LongAether1DTopplingAlternationCompliance.MIN_INITIAL_VALUE, LongAether1DTopplingAlternationCompliance.MAX_INITIAL_VALUE);
							}
						}
					} else {
						if (args.memorySafe) {
							if (args.initialConfiguration.singleSource.compareTo(BigInt.valueOf(FileBackedLongAether1D.MAX_INITIAL_VALUE)) <= 0
									&& args.initialConfiguration.singleSource.compareTo(BigInt.valueOf(FileBackedLongAether1D.MIN_INITIAL_VALUE)) >= 0) {
								model = new FileBackedLongAether1D(args.initialConfiguration.singleSource.longValue(), args.path);
							} else {
								System.out.printf(messages.getString("single-source-out-of-range-message-format"), FileBackedLongAether1D.MIN_INITIAL_VALUE, FileBackedLongAether1D.MAX_INITIAL_VALUE);
							}
						} else {
							if (args.initialConfiguration.singleSource.compareTo(BigInt.valueOf(LongAether1D.MAX_INITIAL_VALUE)) <= 0
									&& args.initialConfiguration.singleSource.compareTo(BigInt.valueOf(LongAether1D.MIN_INITIAL_VALUE)) >= 0) {
								model = new LongAether1D(args.initialConfiguration.singleSource.longValue());
							} else {
								System.out.printf(messages.getString("single-source-out-of-range-message-format"), LongAether1D.MIN_INITIAL_VALUE, LongAether1D.MAX_INITIAL_VALUE);
							}
						}
					}
				} else {
					System.out.printf(messages.getString("initial-config-not-supported-with-these-params-message-format"));
				}
			} else {
				//TODO Add AetherImgMakerBackup class with parameters
				boolean successfullyRestored = true;
				if (args.memorySafe) {
					model = new FileBackedLongAether1D(args.backupToRestorePath, args.path);
				} else if (args.imgGenerationMode == ImageGenerationMode.TOPPLING_ALTERNATION_COMPLIANCE) {
					try {
						model = new LongAether1DTopplingAlternationCompliance(args.backupToRestorePath);							
					} catch (Exception ex) {
						successfullyRestored = false;				
					}
				} else {	
					try {
						model = new LongAether1D(args.backupToRestorePath);							
					} catch (Exception ex) {
						successfullyRestored = false;						
					}
				}
				if (!successfullyRestored) {
					System.out.printf(messages.getString("backup-could-not-be-restored-message-format"));
				}
			}
		} else {
			System.out.printf(messages.getString("grid-not-supported-message-format"), args.model);
		}
		return model;
	}
	
	private static Model create2d(Args args, ResourceBundle messages) throws FileNotFoundException, ClassNotFoundException, IOException {
		Model model = null;
		if (args.grid.side == null) {
			if (args.backupToRestorePath == null) {
				if (args.initialConfiguration.type == InitialConfigType.SINGLE_SOURCE) {
					if (args.imgGenerationMode == ImageGenerationMode.TOPPLING_ALTERNATION_COMPLIANCE) {
						if (args.memorySafe) {
							System.out.printf(messages.getString("memory-safe-not-supported-with-these-params-message-format"));
						} else {
							if (args.initialConfiguration.singleSource.compareTo(BigInt.valueOf(IntAether2DTopplingAlternationCompliance.MAX_INITIAL_VALUE)) <= 0
									&& args.initialConfiguration.singleSource.compareTo(BigInt.valueOf(IntAether2DTopplingAlternationCompliance.MIN_INITIAL_VALUE)) >= 0) {
								model = new IntAether2DTopplingAlternationCompliance(args.initialConfiguration.singleSource.intValue());
							} else if (args.initialConfiguration.singleSource.compareTo(BigInt.valueOf(LongAether2DTopplingAlternationCompliance.MAX_INITIAL_VALUE)) <= 0
									&& args.initialConfiguration.singleSource.compareTo(BigInt.valueOf(LongAether2DTopplingAlternationCompliance.MIN_INITIAL_VALUE)) >= 0) {
								model = new LongAether2DTopplingAlternationCompliance(args.initialConfiguration.singleSource.longValue());
							} else {
								model = new BigIntAether2DTopplingAlternationCompliance(args.initialConfiguration.singleSource);
							}
						}
					} else {
						if (args.memorySafe) {
							if (args.initialConfiguration.singleSource.compareTo(BigInt.valueOf(FileBackedLongAether2D.MAX_INITIAL_VALUE)) <= 0
									&& args.initialConfiguration.singleSource.compareTo(BigInt.valueOf(FileBackedLongAether2D.MIN_INITIAL_VALUE)) >= 0) {
								model = new FileBackedLongAether2D(args.initialConfiguration.singleSource.longValue(), args.path);
							} else {
								System.out.printf(messages.getString("single-source-out-of-range-message-format"), FileBackedLongAether2D.MIN_INITIAL_VALUE, FileBackedLongAether2D.MAX_INITIAL_VALUE);
							}
						} else {
							if (args.initialConfiguration.singleSource.compareTo(BigInt.valueOf(IntAether2D.MAX_INITIAL_VALUE)) <= 0
									&& args.initialConfiguration.singleSource.compareTo(BigInt.valueOf(IntAether2D.MIN_INITIAL_VALUE)) >= 0) {
								model = new IntAether2D(args.initialConfiguration.singleSource.intValue());
							} else if (args.initialConfiguration.singleSource.compareTo(BigInt.valueOf(LongAether2D.MAX_INITIAL_VALUE)) <= 0
									&& args.initialConfiguration.singleSource.compareTo(BigInt.valueOf(LongAether2D.MIN_INITIAL_VALUE)) >= 0) {
								model = new LongAether2D(args.initialConfiguration.singleSource.longValue());
							} else {
								model = new BigIntAether2D(args.initialConfiguration.singleSource);
							}
						}
					}
				} else {
					if (args.memorySafe) {
						System.out.printf(messages.getString("memory-safe-not-supported-with-these-params-message-format"));
					} else {
						if (args.initialConfiguration.min.compareTo(BigInt.valueOf(Integer.MAX_VALUE)) <= 0
								&& args.initialConfiguration.min.compareTo(BigInt.valueOf(Integer.MIN_VALUE)) >= 0
								&& args.initialConfiguration.max.compareTo(BigInt.valueOf(Integer.MAX_VALUE)) <= 0
								&& args.initialConfiguration.max.compareTo(BigInt.valueOf(Integer.MIN_VALUE)) >= 0) {
							model = new IntAether2DRandomConfiguration(args.initialConfiguration.side, args.initialConfiguration.min.intValue(), args.initialConfiguration.max.intValue());
						} else {
							System.out.printf(messages.getString("min-max-out-of-range-message-format"), Integer.MIN_VALUE, Integer.MAX_VALUE);
						}
					}
				}
			} else {
				boolean successfullyRestored = true;
				if (args.memorySafe) {
					model = new FileBackedLongAether2D(args.backupToRestorePath, args.path);
				} else if (args.imgGenerationMode == ImageGenerationMode.TOPPLING_ALTERNATION_COMPLIANCE) {
					try {
						model = new IntAether2DTopplingAlternationCompliance(args.backupToRestorePath);							
					} catch (Exception ex1) {
						try {
							model = new LongAether2DTopplingAlternationCompliance(args.backupToRestorePath);							
						} catch (Exception ex2) {
							try {
								model = new BigIntAether2DTopplingAlternationCompliance(args.backupToRestorePath);							
							} catch (Exception ex3) {
								successfullyRestored = false;
							}						
						}						
					}
				} else {	
					try {
						model = new IntAether2D(args.backupToRestorePath);							
					} catch (Exception ex1) {
						try {
							model = new LongAether2D(args.backupToRestorePath);							
						} catch (Exception ex2) {
							try {
								model = new BigIntAether2D(args.backupToRestorePath);							
							} catch (Exception ex3) {
								try {
									model = new IntAether2DRandomConfiguration(args.backupToRestorePath);			
								} catch (Exception ex4) {
									successfullyRestored = false;					
								}
							}						
						}						
					}
				}
				if (!successfullyRestored) {
					System.out.printf(messages.getString("backup-could-not-be-restored-message-format"));
				}
			}
		} else {
			System.out.printf(messages.getString("grid-not-supported-message-format"), args.model);
		}
		return model;
	}
	
	private static Model create3d(Args args, ResourceBundle messages) throws FileNotFoundException, ClassNotFoundException, IOException {
		Model model = null;
		if (args.grid.side == null) {
			if (args.backupToRestorePath == null) {
				if (args.initialConfiguration.type == InitialConfigType.SINGLE_SOURCE) {
					if (args.imgGenerationMode == ImageGenerationMode.TOPPLING_ALTERNATION_COMPLIANCE) {
						if (args.memorySafe) {
							System.out.printf(messages.getString("memory-safe-not-supported-with-these-params-message-format"));
						} else {
							if (args.initialConfiguration.singleSource.compareTo(BigInt.valueOf(IntAether3DTopplingAlternationCompliance.MAX_INITIAL_VALUE)) <= 0
									&& args.initialConfiguration.singleSource.compareTo(BigInt.valueOf(IntAether3DTopplingAlternationCompliance.MIN_INITIAL_VALUE)) >= 0) {
								model = new IntAether3DTopplingAlternationCompliance(args.initialConfiguration.singleSource.intValue());
							} else if (args.initialConfiguration.singleSource.compareTo(BigInt.valueOf(LongAether3DTopplingAlternationCompliance.MAX_INITIAL_VALUE)) <= 0
									&& args.initialConfiguration.singleSource.compareTo(BigInt.valueOf(LongAether3DTopplingAlternationCompliance.MIN_INITIAL_VALUE)) >= 0) {
								model = new LongAether3DTopplingAlternationCompliance(args.initialConfiguration.singleSource.longValue());
							} else {
								model = new BigIntAether3DTopplingAlternationCompliance(args.initialConfiguration.singleSource);
							}
						}
					} else {
						if (args.memorySafe) {
							if (args.initialConfiguration.singleSource.compareTo(BigInt.valueOf(FileBackedLongAether3D.MAX_INITIAL_VALUE)) <= 0
									&& args.initialConfiguration.singleSource.compareTo(BigInt.valueOf(FileBackedLongAether3D.MIN_INITIAL_VALUE)) >= 0) {
								model = new FileBackedLongAether3D(args.initialConfiguration.singleSource.longValue(), args.path);
							} else {
								System.out.printf(messages.getString("single-source-out-of-range-message-format"), FileBackedLongAether3D.MIN_INITIAL_VALUE, FileBackedLongAether3D.MAX_INITIAL_VALUE);
							}
						} else {
							if (args.initialConfiguration.singleSource.compareTo(BigInt.valueOf(IntAether3D.MAX_INITIAL_VALUE)) <= 0
									&& args.initialConfiguration.singleSource.compareTo(BigInt.valueOf(IntAether3D.MIN_INITIAL_VALUE)) >= 0) {
								model = new IntAether3D(args.initialConfiguration.singleSource.intValue());
							} else if (args.initialConfiguration.singleSource.compareTo(BigInt.valueOf(LongAether3D.MAX_INITIAL_VALUE)) <= 0
									&& args.initialConfiguration.singleSource.compareTo(BigInt.valueOf(LongAether3D.MIN_INITIAL_VALUE)) >= 0) {
								model = new LongAether3D(args.initialConfiguration.singleSource.longValue());
							} else {
								model = new BigIntAether3D(args.initialConfiguration.singleSource);
							}
						}
					}
				} else {
					if (args.initialConfiguration.min.compareTo(BigInt.valueOf(Integer.MAX_VALUE)) <= 0
							&& args.initialConfiguration.min.compareTo(BigInt.valueOf(Integer.MIN_VALUE)) >= 0
							&& args.initialConfiguration.max.compareTo(BigInt.valueOf(Integer.MAX_VALUE)) <= 0
							&& args.initialConfiguration.max.compareTo(BigInt.valueOf(Integer.MIN_VALUE)) >= 0) {
						model = new IntAether3DRandomConfiguration(args.initialConfiguration.side, args.initialConfiguration.min.intValue(), args.initialConfiguration.max.intValue());
					} else {
						System.out.printf(messages.getString("min-max-out-of-range-message-format"), Integer.MIN_VALUE, Integer.MAX_VALUE);
					}
				}
			} else {
				boolean successfullyRestored = true;
				if (args.memorySafe) {
					model = new FileBackedLongAether3D(args.backupToRestorePath, args.path);
				} else if (args.imgGenerationMode == ImageGenerationMode.TOPPLING_ALTERNATION_COMPLIANCE) {
					try {
						model = new IntAether3DTopplingAlternationCompliance(args.backupToRestorePath);							
					} catch (Exception ex1) {
						try {
							model = new LongAether3DTopplingAlternationCompliance(args.backupToRestorePath);							
						} catch (Exception ex2) {
							try {
								model = new BigIntAether3DTopplingAlternationCompliance(args.backupToRestorePath);							
							} catch (Exception ex3) {
								successfullyRestored = false;
							}						
						}						
					}
				} else {	
					try {
						model = new IntAether3D(args.backupToRestorePath);							
					} catch (Exception ex1) {
						try {
							model = new LongAether3D(args.backupToRestorePath);							
						} catch (Exception ex2) {
							try {
								model = new BigIntAether3D(args.backupToRestorePath);							
							} catch (Exception ex3) {
								try {
									model = new IntAether3DRandomConfiguration(args.backupToRestorePath);			
								} catch (Exception ex4) {
									successfullyRestored = false;					
								}
							}						
						}						
					}
				}
				if (!successfullyRestored) {
					System.out.printf(messages.getString("backup-could-not-be-restored-message-format"));
				}
			}
		} else {
			if (args.backupToRestorePath == null) {
				if (args.initialConfiguration.type == InitialConfigType.SINGLE_SOURCE) { 
					if (args.initialConfiguration.singleSource.compareTo(BigInt.valueOf(LongAether3DCubicGrid.MAX_INITIAL_VALUE)) <= 0
							&& args.initialConfiguration.singleSource.compareTo(BigInt.valueOf(LongAether3DCubicGrid.MIN_INITIAL_VALUE)) >= 0) {
						model = new LongAether3DCubicGrid(args.grid.side, args.initialConfiguration.singleSource.longValue());
					} else {
						model = new BigIntAether3DCubicGrid(args.grid.side, args.initialConfiguration.singleSource);
					}
				} else {
					System.out.printf(messages.getString("initial-config-not-supported-message-format"), args.model);
				}
			} else {
				try {
					model = new LongAether3DCubicGrid(args.backupToRestorePath);							
				} catch (Exception ex1) {
					model = new BigIntAether3DCubicGrid(args.backupToRestorePath);			
				}
			}
		}
		return model;
	}
	
	private static Model create4d(Args args, ResourceBundle messages) throws FileNotFoundException, ClassNotFoundException, IOException {
		Model model = null;
		if (args.grid.side == null) {
			if (args.backupToRestorePath == null) {
				if (args.initialConfiguration.type == InitialConfigType.SINGLE_SOURCE) {
					if (args.imgGenerationMode == ImageGenerationMode.TOPPLING_ALTERNATION_COMPLIANCE) {
						if (args.memorySafe) {
							System.out.printf(messages.getString("memory-safe-not-supported-with-these-params-message-format"));
						} else {
							if (args.initialConfiguration.singleSource.compareTo(BigInt.valueOf(IntAether4DTopplingAlternationCompliance.MAX_INITIAL_VALUE)) <= 0
									&& args.initialConfiguration.singleSource.compareTo(BigInt.valueOf(IntAether4DTopplingAlternationCompliance.MIN_INITIAL_VALUE)) >= 0) {
								model = new IntAether4DTopplingAlternationCompliance(args.initialConfiguration.singleSource.intValue());
							} else if (args.initialConfiguration.singleSource.compareTo(BigInt.valueOf(LongAether4DTopplingAlternationCompliance.MAX_INITIAL_VALUE)) <= 0
									&& args.initialConfiguration.singleSource.compareTo(BigInt.valueOf(LongAether4DTopplingAlternationCompliance.MIN_INITIAL_VALUE)) >= 0) {
								model = new LongAether4DTopplingAlternationCompliance(args.initialConfiguration.singleSource.longValue());
							} else {
								model = new BigIntAether4DTopplingAlternationCompliance(args.initialConfiguration.singleSource);
							}
						}
					} else {
						if (args.memorySafe) {
							if (args.initialConfiguration.singleSource.compareTo(BigInt.valueOf(FileBackedLongAether4D.MAX_INITIAL_VALUE)) <= 0
									&& args.initialConfiguration.singleSource.compareTo(BigInt.valueOf(FileBackedLongAether4D.MIN_INITIAL_VALUE)) >= 0) {
								model = new FileBackedLongAether4D(args.initialConfiguration.singleSource.longValue(), args.path);
							} else {
								System.out.printf(messages.getString("single-source-out-of-range-message-format"), FileBackedLongAether4D.MIN_INITIAL_VALUE, FileBackedLongAether4D.MAX_INITIAL_VALUE);
							}
						} else {
							if (args.initialConfiguration.singleSource.compareTo(BigInt.valueOf(IntAether4D.MAX_INITIAL_VALUE)) <= 0
									&& args.initialConfiguration.singleSource.compareTo(BigInt.valueOf(IntAether4D.MIN_INITIAL_VALUE)) >= 0) {
								model = new IntAether4D(args.initialConfiguration.singleSource.intValue());
							} else if (args.initialConfiguration.singleSource.compareTo(BigInt.valueOf(LongAether4D.MAX_INITIAL_VALUE)) <= 0
									&& args.initialConfiguration.singleSource.compareTo(BigInt.valueOf(LongAether4D.MIN_INITIAL_VALUE)) >= 0) {
								model = new LongAether4D(args.initialConfiguration.singleSource.longValue());
							} else {
								model = new BigIntAether4D(args.initialConfiguration.singleSource);
							}
						}
					}
				} else {
					System.out.printf(messages.getString("initial-config-not-supported-with-these-params-message-format"));
				}
			} else {
				boolean successfullyRestored = true;
				if (args.memorySafe) {
					model = new FileBackedLongAether4D(args.backupToRestorePath, args.path);
				} else if (args.imgGenerationMode == ImageGenerationMode.TOPPLING_ALTERNATION_COMPLIANCE) {
					try {
						model = new IntAether4DTopplingAlternationCompliance(args.backupToRestorePath);							
					} catch (Exception ex1) {
						try {
							model = new LongAether4DTopplingAlternationCompliance(args.backupToRestorePath);							
						} catch (Exception ex2) {
							try {
								model = new BigIntAether4DTopplingAlternationCompliance(args.backupToRestorePath);							
							} catch (Exception ex3) {
								successfullyRestored = false;
							}						
						}						
					}
				} else {	
					try {
						model = new IntAether4D(args.backupToRestorePath);							
					} catch (Exception ex1) {
						try {
							model = new LongAether4D(args.backupToRestorePath);							
						} catch (Exception ex2) {
							try {
								model = new BigIntAether4D(args.backupToRestorePath);							
							} catch (Exception ex3) {
								successfullyRestored = false;
							}						
						}						
					}
				}
				if (!successfullyRestored) {
					System.out.printf(messages.getString("backup-could-not-be-restored-message-format"));
				}
			}
		} else {
			System.out.printf(messages.getString("grid-not-supported-message-format"), args.model);
		}
		return model;
	}
	
	private static Model create5d(Args args, ResourceBundle messages) throws FileNotFoundException, ClassNotFoundException, IOException {
		Model model = null;
		if (args.grid.side == null) {
			if (args.backupToRestorePath == null) {
				if (args.initialConfiguration.type == InitialConfigType.SINGLE_SOURCE) {
					if (args.imgGenerationMode == ImageGenerationMode.TOPPLING_ALTERNATION_COMPLIANCE) {
						if (args.memorySafe) {
							System.out.printf(messages.getString("memory-safe-not-supported-with-these-params-message-format"));
						} else {
							if (args.initialConfiguration.singleSource.compareTo(BigInt.valueOf(IntAether5DTopplingAlternationCompliance.MAX_INITIAL_VALUE)) <= 0
									&& args.initialConfiguration.singleSource.compareTo(BigInt.valueOf(IntAether5DTopplingAlternationCompliance.MIN_INITIAL_VALUE)) >= 0) {
								model = new IntAether5DTopplingAlternationCompliance(args.initialConfiguration.singleSource.intValue());
							} else if (args.initialConfiguration.singleSource.compareTo(BigInt.valueOf(LongAether5DTopplingAlternationCompliance.MAX_INITIAL_VALUE)) <= 0
									&& args.initialConfiguration.singleSource.compareTo(BigInt.valueOf(LongAether5DTopplingAlternationCompliance.MIN_INITIAL_VALUE)) >= 0) {
								model = new LongAether5DTopplingAlternationCompliance(args.initialConfiguration.singleSource.longValue());
							} else {
								System.out.printf(messages.getString("single-source-out-of-range-message-format"), LongAether5DTopplingAlternationCompliance.MIN_INITIAL_VALUE, LongAether5DTopplingAlternationCompliance.MAX_INITIAL_VALUE);
							}
						}
					} else {
						if (args.memorySafe) {
							if (args.initialConfiguration.singleSource.compareTo(BigInt.valueOf(FileBackedLongAether5D.MAX_INITIAL_VALUE)) <= 0
									&& args.initialConfiguration.singleSource.compareTo(BigInt.valueOf(FileBackedLongAether5D.MIN_INITIAL_VALUE)) >= 0) {
								model = new FileBackedLongAether5D(args.initialConfiguration.singleSource.longValue(), args.path);
							} else {
								System.out.printf(messages.getString("single-source-out-of-range-message-format"), FileBackedLongAether5D.MIN_INITIAL_VALUE, FileBackedLongAether5D.MAX_INITIAL_VALUE);
							}
						} else {
							if (args.initialConfiguration.singleSource.compareTo(BigInt.valueOf(IntAether5D.MAX_INITIAL_VALUE)) <= 0
									&& args.initialConfiguration.singleSource.compareTo(BigInt.valueOf(IntAether5D.MIN_INITIAL_VALUE)) >= 0) {
								model = new IntAether5D(args.initialConfiguration.singleSource.intValue());
							} else if (args.initialConfiguration.singleSource.compareTo(BigInt.valueOf(LongAether5D.MAX_INITIAL_VALUE)) <= 0
									&& args.initialConfiguration.singleSource.compareTo(BigInt.valueOf(LongAether5D.MIN_INITIAL_VALUE)) >= 0) {
								model = new LongAether5D(args.initialConfiguration.singleSource.longValue());
							} else {
								System.out.printf(messages.getString("single-source-out-of-range-message-format"), LongAether5D.MIN_INITIAL_VALUE, LongAether5D.MAX_INITIAL_VALUE);
							}
						}
					}
				} else {
					System.out.printf(messages.getString("initial-config-not-supported-with-these-params-message-format"));
				}
			} else {
				boolean successfullyRestored = true;
				if (args.memorySafe) {
					model = new FileBackedLongAether5D(args.backupToRestorePath, args.path);
				} else if (args.imgGenerationMode == ImageGenerationMode.TOPPLING_ALTERNATION_COMPLIANCE) {
					try {
						model = new IntAether5DTopplingAlternationCompliance(args.backupToRestorePath);							
					} catch (Exception ex1) {
						try {
							model = new LongAether5DTopplingAlternationCompliance(args.backupToRestorePath);							
						} catch (Exception ex2) {
							successfullyRestored = false;			
						}						
					}
				} else {	
					try {
						model = new IntAether5D(args.backupToRestorePath);							
					} catch (Exception ex1) {
						try {
							model = new LongAether5D(args.backupToRestorePath);							
						} catch (Exception ex2) {
							successfullyRestored = false;				
						}						
					}
				}
				if (!successfullyRestored) {
					System.out.printf(messages.getString("backup-could-not-be-restored-message-format"));
				}
			}
		} else {
			System.out.printf(messages.getString("grid-not-supported-message-format"), args.model);
		}
		return model;
	}
	
	private static Model createNd(Args args, ResourceBundle messages) {
		Model model = null;
		System.out.printf(messages.getString("grid-not-supported-message-format"), args.model);
		return model;
	}
	
}
