import java.lang.management.ManagementFactory;
import java.util.Random;

public class customThread2 implements Runnable {
	/**
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String[] args) {
		String pid = ManagementFactory.getRuntimeMXBean().getName();
		System.out.println("Process id: " + pid);
		// Lanzamos dos hilos de forma concurrente que duren un tiempo
		// aleatorio:
		Random aleatorio = new Random(1337);
		for (int i = 0; i < 4; i++) {
			new Thread(new customThread2()).start();
		}
	}

	public customThread2() {
	}

	/**
	 * Método que contiene las acciones que hará el hilo cuando se ejecute.
	 */
	public void run() {
		long tid = Thread.currentThread().getId();
		for(int i=0; i<Integer.MAX_VALUE; i++){
			for(int j=0; j<Integer.MAX_VALUE; j++){
				for(int k=0; k<Integer.MAX_VALUE; k++){
					int l=i+j+k;
				}
			}
		}
		System.out.println("Thread id: " + tid);
		System.out.println("Hello, World!");
	}
}
