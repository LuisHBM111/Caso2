import java.util.Scanner;

public class Main {
	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		// MODIFICADO: Se implementa un menú con dos opciones según el enunciado.
		System.out.println("Seleccione una opción:");
		System.out.println("1. Generación de referencias");
		System.out.println("2. Simulación del sistema de paginación");
		String opcion = scanner.nextLine();

		if (opcion.equals("1")) {
			// Opción 1: Generación de referencias
			System.out.print("Ingrese el tamaño de página (en bytes): ");
			int pageSize = Integer.parseInt(scanner.nextLine());
			System.out.print("Ingrese la dirección del archivo de imagen (BMP): ");
			String imagePath = scanner.nextLine();

			// Se crea la imagen (usando la clase Imagen)
			Imagen imagen = new Imagen(imagePath);

			// MODIFICADO: Se crea el generador de referencias y se genera la lista
			GeneradorReferencias generador = new GeneradorReferencias(imagen, pageSize);
			java.util.List<MemRef> referencias = generador.generarReferencias();

			System.out.print("Ingrese el nombre del archivo de salida para las referencias: ");
			String outFile = scanner.nextLine();
			try {
				generador.escribirReferencias(outFile, referencias);
				System.out.println("Archivo de referencias generado exitosamente.");
			} catch (Exception e) {
				System.out.println("Error al escribir el archivo: " + e.getMessage());
			}

		} else if (opcion.equals("2")) {
			// Opción 2: Simulación del sistema de paginación
			System.out.print("Ingrese el número de marcos de página: ");
			int numFrames = Integer.parseInt(scanner.nextLine());
			System.out.print("Ingrese el nombre del archivo de referencias: ");
			String refFile = scanner.nextLine();

			try {
				CalculadorDatos calculador = new CalculadorDatos(refFile, numFrames);
				calculador.simular();
			} catch (Exception e) {
				System.out.println("Error en la simulación: " + e.getMessage());
			}

		} else {
			System.out.println("Opción no válida.");
		}

		scanner.close();
	}
}
