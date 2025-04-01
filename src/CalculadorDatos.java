import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class CalculadorDatos {
	public static final long tiempoHit = 50;
	public static final long tiempoFallas = 10000000;

	// Estructuras
	public List<String> referencias;
	private List<Integer> framesEnMemoria;
	private int numFrames;

	// Contadores
	public long hits = 0;
	public long fallas = 0;
	public long tiempoTotalNS = 0;

	// Centinela
	public volatile boolean centinela = false;

	public CalculadorDatos(String refFile, int numFrames) throws Exception {
		this.numFrames = numFrames;
		referencias = new ArrayList<>();
		framesEnMemoria = new ArrayList<>();

		BufferedReader br = new BufferedReader(new FileReader(refFile));
		String line = br.readLine();
		for (int i = 0; i < 4; i++) {
			br.readLine();
		}
		while ((line = br.readLine()) != null) {
			referencias.add(line);
		}
		br.close();
		System.out.println("Archivo de referencias leído: " + referencias.size() + " refs.");
	}

	// Método llamado desde Main para simular
	public void simular() {
		// Crear 2 threads:
		// 1) Lector de referencias
		// 2) Actualizador de bits (aquí se simplifica a un "limpiador" que corre cada 1
		// ms)

		// Thread LECTOR
		SimulacionThread lector = new SimulacionThread(this, "lector");

		// Thread ACTUALIZADOR
		SimulacionThread actualizador = new SimulacionThread(this, "actualizador");

		// Iniciar ambos
		lector.start();
		actualizador.start();

		// Esperar a que terminen
		try {
			lector.join();
			actualizador.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		long totalRefs = referencias.size();
		long tiempoSoloHits = totalRefs * tiempoHit;
		long tiempoSolofallas = totalRefs * tiempoFallas;

		System.out.println("=== Resultados de la simulacion ===");
		System.out.println("Marcos asignados: " + numFrames);
		System.out.println("Total referencias: " + totalRefs);
		System.out.println("Hits: " + hits);
		System.out.println("Fallas: " + fallas);
		System.out.println("Tiempo total (ns): " + tiempoTotalNS);
		System.out.println("Tiempo si todas fueran hits (ns): " + tiempoSoloHits);
		System.out.println("Tiempo si todas fueran fallas (ns): " + tiempoSolofallas);
	}

	// Lógica mínima de acceso a página retorna true si fue hit, false si fue fallas
	public boolean accederPagina(int pageNumber) {
		if (framesEnMemoria.contains(pageNumber)) {
			return true; // Hit
		} else {
			// Fallas
			if (framesEnMemoria.size() < numFrames) {
				framesEnMemoria.add(pageNumber);
			} else {
				// Remplazar la primera (FIFO simplificado)
				framesEnMemoria.remove(0);
				framesEnMemoria.add(pageNumber);
			}
			return false;
		}
	}

	// Simulamos la "limpieza" de bits de referencia
	public void limpiarBits() {
	}
}
