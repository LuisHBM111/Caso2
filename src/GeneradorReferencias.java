import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GeneradorReferencias {
	private Imagen img;
	private int pageSize;

	// Direcciones base para las estructuras en memoria virtual
	private int baseImagen = 0;
	private int sizeImagen;
	private int baseSobelX;
	private int sizeSobelX = 3 * 3 * 4; // 3x3 enteros (4 bytes cada uno = 36)
	private int baseSobelY;
	private int sizeSobelY = 36;
	private int baseRta;
	private int sizeRta;

	public GeneradorReferencias(Imagen img, int pageSize) {
		this.img = img;
		this.pageSize = pageSize;

		sizeImagen = img.alto * img.ancho * 3; // imagen en bytes
		baseSobelX = sizeImagen;
		baseSobelY = baseSobelX + sizeSobelX;
		baseRta = baseSobelY + sizeSobelY;
		sizeRta = img.alto * img.ancho * 3;
	}

	// Clase interna sencilla para representar la referencia
	// (Podríamos usar otra clase, pero aquí se hace inline por simplicidad)
	class MemRef {
		String matrix; // "Imagen", "SOBEL_X", "SOBEL_Y", "Rta"
		int i, j; // índices
		char channel; // 'r','g','b' (si aplica)
		int virtualPage;
		int offset;
		char action; // 'R' o 'W'

		// Con canal (Imagen, Rta)
		MemRef(String matrix, int i, int j, char channel, int vp, int off, char a) {
			this.matrix = matrix;
			this.i = i;
			this.j = j;
			this.channel = channel;
			this.virtualPage = vp;
			this.offset = off;
			this.action = a;
		}

		// Sin canal (SOBEL_X, SOBEL_Y)
		MemRef(String matrix, int i, int j, int vp, int off, char a) {
			this.matrix = matrix;
			this.i = i;
			this.j = j;
			this.channel = '-'; // sin canal
			this.virtualPage = vp;
			this.offset = off;
			this.action = a;
		}

		@Override
		public String toString() {
			String cell;
			if (matrix.equals("Imagen") || matrix.equals("Rta")) {
				cell = matrix + "[" + i + "][" + j + "]." + channel;
			} else {
				cell = matrix + "[" + i + "][" + j + "]";
			}
			return cell + "," + virtualPage + "," + offset + "," + action;
		}
	}

	// Generar la lista de referencias (simulando applySobel)
	public List<MemRef> generarReferencias() {
		List<MemRef> refs = new ArrayList<>();
		for (int i = 1; i < img.alto - 1; i++) {
			for (int j = 1; j < img.ancho - 1; j++) {
				// Recorrido 3x3 vecinos
				for (int ki = -1; ki <= 1; ki++) {
					for (int kj = -1; kj <= 1; kj++) {
						int row = i + ki;
						int col = j + kj;
						// Acceso a Imagen (3 canales)
						for (int c = 0; c < 3; c++) {
							int addr = baseImagen + ((row * img.ancho + col) * 3 + c);
							int vp = addr / pageSize;
							int off = addr % pageSize;
							char ch = (c == 0) ? 'r' : (c == 1) ? 'g' : 'b';
							refs.add(new MemRef("Imagen", row, col, ch, vp, off, 'R'));
						}
						// Acceso a SOBEL_X (3 accesos)
						int sobelRow = ki + 1;
						int sobelCol = kj + 1;
						for (int x = 0; x < 3; x++) {
							int addr = baseSobelX + ((sobelRow * 3 + sobelCol) * 4);
							int vp = addr / pageSize;
							int off = addr % pageSize;
							refs.add(new MemRef("SOBEL_X", sobelRow, sobelCol, vp, off, 'R'));
						}
						// Acceso a SOBEL_Y (3 accesos)
						for (int x = 0; x < 3; x++) {
							int addr = baseSobelY + ((sobelRow * 3 + sobelCol) * 4);
							int vp = addr / pageSize;
							int off = addr % pageSize;
							refs.add(new MemRef("SOBEL_Y", sobelRow, sobelCol, vp, off, 'R'));
						}
					}
				}
				// Escritura en la matriz Rta (3 canales)
				for (int c = 0; c < 3; c++) {
					int addr = baseRta + ((i * img.ancho + j) * 3 + c);
					int vp = addr / pageSize;
					int off = addr % pageSize;
					char ch = (c == 0) ? 'r' : (c == 1) ? 'g' : 'b';
					refs.add(new MemRef("Rta", i, j, ch, vp, off, 'W'));
				}
			}
		}
		return refs;
	}

	// Escribir el archivo de referencias con encabezado
	public void escribirReferencias(String filename, List<MemRef> refs) throws IOException {
		int NR = refs.size();
		// Tamaño total en bytes: imagen + sobelX + sobelY + rta
		int totalMem = sizeImagen + sizeSobelX + sizeSobelY + sizeRta;
		int NP = (totalMem + pageSize - 1) / pageSize; // redondeo hacia arriba

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

		for (MemRef r : refs) {
			bw.write(r.toString());
			bw.newLine();
		}
		bw.close();
	}
}
