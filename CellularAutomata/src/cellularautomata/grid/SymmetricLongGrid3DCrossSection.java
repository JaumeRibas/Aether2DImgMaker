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

public class SymmetricLongGrid3DCrossSection extends SymmetricLongGrid2D implements LongGrid3DProcessor {

	private SymmetricLongGrid3D source;
	private int z;
	
	public SymmetricLongGrid3DCrossSection(SymmetricLongGrid3D source, int z) {
		this.source = source;
		this.z = z;
	}

	@Override
	public int getMinX() {
		return source.getMinXAtZ(z);
	}
	
	@Override
	public int getMinX(int y) {
		return source.getMinX(y, z);
	}
	
	@Override
	public int getMaxX() {
		return source.getMaxXAtZ(z);
	}
	
	@Override
	public int getMaxX(int y) {
		return source.getMaxX(y, z);
	}
	
	@Override
	public int getMinY() {
		return source.getMinYAtZ(z);
	}
	
	@Override
	public int getMinY(int x) {
		return source.getMinY(x, z);
	}
	
	@Override
	public int getMaxY() {
		return source.getMaxYAtZ(z);
	}
	
	@Override
	public int getMaxY(int x) {
		return source.getMaxY(x, z);
	}

	@Override
	public long getValue(int x, int y) throws Exception {
		return source.getValue(x, y, z);
	}

	@Override
	public int getNonSymmetricMinX() {
		return source.getNonSymmetricMinXAtZ(z);
	}

	@Override
	public int getNonSymmetricMaxX() {
		return source.getNonSymmetricMaxXAtZ(z);
	}

	@Override
	public int getNonSymmetricMinY() {
		return source.getNonSymmetricMinYAtZ(z);
	}

	@Override
	public int getNonSymmetricMaxY() {
		return source.getNonSymmetricMaxYAtZ(z);
	}

	@Override
	public int getNonSymmetricMinX(int y) {
		return source.getNonSymmetricMinX(y, z);
	}

	@Override
	public int getNonSymmetricMaxX(int y) {
		return source.getNonSymmetricMaxX(y, z);
	}

	@Override
	public int getNonSymmetricMinY(int x) {
		return source.getNonSymmetricMinY(x, z);
	}

	@Override
	public int getNonSymmetricMaxY(int x) {
		return source.getNonSymmetricMaxY(x, z);
	}

	@Override
	public long getNonSymmetricValue(int x, int y) throws Exception {
		return source.getNonSymmetricValue(x, y, z);
	}

	@Override
	public void beforeProcessing() throws Exception {
		triggerBeforeProcessing();		
	}

	@Override
	public void afterProcessing() throws Exception {
		triggerAfterProcessing();		
	}

	@Override
	public void processGridBlock(LongGrid3D gridBlock) throws Exception {
		if (z >= gridBlock.getMinZ() && z <= gridBlock.getMaxZ()) {
			triggerProcessGridBlock(gridBlock.crossSection(z));
		}
	}
	
	@Override
	public void processGrid() throws Exception {
		source.processGrid();
	}

}
