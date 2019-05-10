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
package cellularautomata.grid;

/**
 * Gets two sets of min and max values: one for the even positions and another for the odd positions.
 * 
 * @author Jaume
 *
 */
public class LongGrid2DEvenOddMinAndMaxProcessor implements LongGrid2DProcessor {

	private long[] evenMinAndMax;
	private long[] oddMinAndMax;

	@Override
	public void processGridBlock(LongGrid2D gridBlock) throws Exception {
		int maxX = gridBlock.getMaxX(), minX = gridBlock.getMinX(), 
				maxY = gridBlock.getMaxY(minX), minY = gridBlock.getMinY(minX);
		//even
		if (evenMinAndMax == null) {
			evenMinAndMax = new long[]{Integer.MAX_VALUE, Integer.MIN_VALUE};
		}
		long evenMaxValue = evenMinAndMax[1], evenMinValue = evenMinAndMax[0];
		for (int x = minX; x <= maxX; x++) {
			minY = gridBlock.getMinY(x);
			maxY = gridBlock.getMaxY(x);
			if ((minY+x)%2 != 0) {
				minY++;
			}
			for (int y = minY; y <= maxY; y+=2) {
				long value = gridBlock.getValueAtPosition(x, y);
				if (value > evenMaxValue)
					evenMaxValue = value;
				if (value < evenMinValue)
					evenMinValue = value;
			}
		}
		evenMinAndMax[0] = evenMinValue;
		evenMinAndMax[1] = evenMaxValue;
		//odd
		if (oddMinAndMax == null) {
			oddMinAndMax = new long[]{Integer.MAX_VALUE, Integer.MIN_VALUE};
		}
		long oddMaxValue = oddMinAndMax[1], oddMinValue = oddMinAndMax[0];
		for (int x = minX; x <= maxX; x++) {
			minY = gridBlock.getMinY(x);
			maxY = gridBlock.getMaxY(x);
			if ((minY+x)%2 == 0) {
				minY++;
			}
			for (int y = minY; y <= maxY; y+=2) {
				long value = gridBlock.getValueAtPosition(x, y);
				if (value > oddMaxValue)
					oddMaxValue = value;
				if (value < oddMinValue)
					oddMinValue = value;
			}
		}
		oddMinAndMax[0] = oddMinValue;
		oddMinAndMax[1] = oddMaxValue;
	}

	@Override
	public void beforeProcessing() {
		evenMinAndMax = null;
		oddMinAndMax = null;
	}

	@Override
	public void afterProcessing() {	
		
	}
	
	public long[] getEvenMinAndMaxValue() {
		return evenMinAndMax;
	}
	
	public long[] getOddMinAndMaxValue() {
		return oddMinAndMax;
	}
}
