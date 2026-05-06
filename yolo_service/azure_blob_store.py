import os
import logging
from typing import Optional, List


log = logging.getLogger(__name__)


class AzureBlobStore:
    def __init__(
        self,
        connection_string: str,
        container_name: str,
        prefix: str = "",
    ) -> None:
        try:
            from azure.storage.blob import BlobServiceClient, ContentSettings  # type: ignore
        except Exception as e:  # pragma: no cover
            raise RuntimeError(
                "azure-storage-blob is required for Azure uploads. "
                "Install it with: pip install azure-storage-blob"
            ) from e

        self._ContentSettings = ContentSettings
        self._bsc = BlobServiceClient.from_connection_string(connection_string)
        self._container = self._bsc.get_container_client(container_name)
        self._prefix = (prefix.strip("/") + "/") if prefix and prefix.strip("/") else ""

    @staticmethod
    def from_env() -> Optional["AzureBlobStore"]:
        conn = os.getenv("AZURE_STORAGE_CONNECTION_STRING", "").strip()
        if not conn:
            return None
        container = os.getenv("AZURE_BLOB_CONTAINER", "imageframes").strip() or "imageframes"
        prefix = os.getenv("AZURE_BLOB_PREFIX", "").strip()
        return AzureBlobStore(conn, container, prefix)

    def upload_png_bytes(self, blob_name: str, data: bytes) -> str:
        name = f"{self._prefix}{blob_name.lstrip('/')}"
        bc = self._container.get_blob_client(name)
        bc.upload_blob(
            data,
            overwrite=True,
            content_settings=self._ContentSettings(content_type="image/png"),
        )
        return name

    def list_png(self, max_results: int = 500) -> List[str]:
        out: List[str] = []
        for b in self._container.list_blobs(name_starts_with=self._prefix):
            name = b.name
            if name.endswith(".png"):
                out.append(name)
            if len(out) >= max_results:
                break
        out.sort()
        return out

    def delete_prefix_png(self) -> int:
        n = 0
        for b in self._container.list_blobs(name_starts_with=self._prefix):
            if not b.name.endswith(".png"):
                continue
            self._container.delete_blob(b.name)
            n += 1
        return n
