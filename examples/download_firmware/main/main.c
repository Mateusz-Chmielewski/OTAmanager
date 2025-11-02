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



static const char *TAG = "download_firmware";

static EventGroupHandle_t wifi_event_group;
const int WIFI_CONNECTED_BIT = BIT0;

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
}
