FROM python:3.11-slim
WORKDIR /app
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt
COPY app/ ./app/
COPY data/ ./data/
COPY cert.pem key.pem ./
ENV FLASK_APP=app/middleware.py
EXPOSE 5000
CMD ["gunicorn", "--certfile=cert.pem", "--keyfile=key.pem", "-w", "4", "-b", "0.0.0.0:5000", "app.middleware:app"]