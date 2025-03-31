import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GeneradorReferencias {
	private Imagen img;
	private int pageSize;
	// Direcciones base para la organización en memoria virtual
	private int baseImagen = 0;
	private int sizeImagen;
	private int baseSobelX;
	private int sizeSobelX = 3 * 3 * 4; // 3x3 enteros (4 bytes cada uno)
	private int baseSobelY;
	private int sizeSobelY = 36;
	private int baseRta;
	private int sizeRta;

	// MODIFICADO: Constructor que inicializa la imagen y el tamaño de página.
	public GeneradorReferencias(Imagen img, int pageSize) {
		this.img = img;
		this.pageSize = pageSize;
		sizeImagen = img.alto * img.ancho * 3;
		baseSobelX = sizeImagen;
		baseSobelY = baseSobelX + sizeSobelX;
		baseRta = baseSobelY + sizeSobelY;
		sizeRta = img.alto * img.ancho * 3;
	}

	// MODIFICADO: Método para generar las referencias según la simulación de
	// applySobel.
	public List<MemRef> generarReferencias() {
		List<MemRef> refs = new ArrayList<>();
		for (int i = 1; i < img.alto - 1; i++) {
			for (int j = 1; j < img.ancho - 1; j++) {
				for (int ki = -1; ki <= 1; ki++) {
					for (int kj = -1; kj <= 1; kj++) {
						int row = i + ki;
						int col = j + kj;
						// Acceso a la imagen: 3 canales (r, g, b)
						for (int c = 0; c < 3; c++) {
							int addr = baseImagen + ((row * img.ancho + col) * 3 + c);
							int vp = addr / pageSize;
							int off = addr % pageSize;
							char canal = (c == 0) ? 'r' : (c == 1) ? 'g' : 'b';
							refs.add(new MemRef("Imagen", row, col, canal, vp, off, 'R'));
						}
						// Acceso al filtro SOBEL_X: se accede 3 veces
						int sobelRow = ki + 1;
						int sobelCol = kj + 1;
						for (int k = 0; k < 3; k++) {
							int addr = baseSobelX + ((sobelRow * 3 + sobelCol) * 4);
							int vp = addr / pageSize;
							int off = addr % pageSize;
							refs.add(new MemRef("SOBEL_X", sobelRow, sobelCol, vp, off, 'R'));
						}
						// Acceso al filtro SOBEL_Y: se accede 3 veces
						for (int k = 0; k < 3; k++) {
							int addr = baseSobelY + ((sobelRow * 3 + sobelCol) * 4);
							int vp = addr / pageSize;
							int off = addr % pageSize;
							refs.add(new MemRef("SOBEL_Y", sobelRow, sobelCol, vp, off, 'R'));
						}
					}
				}
				// Escritura en la imagen de resultado (Rta): 3 canales
				for (int c = 0; c < 3; c++) {
					int addr = baseRta + ((i * img.ancho + j) * 3 + c);
					int vp = addr / pageSize;
					int off = addr % pageSize;
					char canal = (c == 0) ? 'r' : (c == 1) ? 'g' : 'b';
					refs.add(new MemRef("Rta", i, j, canal, vp, off, 'W'));
				}
			}
		}
		return refs;
	}

	// MODIFICADO: Método para escribir el archivo de referencias con encabezado.
	public void escribirReferencias(String filename, List<MemRef> refs) throws IOException {
		int NR = refs.size();
		int totalMemory = sizeImagen + sizeSobelX + sizeSobelY + sizeRta;
		int NP = (totalMemory + pageSize - 1) / pageSize; // división hacia arriba
		BufferedWriter bw = new BufferedWriter(new FileWriter(filename));
		bw.write("TP=" + pageSize);
		bw.newLine();
		bw.write("NF=" + img.alto);
		bw.newLine();
		bw.write("NC=" + img.ancho);
		bw.newLine();
		bw.write("NR=" + NR);
		bw.newLine();
		bw.write("NP=" + NP);
		bw.newLine();
		for (MemRef ref : refs) {
			bw.write(ref.toString());
			bw.newLine();
		}
		bw.close();
	}
}
