public class SimulacionThread extends Thread {
    private CalculadorDatos calc;
    private String rol;

    public SimulacionThread(CalculadorDatos calc, String rol) {
        this.calc = calc;
        this.rol = rol;
    }

    @Override
    public void run() {
        if (rol.equals("lector")) {
            int count = 0;
            for (String ref : calc.referencias) {
                String[] parts = ref.split(",");
                if (parts.length < 4)
                    continue;

                int pageNumber = Integer.parseInt(parts[1].trim());
                // char action = parts[3].trim().charAt(0);

                boolean hit = calc.accederPagina(pageNumber);
                if (hit) {
                    calc.hits++;
                    calc.tiempoTotalNS += CalculadorDatos.tiempoHit;
                } else {
                    calc.fallas++;
                    calc.tiempoTotalNS += CalculadorDatos.tiempoFallas;
                }

                count++;
                if (count % 10000 == 0) {
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                    }
                }
            }
            calc.centinela = true;
        } else if (rol.equals("actualizador")) {
            while (!calc.centinela) {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                }
                calc.limpiarBits();
            }
        }
    }
}
