
import java.lang.management.ManagementFactory;
import java.util.Random;
import java.lang.Math;

public class ejemploLinealGiganteSeno {

	static int[][] matrixA;
	static int[][] matrixB;

	static int rowsA = 6145684;
	static int colsARowsB = 4;
	static int colsB = 4;

	static int[][] result = new int[rowsA][colsB];

	public static int[][] multiply(int[][] A, int[][] B) {

        int aRows = A.length;
        int aColumns = A[0].length;
        int bRows = B.length;
        int bColumns = B[0].length;
        
        if (aColumns != bRows) {
            throw new IllegalArgumentException("A:Rows: " + aColumns + " did not match B:Columns " + bRows + ".");
        }

        int[][] C = new int[aRows][bColumns];
        for (int i = 0; i < aRows; i++) {
            for (int j = 0; j < bColumns; j++) {
                result[i][j] = 0;
            }
        }

        for (int i = 0; i < aRows; i++) { // aRow
            for (int j = 0; j < bColumns; j++) { // bColumn
                for (int k = 0; k < aColumns; k++) { // aColumn
                	result[i][j]+=(int)Math.sin(A[i][k])* (int)Math.sin(B[k][j]);
                }
            }
        }

        return C;
    }

	public static void main(String[] args) {
		String pid = ManagementFactory.getRuntimeMXBean().getName();
		//System.out.printlnprintln("Process id: " + pid);
		matrixA = new int[rowsA][colsARowsB];
		matrixB = new int[colsARowsB][colsB];

		/*try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/

		//Random random = new Random();

		//System.out.printlnprintln("Matriz A: ");
		for (int i = 0; i < rowsA; i++) {
			for (int j = 0; j < colsARowsB; j++) {
				//matrixA[i][j] = random.nextInt(9 - 1 + 1) + 1;
				matrixA[i][j] = 2;
				////System.out.printlnprint(matrixA[i][j] + " ");
			}
			////System.out.printlnprintln("\n");
		}

		//System.out.printlnprintln("Matriz B: ");
		for (int i = 0; i < colsARowsB; i++) {
			for (int j = 0; j < colsB; j++) {
				//matrixB[i][j] = random.nextInt(9 - 1 + 1) + 1;
				matrixB[i][j] = 2;
				////System.out.printlnprint(matrixB[i][j] + " ");
			}
			////System.out.printlnprintln("\n");
		}

		//int[][] result = multiply(matrixA, matrixB);
		multiply(matrixA, matrixB);
		
		//System.out.printlnprintln("Matriz Resultante: ");
		for (int i = 0; i < rowsA; i++) {
			for (int j = 0; j < colsB; j++) {
				//System.out.printlnprint(result[i][j] + " ");
			}
			//System.out.printlnprintln("\n");
		}
		// para alargar la duracion del programa. comentar para ejecucion normal
		/*try {
			Thread.sleep(20000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//System.out.printlnprintln("finalizando.");*/
	}

}
