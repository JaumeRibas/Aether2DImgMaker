package cellularautomata.grid;

public class SymmetricLongGrid4DProjected3DEdge extends LongGrid3D {

	private SymmetricLongGrid4D source;
	private long backgroundValue;
	
	public SymmetricLongGrid4DProjected3DEdge(SymmetricLongGrid4D source, long backgroundValue) {
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
	public long getValueAt(int x, int y, int z) {
		int w = source.getNonSymmetricMaxW(), 
				minW = source.getNonSymmetricMinW();
		long value = source.getValueAt(w, x, y, z);
		while (value == backgroundValue && w > minW) {
			w--;
			value = source.getNonSymmetricValueAt(w, x, y, z);
		}
		return value;
	}
}
