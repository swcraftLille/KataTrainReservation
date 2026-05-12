FROM python:3.11-slim

WORKDIR /app

# Install dependencies
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

COPY booking_reference_service.py .

# Expose on port 8082
EXPOSE 8082

# RUN Booking reference service
COPY booking_reference_launcher.py .
CMD ["python", "booking_reference_launcher.py"]