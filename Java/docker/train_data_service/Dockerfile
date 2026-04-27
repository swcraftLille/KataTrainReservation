FROM python:3.11-slim
WORKDIR /app

# COPY python files
COPY *.py ./
# COPY and install dependencies
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

# COPY default trains data
COPY data/default_trains.json trains.json

EXPOSE 8081

CMD ["python", "train_data_service_launcher.py"]