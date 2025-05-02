from flask import Flask, request, jsonify
from moviepy.editor import VideoFileClip
from faster_whisper import WhisperModel
import uuid
import os
import logging
import shutil

# Configuración
logging.basicConfig(level=logging.DEBUG)
app = Flask(__name__)

model = WhisperModel("base", compute_type="int8")
# Ruta absoluta donde Eclipse coloca el proyecto (cambiar si es necesario)
JAVA_CLIPS_DIR = r"C:\Users\clipd\eclipse-workspace\.metadata\.plugins\org.eclipse.wst.server.core\tmp0\wtpwebapps\GenerateClipsWeb\clips"
ALLOWED_EXTENSIONS = {'mp4', 'mkv', 'avi', 'mov'}
CLIPS_DIR = "clips"  # Directorio temporal para guardar clips en Flask

# Helpers
def allowed_file(filename):
    return '.' in filename and filename.rsplit('.', 1)[1].lower() in ALLOWED_EXTENSIONS

def save_uploaded_file(file):
    ext = file.filename.rsplit('.', 1)[1].lower()
    filename = f"{uuid.uuid4()}.{ext}"
    file.save(filename)
    return filename

# Rutas
@app.route("/process_video", methods=["POST"])
def process_video():
    if 'file' not in request.files:
        return jsonify({"error": "No file part in request"}), 400

    file = request.files['file']
    if file.filename == '' or not allowed_file(file.filename):
        return jsonify({"error": "Invalid file type. Only mp4, mkv, avi, mov allowed."}), 400

    try:
        filename = save_uploaded_file(file)
        logging.info(f"Video uploaded: {filename}")
    except Exception as e:
        logging.exception("Error saving file")
        return jsonify({"error": f"Failed to save file: {str(e)}"}), 500

    try:
        segments, _ = model.transcribe(filename, beam_size=5)
        logging.info("Transcription complete")
    except Exception as e:
        logging.exception("Transcription failed")
        os.remove(filename)
        return jsonify({"error": f"Transcription error: {str(e)}"}), 500

    clips_paths = []

    try:
        with VideoFileClip(filename) as video:
            for seg in segments:
                start = max(0, seg.start)
                end = min(seg.end, start + 15)
                try:
                    subclip = video.subclip(start, end).resize(height=1080)  # Mantiene proporciones

                    if not os.path.exists(CLIPS_DIR):
                        os.makedirs(CLIPS_DIR)
                    out_name = os.path.join(CLIPS_DIR, f"{uuid.uuid4()}.mp4")

                    subclip.write_videofile(
                        out_name,
                        codec="libx264",
                        audio_codec="aac",
                        preset="slow",  # Calidad sobre velocidad
                        ffmpeg_params=[
                            "-crf", "17",  # Calidad casi sin pérdida (menor número = más calidad)
                            "-preset", "slow",  # Codificación más lenta pero con mejor eficiencia
                            "-pix_fmt", "yuv420p",  # Compatibilidad estándar
                            "-movflags", "+faststart"  # Mejora carga progresiva en web
                        ],
                        threads=4,  # Usa múltiples núcleos para procesar
                        logger=None
                    )

                    # Copiar clip a la carpeta del servidor de Eclipse
                    os.makedirs(JAVA_CLIPS_DIR, exist_ok=True)
                    dest_path = os.path.join(JAVA_CLIPS_DIR, os.path.basename(out_name))
                    shutil.copy(out_name, dest_path)

                    # Agregar la ruta relativa que se utilizará en la app Java
                    clips_paths.append(f"clips/{os.path.basename(out_name)}")
                    logging.debug(f"Clip saved and copied: {dest_path}")
                except Exception as e:
                    logging.error(f"Error creating clip: {str(e)}")
    except Exception as e:
        logging.exception("Failed to process video for clips")
        os.remove(filename)
        return jsonify({"error": f"Video processing error: {str(e)}"}), 500
    finally:
        if os.path.exists(filename):
            os.remove(filename)

    if clips_paths:
        logging.info(f"{len(clips_paths)} clips generated.")
        return jsonify({"clips": clips_paths})
    else:
        return jsonify({"message": "No clips generated."}), 200

# Inicio de app
if __name__ == "__main__":
    os.makedirs(CLIPS_DIR, exist_ok=True)
    os.makedirs(JAVA_CLIPS_DIR, exist_ok=True)
    app.run(host="0.0.0.0", port=5000)