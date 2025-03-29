import java.util.Scanner;

public class Main {
	
	public static GeneradorReferencias generadorReferencias;

	public static void main(String[] args) {
		
        Scanner scanner = new Scanner(System.in);
        
        System.out.print("Ingresar la dirección de la imagen: ");
        String inputFilePath = scanner.nextLine();
        System.out.println("La dirección de la imagen ingresada es: " + inputFilePath);
        
        scanner.close();
		
		Imagen imagenIn = new Imagen(inputFilePath);
		Imagen imagenOut = new Imagen(inputFilePath);
		
		FiltroSobel fs = new FiltroSobel(imagenIn, imagenOut);
		fs.applySobel();
		
		imagenOut.escribirImagen("textos/caso2-parrotspeq.txt");

	}

}
