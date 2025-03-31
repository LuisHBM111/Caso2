import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CalculadorDatos {
	// Constantes de tiempo (en nanosegundos)
	public static final long HIT_TIME_NS = 50; // 50 ns para acceso a RAM
	public static final long MISS_TIME_NS = 10_000_000L; // 10 ms = 10,000,000 ns para acceso a SWAP

	private List<MemRef> referencias;
	private int numFrames;
	private int pageSize;

	private long hits = 0;
	private long misses = 0;
	private long tiempoTotalNS = 0;

	private PageTable pageTable;
	private volatile boolean finished = false;

	// MODIFICADO: Constructor que lee el archivo de referencias.
	public CalculadorDatos(String refFilename, int numFrames) throws IOException {
		this.numFrames = numFrames;
		referencias = new ArrayList<>();

		BufferedReader br = new BufferedReader(new FileReader(refFilename));
		String line;
		// Leer encabezado (TP, NF, NC, NR, NP)
		if ((line = br.readLine()) != null) {
			pageSize = Integer.parseInt(line.split("=")[1].trim());
		}
		// Se ignoran las siguientes 4 líneas de encabezado
		for (int i = 0; i < 4; i++) {
			br.readLine();
		}

		// Leer cada referencia
		while ((line = br.readLine()) != null) {
			String[] parts = line.split(",");
			if (parts.length < 4)
				continue;
			String cell = parts[0];
			int vp = Integer.parseInt(parts[1].trim());
			int off = Integer.parseInt(parts[2].trim());
			char act = parts[3].trim().charAt(0);

			// MODIFICADO: Diferenciar entre referencias con canal y sin canal.
			if (cell.contains(".")) {
				String matrix = cell.substring(0, cell.indexOf('['));
				String indexPart = cell.substring(cell.indexOf('['));
				int firstBracket = indexPart.indexOf('[');
				int secondBracket = indexPart.indexOf(']');
				int iVal = Integer.parseInt(indexPart.substring(firstBracket + 1, secondBracket));
				int thirdBracket = indexPart.indexOf('[', secondBracket);
				int fourthBracket = indexPart.indexOf(']', thirdBracket);
				int jVal = Integer.parseInt(indexPart.substring(thirdBracket + 1, fourthBracket));
				char channel = cell.charAt(cell.length() - 1);
				referencias.add(new MemRef(matrix, iVal, jVal, channel, vp, off, act));
			} else {
				String matrix = cell.substring(0, cell.indexOf('['));
				String indexPart = cell.substring(cell.indexOf('['));
				int firstBracket = indexPart.indexOf('[');
				int secondBracket = indexPart.indexOf(']');
				int iVal = Integer.parseInt(indexPart.substring(firstBracket + 1, secondBracket));
				int thirdBracket = indexPart.indexOf('[', secondBracket);
				int fourthBracket = indexPart.indexOf(']', thirdBracket);
				int jVal = Integer.parseInt(indexPart.substring(thirdBracket + 1, fourthBracket));
				referencias.add(new MemRef(matrix, iVal, jVal, vp, off, act));
			}
		}
		br.close();

		System.out.println("Archivo de referencias leído: " + referencias.size() + " referencias.");
		pageTable = new PageTable(numFrames);
	}

	// MODIFICADO: Método que simula el procesamiento de referencias con dos
	// threads.
	public void simular() {
		Thread actualizador = new Thread(new Runnable() {
			public void run() {
				while (!finished) {
					try {
						Thread.sleep(1);
					} catch (InterruptedException e) {
					}
					pageTable.limpiarBitsReferencia();
				}
			}
		});
		actualizador.start();

		Thread lector = new Thread(new Runnable() {
			public void run() {
				int count = 0;
				for (MemRef ref : referencias) {
					boolean hit = pageTable.accederPagina(ref.virtualPage);
					if (hit) {
						hits++;
						tiempoTotalNS += HIT_TIME_NS;
					} else {
						misses++;
						tiempoTotalNS += MISS_TIME_NS;
					}
					count++;
					if (count % 10000 == 0) {
						try {
							Thread.sleep(1);
						} catch (InterruptedException e) {
						}
					}
				}
				finished = true;
			}
		});
		lector.start();

		try {
			lector.join();
			actualizador.join();
		} catch (InterruptedException e) {
		}

		long tiempoSoloHits = referencias.size() * HIT_TIME_NS;
		long tiempoSoloMisses = referencias.size() * MISS_TIME_NS;

		System.out.println("=== Resultados de la simulación ===");
		System.out.println("Marcos asignados: " + numFrames);
		System.out.println("Total de referencias: " + referencias.size());
		System.out.println("Hits: " + hits);
		System.out.println("Fallas (misses): " + misses);
		System.out.println("Tiempo total (ns): " + tiempoTotalNS);
		System.out.println("Tiempo si todas fueran hits (ns): " + tiempoSoloHits);
		System.out.println("Tiempo si todas fueran misses (ns): " + tiempoSoloMisses);
	}
}
