public class SimulacionThread extends Thread {
    private CalculadorDatos calc;
    private String rol; // "lector" o "actualizador"

    public SimulacionThread(CalculadorDatos calc, String rol) {
        this.calc = calc;
        this.rol = rol;
    }

    @Override
    public void run() {
        if (rol.equals("lector")) {
            // Recorrer referencias, parsearlas, contar hits/misses
            int count = 0;
            for (String ref : calc.referencias) {
                // Ejemplo de parseo: "Imagen[0][0].r,0,0,R"
                String[] parts = ref.split(",");
                if (parts.length < 4)
                    continue;

                int pageNumber = Integer.parseInt(parts[1].trim());
                // char action = parts[3].trim().charAt(0);

                // Acceder página
                boolean hit = calc.accederPagina(pageNumber);
                if (hit) {
                    calc.hits++;
                    calc.tiempoTotalNS += CalculadorDatos.HIT_TIME_NS;
                } else {
                    calc.misses++;
                    calc.tiempoTotalNS += CalculadorDatos.MISS_TIME_NS;
                }

                count++;
                // Cada 10,000 refs => pausa 1 ms
                if (count % 10000 == 0) {
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        // Manejo básico
                    }
                }
            }
            calc.finished = true; // Indica al otro thread que puede detenerse
        } else if (rol.equals("actualizador")) {
            // Cada 1 ms limpiamos bits, mientras no termine
            while (!calc.finished) {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    // Manejo básico
                }
                calc.limpiarBits();
            }
        }
    }
}
