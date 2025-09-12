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


public class Model4DWCrossSection<Source_Type extends Model4D> implements Model3D {

	protected final Source_Type source;
	protected final int w;
	
	public Model4DWCrossSection(Source_Type source, int w) {
		if (w > source.getMaxW() || w < source.getMinW()) {
			throw new IllegalArgumentException("The cross section is out of bounds.");
		}
		this.source = source;
		this.w = w;
	}
	
	@Override
	public String getXLabel() {
	    return source.getXLabel();
	}

	@Override
	public String getYLabel() {
	    return source.getYLabel();
	}

	@Override
	public String getZLabel() {
	    return source.getZLabel();
	}

	@Override
	public int getMinX() {
	    return source.getMinXAtW(this.w);
	}

	@Override
	public int getMaxX() {
	    return source.getMaxXAtW(this.w);
	}

	@Override
	public int getMinXAtY(int y) {
	    return source.getMinXAtWY(this.w, y);
	}

	@Override
	public int getMaxXAtY(int y) {
	    return source.getMaxXAtWY(this.w, y);
	}

	@Override
	public int getMinXAtZ(int z) {
	    return source.getMinXAtWZ(this.w, z);
	}

	@Override
	public int getMaxXAtZ(int z) {
	    return source.getMaxXAtWZ(this.w, z);
	}

	@Override
	public int getMinX(int y, int z) {
	    return source.getMinX(this.w, y, z);
	}

	@Override
	public int getMaxX(int y, int z) {
	    return source.getMaxX(this.w, y, z);
	}

	@Override
	public int getMinY() {
	    return source.getMinYAtW(this.w);
	}

	@Override
	public int getMaxY() {
	    return source.getMaxYAtW(this.w);
	}

	@Override
	public int getMinYAtX(int x) {
	    return source.getMinYAtWX(this.w, x);
	}

	@Override
	public int getMaxYAtX(int x) {
	    return source.getMaxYAtWX(this.w, x);
	}

	@Override
	public int getMinYAtZ(int z) {
	    return source.getMinYAtWZ(this.w, z);
	}

	@Override
	public int getMaxYAtZ(int z) {
	    return source.getMaxYAtWZ(this.w, z);
	}

	@Override
	public int getMinY(int x, int z) {
	    return source.getMinY(this.w, x, z);
	}

	@Override
	public int getMaxY(int x, int z) {
	    return source.getMaxY(this.w, x, z);
	}

	@Override
	public int getMinZ() {
	    return source.getMinZAtW(this.w);
	}

	@Override
	public int getMaxZ() {
	    return source.getMaxZAtW(this.w);
	}

	@Override
	public int getMinZAtX(int x) {
	    return source.getMinZAtWX(this.w, x);
	}

	@Override
	public int getMaxZAtX(int x) {
	    return source.getMaxZAtWX(this.w, x);
	}

	@Override
	public int getMinZAtY(int y) {
	    return source.getMinZAtWY(this.w, y);
	}

	@Override
	public int getMaxZAtY(int y) {
	    return source.getMaxZAtWY(this.w, y);
	}

	@Override
	public int getMinZ(int x, int y) {
	    return source.getMinZ(this.w, x, y);
	}

	@Override
	public int getMaxZ(int x, int y) {
	    return source.getMaxZ(this.w, x, y);
	}

	@Override
	public Boolean nextStep() throws Exception {
		Boolean changed = source.nextStep();
		if (w > source.getMaxW() || w < source.getMinW()) {
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
		return source.getSubfolderPath() + "/" + source.getWLabel() + "=" + w;
	}

	@Override
	public void backUp(String backupPath, String backupName) throws Exception {
		source.backUp(backupPath, backupName);
	}
}
