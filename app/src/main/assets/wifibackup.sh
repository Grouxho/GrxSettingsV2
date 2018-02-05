#!/system/bin/sh
export PATH=/system/bin:$PATH

now=`date +"%m_%d_%Y"`

mount -o rw,remount /data

mkdir -p /sdcard/backups/filewifibackup

cp -rf /data/misc/wifi/wpa_supplicant.conf /sdcard/backups/filewifibackup/wpa_supplicant_$now.conf