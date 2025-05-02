package controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/resultados")  // Define la URL para acceder al servlet
public class ClipServlet extends HttpServlet {
    private static final String CLIP_DIR = "/clips";  // Carpeta de clips (dentro de webapp)

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Obtener la ruta completa a la carpeta de clips en el servidor
        String clipsDirPath = getServletContext().getRealPath(CLIP_DIR);
        File clipsDir = new File(clipsDirPath);

        // Verificar si la carpeta existe y es un directorio
        if (clipsDir.exists() && clipsDir.isDirectory()) {
            // Obtener todos los archivos en la carpeta de clips
            File[] files = clipsDir.listFiles((dir, name) -> name.toLowerCase().endsWith(".mp4"));  // Filtrar solo los archivos .mp4

            List<String> clips = new ArrayList<>();
            if (files != null) {
                // Agregar los nombres de los archivos a la lista de clips
                for (File file : files) {
                    clips.add(file.getName());
                }
            }

            // Establecer el atributo 'clips' para pasarlo al JSP
            request.setAttribute("clips", clips);

            // Redirigir al JSP para mostrar los clips
            request.getRequestDispatcher("/result.jsp").forward(request, response);
        } else {
            // Si no se encuentran clips o el directorio no existe, pasar una lista vacía
            request.setAttribute("clips", new ArrayList<String>());
            request.getRequestDispatcher("/result.jsp").forward(request, response);
        }
    }
}
