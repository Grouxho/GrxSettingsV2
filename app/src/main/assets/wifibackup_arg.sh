#!/system/bin/sh
export PATH=/system/bin:$PATH

mount -o rw,remount /data

mkdir -p $1


cp -rf /data/misc/wifi/wpa_supplicant.conf $1/$2

echo "arg 1 = $1 and arg2 = $2" >> $1/logarguments.txt