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
