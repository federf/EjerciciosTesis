import java.lang.management.ManagementFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class HelloWorld2 implements Runnable {
	// arreglo compartido de palabras
	private final String[] words;
	// proxima palabra a imprimir (atomico)
	private final AtomicInteger whichWord;
	// ciclos restantes (atomico)
	private final AtomicInteger cycles;

	// constructor de clase
	private HelloWorld2(String[] words, AtomicInteger whichWord, AtomicInteger cycles) {
		// palabras a imprimir
		this.words = words;
		// siguiente palabra a imprimir (atomico)
		this.whichWord = whichWord;
		// cantidad de ciclos realizados
		this.cycles = cycles;
	}

	// metodo run de runnable
	public void run() {
		// imprimimos el id del thread
		System.out.println("Thread id: "+Thread.currentThread().getId());
		// hasta que se completen los ciclos
		while (cycles.get() > 0) {
			// debemos pasar de la palabra corriente 
			int thisWord = whichWord.get();
			// a la siguiente.
			int nextWord = thisWord + 1;

			for(int i=0; i<1000000; i++){
				for(int j=0; j<1000000; j++){
					for(int k=0; k<1000; k++){
						int l=i+j+k;
					}
				}
			}

			// estamos ciclando?
			boolean cycled = false;
			if (nextWord >= words.length) {
				// marcamos que ciclamos para luego descontar uno al contador
				cycled = true;
				// reseteamos a cero el indice que marca la proxima palabra a imprimir
				nextWord = 0;
			}
			// bloqueamos System.out para evitar condicion de carrera
			synchronized (System.out) {
				// actualizamos atomicamente la palabra corriente para que
				// pase a ser la proxima 
				// (thisWord es el valor esperado y nextWord es el valor con el 
				// cual se reeemplaza en caso de que el valor corriente sea el esperado)
				if (whichWord.compareAndSet(thisWord, nextWord)) {
					// nos toca imprimir
					System.out.print(words[thisWord]);
					// contamos los ciclos
					if (cycled) {
						// decrementamos atomicamente el valor de cycles
						cycles.decrementAndGet();
					}
				}
			}
		}
	}

	public static void test() throws InterruptedException {
		// palabras a imprimir
		String[] words = { "Hola ", "mundo. \n" };
		// indice de la proxima palabra a imprimir (comienza en cero).
		AtomicInteger whichWord = new AtomicInteger(0);
		// cuantos ciclos a imprimir en total
		AtomicInteger cycles = new AtomicInteger(6);
		// Threads, cantidad deseada
		Thread[] threads = new Thread[3];
		for (int i = 0; i < threads.length; i++) {
			// creamos cada thread
			threads[i] = new Thread(new HelloWorld2(words, whichWord, cycles));
			// y lo iniciamos
			threads[i].start();
		}
		// Esperamos a que terminen
		for (int i = 0; i < threads.length; i++) {
			// sincronizamos
			threads[i].join();
		}
	}

	public static void main(String args[]) throws InterruptedException {
		// imprimimos el process id
		String pid = ManagementFactory.getRuntimeMXBean().getName();
		System.out.println("Process id: " + pid);
		test();
	}

}