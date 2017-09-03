package cellularautomata.grid;

public class SymmetricShortGrid4DProjected3DEdge extends ShortGrid3D {

	private SymmetricShortGrid4D source;
	private short backgroundValue;
	
	public SymmetricShortGrid4DProjected3DEdge(SymmetricShortGrid4D source, short backgroundValue) {
		this.source = source;
		this.backgroundValue = backgroundValue;
	}

	@Override
	public int getMinX() {
		return source.getNonSymmetricMinX();
	}

	@Override
	public int getMaxX() {
		return source.getNonSymmetricMaxX();
	}

	@Override
	public int getMinY() {
		return source.getNonSymmetricMinY();
	}

	@Override
	public int getMaxY() {
		return source.getNonSymmetricMaxY();
	}
	
	@Override
	public int getMinZ() {
		return source.getNonSymmetricMinZ();
	}

	@Override
	public int getMaxZ() {
		return source.getNonSymmetricMaxZ();
	}
	
	@Override
	public short getValueAt(int x, int y, int z) {
		int w = source.getNonSymmetricMaxW(), 
				minW = source.getNonSymmetricMinW();
		short value = source.getValueAt(w, x, y, z);
		while (value == backgroundValue && w > minW) {
			w--;
			value = source.getNonSymmetricValueAt(w, x, y, z);
		}
		return value;
	}
}
