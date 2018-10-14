import java.lang.management.ManagementFactory;
import java.util.Random;
import java.lang.Math;

import java.io.BufferedReader;
import java.io.InputStreamReader;

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
public class ejemploParaleloEnorme extends Exception {

	public static String[] threadNames;


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


	static void multiply(int index ) {
		int aRows = matrixA.length;
		int aColumns = matrixA[0].length;
		for (int i = 0; i < aRows; i++) { // aRow
			for (int j = 0; j < aColumns; j++) { // aColumn
				synchronized(finalResult[i]){
				finalResult[i][j]= finalResult[i][j] + 	(matrixA[i][index] * matrixB[index][j]);
				}
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
	static int rowsA = 85333;
	//static int rowsA = 10145684;
	// con este indice es posible aumentar el tamaño de la muestra sin modificar
	// como se divide el trabajo entre los 4 threads
	static int colsARowsB = 4;
	static int colsB = 4;

	// resultado final
	//static int2DMatrix finalResult = new int2DMatrix(colsB,rowsA);
	static int[][] finalResult = new int[rowsA][colsB];

	
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


	public static void main(String[] args) throws Exception{
		String pid = ManagementFactory.getRuntimeMXBean().getName().split("@")[0];
		//System.out.println("Process id: " + pid);

		matrixA = new int[rowsA][colsARowsB];
		matrixB = new int[colsARowsB][colsB];

		//Random random=new Random();
		
		//System.out.pritln("Matriz A: ");
		for(int i=0; i<rowsA; i++){
			for(int j=0; j<colsARowsB; j++){
				//matrixA[i][j]=random.nextInt(9 - 1 + 1) + 1;
				matrixA[i][j]=2;
				//System.out.printlnprint(matrixA[i][j]+" ");
			}
			//System.out.pritln("\n");
		}

		//System.out.pritln("Matriz B: ");
		for(int i=0; i<colsARowsB; i++){
			for(int j=0; j<colsB; j++){
				//matrixB[i][j]=random.nextInt(9 - 1 + 1) + 1;
				matrixB[i][j]=2;
				//System.out.printlnprint(matrixB[i][j]+" ");
			}
			//System.out.pritln("\n");
		}
		
		for(int i=0; i<rowsA; i++){
			for(int j=0; j<colsB; j++){
				finalResult[i][j]=0;
			}
		}

		MultThread t0 = new MultThread(0,1);

		MultThread t1 = new MultThread(2,3);
		threadNames=new String[] {t0.getName(), t1.getName()};

		t0.start();
		t1.start();

		// INTENTO DE OBTENER LOS NATIVE ID DE LOS THREAD
		//System.out.println("comando : "+"jstack -l " + pid); //+ " | grep \"Thread-\"");
		ejemploParaleloEnorme obj = new ejemploParaleloEnorme();

		String command= "jstack -l " + pid ;//+ " | grep \"CompilerThread\"";
		String nativeIDs = obj.executeJstack(command);

		//System.out.println(nativeIDs);
		String[] nids=nativeIDs.split(" ");

		//for(int i=0; i<nids.length; i++){
		//	System.out.println("nid "+i+" "+nids[i]);
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
		

		//System.out.pritln("\nMatriz resultante: ");
		//finalResult.print();
		/*System.out.pritln("\nMatriz resultante: ");
		for(int i=0; i<rowsA; i++){
			for(int j=0; j<colsB; j++){
				//System.out.printlnprint(finalResult[i][j]+" ");
			}
			//System.out.pritln("\n");
		}*/


		//System.out.pritln("matriz A de "+matrixA.length+" columnas por "+matrixA[1].length+" filas");
		//System.out.pritln("matriz B de "+matrixB.length+" columnas por "+matrixB[1].length+" filas");
		//System.out.pritln("matriz C de "+finalResult.length+" columnas por "+finalResult[1].length+" filas");
	}

}