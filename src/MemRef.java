public class MemRef {
    public String matrix; // "Imagen", "SOBEL_X", "SOBEL_Y", "Rta"
    public int i, j; // índices de la matriz
    public char channel; // 'r', 'g', 'b' (solo para Imagen y Rta)
    public int virtualPage;
    public int offset;
    public char action; // 'R' para lectura, 'W' para escritura

    // Constructor para referencias con canal (Imagen, Rta)
    public MemRef(String matrix, int i, int j, char channel, int virtualPage, int offset, char action) {
        this.matrix = matrix;
        this.i = i;
        this.j = j;
        this.channel = channel;
        this.virtualPage = virtualPage;
        this.offset = offset;
        this.action = action;
    }

    // Constructor para referencias sin canal (SOBEL_X, SOBEL_Y)
    public MemRef(String matrix, int i, int j, int virtualPage, int offset, char action) {
        this.matrix = matrix;
        this.i = i;
        this.j = j;
        this.virtualPage = virtualPage;
        this.offset = offset;
        this.action = action;
    }

    // MODIFICADO: toString formateado según el enunciado.
    public String toString() {
        String cell = "";
        if (matrix.equals("Imagen") || matrix.equals("Rta")) {
            cell = matrix + "[" + i + "][" + j + "]." + channel;
        } else {
            cell = matrix + "[" + i + "][" + j + "]";
        }
        return cell + "," + virtualPage + "," + offset + "," + action;
    }
}
