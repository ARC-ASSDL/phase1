# eVTOL YOLO Service

## Setup & Run
```bash
cd yolo_service
pip install -r requirements.txt
python yolo_service.py
```
Service runs on **http://localhost:5050**

## What it does
- Receives frames (base64 PNG) at 10fps from the frontend
- Saves raw frames to local disk or Azure Blob Storage
- Detects buildings/walls/vehicles/structures using OpenCV colour-segmentation (no YOLO/COCO — avoids false positives on 3-D renders)
- Computes world-space coordinates using quaternion-corrected camera→world transform
- Saves annotated frames (YOLO-style bboxes)
- Returns JSON with obstacle list + annotated image to frontend

## Azure Blob Storage (store frames in the cloud)
You can either set these as environment variables, or put them into `yolo_service/.env` (loaded automatically on startup):
- `CAPTURE_DEST=azure` (or `both` to also keep local copies)
- `AZURE_STORAGE_CONNECTION_STRING=<your storage account connection string>`
- `AZURE_BLOB_CONTAINER=imageframes` (default: `imageframes`)
- `AZURE_BLOB_PREFIX=` (optional, e.g. `dev/` to group uploads)

Uploaded blob names:
- `raw/<timestamp>_frame_<index>.png`
- `annotated/<timestamp>_frame_<index>_annotated.png`

## Folder structure after running
```
yolo_service/
├── yolo_service.py
├── requirements.txt
└── captured_frames/   (only when CAPTURE_DEST=local/both)
    ├── 20250429_120000_frame_000001.png   ← raw frames
    └── annotated/
        └── 20250429_120000_frame_000001_annotated.png
```

## API
| Method | Path           | Description                    |
|--------|----------------|--------------------------------|
| GET    | /health        | Service status                 |
| POST   | /detect        | Send frame, get obstacles      |
| POST   | /reset_smooth  | Clear EMA state (call on reset)|
| GET    | /frames        | List saved frames              |
| POST   | /clear         | Delete all saved frames        |
