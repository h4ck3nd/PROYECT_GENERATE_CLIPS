package controller;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.ArrayList;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

@WebServlet("/video")
@MultipartConfig
public class VideoServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Obtener el archivo subido desde el formulario
        Part videoPart = request.getPart("file"); // 'file' es el nombre del campo en el formulario HTML

        // Verificar si se subió el archivo correctamente
        if (videoPart == null) {
            response.getWriter().println("Error: No se encontró el archivo.");
            return;
        }

        // Obtener el nombre del archivo y guardarlo temporalmente en el servidor
        String videoFileName = videoPart.getSubmittedFileName();
        String uploadsDirPath = request.getServletContext().getRealPath("/uploads");
        File uploadsDir = new File(uploadsDirPath);
        if (!uploadsDir.exists()) {
            uploadsDir.mkdirs();  // Crear el directorio si no existe
        }

        File videoFile = new File(uploadsDir, videoFileName);
        videoPart.write(videoFile.getAbsolutePath());

        // Crear la URL de destino para el servidor Flask
        URL url = new URL("http://localhost:5000/process_video");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW");

        // Crear flujo de salida
        OutputStream os = connection.getOutputStream();
        PrintWriter writer = new PrintWriter(new OutputStreamWriter(os, "UTF-8"), true);

        // Añadir el archivo al cuerpo de la solicitud
        String boundary = "----WebKitFormBoundary7MA4YWxkTrZu0gW";
        String CRLF = "\r\n"; // newline

        writer.append("--" + boundary).append(CRLF);
        writer.append("Content-Disposition: form-data; name=\"file\"; filename=\"" + videoFileName + "\"").append(CRLF);
        writer.append("Content-Type: " + connection.guessContentTypeFromName(videoFileName)).append(CRLF);  // Detecta el tipo de contenido
        writer.append(CRLF);
        writer.flush();

        // Escribir el archivo
        FileInputStream fileInputStream = new FileInputStream(videoFile);
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = fileInputStream.read(buffer)) != -1) {
            os.write(buffer, 0, bytesRead);
        }
        fileInputStream.close();
        os.flush();

        // Fin de la solicitud
        writer.append(CRLF).flush();
        writer.append("--" + boundary + "--").append(CRLF);
        writer.flush();

        // Obtener la respuesta del servidor Flask
        int responseCode = connection.getResponseCode();
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
        String inputLine;
        StringBuilder content = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();

        // Verificar si hubo error en la respuesta del servidor Flask
        if (responseCode >= 400) {
            response.getWriter().println("Error: " + content.toString());
            return;
        }

     // Procesar la respuesta del servidor Flask (JSON)
        try {
            JSONObject jsonObject = new JSONObject(content.toString());

            if (jsonObject.has("clips")) {
                JSONArray jsonArray = jsonObject.getJSONArray("clips");
                List<String> clips = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    clips.add(jsonArray.getString(i));
                }

                // Guardar en sesión para que esté disponible tras el redirect
                request.getSession().setAttribute("clips", clips);

                // Redirigir a index.jsp con parámetro para indicar que se deben mostrar los clips
                String redirectUrl = request.getContextPath() + "/index.jsp?showClips=true";
                response.sendRedirect(redirectUrl);
            } else {
                // Si no hay "clips", muestra el contenido por depuración
                response.getWriter().println("Respuesta sin clips: " + content.toString());
            }

        } catch (JSONException e) {
            // En caso de que la respuesta no sea un JSON válido
            response.getWriter().println("Error al procesar la respuesta JSON: " + e.getMessage());
        }

    }
}
