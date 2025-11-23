#include <stdio.h>
#include <stdbool.h>
#include <unistd.h>
#include <string.h>
#include "freertos/FreeRTOS.h"
#include "freertos/task.h"
#include "freertos/event_groups.h"
#include "esp_system.h"
#include "esp_wifi.h"
#include "esp_event.h"
#include "esp_log.h"
#include "nvs_flash.h"

#include "esp_http_client.h"
#include "esp_https_ota.h"
#include "esp_crt_bundle.h"
#include "Esp_ota_ops.h"


#define SSID "ssid"
#define PASSWORD "password"
//#define FIRMWARE_URL "http://192.168.0.198:8080/firmware/test"
#define FIRMWARE_URL "http://192.168.0.198:8080/firmware/download/fe6469ac-8a21-4be4-b5fb-726242d4f918"
#define VERSION_URL "http://192.168.0.198:8080/firmware/version/fe6469ac-8a21-4be4-b5fb-726242d4f918"

#define MAX_HTTP_OUTPUT_BUFFER 2048


static const char *TAG = "download_firmware";

static EventGroupHandle_t wifi_event_group;
const int WIFI_CONNECTED_BIT = BIT0;

void blink_task(void *pvParameter) {
	while (1) {
//		ESP_LOGI(TAG, "Blink on!");
//		vTaskDelay(1000 / portTICK_PERIOD_MS);
//		ESP_LOGI(TAG, "Blink off!");
		vTaskDelay(1000 / portTICK_PERIOD_MS);
	}
}

static void wifi_event_handler(void* arg, esp_event_base_t event_base, int32_t event_id, void* event_data)
{
	if (event_base == WIFI_EVENT) {
		if (event_id == WIFI_EVENT_STA_START) {
			esp_wifi_connect();
		} else if (event_id == WIFI_EVENT_STA_DISCONNECTED) {
			ESP_LOGI(TAG, "Disconnected, retrying...");
			esp_wifi_connect();
		}
	} else if (event_base == IP_EVENT && event_id == IP_EVENT_STA_GOT_IP) {
		ip_event_got_ip_t* event = (ip_event_got_ip_t*) event_data;
		ESP_LOGI(TAG, "Got IP: " IPSTR, IP2STR(&event->ip_info.ip));
		xEventGroupSetBits(wifi_event_group, WIFI_CONNECTED_BIT);
	}
}

esp_err_t http_event_handler(esp_http_client_event_t *evt)
{
	// ESP_LOGD(TAG, "HTTP event id: %d", evt->event_id);
	return ESP_OK;
}

esp_err_t fetch_version_and_save_to_nvs(void)
{
    char local_response_buffer[MAX_HTTP_OUTPUT_BUFFER] = {0};

	esp_http_client_config_t config = {
		.url = VERSION_URL,
		.method = HTTP_METHOD_GET,
	};

	esp_http_client_handle_t client = esp_http_client_init(&config);
	esp_err_t err = esp_http_client_open(client, 0);

	if (err == ESP_OK) {
		int content_length = esp_http_client_fetch_headers(client);
		ESP_LOGI(TAG, "HTTP GET Status = %d, content_length = %d",
				esp_http_client_get_status_code(client),
				content_length);

		int data_read = esp_http_client_read(client, local_response_buffer, MAX_HTTP_OUTPUT_BUFFER - 1);

		if (data_read >= 0) {
			local_response_buffer[data_read] = 0;
			ESP_LOGI(TAG, "Version response (%d bytes): %s", data_read, local_response_buffer);

			nvs_handle_t nvs_handle;
			err = nvs_open("storage", NVS_READWRITE, &nvs_handle);
			if (err == ESP_OK) {
				err = nvs_set_str(nvs_handle, "firmware_ver", local_response_buffer);
				if (err == ESP_OK) {
					err = nvs_commit(nvs_handle);
					ESP_LOGI(TAG, "Version saved to NVS: %s", local_response_buffer);
				} else {
					ESP_LOGE(TAG, "Failed to write to NVS");
				}
				nvs_close(nvs_handle);
			} else {
				ESP_LOGE(TAG, "Failed to open NVS");
			}
		} else {
			ESP_LOGE(TAG, "Failed to read response");
		}
	} else {
		ESP_LOGE(TAG, "Failed to open HTTP connection: %s", esp_err_to_name(err));
	}

	esp_http_client_close(client);
	esp_http_client_cleanup(client);
	return err;
}

esp_err_t read_version_from_nvs(char *version_buffer, size_t buffer_size)
{
    nvs_handle_t nvs_handle;
    esp_err_t err = nvs_open("storage", NVS_READONLY, &nvs_handle);
    
    if (err == ESP_OK) {
        size_t required_size = buffer_size;
        err = nvs_get_str(nvs_handle, "firmware_ver", version_buffer, &required_size);
        
        if (err == ESP_OK) {
            ESP_LOGI(TAG, "Version read from NVS: %s", version_buffer);
        } else if (err == ESP_ERR_NVS_NOT_FOUND) {
            ESP_LOGI(TAG, "Version not found in NVS");
        } else {
            ESP_LOGE(TAG, "Failed to read from NVS");
        }
        nvs_close(nvs_handle);
    }
    
    return err;
}

// esp_err_t ota_example_verify_task(void)
// {
// 	esp_http_client_config_t config = {
// 		.url = VERSION_URL,
// 		.event_handler = http_event_handler,
// 		.keep_alive_enable = true,
// 		.skip_cert_common_name_check = true,
// 		.crt_bundle_attach = esp_crt_bundle_attach,
// 	};

// 	esp_https_ota_config_t ota_config = {
// 		.http_config = &config,
// 	};

// 	return esp_https_ota_verify(&ota_config);
// }

void ota_task(void *pvParameter) {
	while (1) {
		char firmware_url[256];
		char stored_version[64];
        esp_err_t nvs_err = read_version_from_nvs(stored_version, sizeof(stored_version));
		if (nvs_err != ESP_OK) {
			stored_version[0] = '\0';
		}
		snprintf(firmware_url, sizeof(firmware_url), "%s?current_version=%s", FIRMWARE_URL, stored_version);


		esp_http_client_config_t config = {
			.url = firmware_url,
			.event_handler = http_event_handler,
			.keep_alive_enable = true,
			.skip_cert_common_name_check = true,
			.crt_bundle_attach = esp_crt_bundle_attach,
		};

		esp_https_ota_config_t ota_config = {
			.http_config = &config,
		};

		ESP_LOGI(TAG, "Starting OTA from %s", firmware_url);
		esp_err_t ret = esp_https_ota(&ota_config);
		if (ret == ESP_OK) {
			ESP_LOGI(TAG, "OTA successful, restarting...");
			vTaskDelay(1000 / portTICK_PERIOD_MS);
			fetch_version_and_save_to_nvs();
			esp_restart();
		} else {
			ESP_LOGE(TAG, "OTA failed...");
		}

		vTaskDelay(60 * 1000 / portTICK_PERIOD_MS);
	}

	vTaskDelete(NULL);
}

void wifi_connect(char* wifi_ssid, char* wifi_password) {
	wifi_config_t wifi_config = {
		.sta = {
			.threshold.authmode = WIFI_AUTH_WPA2_PSK,
		},
	};

	strncpy((char*)wifi_config.sta.ssid, wifi_ssid, sizeof(wifi_config.sta.ssid));
	strncpy((char*)wifi_config.sta.password, wifi_password, sizeof(wifi_config.sta.password));	

	ESP_ERROR_CHECK(esp_wifi_set_mode(WIFI_MODE_STA));
	ESP_ERROR_CHECK(esp_wifi_set_config(ESP_IF_WIFI_STA, &wifi_config));
	ESP_ERROR_CHECK(esp_wifi_start());

	ESP_LOGI(TAG, "Connecting to WiFi SSID: %s", wifi_ssid);
	xEventGroupWaitBits(wifi_event_group, WIFI_CONNECTED_BIT, false, true, portMAX_DELAY);
	ESP_LOGI(TAG, "Connected to WiFi SSID: %s", wifi_ssid);
}

void wifi_init() {
    wifi_event_group = xEventGroupCreate();
    
    esp_netif_create_default_wifi_sta();
    
    wifi_init_config_t cfg = WIFI_INIT_CONFIG_DEFAULT();
    ESP_ERROR_CHECK(esp_wifi_init(&cfg));
    
    ESP_ERROR_CHECK(esp_event_handler_register(WIFI_EVENT, ESP_EVENT_ANY_ID, &wifi_event_handler, NULL));
    ESP_ERROR_CHECK(esp_event_handler_register(IP_EVENT, IP_EVENT_STA_GOT_IP, &wifi_event_handler, NULL));
}

void system_init() {
	ESP_ERROR_CHECK(nvs_flash_init());
	ESP_ERROR_CHECK(esp_netif_init());
	ESP_ERROR_CHECK(esp_event_loop_create_default());
}

void app_main(void)
{
	ESP_LOGI(TAG, "OTA example app_main start");

    system_init();
    wifi_init();
    wifi_connect(SSID, PASSWORD);

	xTaskCreate(&ota_task, "ota_task", 8192, NULL, 5, NULL);
	xTaskCreate(&blink_task, "blink_task", 2048, NULL, 5, NULL);
}
