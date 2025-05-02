<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<html>
<head>
    <title>Generador de Shorts de YouTube</title>
</head>
<body>
  <h2>Generador de Shorts de YouTube</h2>
  
  <!-- Formulario para subir un archivo de video -->
  <form method="post" enctype="multipart/form-data" action="<%= request.getContextPath() %>/video">
    <label for="videoFile">Selecciona un archivo de video:</label>
    <br><br>
    <input type="file" name="file" accept="video/*" required />
    <br><br>
    <input type="submit" value="Procesar" />
  </form>

  <br>

  <h2>Ver los clips disponibles</h2>
  <!-- Solo mostrar el botón para ver los clips si el parámetro 'showClips' está presente en la URL -->
  <c:if test="${param.showClips != null && param.showClips == 'true'}">
      <!-- Formulario para ver los clips -->
      <form action="<%= request.getContextPath() %>/resultados" method="get">
          <button type="submit">Ver Clips</button>
      </form>
  </c:if>

</body>
</html>
