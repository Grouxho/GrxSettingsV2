#!/system/bin/sh
export PATH=/system/bin:$PATH

mount -o rw,remount /data

cp -rf /sdcard/backups/wifibackup/wpa_supplicant.conf /data/misc/wifi/