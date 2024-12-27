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
import caimgmaker.args.InitialConfigParameterValue.InitialConfigType;
import cellularautomata.automata.aether.BigIntAether2D;
import cellularautomata.automata.aether.BigIntAetherTopplingAlternationCompliance2D;
import cellularautomata.automata.aether.BigIntAether3D;
import cellularautomata.automata.aether.BigIntAetherCubicGrid3D;
import cellularautomata.automata.aether.BigIntAetherTopplingAlternationCompliance3D;
import cellularautomata.automata.aether.BigIntAether4D;
import cellularautomata.automata.aether.BigIntAetherTopplingAlternationCompliance4D;
import cellularautomata.automata.aether.FileBackedLongAether1D;
import cellularautomata.automata.aether.FileBackedLongAether2D;
import cellularautomata.automata.aether.FileBackedLongAether3D;
import cellularautomata.automata.aether.FileBackedLongAether4D;
import cellularautomata.automata.aether.FileBackedLongAether5D;
import cellularautomata.automata.aether.IntAether2D;
import cellularautomata.automata.aether.IntAetherRandomConfiguration2D;
import cellularautomata.automata.aether.IntAetherTopplingAlternationCompliance2D;
import cellularautomata.automata.aether.IntAether3D;
import cellularautomata.automata.aether.IntAetherRandomConfiguration3D;
import cellularautomata.automata.aether.IntAetherTopplingAlternationCompliance3D;
import cellularautomata.automata.aether.IntAether4D;
import cellularautomata.automata.aether.IntAetherTopplingAlternationCompliance4D;
import cellularautomata.automata.aether.IntAether5D;
import cellularautomata.automata.aether.IntAetherTopplingAlternationCompliance5D;
import cellularautomata.automata.aether.LongAether1D;
import cellularautomata.automata.aether.LongAetherTopplingAlternationCompliance1D;
import cellularautomata.automata.aether.LongAether2D;
import cellularautomata.automata.aether.LongAetherTopplingAlternationCompliance2D;
import cellularautomata.automata.aether.LongAether3D;
import cellularautomata.automata.aether.LongAetherCubicGrid3D;
import cellularautomata.automata.aether.LongAetherTopplingAlternationCompliance3D;
import cellularautomata.automata.aether.LongAether4D;
import cellularautomata.automata.aether.LongAetherTopplingAlternationCompliance4D;
import cellularautomata.automata.aether.LongAether5D;
import cellularautomata.automata.aether.LongAetherTopplingAlternationCompliance5D;
import cellularautomata.model.Model;
import cellularautomata.numbers.BigInt;

public final class AetherFactory {
	
	private AetherFactory() {}
	
	public static Model create(Args args, ResourceBundle messages) throws FileNotFoundException, ClassNotFoundException, IOException {
		Model model = null;
		if (args.initialConfiguration == null && args.backupToRestorePath == null) {
			System.out.printf(messages.getString("initial-config-needed-format"), args.model);
		} else if (args.backupToRestorePath != null && args.grid == null) {
			System.out.println(messages.getString("grid-type-needed-in-order-to-restore"));//TODO Add AetherImgMakerBackup class with parameters
		} else if (args.memorySafe && args.initialConfiguration.type != InitialConfigType.SINGLE_SOURCE) {
			System.out.printf(messages.getString("param-not-supported-for-this-initial-config-format"), args.model, Args.MEMORY_SAFE);
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
					if (args.topplingAlternationCompliance) {
						if (args.memorySafe) {
							System.out.printf(messages.getString("param-not-supported-with-other-params-format"), Args.MEMORY_SAFE);
						} else {
							if (args.initialConfiguration.singleSource.compareTo(BigInt.valueOf(LongAetherTopplingAlternationCompliance1D.MAX_INITIAL_VALUE)) <= 0
									&& args.initialConfiguration.singleSource.compareTo(BigInt.valueOf(LongAetherTopplingAlternationCompliance1D.MIN_INITIAL_VALUE)) >= 0) {
								model = new LongAetherTopplingAlternationCompliance1D(args.initialConfiguration.singleSource.longValue());
							} else {
								System.out.printf(messages.getString("single-source-out-of-range-format"), LongAetherTopplingAlternationCompliance1D.MIN_INITIAL_VALUE, LongAetherTopplingAlternationCompliance1D.MAX_INITIAL_VALUE);
							}
						}
					} else {
						if (args.memorySafe) {
							if (args.initialConfiguration.singleSource.compareTo(BigInt.valueOf(FileBackedLongAether1D.MAX_INITIAL_VALUE)) <= 0
									&& args.initialConfiguration.singleSource.compareTo(BigInt.valueOf(FileBackedLongAether1D.MIN_INITIAL_VALUE)) >= 0) {
								model = new FileBackedLongAether1D(args.initialConfiguration.singleSource.longValue(), args.path);
							} else {
								System.out.printf(messages.getString("single-source-out-of-range-format"), FileBackedLongAether1D.MIN_INITIAL_VALUE, FileBackedLongAether1D.MAX_INITIAL_VALUE);
							}
						} else {
							if (args.initialConfiguration.singleSource.compareTo(BigInt.valueOf(LongAether1D.MAX_INITIAL_VALUE)) <= 0
									&& args.initialConfiguration.singleSource.compareTo(BigInt.valueOf(LongAether1D.MIN_INITIAL_VALUE)) >= 0) {
								model = new LongAether1D(args.initialConfiguration.singleSource.longValue());
							} else {
								System.out.printf(messages.getString("single-source-out-of-range-format"), LongAether1D.MIN_INITIAL_VALUE, LongAether1D.MAX_INITIAL_VALUE);
							}
						}
					}
				} else {
					System.out.println(messages.getString("initial-config-not-supported-with-these-params"));
				}
			} else {
				//TODO Add AetherImgMakerBackup class with parameters
				boolean successfullyRestored = true;
				if (args.memorySafe) {
					model = new FileBackedLongAether1D(args.backupToRestorePath, args.path);
				} else if (args.topplingAlternationCompliance) {
					try {
						model = new LongAetherTopplingAlternationCompliance1D(args.backupToRestorePath);							
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
					System.out.println(messages.getString("backup-could-not-be-restored"));
				}
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
					if (args.topplingAlternationCompliance) {
						if (args.memorySafe) {
							System.out.printf(messages.getString("param-not-supported-with-other-params-format"), Args.MEMORY_SAFE);
						} else {
							if (args.initialConfiguration.singleSource.compareTo(BigInt.valueOf(IntAetherTopplingAlternationCompliance2D.MAX_INITIAL_VALUE)) <= 0
									&& args.initialConfiguration.singleSource.compareTo(BigInt.valueOf(IntAetherTopplingAlternationCompliance2D.MIN_INITIAL_VALUE)) >= 0) {
								model = new IntAetherTopplingAlternationCompliance2D(args.initialConfiguration.singleSource.intValue());
							} else if (args.initialConfiguration.singleSource.compareTo(BigInt.valueOf(LongAetherTopplingAlternationCompliance2D.MAX_INITIAL_VALUE)) <= 0
									&& args.initialConfiguration.singleSource.compareTo(BigInt.valueOf(LongAetherTopplingAlternationCompliance2D.MIN_INITIAL_VALUE)) >= 0) {
								model = new LongAetherTopplingAlternationCompliance2D(args.initialConfiguration.singleSource.longValue());
							} else {
								model = new BigIntAetherTopplingAlternationCompliance2D(args.initialConfiguration.singleSource);
							}
						}
					} else {
						if (args.memorySafe) {
							if (args.initialConfiguration.singleSource.compareTo(BigInt.valueOf(FileBackedLongAether2D.MAX_INITIAL_VALUE)) <= 0
									&& args.initialConfiguration.singleSource.compareTo(BigInt.valueOf(FileBackedLongAether2D.MIN_INITIAL_VALUE)) >= 0) {
								model = new FileBackedLongAether2D(args.initialConfiguration.singleSource.longValue(), args.path);
							} else {
								System.out.printf(messages.getString("single-source-out-of-range-format"), FileBackedLongAether2D.MIN_INITIAL_VALUE, FileBackedLongAether2D.MAX_INITIAL_VALUE);
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
						System.out.printf(messages.getString("param-not-supported-with-other-params-format"), Args.MEMORY_SAFE);
					} else if (args.topplingAlternationCompliance) {
						System.out.printf(messages.getString("param-incompatible-with-initial-config"), Args.TOPPLING_ALTERNATION_COMPLIANCE);
					} else {
						if (args.initialConfiguration.min.compareTo(BigInt.valueOf(Integer.MAX_VALUE)) <= 0
								&& args.initialConfiguration.min.compareTo(BigInt.valueOf(Integer.MIN_VALUE)) >= 0
								&& args.initialConfiguration.max.compareTo(BigInt.valueOf(Integer.MAX_VALUE)) <= 0
								&& args.initialConfiguration.max.compareTo(BigInt.valueOf(Integer.MIN_VALUE)) >= 0) {
							model = new IntAetherRandomConfiguration2D(args.initialConfiguration.side, args.initialConfiguration.min.intValue(), args.initialConfiguration.max.intValue());
						} else {
							System.out.printf(messages.getString("min-max-out-of-range-format"), Integer.MIN_VALUE, Integer.MAX_VALUE);
						}
					}
				}
			} else {
				boolean successfullyRestored = true;
				if (args.memorySafe) {
					model = new FileBackedLongAether2D(args.backupToRestorePath, args.path);
				} else if (args.topplingAlternationCompliance) {
					try {
						model = new IntAetherTopplingAlternationCompliance2D(args.backupToRestorePath);							
					} catch (Exception ex1) {
						try {
							model = new LongAetherTopplingAlternationCompliance2D(args.backupToRestorePath);							
						} catch (Exception ex2) {
							try {
								model = new BigIntAetherTopplingAlternationCompliance2D(args.backupToRestorePath);							
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
									model = new IntAetherRandomConfiguration2D(args.backupToRestorePath);			
								} catch (Exception ex4) {
									successfullyRestored = false;					
								}
							}						
						}						
					}
				}
				if (!successfullyRestored) {
					System.out.println(messages.getString("backup-could-not-be-restored"));
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
					if (args.topplingAlternationCompliance) {
						if (args.memorySafe) {
							System.out.printf(messages.getString("param-not-supported-with-other-params-format"), Args.MEMORY_SAFE);
						} else {
							if (args.initialConfiguration.singleSource.compareTo(BigInt.valueOf(IntAetherTopplingAlternationCompliance3D.MAX_INITIAL_VALUE)) <= 0
									&& args.initialConfiguration.singleSource.compareTo(BigInt.valueOf(IntAetherTopplingAlternationCompliance3D.MIN_INITIAL_VALUE)) >= 0) {
								model = new IntAetherTopplingAlternationCompliance3D(args.initialConfiguration.singleSource.intValue());
							} else if (args.initialConfiguration.singleSource.compareTo(BigInt.valueOf(LongAetherTopplingAlternationCompliance3D.MAX_INITIAL_VALUE)) <= 0
									&& args.initialConfiguration.singleSource.compareTo(BigInt.valueOf(LongAetherTopplingAlternationCompliance3D.MIN_INITIAL_VALUE)) >= 0) {
								model = new LongAetherTopplingAlternationCompliance3D(args.initialConfiguration.singleSource.longValue());
							} else {
								model = new BigIntAetherTopplingAlternationCompliance3D(args.initialConfiguration.singleSource);
							}
						}
					} else {
						if (args.memorySafe) {
							if (args.initialConfiguration.singleSource.compareTo(BigInt.valueOf(FileBackedLongAether3D.MAX_INITIAL_VALUE)) <= 0
									&& args.initialConfiguration.singleSource.compareTo(BigInt.valueOf(FileBackedLongAether3D.MIN_INITIAL_VALUE)) >= 0) {
								model = new FileBackedLongAether3D(args.initialConfiguration.singleSource.longValue(), args.path);
							} else {
								System.out.printf(messages.getString("single-source-out-of-range-format"), FileBackedLongAether3D.MIN_INITIAL_VALUE, FileBackedLongAether3D.MAX_INITIAL_VALUE);
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
					if (args.memorySafe) {
						System.out.printf(messages.getString("param-not-supported-with-other-params-format"), Args.MEMORY_SAFE);
					} else if (args.topplingAlternationCompliance) {
						System.out.printf(messages.getString("param-incompatible-with-initial-config"), Args.TOPPLING_ALTERNATION_COMPLIANCE);
					} else {
						if (args.initialConfiguration.min.compareTo(BigInt.valueOf(Integer.MAX_VALUE)) <= 0
								&& args.initialConfiguration.min.compareTo(BigInt.valueOf(Integer.MIN_VALUE)) >= 0
								&& args.initialConfiguration.max.compareTo(BigInt.valueOf(Integer.MAX_VALUE)) <= 0
								&& args.initialConfiguration.max.compareTo(BigInt.valueOf(Integer.MIN_VALUE)) >= 0) {
							model = new IntAetherRandomConfiguration3D(args.initialConfiguration.side, args.initialConfiguration.min.intValue(), args.initialConfiguration.max.intValue());
						} else {
							System.out.printf(messages.getString("min-max-out-of-range-format"), Integer.MIN_VALUE, Integer.MAX_VALUE);
						}
					}
				}
			} else {
				boolean successfullyRestored = true;
				if (args.memorySafe) {
					model = new FileBackedLongAether3D(args.backupToRestorePath, args.path);
				} else if (args.topplingAlternationCompliance) {
					try {
						model = new IntAetherTopplingAlternationCompliance3D(args.backupToRestorePath);							
					} catch (Exception ex1) {
						try {
							model = new LongAetherTopplingAlternationCompliance3D(args.backupToRestorePath);							
						} catch (Exception ex2) {
							try {
								model = new BigIntAetherTopplingAlternationCompliance3D(args.backupToRestorePath);							
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
									model = new IntAetherRandomConfiguration3D(args.backupToRestorePath);			
								} catch (Exception ex4) {
									successfullyRestored = false;					
								}
							}						
						}						
					}
				}
				if (!successfullyRestored) {
					System.out.println(messages.getString("backup-could-not-be-restored"));
				}
			}
		} else {
			if (args.backupToRestorePath == null) {
				if (args.initialConfiguration.type == InitialConfigType.SINGLE_SOURCE) { 
					if (args.initialConfiguration.singleSource.compareTo(BigInt.valueOf(LongAetherCubicGrid3D.MAX_INITIAL_VALUE)) <= 0
							&& args.initialConfiguration.singleSource.compareTo(BigInt.valueOf(LongAetherCubicGrid3D.MIN_INITIAL_VALUE)) >= 0) {
						model = new LongAetherCubicGrid3D(args.grid.side, args.initialConfiguration.singleSource.longValue());
					} else {
						model = new BigIntAetherCubicGrid3D(args.grid.side, args.initialConfiguration.singleSource);
					}
				} else {
					System.out.printf(messages.getString("initial-config-not-supported-format"), args.model);
				}
			} else {
				try {
					model = new LongAetherCubicGrid3D(args.backupToRestorePath);							
				} catch (Exception ex1) {
					model = new BigIntAetherCubicGrid3D(args.backupToRestorePath);			
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
					if (args.topplingAlternationCompliance) {
						if (args.memorySafe) {
							System.out.printf(messages.getString("param-not-supported-with-other-params-format"), Args.MEMORY_SAFE);
						} else {
							if (args.initialConfiguration.singleSource.compareTo(BigInt.valueOf(IntAetherTopplingAlternationCompliance4D.MAX_INITIAL_VALUE)) <= 0
									&& args.initialConfiguration.singleSource.compareTo(BigInt.valueOf(IntAetherTopplingAlternationCompliance4D.MIN_INITIAL_VALUE)) >= 0) {
								model = new IntAetherTopplingAlternationCompliance4D(args.initialConfiguration.singleSource.intValue());
							} else if (args.initialConfiguration.singleSource.compareTo(BigInt.valueOf(LongAetherTopplingAlternationCompliance4D.MAX_INITIAL_VALUE)) <= 0
									&& args.initialConfiguration.singleSource.compareTo(BigInt.valueOf(LongAetherTopplingAlternationCompliance4D.MIN_INITIAL_VALUE)) >= 0) {
								model = new LongAetherTopplingAlternationCompliance4D(args.initialConfiguration.singleSource.longValue());
							} else {
								model = new BigIntAetherTopplingAlternationCompliance4D(args.initialConfiguration.singleSource);
							}
						}
					} else {
						if (args.memorySafe) {
							if (args.initialConfiguration.singleSource.compareTo(BigInt.valueOf(FileBackedLongAether4D.MAX_INITIAL_VALUE)) <= 0
									&& args.initialConfiguration.singleSource.compareTo(BigInt.valueOf(FileBackedLongAether4D.MIN_INITIAL_VALUE)) >= 0) {
								model = new FileBackedLongAether4D(args.initialConfiguration.singleSource.longValue(), args.path);
							} else {
								System.out.printf(messages.getString("single-source-out-of-range-format"), FileBackedLongAether4D.MIN_INITIAL_VALUE, FileBackedLongAether4D.MAX_INITIAL_VALUE);
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
					System.out.println(messages.getString("initial-config-not-supported-with-these-params"));
				}
			} else {
				boolean successfullyRestored = true;
				if (args.memorySafe) {
					model = new FileBackedLongAether4D(args.backupToRestorePath, args.path);
				} else if (args.topplingAlternationCompliance) {
					try {
						model = new IntAetherTopplingAlternationCompliance4D(args.backupToRestorePath);							
					} catch (Exception ex1) {
						try {
							model = new LongAetherTopplingAlternationCompliance4D(args.backupToRestorePath);							
						} catch (Exception ex2) {
							try {
								model = new BigIntAetherTopplingAlternationCompliance4D(args.backupToRestorePath);							
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
					System.out.println(messages.getString("backup-could-not-be-restored"));
				}
			}
		} else {
			System.out.printf(messages.getString("grid-not-supported-format"), args.model);
		}
		return model;
	}
	
	private static Model create5d(Args args, ResourceBundle messages) throws FileNotFoundException, ClassNotFoundException, IOException {
		Model model = null;
		if (args.grid.side == null) {
			if (args.backupToRestorePath == null) {
				if (args.initialConfiguration.type == InitialConfigType.SINGLE_SOURCE) {
					if (args.topplingAlternationCompliance) {
						if (args.memorySafe) {
							System.out.printf(messages.getString("param-not-supported-with-other-params-format"), Args.MEMORY_SAFE);
						} else {
							if (args.initialConfiguration.singleSource.compareTo(BigInt.valueOf(IntAetherTopplingAlternationCompliance5D.MAX_INITIAL_VALUE)) <= 0
									&& args.initialConfiguration.singleSource.compareTo(BigInt.valueOf(IntAetherTopplingAlternationCompliance5D.MIN_INITIAL_VALUE)) >= 0) {
								model = new IntAetherTopplingAlternationCompliance5D(args.initialConfiguration.singleSource.intValue());
							} else if (args.initialConfiguration.singleSource.compareTo(BigInt.valueOf(LongAetherTopplingAlternationCompliance5D.MAX_INITIAL_VALUE)) <= 0
									&& args.initialConfiguration.singleSource.compareTo(BigInt.valueOf(LongAetherTopplingAlternationCompliance5D.MIN_INITIAL_VALUE)) >= 0) {
								model = new LongAetherTopplingAlternationCompliance5D(args.initialConfiguration.singleSource.longValue());
							} else {
								System.out.printf(messages.getString("single-source-out-of-range-format"), LongAetherTopplingAlternationCompliance5D.MIN_INITIAL_VALUE, LongAetherTopplingAlternationCompliance5D.MAX_INITIAL_VALUE);
							}
						}
					} else {
						if (args.memorySafe) {
							if (args.initialConfiguration.singleSource.compareTo(BigInt.valueOf(FileBackedLongAether5D.MAX_INITIAL_VALUE)) <= 0
									&& args.initialConfiguration.singleSource.compareTo(BigInt.valueOf(FileBackedLongAether5D.MIN_INITIAL_VALUE)) >= 0) {
								model = new FileBackedLongAether5D(args.initialConfiguration.singleSource.longValue(), args.path);
							} else {
								System.out.printf(messages.getString("single-source-out-of-range-format"), FileBackedLongAether5D.MIN_INITIAL_VALUE, FileBackedLongAether5D.MAX_INITIAL_VALUE);
							}
						} else {
							if (args.initialConfiguration.singleSource.compareTo(BigInt.valueOf(IntAether5D.MAX_INITIAL_VALUE)) <= 0
									&& args.initialConfiguration.singleSource.compareTo(BigInt.valueOf(IntAether5D.MIN_INITIAL_VALUE)) >= 0) {
								model = new IntAether5D(args.initialConfiguration.singleSource.intValue());
							} else if (args.initialConfiguration.singleSource.compareTo(BigInt.valueOf(LongAether5D.MAX_INITIAL_VALUE)) <= 0
									&& args.initialConfiguration.singleSource.compareTo(BigInt.valueOf(LongAether5D.MIN_INITIAL_VALUE)) >= 0) {
								model = new LongAether5D(args.initialConfiguration.singleSource.longValue());
							} else {
								System.out.printf(messages.getString("single-source-out-of-range-format"), LongAether5D.MIN_INITIAL_VALUE, LongAether5D.MAX_INITIAL_VALUE);
							}
						}
					}
				} else {
					System.out.println(messages.getString("initial-config-not-supported-with-these-params"));
				}
			} else {
				boolean successfullyRestored = true;
				if (args.memorySafe) {
					model = new FileBackedLongAether5D(args.backupToRestorePath, args.path);
				} else if (args.topplingAlternationCompliance) {
					try {
						model = new IntAetherTopplingAlternationCompliance5D(args.backupToRestorePath);							
					} catch (Exception ex1) {
						try {
							model = new LongAetherTopplingAlternationCompliance5D(args.backupToRestorePath);							
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
					System.out.println(messages.getString("backup-could-not-be-restored"));
				}
			}
		} else {
			System.out.printf(messages.getString("grid-not-supported-format"), args.model);
		}
		return model;
	}
	
	private static Model createNd(Args args, ResourceBundle messages) {
		Model model = null;
		System.out.printf(messages.getString("grid-not-supported-format"), args.model);
		return model;
	}
	
}
