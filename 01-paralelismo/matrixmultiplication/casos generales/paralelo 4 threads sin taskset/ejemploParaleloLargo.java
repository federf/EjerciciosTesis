import java.lang.management.ManagementFactory;
import java.util.Random;

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
public class ejemploParaleloLargo extends Exception {

	// para poder multiplicar dos matrices deben
	// tener tamaño AxB y BxC
	// de preferencia usare tamaños de columna y fila pares
	// para poder dividir facilmente el trabajo entre varios core
	static int[][] matrixA;
	static int[][] matrixB;

	// tamaño de las matrices
	static int rowsA = 3000;
	// con este indice es posible aumentar el tamaño de la muestra sin modificar
	// como se divide el trabajo entre los 4 threads
	static int colsARowsB = 1000;
	static int colsB = 2500;

	// cantidad de threads
	public static int cantThreads=4;

	// enteros utilizados para indicar cuantas filas de A y columnas de B procesa cada thread
	//public static int partColumns=rowsA/cantThreads;
	//public static int remainderColumns=rowsA%cantThreads;
	public static int partRows=rowsA/cantThreads;
	public static int remainderRows=rowsA/cantThreads;


	// resultado final
	//static int2DMatrix finalResult = new int2DMatrix(colsB,rowsA);
	static int[][] finalResult = new int[rowsA][colsB];

	public static class MultThread extends Thread {

		int index;
		public MultThread(int i){
			index=i;
		}

		@Override
		public void run() {
			multiply(index);
		}
	}

	static void multiply(int index) {
		int aRows = matrixA.length;
		int aColumns = matrixA[0].length;
		for (int i = index*partRows; i < (index+1)*partRows; i++) { // aRow
			for (int j = 0; j < colsB; j++) { // bColumn
				for(int k=0; k<colsARowsB; k++){
					//synchronized(finalResult[i]){
					finalResult[i][j]= finalResult[i][j] + 	matrixA[i][k]*matrixB[k][j];
					//}	
				}
			}
		}

		if(remainderRows>0){
			int row=cantThreads*partRows+index-1;
			if(row>=cantThreads*partRows && row<rowsA){
					for (int j = 0; j < colsB; j++) { // bColumn
						for(int k=0; k<colsARowsB; k++){
							//synchronized(finalResult[row]){
								finalResult[row][j]= finalResult[row][j] + 	matrixA[row][k]*matrixB[k][j];
							//}	
						}
					}
				//}
			}
		}
	}


	public static void repOk() {
		int errorCount = 0;
		for (int i = 0; i < rowsA; i++) {
			for (int j = 0; j < colsB; j++) {
				if (finalResult[i][j] != 40000) {
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

		Random random=new Random();

		System.out.println("multiplicación de A["+rowsA+"]["+colsARowsB+"] por B["+colsARowsB+"]["+colsB+"]");
		
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
				matrixB[i][j]=20;
				////System.out.printlnprint(matrixB[i][j]+" ");
			}
			////System.out.printlnprintln("\n");
		}
		
		for(int i=0; i<rowsA; i++){
			for(int j=0; j<colsB; j++){
				finalResult[i][j]=0;
			}
		}

		
		MultThread t0 = new MultThread(0);
		MultThread t1 = new MultThread(1);
		MultThread t2 = new MultThread(2);
		MultThread t3 = new MultThread(3);
		t0.start();
		t1.start();
		t2.start();
		t3.start();
		t0.join();
		t1.join();
		t2.join();
		t3.join();
		
		

		////System.out.printlnprintln("\nMatriz resultante: ");
		//finalResult.print();
		//System.out.printlnprintln("\nMatriz resultante: ");
		/*for(int i=0; i<rowsA; i++){
			for(int j=0; j<colsB; j++){
				//System.out.printlnprint(finalResult[i][j]+" ");
			}
			//System.out.printlnprintln("\n");
		}*/

		repOk();

		//System.out.printlnprintln("matriz A de "+matrixA.length+" columnas por "+matrixA[1].length+" filas");
		//System.out.printlnprintln("matriz B de "+matrixB.length+" columnas por "+matrixB[1].length+" filas");
		//System.out.printlnprintln("matriz C de "+finalResult.length+" columnas por "+finalResult[1].length+" filas");
	}

}