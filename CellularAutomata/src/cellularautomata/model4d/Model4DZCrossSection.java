/* Aether2DImgMaker -- console app to generate images of the Aether cellular automaton in 2D
    Copyright (C) 2017-2025 Jaume Ribas

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
package cellularautomata.model4d;

import cellularautomata.model3d.Model3D;


public class Model4DZCrossSection<Source_Type extends Model4D> implements Model3D {

	protected final Source_Type source;
	protected final int z;
	
	public Model4DZCrossSection(Source_Type source, int z) {
		if (z > source.getMaxZ() || z < source.getMinZ()) {
			throw new IllegalArgumentException("The cross section is out of bounds.");
		}
		this.source = source;
		this.z = z;
	}
	
	@Override
	public String getXLabel() {
	    return source.getWLabel();
	}

	@Override
	public String getYLabel() {
	    return source.getXLabel();
	}

	@Override
	public String getZLabel() {
	    return source.getYLabel();
	}

	@Override
	public int getMinX() {
	    return source.getMinWAtZ(this.z);
	}

	@Override
	public int getMaxX() {
	    return source.getMaxWAtZ(this.z);
	}

	@Override
	public int getMinXAtY(int y) {
	    return source.getMinWAtXZ(y, this.z);
	}

	@Override
	public int getMaxXAtY(int y) {
	    return source.getMaxWAtXZ(y, this.z);
	}

	@Override
	public int getMinXAtZ(int z) {
	    return source.getMinWAtYZ(z, this.z);
	}

	@Override
	public int getMaxXAtZ(int z) {
	    return source.getMaxWAtYZ(z, this.z);
	}

	@Override
	public int getMinX(int y, int z) {
	    return source.getMinW(y, z, this.z);
	}

	@Override
	public int getMaxX(int y, int z) {
	    return source.getMaxW(y, z, this.z);
	}

	@Override
	public int getMinY() {
	    return source.getMinXAtZ(this.z);
	}

	@Override
	public int getMaxY() {
	    return source.getMaxXAtZ(this.z);
	}

	@Override
	public int getMinYAtX(int x) {
	    return source.getMinXAtWZ(x, this.z);
	}

	@Override
	public int getMaxYAtX(int x) {
	    return source.getMaxXAtWZ(x, this.z);
	}

	@Override
	public int getMinYAtZ(int z) {
	    return source.getMinXAtYZ(z, this.z);
	}

	@Override
	public int getMaxYAtZ(int z) {
	    return source.getMaxXAtYZ(z, this.z);
	}

	@Override
	public int getMinY(int x, int z) {
	    return source.getMinX(x, z, this.z);
	}

	@Override
	public int getMaxY(int x, int z) {
	    return source.getMaxX(x, z, this.z);
	}

	@Override
	public int getMinZ() {
	    return source.getMinYAtZ(this.z);
	}

	@Override
	public int getMaxZ() {
	    return source.getMaxYAtZ(this.z);
	}

	@Override
	public int getMinZAtX(int x) {
	    return source.getMinYAtWZ(x, this.z);
	}

	@Override
	public int getMaxZAtX(int x) {
	    return source.getMaxYAtWZ(x, this.z);
	}

	@Override
	public int getMinZAtY(int y) {
	    return source.getMinYAtXZ(y, this.z);
	}

	@Override
	public int getMaxZAtY(int y) {
	    return source.getMaxYAtXZ(y, this.z);
	}

	@Override
	public int getMinZ(int x, int y) {
	    return source.getMinY(x, y, this.z);
	}

	@Override
	public int getMaxZ(int x, int y) {
	    return source.getMaxY(x, y, this.z);
	}

	@Override
	public Boolean nextStep() throws Exception {
		Boolean changed = source.nextStep();
		if (z > source.getMaxZ() || z < source.getMinZ()) {
			throw new UnsupportedOperationException("The cross section is out of bounds.");
		}
		return changed;
	}
	
	@Override
	public Boolean isChanged() {
		return source.isChanged();
	}

	@Override
	public long getStep() {
		return source.getStep();
	}

	@Override
	public String getName() {
		return source.getName();
	}

	@Override
	public String getSubfolderPath() {
		return source.getSubfolderPath() + "/" + source.getZLabel() + "=" + z;
	}

	@Override
	public void backUp(String backupPath, String backupName) throws Exception {
		source.backUp(backupPath, backupName);
	}
}
