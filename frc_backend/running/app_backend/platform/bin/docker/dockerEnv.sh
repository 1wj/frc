#!/bin/sh

function log_info()
{
  local date=`date`
  local para=$1
  echo "$date $1"
  $1
  echo "log info:$date $1" &>> $SYS_LOG
}
SYS_LOG=dockerEnv.log

if [ "$isSidecarEnable" != "" ]; then
  if [ "$isSidecarEnable" != "false" ]; then

    export tsidecarPort=$sidecarPort
    export tserverPort=$serverPort
    
    export sidecarPort=$tserverPort
    export serverPort=$tsidecarPort
  else  
    echo "isSidecarEnable=false"> isSidecarEnable.txt
  fi
else  
  echo "isSidecarEnable="> isSidecarEnable.txt
fi

#程式路徑
Path=$1
#檔案類型
TypeName=$2
#排除檔案
removeName=$3
#排除檔案2
removeName2=$4
#排除檔案3
removeName3=$5
#排除檔案4
removeName4=$6

#修改方法一
find $Path/ -name "*.$TypeName" -a ! -name "$removeName1" -a ! -name "$removeName2" -a ! -name "$removeName3" -a ! -name "$removeName4"  | xargs  grep -r '@' > envSpace.txt

sed 's/[[:space:]]//g' envSpace.txt > env.txt
envDate=$(cat env.txt)

#迴圈解析@
for date in ${envDate}; do
 #echo $date
 field=2
 env=test
 filePath=$(cut -d':' -f1 <<< "$date")
 #echo $filePath
 while [[ "$env" != ""  ]]; do
  env=$(cut -d'@' -f$field <<< "$date")
  let "field+=2"
  if [ "$env" != "" ]; then
   #修改方法二
   envReplace=${env//./_}
   envReplace=${envReplace//-/DH}
   log_info "sed -i s#@"$env"@#"$(eval echo \$$envReplace)"#g $filePath"
  fi
 done
done

rm -f envSpace.txt
rm -f env.txt

