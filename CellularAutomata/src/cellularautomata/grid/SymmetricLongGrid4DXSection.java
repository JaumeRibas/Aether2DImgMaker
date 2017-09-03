package cellularautomata.grid;

public class SymmetricLongGrid4DXSection extends SymmetricLongGrid2D {

	private SymmetricLongGrid4D source;
	private int y;
	private int z;
	
	public SymmetricLongGrid4DXSection(SymmetricLongGrid4D source, int y, int z) {
		this.source = source;
		this.y = y;
		this.z = z;
	}

	@Override
	public int getMinX() {
		return source.getMinW();
	}

	@Override
	public int getMaxX() {
		return source.getMaxW();
	}

	@Override
	public int getMinY() {
		return source.getMinX();
	}

	@Override
	public int getMaxY() {
		return source.getMaxX();
	}

	@Override
	public long getValueAt(int x, int y) {
		return source.getValueAt(x, y, this.y, z);
	}

	@Override
	public int getNonSymmetricMinX() {
		return source.getNonSymmetricMinW();
	}

	@Override
	public int getNonSymmetricMaxX() {
		return source.getNonSymmetricMaxW();
	}

	@Override
	public int getNonSymmetricMinY() {
		return source.getNonSymmetricMinX();
	}

	@Override
	public int getNonSymmetricMaxY() {
		return source.getNonSymmetricMaxX();
	}

	@Override
	public long getNonSymmetricValueAt(int x, int y) {
		return source.getNonSymmetricValueAt(x, y, this.y, z);
	}

}
