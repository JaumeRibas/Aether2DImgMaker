/* Aether2DImgMaker -- console app to generate images of the Aether cellular automaton in 2D
    Copyright (C) 2017-2020 Jaume Ribas

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
package cellularautomata.grid2d;
import cellularautomata.grid.GridProcessor;

public class Grid2DBoundsProcessor<G extends Grid2D> implements GridProcessor<G> {

	private int[] minAndMaxX;
	private int[] minAndMaxY;

	@Override
	public void processGridBlock(G gridBlock) throws Exception {
		if (minAndMaxX == null) {
			minAndMaxX = new int[]{gridBlock.getMinX(), gridBlock.getMaxX()};
		} else {
			int blockMinX = gridBlock.getMinX();
			if (blockMinX < minAndMaxX[0]) minAndMaxX[0] = blockMinX;
			int blockMaxX = gridBlock.getMaxX();
			if (blockMaxX > minAndMaxX[1]) minAndMaxX[1] = blockMaxX;
		}
		if (minAndMaxY == null) {
			minAndMaxY = new int[]{gridBlock.getMinY(), gridBlock.getMaxY()};
		} else {
			int blockMinY = gridBlock.getMinY();
			if (blockMinY < minAndMaxY[0]) minAndMaxY[0] = blockMinY;
			int blockMaxY = gridBlock.getMaxY();
			if (blockMaxY > minAndMaxY[1]) minAndMaxY[1] = blockMaxY;
		}
	}

	@Override
	public void beforeProcessing() {
		minAndMaxX = null;
		minAndMaxY = null;
	}

	@Override
	public void afterProcessing() {	
		
	}
	
	public int[] getMinAndMaxX() {
		return minAndMaxX;
	}
	
	public int[] getMinAndMaxY() {
		return minAndMaxY;
	}
}
