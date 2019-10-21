package cellularautomata.grid4d;

public class NonsymmetricLongGridSection4D extends NonsymmetricGridSection4D implements LongGrid4D {
	
	public NonsymmetricLongGridSection4D(SymmetricLongGrid4D grid) {
		super(grid);
	}

	@Override
	public long getValueAtPosition(int w, int x, int y, int z) throws Exception {
		return ((SymmetricLongGrid4D) grid).getValueAtNonsymmetricPosition(w, x, y, z);
	}

}
