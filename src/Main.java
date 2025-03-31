import java.util.Scanner;

public class Main {
	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);

		System.out.println("Seleccione una opcion:");
		System.out.println("1. Generacion de referencias");
		System.out.println("2. Simulacion del sistema de paginacion");
		String opcion = sc.nextLine();

		if (opcion.equals("1")) {
			// Opción 1: Generación de referencias
			try {
				System.out.print("Ingrese el tamaño de pagina (en bytes): ");
				int pageSize = Integer.parseInt(sc.nextLine());
				System.out.print("Ingrese la ruta de la imagen BMP: ");
				String imagePath = sc.nextLine();

				// Se crea la imagen y el generador de referencias
				Imagen img = new Imagen(imagePath);
				GeneradorReferencias gen = new GeneradorReferencias(img, pageSize);

				// Se generan las referencias simulando el filtro Sobel
				var referencias = gen.generarReferencias();

				System.out.print("Ingrese el nombre del archivo de salida (por ejemplo refs.txt): ");
				String outFile = sc.nextLine();
				gen.escribirReferencias(outFile, referencias);
				System.out.println("Archivo de referencias generado exitosamente.");
			} catch (Exception e) {
				System.out.println("Error en la generacion de referencias: " + e.getMessage());
			}

		} else if (opcion.equals("2")) {
			// Opción 2: Simulación del sistema de paginación
			try {
				System.out.print("Ingrese el numero de marcos de página: ");
				int numFrames = Integer.parseInt(sc.nextLine());
				System.out.print("Ingrese la ruta del archivo de referencias: ");
				String refFile = sc.nextLine();

				// CalculadorDatos se encarga de leer y preparar la simulación
				CalculadorDatos calc = new CalculadorDatos(refFile, numFrames);
				calc.simular(); // Aquí se lanzan los threads y se muestran resultados
			} catch (Exception e) {
				System.out.println("Error en la simulacion: " + e.getMessage());
			}
		} else {
			System.out.println("Opcion no valida.");
		}

		sc.close();
	}
}
