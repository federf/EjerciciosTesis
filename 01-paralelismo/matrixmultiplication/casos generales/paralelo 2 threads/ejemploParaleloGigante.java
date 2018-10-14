import java.lang.management.ManagementFactory;
import java.util.Random;
import java.lang.Math;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/*
 * IDEA:
 * 	voy a dividir el trabajo de realizar la multiplicacion entre matrices
 *	de la siguiente manera
 *	cada thread realiza parte de la multiplicacion de MxN y NxL de la siguiente manera,
 *	Dividimos (M / cantThreads) en caso de que no sea multiplo de cantThreads
 *	luego se asignan los restantes entre los threads (algunos quedan con un poco menos de trabajo que otros)
 *	Ej: 4 Threads
 *		T0 se encarga de ciclar M desde cero hasta ((M/threads)-1)
 		tambien de alguna que otra columna que sobre de la division
 		T1 lo mismo pero desde (M/Threads) hasta (2(M/Threads)-1)
 		Y asi sucesivamente.
 */
public class ejemploParaleloGigante extends Exception {

	public static String[] threadNames;
	public static int cantThreads=2;

	// para poder multiplicar dos matrices deben
	// tener tamaño AxB y BxC
	// de preferencia usare tamaños de columna y fila pares
	// para poder dividir facilmente el trabajo entre varios core
	static int[][] matrixA;
	static int[][] matrixB;

	// tamaño de las matrices
	static int rowsA = 3000;
	static int colsARowsB = 300;
	static int colsB = 2500;


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
	
	public static boolean stringContainsItemFromList(String inputStr, String[] items){
		//System.out.println(inputStr);
	    for(int i =0; i < items.length; i++){
	        if(inputStr.contains(items[i])){
	            return true;
	        }
	    }
	    return false;
	}

	private String executeJstack(String command) {

		StringBuffer output = new StringBuffer();

		Process p;
		try {
			p = Runtime.getRuntime().exec(command);
			p.waitFor();
			BufferedReader reader = 
                            new BufferedReader(new InputStreamReader(p.getInputStream()));

            String line = "";			
			while ((line = reader.readLine())!= null) {
				if(stringContainsItemFromList(line, threadNames)){
					// filtro el resultado de jstack -l pid del proceso principal
					// en busca de los 4 threads que contiene, luego de esos threads
					// obtengo su nid y lo convierto en entero ya que esta en hexadecimal
					int nid=Integer.parseInt(line.split(" ")[5].split("=")[1].split("x")[1], 16);
					output.append(nid + " ");
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return output.toString();

	}

	private void executeCommand(String command) {

		StringBuffer output = new StringBuffer();

		Process p;
		try {
			p = Runtime.getRuntime().exec(command);
			p.waitFor();
			BufferedReader reader = 
                            new BufferedReader(new InputStreamReader(p.getInputStream()));

            String line = "";			
			while ((line = reader.readLine())!= null) {
				output.append(line + "\n");
			}
			//System.out.println("output de "+command+" = "+output);

		} catch (Exception e) {
			e.printStackTrace();
		}

		//return output.toString();

	}

	static void multiply(int index) {
		int aRows = matrixA.length;
		int aColumns = matrixA[0].length;
		for (int i = index*partRows; i < (index+1)*partRows; i++) { // aRow
			//for (int j = index*partColumns; j < (index+1)*partColumns; j++) { // bColumn
			for (int j = 0; j < colsB; j++) { // bColumn
				for(int k=0; k<colsARowsB; k++){
					////synchronized(finalResult[i]){
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
							////synchronized(finalResult[row]){
								finalResult[row][j]= finalResult[row][j] + 	matrixA[row][k]*matrixB[k][j];
							//}	
						}
					}
			}
		}
	}

	
	/*public static void repOk() {
		int errorCount = 0;
		for (int i = 0; i < rowsA; i++) {
			for (int j = 0; j < colsB; j++) {
				if (finalResult[i][j] != multIJ(i,j)) {
					errorCount++;
				}
			}
		}
		System.out.println("errores en multiplicacion: " + errorCount);
	}

	public static int multIJ(int i, int j){
		int result=0;
		for(int k=0; k<colsARowsB; k++){
			result+=matrixA[i][k]*matrixB[k][j];
		}
		return result;
	}*/


	public static void main(String[] args) throws Exception{
		String pid = ManagementFactory.getRuntimeMXBean().getName().split("@")[0];
		//System.out.println("Process id: " + pid);

		System.out.println("multiplicación de A["+rowsA+"]["+colsARowsB+"] por B["+colsARowsB+"]["+colsB+"]");

		matrixA = new int[rowsA][colsARowsB];
		matrixB = new int[colsARowsB][colsB];

		Random random=new Random();
		
		//System.out.println("Matriz A: ");
		for(int i=0; i<rowsA; i++){
			for(int j=0; j<colsARowsB; j++){
				matrixA[i][j]=random.nextInt(9 - 1 + 1) + 1;
				//matrixA[i][j]=3;
				//System.out.print(matrixA[i][j]+" ");
			}
			//System.out.println("\n");
		}

		//System.out.println("Matriz B: ");
		for(int i=0; i<colsARowsB; i++){
			for(int j=0; j<colsB; j++){
				matrixB[i][j]=random.nextInt(9 - 1 + 1) + 1;
				//matrixB[i][j]=2;
				//System.out.print(matrixB[i][j]+" ");
			}
			//System.out.println("\n");
		}
		
		for(int i=0; i<rowsA; i++){
			for(int j=0; j<colsB; j++){
				finalResult[i][j]=0;
			}
		}

		MultThread t0 = new MultThread(0);

		MultThread t1 = new MultThread(1);

		threadNames=new String[] {t0.getName(), t1.getName()};

		t0.start();
		t1.start();

		// INTENTO DE OBTENER LOS NATIVE ID DE LOS THREAD
		//System.out.println("comando : "+"jstack -l " + pid); //+ " | grep \"Thread-\"");
		ejemploParaleloGigante obj = new ejemploParaleloGigante();

		String command= "jstack -l " + pid ;//+ " | grep \"CompilerThread\"";
		String nativeIDs = obj.executeJstack(command);

		//System.out.println(nativeIDs);
		String[] nids=nativeIDs.split(" ");

		//for(int i=0; i<nids.length; i++){
		//	//System.out.println("nid "+i+" "+nids[i]);
		//}

		if(nids.length>1){
			for(int i=0; i<nids.length; i++){
				String taskset="taskset -p "+i+" "+nids[i];
				obj.executeCommand(taskset);
				String confirmacion="taskset -p "+nids[i];
				obj.executeCommand(confirmacion);
			}
		}
		

		t0.join();
		t1.join();		
		

		/*System.out.println("\nMatriz resultante: ");
		for(int i=0; i<rowsA; i++){
			for(int j=0; j<colsB; j++){
				//System.out.print(finalResult[i][j]+" ");
			}
			//System.out.println("\n");
		}*/

		//repOk();


		//System.out.println("matriz A de "+matrixA.length+" columnas por "+matrixA[1].length+" filas");
		//System.out.println("matriz B de "+matrixB.length+" columnas por "+matrixB[1].length+" filas");
		//System.out.println("matriz C de "+finalResult.length+" columnas por "+finalResult[1].length+" filas");
	}

}