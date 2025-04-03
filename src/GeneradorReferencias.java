import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GeneradorReferencias {
	private Imagen img;
	private int pageSize;
	private int baseImagen = 0;
	private int sizeImagen;
	private int baseSobelX;
	private int sizeSobelX = 3 * 3 * 4;
	private int baseSobelY;
	private int sizeSobelY = 36;
	private int baseRta;
	private int sizeRta;

	public GeneradorReferencias(Imagen img, int pageSize) {
		this.img = img;
		this.pageSize = pageSize;
		sizeImagen = img.alto * img.ancho * 3;
		baseSobelX = sizeImagen;
		baseSobelY = baseSobelX + sizeSobelX;
		baseRta = baseSobelY + sizeSobelY;
		sizeRta = img.alto * img.ancho * 3;
	}

	// Genera la lista de referencias como cadenas de texto, sin usar una clase de
	// referencia.
	public List<String> generarReferencias() {
		List<String> refs = new ArrayList<>();
		for (int i = 1; i < img.alto - 1; i++) {
			for (int j = 1; j < img.ancho - 1; j++) {
				for (int ki = -1; ki <= 1; ki++) {
					for (int kj = -1; kj <= 1; kj++) {
						int row = i + ki;
						int col = j + kj;
						for (int c = 0; c < 3; c++) {
							int addr = baseImagen + ((row * img.ancho + col) * 3 + c);
							int vp = addr / pageSize;
							int off = addr % pageSize;
							char canal = (c == 0) ? 'r' : (c == 1) ? 'g' : 'b';
							String refStr = "Imagen[" + row + "][" + col + "]." + canal + ","
									+ vp + "," + off + ",R";
							refs.add(refStr);
						}
						int sobelRow = ki + 1;
						int sobelCol = kj + 1;
						for (int x = 0; x < 3; x++) {
							int addr = baseSobelX + ((sobelRow * 3 + sobelCol) * 4);
							int vp = addr / pageSize;
							int off = addr % pageSize;
							String refStr = "SOBEL_X[" + sobelRow + "][" + sobelCol + "],"
									+ vp + "," + off + ",R";
							refs.add(refStr);
						}
						for (int x = 0; x < 3; x++) {
							int addr = baseSobelY + ((sobelRow * 3 + sobelCol) * 4);
							int vp = addr / pageSize;
							int off = addr % pageSize;
							String refStr = "SOBEL_Y[" + sobelRow + "][" + sobelCol + "],"
									+ vp + "," + off + ",R";
							refs.add(refStr);
						}
					}
				}
				for (int c = 0; c < 3; c++) {
					int addr = baseRta + ((i * img.ancho + j) * 3 + c);
					int vp = addr / pageSize;
					int off = addr % pageSize;
					char canal = (c == 0) ? 'r' : (c == 1) ? 'g' : 'b';
					String refStr = "Rta[" + i + "][" + j + "]." + canal + ","
							+ vp + "," + off + ",W";
					refs.add(refStr);
				}
			}
		}
		return refs;
	}

	// Escribe el archivo de referencias con encabezado, siguiendo el formato del
	// caso.
	public void escribirReferencias(String filename, List<String> refs) throws IOException {
		int NR = refs.size();
		int totalMemory = sizeImagen + sizeSobelX + sizeSobelY + sizeRta;
		int NP = (totalMemory + pageSize - 1) / pageSize;

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
		for (String s : refs) {
			bw.write(s);
			bw.newLine();
		}
		bw.close();
	}
}
