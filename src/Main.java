import java.util.Scanner;

public class Main {
	public static void main(String[] args) {
		Scanner var1 = new Scanner(System.in);

		System.out.println("Seleccione una opcion:");
		System.out.println("1. Generacion de referencias");
		System.out.println("2. Simulacion del sistema de paginacion");
		String opcion = var1.nextLine();

		if (opcion.equals("1")) {
			// Opción 1: Generación de referencias
			try {
				System.out.print("Ingrese el tamaño de pagina (en bytes): ");
				int tamaPagi = Integer.parseInt(var1.nextLine());
				System.out.print("Ingrese la ruta de la imagen BMP: ");
				String direcImagen = var1.nextLine();

				Imagen img = new Imagen(direcImagen);
				GeneradorReferencias gen = new GeneradorReferencias(img, tamaPagi);

				var referencias = gen.generarReferencias();

				System.out.print("Ingrese el nombre del archivo de salida (por ejemplo refs.txt): ");
				String outFile = var1.nextLine();
				gen.escribirReferencias(outFile, referencias);
				System.out.println("Archivo de referencias generado exitosamente.");
			} catch (Exception e) {
				System.out.println("Error: " + e.getMessage());
			}

		} else if (opcion.equals("2")) {
			// Opción 2: Simulación del sistema de paginación
			try {
				System.out.print("Ingrese el numero de marcos de página: ");
				int numFrames = Integer.parseInt(var1.nextLine());
				System.out.print("Ingrese la ruta del archivo de referencias: ");
				String refFile = var1.nextLine();

				CalculadorDatos calc = new CalculadorDatos(refFile, numFrames);
				calc.simular();
			} catch (Exception e) {
				System.out.println("Error: " + e.getMessage());
			}
		} else {
			System.out.println("Opcion no valida.");
		}

		var1.close();
	}
}
