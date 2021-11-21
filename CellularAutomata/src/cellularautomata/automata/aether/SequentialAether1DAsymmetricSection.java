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
package cellularautomata.automata.aether;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.LongConsumer;

import org.apache.commons.io.FileUtils;

import cellularautomata.LongInputStreamIterator;
import cellularautomata.model1d.SequentialLongModel1D;

public class SequentialAether1DAsymmetricSection implements SequentialLongModel1D {
	
	private static final String fileNameFormat = "anisotropic_grid_d=1_step=%d.data";
	private File gridFolder;
	private File currentFile;
	private long step;
	private long initialValue;
	private int maxX;
	private List<LongInputStreamIterator> iterators = new ArrayList<LongInputStreamIterator>();
	
	public SequentialAether1DAsymmetricSection(long initialValue, String folderPath) throws IOException {
		this.initialValue = initialValue;
		gridFolder = new File(folderPath + File.separator + getSubfolderPath() + File.separator + "grid");
		if (!gridFolder.exists()) {
			gridFolder.mkdirs();
		} else {
			FileUtils.cleanDirectory(gridFolder);
		}
		step = 0;
		maxX = 2;
		createFirstFile();
	}
	
	private void createFirstFile() throws IOException {
		DataOutputStream outputStream = null;
		try {
			currentFile = new File(gridFolder.getPath() + File.separator + String.format(fileNameFormat, step));
			outputStream = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(currentFile)));
			outputStream.writeLong(initialValue);
			outputStream.writeLong(0);
			outputStream.writeLong(0);
			outputStream.writeLong(0);
			outputStream.writeLong(0);
		} finally {
			if (outputStream != null) {
				outputStream.close();
			}
		}
	}
	
	private void invalidateIterators() {
		for (LongInputStreamIterator iterator : iterators) {
			iterator.invalidate();
		}
		iterators.clear();
	}

	@Override
	public boolean nextStep() throws Exception {
		return nextStep(new LongConsumer() {
			
			@Override
			public void accept(long value) {
				//do nothing
			}
		});
	}
	
	/**
	 * Computes the next step of the model and executes an action for each new value in a consistent order.
	 * 
	 * @param action an action to execute for each value
	 * @return true if the state changed or false otherwise
	 * @throws IOException
	 */
	public boolean nextStep(LongConsumer action) throws IOException {
		invalidateIterators();//to release the current file so it can be deleted
		DataInputStream oldInput = null;
		DataOutputStream newOutput = null;
		try {
			oldInput = new DataInputStream(new BufferedInputStream(new FileInputStream(currentFile)));
			File newFile = new File(gridFolder.getPath() + File.separator + String.format(fileNameFormat, step + 1));
			newOutput = new DataOutputStream(new FileOutputStream(newFile));
			boolean firstTwoSlicesChanged = false;
			long currentOldValue, greaterXOldValue, smallerXOldValue, currentNewValue = 0, greaterXNewValue = 0, smallerXNewValue = 0;
			//x = 0
			currentOldValue = oldInput.readLong();
			greaterXOldValue = oldInput.readLong();
			if (greaterXOldValue < currentOldValue) {
				long toShare = currentOldValue - greaterXOldValue;
				long share = toShare/3;
				if (share != 0) {
					firstTwoSlicesChanged = true;
					currentNewValue += currentOldValue - toShare + share + toShare%3;
					greaterXNewValue += share;
				} else {
					currentNewValue += currentOldValue;
				}			
			} else {
				currentNewValue += currentOldValue;
			}
			//x = 1
			//slide new values
			smallerXNewValue = currentNewValue;
			currentNewValue = greaterXNewValue;
			greaterXNewValue = 0;
			//reuse values obtained previously
			smallerXOldValue = currentOldValue;
			currentOldValue = greaterXOldValue;
			greaterXOldValue = oldInput.readLong();
			if (smallerXOldValue < currentOldValue) {
				if (greaterXOldValue < currentOldValue) {
					if (smallerXOldValue == greaterXOldValue) {
						// gn == sn < current
						long toShare = currentOldValue - greaterXOldValue; 
						long share = toShare/3;
						if (share != 0) {
							firstTwoSlicesChanged = true;
						}
						smallerXNewValue += share + share;//one more for the symmetric position at the other side
						currentNewValue += currentOldValue - toShare + share + toShare%3;
						greaterXNewValue += share;
					} else if (smallerXOldValue < greaterXOldValue) {
						// sn < gn < current
						long toShare = currentOldValue - greaterXOldValue; 
						long share = toShare/3;
						if (share != 0) {
							firstTwoSlicesChanged = true;
						}
						smallerXNewValue += share + share;//one more for the symmetric position at the other side
						greaterXNewValue += share;
						long currentRemainingValue = currentOldValue - share - share;
						toShare = currentRemainingValue - smallerXOldValue; 
						share = toShare/2;
						if (share != 0) {
							firstTwoSlicesChanged = true;
						}
						smallerXNewValue += share + share;//one more for the symmetric position at the other side
						currentNewValue += currentRemainingValue - toShare + share + toShare%2;
					} else {
						// gn < sn < current
						long toShare = currentOldValue - smallerXOldValue; 
						long share = toShare/3;
						if (share != 0) {
							firstTwoSlicesChanged = true;
						}
						smallerXNewValue += share + share;//one more for the symmetric position at the other side
						greaterXNewValue += share;
						long currentRemainingValue = currentOldValue - share - share;
						toShare = currentRemainingValue - greaterXOldValue; 
						share = toShare/2;
						if (share != 0) {
							firstTwoSlicesChanged = true;
						}
						currentNewValue += currentRemainingValue - toShare + share + toShare%2;
						greaterXNewValue += share;
					}
				} else {
					// sn < current <= gn
					long toShare = currentOldValue - smallerXOldValue; 
					long share = toShare/2;
					if (share != 0) {
						firstTwoSlicesChanged = true;
					}
					smallerXNewValue += share + share;//one more for the symmetric position at the other side
					currentNewValue += currentOldValue - toShare + share + toShare%2;
				}
			} else {
				if (greaterXOldValue < currentOldValue) {
					// gn < current <= sn
					long toShare = currentOldValue - greaterXOldValue; 
					long share = toShare/2;
					if (share != 0) {
						firstTwoSlicesChanged = true;
					}
					currentNewValue += currentOldValue - toShare + share + toShare%2;
					greaterXNewValue += share;
				} else {
					currentNewValue += currentOldValue;
				}
			}
			newOutput.writeLong(smallerXNewValue);
			action.accept(smallerXNewValue);
			// x >= 2
			boolean anyOtherChanged = toppleRangeBeyondX1(oldInput, newOutput, currentOldValue, greaterXOldValue, smallerXOldValue, 
						currentNewValue, greaterXNewValue, smallerXNewValue, action);
			oldInput.close();
			newOutput.close();
			currentFile.delete();
			currentFile = newFile;
			step++;
			return firstTwoSlicesChanged || anyOtherChanged;
		} finally {
			if (oldInput != null) {
				oldInput.close();
			}
			if (newOutput != null) {
				newOutput.close();
			}
		}		
	}
	
	private boolean toppleRangeBeyondX1(DataInputStream oldInput, DataOutputStream newOutput, long currentOldValue, long greaterXOldValue, long smallerXOldValue, 
			long currentNewValue, long greaterXNewValue, long smallerXNewValue, LongConsumer action) throws IOException {
		boolean anySliceToppled = false;
		boolean currentSliceToppled = false;
		boolean previousSliceToppled = false;
		boolean endReached = false;
		while (!endReached) {		
			//slide new values
			smallerXNewValue = currentNewValue;
			currentNewValue = greaterXNewValue;
			greaterXNewValue = 0;	
			//reuse values obtained previously
			smallerXOldValue = currentOldValue;
			currentOldValue = greaterXOldValue;
			try {
				greaterXOldValue = oldInput.readLong();					
			} catch (EOFException e) {
				endReached = true;
			}
			if (!endReached) {
				previousSliceToppled = currentSliceToppled;
				currentSliceToppled = false;
				if (smallerXOldValue < currentOldValue) {
					if (greaterXOldValue < currentOldValue) {
						if (smallerXOldValue == greaterXOldValue) {
							// gn == sn < current
							long toShare = currentOldValue - greaterXOldValue; 
							long share = toShare/3;
							if (share != 0) {
								currentSliceToppled = true;
							}
							smallerXNewValue += share;
							currentNewValue += currentOldValue - toShare + share + toShare%3;
							greaterXNewValue += share;
						} else if (smallerXOldValue < greaterXOldValue) {
							// sn < gn < current
							long toShare = currentOldValue - greaterXOldValue; 
							long share = toShare/3;
							if (share != 0) {
								currentSliceToppled = true;
							}
							smallerXNewValue += share;
							greaterXNewValue += share;
							long currentRemainingValue = currentOldValue - share - share;
							toShare = currentRemainingValue - smallerXOldValue; 
							share = toShare/2;
							if (share != 0) {
								currentSliceToppled = true;
							}
							smallerXNewValue += share;
							currentNewValue += currentRemainingValue - toShare + share + toShare%2;
						} else {
							// gn < sn < current
							long toShare = currentOldValue - smallerXOldValue; 
							long share = toShare/3;
							if (share != 0) {
								currentSliceToppled = true;
							}
							smallerXNewValue += share;
							greaterXNewValue += share;
							long currentRemainingValue = currentOldValue - share - share;
							toShare = currentRemainingValue - greaterXOldValue; 
							share = toShare/2;
							if (share != 0) {
								currentSliceToppled = true;
							}
							currentNewValue += currentRemainingValue - toShare + share + toShare%2;
							greaterXNewValue += share;
						}
					} else {
						// sn < current <= gn
						long toShare = currentOldValue - smallerXOldValue; 
						long share = toShare/2;
						if (share != 0) {
							currentSliceToppled = true;
						}
						smallerXNewValue += share;
						currentNewValue += currentOldValue - toShare + share + toShare%2;
					}
				} else {
					if (greaterXOldValue < currentOldValue) {
						// gn < current <= sn
						long toShare = currentOldValue - greaterXOldValue; 
						long share = toShare/2;
						if (share != 0) {
							currentSliceToppled = true;
						}
						currentNewValue += currentOldValue - toShare + share + toShare%2;
						greaterXNewValue += share;
					} else {
						currentNewValue += currentOldValue;
					}
				}
				anySliceToppled = anySliceToppled || currentSliceToppled;
			}
			newOutput.writeLong(smallerXNewValue);
			action.accept(smallerXNewValue);
		}
		newOutput.writeLong(currentNewValue);
		action.accept(currentNewValue);
		if (currentSliceToppled || previousSliceToppled) {
			newOutput.writeLong(0); //extend the grid
			maxX++;
		}
		return anySliceToppled;
	}

	@Override
	public void forEach(LongConsumer action) throws IOException {
		DataInputStream inputStream = null;
		try {
			inputStream = new DataInputStream(new BufferedInputStream(new FileInputStream(currentFile)));
			while (true) {
				action.accept(inputStream.readLong());
			}
		} catch (EOFException e) {
			//end of file reached
		} finally {
			if (inputStream != null) {
				inputStream.close();
			}
		}		
	}

	@Override
	public int getMinX() {
		return 0;
	}

	@Override
	public int getMaxX() {
		return maxX;
	}
	
	/**
	 * Returns the initial value
	 * 
	 * @return the value at the origin at step 0
	 */
	public long getInitialValue() {
		return initialValue;
	}

	@Override
	public long getStep() {
		return step;
	}

	@Override
	public String getName() {
		return "Aether";
	}

	@Override
	public String getSubfolderPath() {
		return getName() + "/1D/" + initialValue;
	}

	@Override
	public void backUp(String backupPath, String backupName) throws FileNotFoundException, IOException {
		// TODO implement
	}

	@Override
	public long[] getEvenOddPositionsMinAndMax(boolean isEven) throws Exception {
		long min = Long.MAX_VALUE, max = Long.MIN_VALUE;
		DataInputStream inputStream = null;
		try {
			inputStream = new DataInputStream(new BufferedInputStream(new FileInputStream(currentFile)));
			while (true) {
				long value = inputStream.readLong();
				if (value < min) {
					min = value;
				}
				if (value > max) {
					max = value;
				}
				inputStream.skipBytes(Long.BYTES);	
			}
		} catch (EOFException e) {
			return new long[] { min, max };
		} finally {
			if (inputStream != null) {
				inputStream.close();
			}
		}
	}

	@Override
	public Iterator<Long> iterator() {
		try {
			LongInputStreamIterator iterator = new LongInputStreamIterator(new DataInputStream(new BufferedInputStream(new FileInputStream(currentFile))), iterators);
			iterators.add(iterator);
			return iterator;
		} catch (FileNotFoundException e) {
			return null;
		}
	}
	
}
