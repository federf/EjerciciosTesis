
import java.lang.management.ManagementFactory;
import java.util.Random;

public class ejemploSecuencialChico {

	static int[][] matrixA;
	static int[][] matrixB;

	static int rowsA = 6;
	static int colsARowsB = 6;
	static int colsB = 6;

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
                	result[i][j]+=A[i][k]*B[k][j];
                }
            }
        }

        return C;
    }

	public static void main(String[] args) {
		String pid = ManagementFactory.getRuntimeMXBean().getName();
		//System.out.println("Process id: " + pid);
		matrixA = new int[rowsA][colsARowsB];
		matrixB = new int[colsARowsB][colsB];

		/*try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/

		System.out.println("multiplicación de A["+rowsA+"]["+colsARowsB+"] por B["+colsARowsB+"]["+colsB+"]");

		Random random = new Random();

		//System.out.println("Matriz A: ");
		for (int i = 0; i < rowsA; i++) {
			for (int j = 0; j < colsARowsB; j++) {
				matrixA[i][j] = random.nextInt(9 - 1 + 1) + 1;
				//matrixA[i][j] = 2;
				//System.out.printlnprint(matrixA[i][j] + " ");
			}
			//System.out.println("\n");
		}

		//System.out.println("Matriz B: ");
		for (int i = 0; i < colsARowsB; i++) {
			for (int j = 0; j < colsB; j++) {
				matrixB[i][j] = random.nextInt(9 - 1 + 1) + 1;
				//matrixB[i][j] = 2;
				////System.out.printlnprint(matrixB[i][j] + " ");
			}
			////System.out.println("\n");
		}

		//int[][] result = multiply(matrixA, matrixB);
		multiply(matrixA, matrixB);
		
		//System.out.println("Matriz Resultante: ");
		for (int i = 0; i < rowsA; i++) {
			for (int j = 0; j < colsB; j++) {
				////System.out.printlnprint(result[i][j] + " ");
			}
			////System.out.println("\n");
		}
		// para alargar la duracion del programa. comentar para ejecucion normal
		/*try {
			Thread.sleep(20000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//System.out.println("finalizando.");*/
	}

}
