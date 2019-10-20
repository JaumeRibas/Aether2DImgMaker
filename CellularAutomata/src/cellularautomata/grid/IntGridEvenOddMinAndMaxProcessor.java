/* Aether2DImgMaker -- console app to generate images of the Aether cellular automaton in 2D
    Copyright (C) 2017-2019 Jaume Ribas

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

public class IntGridEvenOddMinAndMaxProcessor<G extends IntGrid> implements GridProcessor<G> {

	private int[] evenMinAndMax;
	private int[] oddMinAndMax;
	
	
	@Override
	public void processGridBlock(G gridBlock) throws Exception {
		//even
		int[] blockEvenMinAndMax = gridBlock.getEvenOddPositionsMinAndMaxValue(true);
		if (evenMinAndMax == null) {
			evenMinAndMax = new int[]{Integer.MAX_VALUE, Integer.MIN_VALUE};
		}
		if (blockEvenMinAndMax[0] < evenMinAndMax[0]) evenMinAndMax[0] = blockEvenMinAndMax[0];
		if (blockEvenMinAndMax[1] > evenMinAndMax[1]) evenMinAndMax[1] = blockEvenMinAndMax[1];
		//odd
		int[] blockOddMinAndMax = gridBlock.getEvenOddPositionsMinAndMaxValue(false);
		if (oddMinAndMax == null) {
			oddMinAndMax = new int[]{Integer.MAX_VALUE, Integer.MIN_VALUE};
		}
		if (blockOddMinAndMax[0] < oddMinAndMax[0]) oddMinAndMax[0] = blockOddMinAndMax[0];
		if (blockOddMinAndMax[1] > oddMinAndMax[1]) oddMinAndMax[1] = blockOddMinAndMax[1];
	}

	@Override
	public void beforeProcessing() {
		evenMinAndMax = null;
		oddMinAndMax = null;
	}

	@Override
	public void afterProcessing() {	
		
	}
	
	public int[] getEvenMinAndMaxValue() {
		return evenMinAndMax;
	}
	
	public int[] getOddMinAndMaxValue() {
		return oddMinAndMax;
	}
}
