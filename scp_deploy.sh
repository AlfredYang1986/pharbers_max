host=spark@192.168.100.176
dir=/home/spark/
target=$host:$dir
name=pharbers-client-1.1

rm -rf target/$name
cd target/
unzip universal/$name.zip
cp -r ../pharbers_config $name/
cp -r ../jar $name/
scp -r $name $target

