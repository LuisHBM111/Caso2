import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class CalculadorDatos {
	// Constantes de tiempo (en ns)
	public static final long HIT_TIME_NS = 50; // 50 ns para acceso RAM
	public static final long MISS_TIME_NS = 10_000_000; // 10 ms para falla de página

	// Estructuras
	public List<String> referencias; // cada línea = "Celda,VP,Offset,Accion"
	private List<Integer> framesEnMemoria; // páginas cargadas (simple)
	private int numFrames;

	// Contadores
	public long hits = 0;
	public long misses = 0;
	public long tiempoTotalNS = 0;

	// Bandera para terminar
	public volatile boolean finished = false;

	public CalculadorDatos(String refFile, int numFrames) throws Exception {
		this.numFrames = numFrames;
		referencias = new ArrayList<>();
		framesEnMemoria = new ArrayList<>();

		// Leer el archivo de referencias
		BufferedReader br = new BufferedReader(new FileReader(refFile));
		// 1) Leer la primera línea: TP=<pageSize>
		String line = br.readLine();
		// 2) Omitir las siguientes 4 líneas (NF, NC, NR, NP)
		for (int i = 0; i < 4; i++) {
			br.readLine();
		}
		// 3) Leer el resto (referencias)
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

		// Al finalizar, imprimir resultados
		long totalRefs = referencias.size();
		long tiempoSoloHits = totalRefs * HIT_TIME_NS;
		long tiempoSoloMisses = totalRefs * MISS_TIME_NS;

		System.out.println("=== Resultados de la simulacion ===");
		System.out.println("Marcos asignados: " + numFrames);
		System.out.println("Total referencias: " + totalRefs);
		System.out.println("Hits: " + hits);
		System.out.println("Fallas: " + misses);
		System.out.println("Tiempo total (ns): " + tiempoTotalNS);
		System.out.println("Tiempo si todas fueran hits (ns): " + tiempoSoloHits);
		System.out.println("Tiempo si todas fueran misses (ns): " + tiempoSoloMisses);
	}

	// Lógica mínima de acceso a página (sin candados, ni sofisticación)
	// Retorna true si fue hit, false si fue miss
	public boolean accederPagina(int pageNumber) {
		if (framesEnMemoria.contains(pageNumber)) {
			return true; // Hit
		} else {
			// Miss
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
	// (aquí no tenemos bits, así que lo dejamos en vacío o con un print)
	public void limpiarBits() {
		// Sin candados, sin estructuras sofisticadas
		// Se deja en blanco o con un print para ilustrar
		// System.out.println("Limpiando bits de referencia (simulado)...");
	}
}
