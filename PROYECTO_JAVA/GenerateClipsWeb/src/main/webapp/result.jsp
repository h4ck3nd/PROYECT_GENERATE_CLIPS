<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Resultados - Clips</title>
    <style>
        /* Estilos generales */
        body {
            font-family: Arial, sans-serif;
            background-color: #f4f4f9;
            color: #333;
            padding: 20px;
        }

        h2 {
            text-align: center;
            font-size: 2em;
            color: #5a5a5a;
            margin-bottom: 20px;
        }

        /* Estilo de los contenedores de los clips */
        .video-container {
            display: flex;
            flex-wrap: wrap;
            justify-content: center;
            gap: 20px; /* Espaciado entre videos */
            margin-top: 20px;
        }

        .video-item {
            border: 2px solid #ddd;
            border-radius: 8px;
            overflow: hidden;
            background-color: #fff;
            width: 320px; /* Ancho fijo para los videos */
            box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
            transition: transform 0.3s ease;
        }

        .video-item:hover {
            transform: scale(1.05);
        }

        video {
            width: 100%;
            height: auto;
        }

        .video-title {
            text-align: center;
            font-size: 1.2em;
            padding: 10px;
            background-color: #f1f1f1;
            color: #333;
            border-top: 1px solid #ddd;
        }

        /* Estilo para mensaje cuando no hay clips */
        .no-clips-message {
            text-align: center;
            font-size: 1.5em;
            color: #888;
            margin-top: 40px;
        }

    </style>
</head>
<body>
	<a href="<%= request.getContextPath() %>/index.jsp?showClips=true" style="text-decoration: none; color: black; font-weight: bold;">Volver Atras</a>
    <h2>Lista de Clips</h2>

    <!-- Verificación de que la lista no esté vacía -->
    <c:if test="${not empty clips}">
        <div class="video-container">
            <c:forEach var="clip" items="${clips}">
                <div class="video-item">
                    <!-- Reproductor de video -->
                    <video controls>
                        <source src="<%= request.getContextPath() %>/clips/${clip}" type="video/mp4">
                        Your browser does not support the video tag.
                    </video>
                    <div class="video-title">${clip}</div>
                </div>
            </c:forEach>
        </div>
    </c:if>

    <!-- Si la lista de clips está vacía -->
    <c:if test="${empty clips}">
        <p class="no-clips-message">No se encontraron clips.</p>
    </c:if>

</body>
</html>
