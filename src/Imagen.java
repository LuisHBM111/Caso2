import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class Imagen {
    byte[] header = new byte[54];
    byte[][][] imagen;
    int alto, ancho; // en pixeles
    int padding;

    // MODIFICADO: Se conserva el método para leer una imagen BMP.
    public Imagen(String nombre) {
        try {
            FileInputStream fis = new FileInputStream(nombre);
            fis.read(header);

            // Extraer el ancho y alto de la imagen desde la cabecera (little endian)
            ancho = ((header[21] & 0xFF) << 24) | ((header[20] & 0xFF) << 16) |
                    ((header[19] & 0xFF) << 8) | (header[18] & 0xFF);
            alto = ((header[25] & 0xFF) << 24) | ((header[24] & 0xFF) << 16) |
                    ((header[23] & 0xFF) << 8) | (header[22] & 0xFF);

            System.out.println("Ancho: " + ancho + " px, Alto: " + alto + " px");
            imagen = new byte[alto][ancho][3];

            int rowSizeSinPadding = ancho * 3;
            padding = (4 - (rowSizeSinPadding % 4)) % 4;

            // Leer los datos de los píxeles (BGR)
            byte[] pixel = new byte[3];
            for (int i = 0; i < alto; i++) {
                for (int j = 0; j < ancho; j++) {
                    fis.read(pixel);
                    imagen[i][j][0] = pixel[0];
                    imagen[i][j][1] = pixel[1];
                    imagen[i][j][2] = pixel[2];
                }
                fis.skip(padding);
            }
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // MODIFICADO: Se conserva el método para escribir una imagen BMP.
    public void escribirImagen(String output) {
        byte pad = 0;
        try {
            FileOutputStream fos = new FileOutputStream(output);
            fos.write(header);
            byte[] pixel = new byte[3];

            for (int i = 0; i < alto; i++) {
                for (int j = 0; j < ancho; j++) {
                    pixel[0] = imagen[i][j][0];
                    pixel[1] = imagen[i][j][1];
                    pixel[2] = imagen[i][j][2];
                    fos.write(pixel);
                }
                for (int k = 0; k < padding; k++)
                    fos.write(pad);
            }
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
