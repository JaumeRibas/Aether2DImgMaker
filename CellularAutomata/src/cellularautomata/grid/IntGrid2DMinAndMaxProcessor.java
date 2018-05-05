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

public class IntGrid2DMinAndMaxProcessor implements IntGrid2DProcessor {

	private int[] minAndMax;

	@Override
	public void processGridBlock(IntGrid2D gridBlock) throws Exception {
		if (minAndMax == null) {
			minAndMax = new int[]{Integer.MAX_VALUE, Integer.MIN_VALUE};
		}
		int maxX = gridBlock.getMaxX(), minX = gridBlock.getMinX(), 
				maxY = gridBlock.getMaxY(minX), minY = gridBlock.getMinY(minX);
		int maxValue = minAndMax[1], minValue = minAndMax[0];
		for (int x = minX; x <= maxX; x++) {
			minY = gridBlock.getMinY(x);
			maxY = gridBlock.getMaxY(x);
			for (int y = minY; y <= maxY; y++) {
				int value = gridBlock.getValue(x, y);
				if (value > maxValue)
					maxValue = value;
				if (value < minValue)
					minValue = value;
			}
		}
		minAndMax[0] = minValue;
		minAndMax[1] = maxValue;
	}

	@Override
	public void beforeProcessing() {
		minAndMax = null;
	}

	@Override
	public void afterProcessing() {	
		
	}
	
	public int[] getMinAndMaxValue() {
		return minAndMax;
	}
}
