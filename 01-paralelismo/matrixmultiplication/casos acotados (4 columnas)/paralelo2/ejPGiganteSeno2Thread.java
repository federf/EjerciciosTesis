import java.lang.management.ManagementFactory;
import java.util.Random;
import java.lang.Math;

/*
 * IDEA:
 * 	voy a dividir el trabajo de realizar la multiplicacion entre matrices
 *	de la siguiente manera
 *	cada thread recibira una matriz A y una columna B que pertenece a la 2da matriz
 *	asi cada thread solo realizara una multiplicacion entre la matriz A de tamaño AxB y una 
 *	columna de tamaño Bx1
 *	luego reuniria el resultado de todas las matrices de a 2
 *	por ejemplo en una multiplicacion entre matrices de 5x4 y 4x4
 *	con 4 threads realizare 
 *	4 multiplicaciones entre una matriz de 5x4 y una de 4x1
 *	luego unire de a 2 los resultados para divir el trabajo entre
 *	los 4 threads (1 y 2, 3 y 4 y luego el resultado de estos (1-2 con 3-4)
 */
public class ejPGiganteSeno2Thread extends Exception {


	public static class MultThread extends Thread {

		int index;
		int index2;
		public MultThread(int i, int i2){
			index=i;
			index2=i2;
		}

		@Override
		public void run() {
			multiply(index);
			multiply(index2);
		}
	}


	static synchronized void multiply(int index ) {
		int aRows = matrixA.length;
		int aColumns = matrixA[0].length;
		for (int i = 0; i < aRows; i++) { // aRow
			for (int j = 0; j < aColumns; j++) { // aColumn
				//synchronized(finalResult){
					finalResult[i][j] = finalResult[i][j] + ((int)Math.sin(matrixA[i][index]) * (int)Math.sin(matrixB[index][j]));
				//}
			}
		}
	}

	// para poder multiplicar dos matrices deben
	// tener tamaño AxB y BxC
	// de preferencia usare tamaños de columna y fila pares
	// para poder dividir facilmente el trabajo entre varios core
	static int[][] matrixA;
	static int[][] matrixB;

	// tamaño de las matrices
	static int rowsA = 6145684;
	// con este indice es posible aumentar el tamaño de la muestra sin modificar
	// como se divide el trabajo entre los 4 threads
	static int colsARowsB = 4;
	static int colsB = 4;

	// resultado final
	//static int2DMatrix finalResult = new int2DMatrix(colsB,rowsA);
	static int[][] finalResult = new int[rowsA][colsB];

	public static void repOk() {
		int errorCount = 0;
		for (int i = 0; i < rowsA; i++) {
			for (int j = 0; j < colsB; j++) {
				if (finalResult[i][j] != 16) {
					errorCount++;
				}
			}
		}
		System.out.println("errores en multiplicacion: " + errorCount);
	}
	public static void main(String[] args) throws Exception{
		String pid = ManagementFactory.getRuntimeMXBean().getName();
		//System.out.printlnprintln("Process id: " + pid);
		matrixA = new int[rowsA][colsARowsB];
		matrixB = new int[colsARowsB][colsB];

		//Random random=new Random();
		
		//System.out.printlnprintln("Matriz A: ");
		for(int i=0; i<rowsA; i++){
			for(int j=0; j<colsARowsB; j++){
				//matrixA[i][j]=random.nextInt(9 - 1 + 1) + 1;
				matrixA[i][j]=2;
				////System.out.printlnprint(matrixA[i][j]+" ");
			}
			////System.out.printlnprintln("\n");
		}

		//System.out.printlnprintln("Matriz B: ");
		for(int i=0; i<colsARowsB; i++){
			for(int j=0; j<colsB; j++){
				//matrixB[i][j]=random.nextInt(9 - 1 + 1) + 1;
				matrixB[i][j]=2;
				////System.out.printlnprint(matrixB[i][j]+" ");
			}
			////System.out.printlnprintln("\n");
		}
		
		for(int i=0; i<rowsA; i++){
			for(int j=0; j<colsB; j++){
				finalResult[i][j]=0;
			}
		}

		
		MultThread t0 = new MultThread(0,1);
		MultThread t1 = new MultThread(2,3);
		t0.start();
		t1.start();
		t0.join();
		t1.join();
		
		

		////System.out.printlnprintln("\nMatriz resultante: ");
		//finalResult.print();
		//System.out.printlnprintln("\nMatriz resultante: ");
		for(int i=0; i<rowsA; i++){
			for(int j=0; j<colsB; j++){
				//System.out.printlnprint(finalResult[i][j]+" ");
			}
			//System.out.printlnprintln("\n");
		}

		//repOk();

		//System.out.printlnprintln("matriz A de "+matrixA.length+" columnas por "+matrixA[1].length+" filas");
		//System.out.printlnprintln("matriz B de "+matrixB.length+" columnas por "+matrixB[1].length+" filas");
		//System.out.printlnprintln("matriz C de "+finalResult.length+" columnas por "+finalResult[1].length+" filas");
	}

}