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

public class LongGridEvenOddMinAndMaxProcessor<G extends LongGrid> implements GridProcessor<G> {

	private long[] evenMinAndMax;
	private long[] oddMinAndMax;
	
	
	@Override
	public void processGridBlock(G gridBlock) throws Exception {
		//even
		long[] blockEvenMinAndMax = gridBlock.getMinAndMaxValueAtEvenPositions();
		if (evenMinAndMax == null) {
			evenMinAndMax = new long[]{Integer.MAX_VALUE, Integer.MIN_VALUE};
		}
		if (blockEvenMinAndMax[0] < evenMinAndMax[0]) evenMinAndMax[0] = blockEvenMinAndMax[0];
		if (blockEvenMinAndMax[1] > evenMinAndMax[1]) evenMinAndMax[1] = blockEvenMinAndMax[1];
		//odd
		long[] blockOddMinAndMax = gridBlock.getMinAndMaxValueAtOddPositions();
		if (oddMinAndMax == null) {
			oddMinAndMax = new long[]{Integer.MAX_VALUE, Integer.MIN_VALUE};
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
	
	public long[] getEvenMinAndMaxValue() {
		return evenMinAndMax;
	}
	
	public long[] getOddMinAndMaxValue() {
		return oddMinAndMax;
	}
}
