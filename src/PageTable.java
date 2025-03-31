import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class PageTable {
    private int numFrames;
    // MODIFICADO: Se utiliza LinkedHashMap para mantener el orden de inserción.
    private Map<Integer, Frame> frames;

    public PageTable(int numFrames) {
        this.numFrames = numFrames;
        frames = new LinkedHashMap<>();
    }

    // MODIFICADO: Método sincronizado para acceder a una página.
    public synchronized boolean accederPagina(int pageNumber) {
        if (frames.containsKey(pageNumber)) {
            frames.get(pageNumber).referenciado = true;
            return true;
        } else {
            if (frames.size() < numFrames) {
                frames.put(pageNumber, new Frame(pageNumber));
            } else {
                boolean reemplazado = false;
                for (Iterator<Map.Entry<Integer, Frame>> it = frames.entrySet().iterator(); it.hasNext();) {
                    Map.Entry<Integer, Frame> entry = it.next();
                    if (!entry.getValue().referenciado) {
                        it.remove();
                        frames.put(pageNumber, new Frame(pageNumber));
                        reemplazado = true;
                        break;
                    }
                }
                if (!reemplazado) {
                    // Si todas están referenciadas, reemplazar la primera.
                    Iterator<Integer> it = frames.keySet().iterator();
                    if (it.hasNext()) {
                        int key = it.next();
                        frames.remove(key);
                        frames.put(pageNumber, new Frame(pageNumber));
                    }
                }
            }
            return false;
        }
    }

    // MODIFICADO: Método sincronizado para limpiar los bits de referencia.
    public synchronized void limpiarBitsReferencia() {
        for (Frame frame : frames.values()) {
            frame.referenciado = false;
        }
    }
}
