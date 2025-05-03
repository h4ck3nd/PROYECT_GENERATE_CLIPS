# Sistema de Generación de Shorts desde Videos con Flask y Java

Este proyecto permite subir un video, transcribirlo con `faster-whisper`, generar clips de 15s basados en el contenido, y usarlos desde una app Java (Eclipse).

---

## 🔧 Parte 1: Servidor Python (API Flask)

### 🚀 Requisitos

- Python 3.8 a 3.11
- `ffmpeg` instalado y accesible en el sistema (`ffmpeg -version`)

### 📁 Instalación

1. Clona el proyecto o copia los archivos.

  - Hay que reemplazar dentro del script de ``python`` las partes que pone [USER] por tu usuario de sistema y crear en la carpeta de ``ClonePageWeb`` (Pero de la Temporal) la carpeta ``clips``.
  
2. Crea y activa un entorno virtual:

```bash
python3.11 -m venv .venv
source venv/bin/activate   # Linux/macOS
venv\Scripts\activate      # Windows
```

3. Instala dependencias:

```bash
pip install -r requirements.txt
```

4. Asegúrate de tener el directorio destino de Eclipse correctamente configurado en la variable JAVA_CLIPS_DIR del script Python.

## ▶️ Ejecución

Inicia el servidor Flask (escuchando en el puerto 5000):

```bash
python generateClips.py
```

Esto levanta un endpoint POST en:

```bash
http://localhost:5000/process_video
```

## 📤 Uso del endpoint

Envía un video (mp4, avi, mov, mkv) al endpoint /process_video. Devuelve un JSON con rutas relativas de clips creados.
