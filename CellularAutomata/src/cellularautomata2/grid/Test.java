package cellularautomata2.grid;

import cellularautomata2.arrays.Coordinates;
import cellularautomata2.arrays.PositionCommand;
import cellularautomata2.arrays.Utils;

public class Test {
	public static void main(String[] args) {
		testGridRegionForEachOddPosition();
	}

	public static void testGridRegionForEachPosition() {
		int dimension = 4;
		int[] vertex1 = new int[dimension];
		int[] vertex2 = new int[dimension];
		for (int i = 0; i < dimension; i++) {
			vertex1[i] = 0;
			vertex2[i] = 2;
		}
		AlignedRectangle testRectangle = new AlignedRectangle(vertex1, vertex2);
		testRectangle.forEachPosition(new PositionCommand() {
			
			@Override
			public void execute(Coordinates coordinates) {
				System.out.println(coordinates);
			}
		});
	}
	
	public static void testGridRegionForEachEvenPosition() {
		int dimension = 4;
		int[] vertex1 = new int[dimension];
		int[] vertex2 = new int[dimension];
		for (int i = 0; i < dimension; i++) {
			vertex1[i] = 0;
			vertex2[i] = 2;
		}
		AlignedRectangle testRectangle = new AlignedRectangle(vertex1, vertex2);
		testRectangle.forEachEvenPosition(new PositionCommand() {
			
			@Override
			public void execute(Coordinates coordinates) {
				if (!Utils.isEvenPosition(coordinates.getCopyAsArray())) {
					System.out.println("Wroong!");
				}
				System.out.println(coordinates);
			}
		});
	}
	
	public static void testGridRegionForEachOddPosition() {
		int dimension = 4;
		int[] vertex1 = new int[dimension];
		int[] vertex2 = new int[dimension];
		for (int i = 0; i < dimension; i++) {
			vertex1[i] = 0;
			vertex2[i] = 2;
		}
		AlignedRectangle testRectangle = new AlignedRectangle(vertex1, vertex2);
		testRectangle.forEachOddPosition(new PositionCommand() {
			
			@Override
			public void execute(Coordinates coordinates) {
				if (Utils.isEvenPosition(coordinates.getCopyAsArray())) {
					System.out.println("Wroong!");
				}
				System.out.println(coordinates);
			}
		});
	}
}