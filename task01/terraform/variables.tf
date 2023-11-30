variable "yc_token" {
	type        = string
	description = "Yandex Cloud API key"
	sensitive   = true
}

variable "yc_cloud_id" {
	type        = string
	description = "Yandex Cloud ID"
	sensitive   = true
}

variable "yc_folder_id" {
	type        = string
	description = "Yandex Cloud folder ID"
	sensitive   = true
}

variable "docker_registry_name" {
	type        = string
	description = "Name of Docker container registry"
}

variable "photo_bucket_name" {
	type        = string
	description = "Name of bucket with original photos"
}

variable "face_detector_name" {
	type        = string
	description = "Name of photo processor which detects faces"
}

variable "face_detector_zip_file_path" {
	type        = string
	description = "Path to ZIP file with photo processor which detects faces"
}

variable "face_detector_hash" {
	type        = string
	description = "Hash of photo processor which detects faces"
}

variable "photo_trigger_name" {
	type        = string
	description = "Name of trigger on new original photos"
}

variable "photo_task_queue_name" {
	type        = string
	description = "Name of photo processing task queue"
}

variable "photo_task_queue_trigger_name" {
	type        = string
	description = "Name of trigger on new tasks in photo processing task queue"
}

variable "face_cutter_name" {
	type        = string
	description = "Name of photo processor which cuts face photos"
}

variable "face_cutter_zip_file_path" {
	type        = string
	description = "Path to ZIP file with photo processor which cuts face photos"
}

variable "face_cutter_hash" {
	type        = string
	description = "Hash of photo processor which cuts face photos"
}

variable "telegram_bot_backend_name" {
	type        = string
	description = "Name of Telegram bot back-end"
}

variable "telegram_bot_backend_zip_file_path" {
	type        = string
	description = "Path to ZIP file with Telegram bot back-end"
}

variable "telegram_bot_backend_hash" {
	type        = string
	description = "Hash of Telegram bot back-end"
}

variable "faces_bucket_name" {
	type        = string
	description = "Name of bucket with cut faces"
}

variable "photos_db_name" {
	type        = string
	description = "Name of photo DB"
}

variable "api_gateway_name" {
	type        = string
	description = "Name of API gateway"
}
