resource "yandex_iam_service_account" "task01" {
	folder_id = var.yc_folder_id
	name = "task01-account"
}

// Grant permissions to the service account
resource "yandex_resourcemanager_folder_iam_member" "task01_admin_permission" {
	folder_id = var.yc_folder_id
	role = "admin"
	member = "serviceAccount:${yandex_iam_service_account.task01.id}"
}
resource "yandex_resourcemanager_folder_iam_member" "task01_object_storage_edit_permission" {
	folder_id = var.yc_folder_id
	role = "storage.editor"
	member = "serviceAccount:${yandex_iam_service_account.task01.id}"
}
resource "yandex_resourcemanager_folder_iam_member" "task01_functions_invoke_permission" {
	folder_id = var.yc_folder_id
	role = "functions.functionInvoker"
	member = "serviceAccount:${yandex_iam_service_account.task01.id}"
}

resource "yandex_iam_service_account_static_access_key" "task01_object_storage_static_key" {
	service_account_id = yandex_iam_service_account.task01.id
	description = "Static access key for object storage in task 1"
}

resource "yandex_container_registry" "default" {
	name = var.docker_registry_name
	folder_id = var.yc_folder_id
}

resource "yandex_storage_bucket" "photos" {
	bucket = var.photo_bucket_name
	max_size = 1073741824
	default_storage_class = "COLD"
	access_key = yandex_iam_service_account_static_access_key.task01_object_storage_static_key.access_key
	secret_key = yandex_iam_service_account_static_access_key.task01_object_storage_static_key.secret_key
}

resource "yandex_function" "face_detector" {
	name = var.face_detector_name
	description = "Detector of faces on photos"
	user_hash = var.face_detector_hash
	runtime = "java17"
	entrypoint = ""
	memory = 128
	execution_timeout = 600
	service_account_id = yandex_iam_service_account.task01.id
	content {
		zip_filename = var.face_detector_zip_file_path
	}
}

resource "yandex_function_trigger" "photos" {
	name = var.photo_trigger_name
	description = "Runs face detector when new photo is uploaded"
	object_storage {
		bucket_id = yandex_storage_bucket.photos.id
		create = true
		update = false
		batch_size = 1
		batch_cutoff = 20
	}
	function {
		id = yandex_function.face_detector.id
		service_account_id = yandex_iam_service_account.task01.id
	}
}

resource "yandex_message_queue" "photo_tasks" {
	name = var.photo_task_queue_name
	visibility_timeout_seconds = 600
	receive_wait_time_seconds = 20
	max_message_size = 16384
	message_retention_seconds = 604800
	access_key = yandex_iam_service_account_static_access_key.task01_object_storage_static_key.access_key
	secret_key = yandex_iam_service_account_static_access_key.task01_object_storage_static_key.secret_key
}

resource "yandex_function" "face_cutter" {
	name = var.face_cutter_name
	description = "Face photo cutter"
	user_hash = var.face_cutter_hash
	runtime = "java17"
	entrypoint = ""
	memory = 128
	execution_timeout = 300
	service_account_id = yandex_iam_service_account.task01.id
	content {
		zip_filename = var.face_cutter_zip_file_path
	}
}

resource "yandex_function_trigger" "photo_tasks" {
	name = var.photo_task_queue_trigger_name
	description = "Runs face photo cutter when new task is put into photo processing task queue"
	message_queue {
		queue_id = yandex_message_queue.photo_tasks.arn
		service_account_id = yandex_iam_service_account.task01.id
		batch_size = 1
		batch_cutoff = 20
	}
	function {
		id = yandex_function.face_cutter.id
		service_account_id = yandex_iam_service_account.task01.id
	}
}

resource "yandex_function" "telegram-bot-backend" {
	name = var.telegram_bot_backend_name
	description = "Telegram bot back-end"
	user_hash = var.telegram_bot_backend_hash
	runtime = "java17"
	entrypoint = ""
	memory = 128
	execution_timeout = 180
	service_account_id = yandex_iam_service_account.task01.id
	content {
		zip_filename = var.telegram_bot_backend_zip_file_path
	}
	environment = {
		PHOTOS_BUCKET_NAME = var.photo_bucket_name
		FACES_BUCKET_NAME = var.faces_bucket_name
		DATABASE_CONNECTION_STRING = yandex_ydb_database_serverless.photos.ydb_full_endpoint

		ACCESS_KEY_ID = yandex_iam_service_account_static_access_key.task01_object_storage_static_key.access_key
		SECRET_KEY = yandex_iam_service_account_static_access_key.task01_object_storage_static_key.secret_key
	}
}

resource "yandex_storage_bucket" "faces" {
	bucket = var.faces_bucket_name
	max_size = 1073741824
	default_storage_class = "ICE"
	access_key = yandex_iam_service_account_static_access_key.task01_object_storage_static_key.access_key
	secret_key = yandex_iam_service_account_static_access_key.task01_object_storage_static_key.secret_key
}

resource "yandex_ydb_database_serverless" "photos" {
	name = var.photos_db_name
	description = "Database with information about photos"
	folder_id = var.yc_folder_id
	deletion_protection = false
}

resource "yandex_ydb_table" "photos" {
	path = "photos"
	connection_string = yandex_ydb_database_serverless.photos.ydb_full_endpoint

	column {
		name = "face_photo_object_id"
		type = "Utf8"
		not_null = true
	}
	column {
		name = "original_photo_object_id"
		type = "Utf8"
		not_null = true
	}
	column {
		name = "left-coordinate"
		type = "Int32"
		not_null = true
	}
	column {
		name = "top-coordinate"
		type = "Int32"
		not_null = true
	}
	column {
		name = "right-coordinate"
		type = "Int32"
		not_null = true
	}
	column {
		name = "bottom-coordinate"
		type = "Int32"
		not_null = true
	}
	column {
		name = "name"
		type = "Utf8"
		not_null = false
	}

	primary_key = ["face_photo_object_id"]
}
