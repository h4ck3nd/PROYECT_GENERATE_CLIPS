<%-- error.jsp --%>
<html>
<head>
    <title>Error</title>
</head>
<body>
    <h1>Error al procesar el video</h1>
    <p><%= request.getAttribute("errorMessage") %></p>
</body>
</html>
