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
package cellularautomata.grid;

public class ShortGridMinAndMaxProcessor<G extends ShortGrid> implements GridProcessor<G> {

	private short[] minAndMax;

	@Override
	public void processGridBlock(G gridBlock) throws Exception {
		if (minAndMax == null) {
			minAndMax = new short[]{Short.MAX_VALUE, Short.MIN_VALUE};
		}
		short[] blockMinAndMax = gridBlock.getMinAndMax();
		if (blockMinAndMax[0] < minAndMax[0]) minAndMax[0] = blockMinAndMax[0];
		if (blockMinAndMax[1] > minAndMax[1]) minAndMax[1] = blockMinAndMax[1];
	}

	@Override
	public void beforeProcessing() {
		minAndMax = null;
	}

	@Override
	public void afterProcessing() {	
		
	}
	
	public short[] getMinAndMaxValue() {
		return minAndMax;
	}
}
