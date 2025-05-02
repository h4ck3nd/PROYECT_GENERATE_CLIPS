# ☕ Parte 2: Cliente Java (Eclipse)

## 🎯 Objetivo

Consumir el endpoint Python y mostrar clips generados (o integrarlos en la UI Java).

## 🧩 Estructura Sugerida

- Proyecto Dynamic Web Project en Eclipse con soporte para Servlets.

- Carpeta de destino para clips en:

```css
<ruta del workspace Eclipse>\.metadata\.plugins\org.eclipse.wst.server.core\tmp0\wtpwebapps\GenerateClipsWeb\clips
```

> Esta ruta debe coincidir con ``JAVA_CLIPS_DIR`` en Python.

## 📦 Dependencias

- Apache HttpClient o HttpURLConnection para hacer POST multipart a Flask.

- Biblioteca JSON como ``org.json``, ``Jackson`` o similar.

## 🛠️ Código de ejemplo (simplificado)

```java
// POST al servidor Python
HttpURLConnection conn = (HttpURLConnection) new URL("http://localhost:5000/process_video").openConnection();
conn.setRequestMethod("POST");
// Agrega encabezados y archivo .mp4 como multipart/form-data
// Luego lee la respuesta JSON con rutas de clips
```

## 📂 Importar en Eclipse

1. Crea un nuevo Dynamic Web Project.

2. Copia el código cliente en un servlet o controlador.

3. Crea carpeta ``clips/`` en el webapp.

4. Usa el servlet para mostrar o listar los archivos generados.
