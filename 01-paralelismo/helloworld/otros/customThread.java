import java.lang.management.ManagementFactory;
import java.util.Random;

/**
 * 
 * @author fmgarcia
 */
public class customThread implements Runnable {
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
			// Un hilo tendrá un tiempo de ejecución comprendido entre los 0 y
			// 10 segundos.
			new Thread(new customThread(i, aleatorio.nextInt(10000))).start();
		}
	}

	public customThread(int nombre, int duración) {
	}

	/**
	 * Método que contiene las acciones que hará el hilo cuando se ejecute.
	 */
	public void run() {
		long tid = Thread.currentThread().getId();
		System.out.println("Thread id: " + tid);
		System.out.println("Hello, World!");
	}
}
